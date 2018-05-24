package ASAP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.WebDriver;







import com.mercury.qualitycenter.otaclient.ClassFactory;
import com.mercury.qualitycenter.otaclient.ITDConnection;
import com.mercury.qualitycenter.otaclient.ITest;


public class Driver {
	
	//Initialize
	private WebDriver driver;
	private HashMap <Integer, String> Temp = new HashMap<Integer, String>();
	private HashMap <String, String> Dictionary = new HashMap<String, String>();	
	private HashMap <String, String> Environment = new HashMap<String, String>();
	private HashMap <String, String> objGlobalDictOriginal = new HashMap<String, String>();
	private String strJUnitTestName;
	Reporting Reporter;	
	private String Skip, strTestName;
    
 
    //Initializing Classes
    private DBActivities objDB = new DBActivities(); //DB Activities
    private Connection objConn = null; //DB Connection
    private ResultSet objRS = null;	  //DB recordSet
    private QualityCenter QC; //Quality Center
	

    //Main
	public boolean main(WebDriver driverTemp, ITDConnection objTD,  String Env, String Datasheet, String RootPath, String strTestPlanPath, String strTestSetPath, String strTestSetName, String driverType, String strGuiPackage) throws SQLException{
		
		//Initialize Variables
		int iScriptStartRow, iCurrentHeaderRow, iScriptEndRow;		
		//String strTestName;
		
		//Assign value to the driver
		driver = driverTemp;
		
        //******************* Start up Parameters ************************
        String User = System.getProperty("user.name");
        //******************* Start up Parameters ************************
        
         //Set the implicit time out for the driver
        driver.manage().timeouts().implicitlyWait(20000, TimeUnit.MILLISECONDS);
        
        //******************* Fetch Current TimeStamp ************************
        java.util.Date today = new java.util.Date();
        Timestamp now = new java.sql.Timestamp(today.getTime());
        String timeStamp = now.toString().replaceAll(":", "").replaceAll("-", "").replaceAll(" ", "");
        timeStamp = timeStamp.replace(".", "");        
        
        //********************* Set all paths *****************************        
        String ExecutionFolderPath = RootPath + "Execution";
        String StorageFolderPath = RootPath + "trunk/Storage";
        String DatasheetsPath = StorageFolderPath + "/DataSheets";
        String EnvironmentXLSPath = StorageFolderPath + "/Environments/Environments.xls";
        String CurrentExecutionFolder = ExecutionFolderPath + "/" + Env + "/" + Datasheet + "/" + User;
        String CurrentExecutionDatasheet = CurrentExecutionFolder + "/" + Datasheet + ".xls";
        String HTMPReports = CurrentExecutionFolder + "/HTML_REP_" + timeStamp + "_" + driverType;
        String SnapshotsFolder = HTMPReports + "/Snapshots";
        //********************* Set all paths *****************************
        
        //************** Adding Path to Environment Variables *************        
        Environment.put("ROOTPATH", RootPath);
        Environment.put("EXECUTIONFOLDERPATH", ExecutionFolderPath);
        Environment.put("STORAGEFOLDERPATH", StorageFolderPath);
        Environment.put("DATASHEETSPATH", DatasheetsPath);
        Environment.put("ENVIRONMENTXLSPATH", EnvironmentXLSPath);
        Environment.put("CURRENTEXECUTIONFOLDER", CurrentExecutionFolder);
        Environment.put("CURRENTEXECUTIONDATASHEET", CurrentExecutionDatasheet);
        Environment.put("HTMLREPORTSPATH", HTMPReports);
        Environment.put("SNAPSHOTSFOLDER", SnapshotsFolder);
        Environment.put("TESTSETPATH", strTestSetPath);
        Environment.put("AUTO_TESTPLANPATH", strTestPlanPath);
        Environment.put("TESTSETNAME", strTestSetName);
        Environment.put("ENV_CODE", Env);
        //************** Adding Path to Environment Variables ***************
                
        //***** Query Environments.xls to fetch the environment params ******
        
        //Connect to Excel sheet
        objDB = new DBActivities();
        objConn = null;
        String SQL = "";
        
        //Call the function to add Environment Xls info to Environment Hashmap
        if (fGetEnvironmentXlsDetails()==false)
        {
        	System.out.println("Failed to get information from the Environment xls file");
        	return false;        	
        }
		        
	        //******************** Create Execution Folder **********************
        try{
	        // Create multiple directories
        	boolean success = (new File(SnapshotsFolder)).mkdirs();
        	if (success) 
        	{
        		System.out.println("Directories: " + SnapshotsFolder + " created");
        	}
      	}
        catch (Exception e)
        {	
        	//Catch exception if any
      		System.err.println("Error: " + e.getMessage());
      		return false;
      	}
        
      	//Copy the calendar XLS from storage folder if not present
        if (!(new File(CurrentExecutionDatasheet)).exists())
        {
        	//Function call to copy the xls
        	fCopyXLS(DatasheetsPath + "/" + Datasheet + ".xls", CurrentExecutionDatasheet);
        }
        
        //******************** Create Summary report ************************
        Reporter =  new Reporting(objTD, driver,Environment,Dictionary);
        Reporter.fnCreateSummaryReport();

        //Instance of Gui and Quality Center
        QC = new QualityCenter();
        //GuiFunctions objGui = new GuiFunctions(driver,Dictionary,Environment,Reporter);
        
        //Set JUnit Test Name
        strJUnitTestName = driverType.toUpperCase();
        
        //Calling Clear Skip
        fClearSkip("ABS");
        
        //Call the function to get the Header Rows in an array List
        ArrayList <String> arrHeaders = fGetHeaderRows(CurrentExecutionDatasheet);
        
        //Validate if it is null
        if (arrHeaders == null){
        	System.out.println("Failed to execute the fGetheaderRows function");
        	return false;
        }
        
        
        
        //Loop through all the elements in the array list for execution
        for (int z = 0; z < arrHeaders.size(); z ++)
        {
        	//Get the Script Start row, end row and the Current row
        	
            iScriptStartRow = Integer.parseInt(arrHeaders.get(z).split("-")[0]);
            iCurrentHeaderRow = iScriptStartRow;
            iScriptEndRow = Integer.parseInt(arrHeaders.get(z).split("-")[1]); 
            
            //Loop through the Start rows and end to perform the execution
            do{

	            for (int y = iScriptStartRow; y < iScriptEndRow; y ++)
	            {
                    //Call function to get all params or its values in global dictionary
                    if (fProcessDataFile(y) == 1)
                    {
                        //If Skip is null execute the bus function
                    	if (Skip.equals(""))
                        {
                    		//Function call to get the reference data from keep refer sheet
                    		Boolean resultGetRefer = fGetReferenceData();
                    		
                            if (!(resultGetRefer))
                            {
                            	System.out.println("Error in executing fGetReferenceData");
                            	return false;
                            	
                            }
                            //Call function to create a HTML report
                            Reporter.fnCreateHtmlReport(Dictionary.get("TEST_NAME") + "-"+ Dictionary.get("ACTION"));
                            
                    		//Function call to create Add Test in QC
                            if (objTD!= null && objTD.connected() == true)
                            {
                            	//if the Test Plan Path is not mentioned in the calendar then report it and assigned the Automation Test Plan path
                            	if (Dictionary.get("TEST_PLAN_PATH")==null || Dictionary.get("TEST_PLAN_PATH").isEmpty() || Dictionary.get("TEST_PLAN_PATH").equals("")){
                                	//ASsign the default Automation Test PLan path and report it as well
                            		System.out.println("Test PLan path is not mentioned int the calendar. Hence setting it to the Automation Test Plan path " + Environment.get("AUTO_TESTPLANPATH"));
                                	Environment.put("TESTPLANPATH", Environment.get("AUTO_TESTPLANPATH"));
                            	}else{
                                	//Get the Test PLan path from the Calenadr
                                	Environment.put("TESTPLANPATH", Dictionary.get("TEST_PLAN_PATH"));
                            	}
                            	
                            	//Add test in QC
                    			if (QC.fAddTest(objTD, Dictionary, Environment, Dictionary.get("TEST_NAME"))== false)
    	                    		{
    	                    			System.out.println("Unable to add test " + Dictionary.get("TEST_NAME") + " in QC");
    	                    			return false;
    	                    		}	 
                    		}
                            //Execute command 
                            Boolean result = Execute("GuiFunctions." + strGuiPackage , Dictionary.get("ACTION"));

                    		//Function call to set the reference data from keep refer sheet
                    		Boolean resultSetRefer = fSetReferenceData();
                    		
                            if (!(resultSetRefer))
                            {
                            	System.out.println("Error in executing fSetReferenceData");
                            	return false;
                            }
                            
                            String strResult = "";
                            if (result){
                            	System.out.println("PASSED");
                            	strResult = "PASSED";
                            }else{
                            	System.out.println("FAILED");
                            	strResult = "FAILED";
                            }
                            
                            //Function call to update status of the TC in QC
                            if (objTD!= null && objTD.connected() == true)
                    		{
                			  if (QC.fUpdateTestStatusInQC(objTD,strResult,Dictionary)==false)
                                {
                                	System.out.print("Failed to update TC status in Test lab in QC. Continuing execution...");
                                }
                    		}

                            //Call function to close HTML report
                            Reporter.fnCloseHtmlReport();
                            
                          //Call function to attach the result to current run in test set
                            if (objTD!= null && objTD.connected() == true)
                            {
                            	if (QC.fAttachResultsToRun(objTD, Dictionary, Environment)==false)
                            	{
                            		System.out.print("Failed to attach attachment to test in Test lab in QC");
                            		//System.exit(0);
                        
                            	}
                            }
                            
                            //Call function to update the currently executed row with X
                            fUpdateTestCaseRowSkip(iScriptStartRow);
                        }
                    }
                       
                    //Increment iScriptStartRow
                    iScriptStartRow++;
                } 
            	
            }while (iScriptStartRow != iScriptEndRow); //Execute till Start row reaches end row
            //Call function to update the current row
            fUpdateTestCaseRowSkip(iCurrentHeaderRow);
        	
        }
        
        Reporter.fnCloseTestSummary(); 
        
        //Zip Over all Reports         
		File directoryToZip = new File(Environment.get("HTMLREPORTSPATH"));
		List<File> fileList = new ArrayList<File>();		
		QualityCenter.getAllFiles(directoryToZip, fileList);
		QualityCenter.writeZipFile(directoryToZip, fileList,Environment.get("EXECUTIONFOLDERPATH") + "//Jenkins_Build_Report.zip");		
        
        //Move 
        
        //Delete
        
        
        driver.quit();
        return true;
		
	}
	
	public static void main(String[] args) {
    }
	

    //*****************************************************************************************
    //*	Name		    : Execute
    //*	Description	    : Function to give dynamic calls to the methods from BusFunctions Class
    //*	Author		    : Anil Agarwal
    //*	Input Params	: String className, String methodName
    //*	Return Values	: Boolean depending on the success of the Business function invoked
    //*****************************************************************************************
	public boolean Execute(String className, String methodName)
	{
		try{
			//Param Object array
			Object params[]= {};
			
			//get the class
			Class<?> thisClass = Class.forName(className);
			
			Class[] types = new Class[] { org.openqa.selenium.WebDriver.class, java.util.HashMap.class, java.util.HashMap.class, ASAP.Reporting.class  };  
			
			//create a constructor with the types array  
			Constructor constructor = thisClass.getConstructor(types);  
			  
			//create an array of argument values  
			Object[] args = new Object[] {driver, Dictionary, Environment,Reporter};  
			  
			//now use ther args array to create an instance  
			Object guiFunctions =  constructor.newInstance(args);  
			
			//get an instance
			//Object iClass = thisClass.c
			//GuiManageContacts guiFunctions = new GuiManageContacts(driver, Dictionary, Environment,Reporter);
			
			//Get method
			Method  method = thisClass.getDeclaredMethod(methodName, new Class[] {}); 
			//thisClass.getDeclaredMethod (methodName,null);
			
			//call the method

			Object objReturn  = method.invoke(guiFunctions, params);
			
			if (objReturn.equals(true)){
				return true;
			}
			else{
				return false;
			}
			
		/*}catch (InstantiationException eIE){
			System.err.print(eIE);
			return false;*/
		} catch (NoSuchMethodException eNSME){
			eNSME.printStackTrace();
			return false;
		}catch (ClassNotFoundException eCNFE){
			System.err.print(eCNFE);
			return false;
		}catch (IllegalAccessException eIAE){
			System.err.print(eIAE);
			return false;
		}catch (InvocationTargetException eITE)	{
			System.err.print(eITE);
			return false;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
    //*****************************************************************************************
   //*	Name		    : fCopyXLS
   //*	Description	    : Function to copy given XLS
	//*	Author		    : Anil Agarwal
   //*	Input Params	: string inputXLS - Path of the input XLS
   //*				  	  String destXLS - PAth to store the Destination xls	
   //*	Return Values	: Connection object having the connection
   //*****************************************************************************************
   public void fCopyXLS(String inputXLS, String destXLS){
	   try
	   {  
		   		//Initialize Files
		   		File f1 = new File(inputXLS);  
				File f2 = new File(destXLS);  
				InputStream in = new FileInputStream(f1);    
				OutputStream out = new FileOutputStream(f2);   
				byte[] buf = new byte[1024];  
				int len;  
					
				while ((len = in.read(buf)) > 0)
				{  
					out.write(buf, 0, len);  
				}  
					
				in.close();  
				out.close();  
			
   		} 
   		catch(FileNotFoundException ex)
   		{  
				System.out.println(ex.getMessage() + " in the specified directory.");  
				System.exit(0);  
   		} 
	   	catch(IOException e)
	   	{  
				System.out.println(e.getMessage());
				System.exit(0);  
		}  
   	}
   
	
    //***********************************************************************
    //* Name			: fGetEnvironmentXlsDetails
    //*	Description	    : Function to Get the Environment xls details in hashmap
    //*	Author		    : Anil Agarwal
    //* Input Params	: String strEnvXlsPath - The Environment xls path
    //*	Return Values	: boolean
    //***********************************************************************
	public boolean fGetEnvironmentXlsDetails() {

		try {
		    int iVersion = -1;
		    int iEnvironment = -1;
		    boolean bFlag = false;
			
		    //Get the Column Index for the VERSION Column
		    iVersion = fGetColumnIndex(Environment.get("ENVIRONMENTXLSPATH"), "ENVIRONMENTS", "VERSION");
		    
		    //Check if the index value is proper
		    if (iVersion == -1 ){
		    	System.out.println("Failed to find the Version Column in the file " + Environment.get("ENVIRONMENTXLSPATH"));
		    	return false;
		    }

		    //Get the Column Index for the ENVIRONMENT Column
		    iEnvironment = fGetColumnIndex(Environment.get("ENVIRONMENTXLSPATH"), "ENVIRONMENTS", "ENVIRONMENT");
		    
		    //Check if the index value is proper
		    if (iEnvironment == -1 ){
		    	System.out.println("Failed to find the Environment Column in the file " + Environment.get("ENVIRONMENTXLSPATH"));
		    	return false;
		    }
		    
			//Create the FileInputStream obhect			
			FileInputStream file = new FileInputStream(new File(Environment.get("ENVIRONMENTXLSPATH")));		     
		    //Get the workbook instance for XLS file 
		    HSSFWorkbook workbook = new HSSFWorkbook(file);
		 
		    //Get first sheet from the workbook
		    HSSFSheet sheet = workbook.getSheet("ENVIRONMENTS");
		    
		    //Get the Number of Rows
		    int iRowNum = sheet.getLastRowNum();
		    
		    //Get the Column count
		    int iColCount = sheet.getRow(0).getLastCellNum();
		    
		    for (int iRow = 0; iRow <= iRowNum; iRow++)
		    {
	            //Check if the version and the envioronment value is matching
	            //String strVersion = sheet.getRow(iRow).getCell(iVersion).getStringCellValue().trim().toUpperCase();
	            String strEnvironment = sheet.getRow(iRow).getCell(iEnvironment).getStringCellValue().trim().toUpperCase();
	            //Currently checking only on the basis of envrionment
	            if (!strEnvironment.equals(Environment.get("ENV_CODE")))			            	
	            {
	            	continue;
	            }
	            
	            //Set the flag value to true
	            bFlag = true;
	            String strKey = "";
	            String strValue = "";
		        //Loop through all the columns
		        for (int iCell = 0; iCell < iColCount; iCell ++ )
		        {
                    //Put the Details in Environment Hashmap
		            strKey = sheet.getRow(0).getCell(iCell).getStringCellValue().trim().toUpperCase();
		            
	        		//Fetch the value for the Header Row
	        		if (sheet.getRow(iRow).getCell(iCell, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null)
	        		{
	        			strValue = "";
	        		}else{
	        			strValue = sheet.getRow(iRow).getCell(iCell).getStringCellValue();
	        		}

		            Environment.put(strKey, strValue);
		        }
		        break;
		    }
		    //Close the file
		    file.close();

		    //If bFlag is true
		    if (bFlag == false)
		    {
		    	System.out.println("Environment Code " + Environment.get("ENV_CDODE") + " not found in the Environment xls");
		    	return false;
		    }

		    return true;
		     
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		    return false;
		} catch (IOException e) {
		    e.printStackTrace();
		    return false;
		}
	}

    //***********************************************************************
    //* Name			: fGetHeaderRows
    //*	Description	    : Function to Get the Header rows in an Array List
    //*	Author		    : Anil Agarwal
    //* Input Params	: String strCalendarPath - The Calendar xls path
    //*	Return Values	: ArrayList
    //***********************************************************************
	public ArrayList<String> fGetHeaderRows(String strCalendarPath) {
		// TODO Auto-generated method stub
		try {
		    int iHeader = -1;
		    int iID = -1;
		    int iSkip = -1;
		    String strIndex = "";
		    String strSkip = "";
		    boolean bFlag = false;
		    boolean bPreviousFlag = false;
		    ArrayList<String> arrIndexes = new ArrayList<String>();
		    String strTemp = "";
		    String strTemp1 = "";
		    String strTemp2  = "";
	        
	        
		    //Get the Column Index for the Skip Column
		    iSkip = fGetColumnIndex(Environment.get("CURRENTEXECUTIONDATASHEET"), strJUnitTestName, "SKIP");
		    
		    //Check if the index value is proper
		    if (iSkip == -1 ){
		    	System.out.println("Failed to find the Skip Column in the file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
		    	return null;
		    }

		    //Get the Column Index for the HEADER_IND Column
		    iHeader = fGetColumnIndex(Environment.get("CURRENTEXECUTIONDATASHEET"), strJUnitTestName, "HEADER_IND");
		    
		    //Check if the index value is proper
		    if (iHeader == -1 ){
		    	System.out.println("Failed to find the HEADER_IND Column in the file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
		    	return null;
		    }
		    
		    //Get the Column Index for the Skip Column
		    iID = fGetColumnIndex(Environment.get("CURRENTEXECUTIONDATASHEET"), strJUnitTestName, "ID");
		    
		    //Check if the index value is proper
		    if (iID == -1 ){
		    	System.out.println("Failed to find the ID Column in the file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
		    	return null;
		    }
		    
			//Create the FileInputStream obhect	
			FileInputStream file = new FileInputStream(new File(Environment.get("CURRENTEXECUTIONDATASHEET")));		     
		    //Get the workbook instance for XLS file 
		    HSSFWorkbook workbook = new HSSFWorkbook(file);
		 
		    //Get first sheet from the workbook
		    HSSFSheet sheet = workbook.getSheet(strJUnitTestName);
		    
		    //Get the rownumber
		    int iRowNum = sheet.getLastRowNum();
		    
		    //Loop through all the rows
		    for (int iRow = 0; iRow <= iRowNum; iRow++)
		    {
        		//Fetch the value for the Header Row
        		if (sheet.getRow(iRow).getCell(iHeader, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null)
        		{
        			strTemp = "";
        		}else{
        			strTemp = sheet.getRow(iRow).getCell(iHeader).getStringCellValue();
        		}
	            
	            //if StrTemp is "" then exit the Loop
	            if (strTemp.trim().toUpperCase().equals("HEADER"))
	            {
	            	//Fetch the value of the Skip indicator
	        		if (sheet.getRow(iRow).getCell(iSkip, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null)
	        		{
	        			strSkip = "";
	        		}else{
	        			strSkip = sheet.getRow(iRow).getCell(iSkip).getStringCellValue();
	        		}
	        		
	            	//Fetch the value if ID
	        		if (sheet.getRow(iRow).getCell(iID, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null)
	        		{
	        			strIndex = "";
	        		}else{
	        			strIndex = sheet.getRow(iRow).getCell(iID).getStringCellValue();
	        		}
	        		
	        		//if Skip is "" set flag value to true
	        		if (strSkip.equals(""))
	        		{
	        			bFlag = true;
	        			strTemp1 = strIndex;
	        		}else{
	        			bFlag = false;
	        		}
		            	
		            //Add the same in an array
	        		if (bPreviousFlag){
	        			strIndex = strTemp2 + "-" + strIndex;
	        			arrIndexes.add(strIndex);
	        			strTemp2 = "";
	        			bPreviousFlag = false;
	        		}
	        		
	        		//If bFlag is true then save the value of the CUrrent index
	        		if (bFlag)
	        		{
	        			bPreviousFlag = true;
	        			strTemp2 = strTemp1;
	        		}else{
	        			bPreviousFlag = false;
	        			strTemp2 = "";
	        		}
	            }
		    }
		    file.close();
		    return arrIndexes;
		     
		} catch (FileNotFoundException e) {
			System.out.println("Got Exception while executing the fGetHeaderRow function. Exception is " + e);
		    e.printStackTrace();
		    return null;
		} catch (IOException e) {
			System.out.println("Got Exception while executing the fGetHeaderRow function. Exception is " + e);
		    e.printStackTrace();
		    return null;
		}catch (Exception e) {
			System.out.println("Got Exception while executing the fGetHeaderRow function. Exception is " + e);
		    e.printStackTrace();
		    return null;
		}
	}

    //***********************************************************************
    //* Name			: fProcessDataFile
    //*	Description	    : Function to Get the Header rows in an Array List
    //*	Author		    : Anil Agarwal
    //* Input Params	: int iStartRow - The start row id
    //*	Return Values	: int
    //***********************************************************************
	public int fProcessDataFile(int iStartRow) {
		
		try {
		    int iSkip = -1;
		    int iHeader = -1;
		    int iret = -1;
		    int iTestName = -1;
	        String strTemp = "";
	        int intCounter = 1;
	        String strTemp1 = "";
	        
	        
		    //Get the Column Index for the Skip Column
		    iSkip = fGetColumnIndex(Environment.get("CURRENTEXECUTIONDATASHEET"), strJUnitTestName, "SKIP");
		    
		    //Check if the index value is proper
		    if (iSkip == -1 ){
		    	System.out.println("Failed to find the Skip Column in the file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
		    	return -1;
		    }

		    //Get the Column Index for the HEADER_IND Column
		    iHeader = fGetColumnIndex(Environment.get("CURRENTEXECUTIONDATASHEET"), strJUnitTestName, "HEADER_IND");
		    
		    //Check if the index value is proper
		    if (iHeader == -1 ){
		    	System.out.println("Failed to find the HEADER_IND Column in the file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
		    	return -1;
		    }
		    
		    //Get the Column Index for the Skip Column
		    iTestName = fGetColumnIndex(Environment.get("CURRENTEXECUTIONDATASHEET"), strJUnitTestName, "TEST_NAME");
		    
		    //Check if the index value is proper
		    if (iTestName == -1 ){
		    	System.out.println("Failed to find the TEST_NAME Column in the file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
		    	return -1;
		    }
		    
			//Create the FileInputStream obhect			
			FileInputStream file = new FileInputStream(new File(Environment.get("CURRENTEXECUTIONDATASHEET")));		     
		    //Get the workbook instance for XLS file 
		    HSSFWorkbook workbook = new HSSFWorkbook(file);
		 
		    //Get first sheet from the workbook
		    HSSFSheet sheet = workbook.getSheet(strJUnitTestName);
		    
		    //Get the Row object
		    Row row = sheet.getRow(iStartRow);
		    
		    //Check if the Row is null
		    if (row == null)
		    {
		    	System.out.println("Failed to get row " + iStartRow + " in the file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
		    	file.close();
		    	return -1;
		    }
		    
		    //Get the Column Count
		    int iColCount = row.getLastCellNum();
		    
		    //Fetch the value for the Header Index
    		if (row.getCell(iHeader, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null)
    		{
    			strTemp = "";
    		}
    		else
    		{
    			strTemp = row.getCell(iHeader).getStringCellValue();
    		}
    		
    		//If the value is header then Clear the Dictionary objects
    		if (strTemp.equals("HEADER"))
    		{
                //Clear hash maps
                Temp.clear();
                Dictionary.clear();
                objGlobalDictOriginal.clear();
    		}		    
		    
	        //Loop through all the columns
	        for (int iCell = 0; iCell < iColCount; iCell++)
	        {
	        	
            	//Fetch the value from the Calendar
        		if (row.getCell(iCell, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null)
        		{
        			strTemp1 = "";
        		}
        		else
        		{
        			strTemp1 = row.getCell(iCell).getStringCellValue();
        		}	        
	        		
        		//If the Header Row is not Blank then 
        		if (strTemp.equals("HEADER"))
        		{
                	//Add the Keys in the Temp Dictionary
            		Temp.put(intCounter,strTemp1);
            		iret = 0;
            		intCounter++;
        		}
        		else
        		{
	        		//If it is header column then assign strTemp1 to Header
	        		if (iCell == iHeader)
	        		{
	        			strTemp1 = "HEADER";
	        		}else if (iCell == iSkip)
	        		{
	        			Skip = strTemp1;
	        		}else if (iCell == iTestName)
	        		{
	        			strTestName = strTemp1;
	        		}
	        		
	        		//Add the Info in GlobalDictionary
                	if (Temp.containsKey((intCounter))){
                		//DOnt add the Skip column
                		if (iCell != iSkip){
                    		Dictionary.put(Temp.get(intCounter), strTemp1);
                    		//Put the same in objGlobalDictOriginal hashmap for use during get and set reference data
                    		objGlobalDictOriginal.put(Temp.get(intCounter), strTemp1);
                		}
                	}
                	intCounter ++;
                    iret = 1;
        		}
	        }
		    file.close();

		    return iret;
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		    return -1;
		} catch (IOException e) {
		    e.printStackTrace();
		    return -1;
		}catch (Exception e) {
		    e.printStackTrace();
		    return -1;
		}
	}

	//*****************************************************************************************
    //*	Name		    : fClearSkip
    //*	Description	    : Clears the SKIP column in the data table
    //*	Author		    : Anil Agarwal
    //* Input Params	: sActionValue - The action to clear the skip field (A - Clear All, F - Clear Failed, 
    //*									No Run, and X, S - Clear Skipped and X, ABS - Clear all but Skipped) 
    //*	Return Values	: None
    //*****************************************************************************************  
	public void fClearSkip(String strActionValue) {
		// TODO Auto-generated method stub
		String [] arrTemp = null; 
	    int iSkip = -1;
	    
		
		try {
			
			if(strActionValue.equals("A"))
			{
				//Clear any Text present in the skip column
			}
		
			else if(strActionValue.equals("F"))
			{
				arrTemp = new String []{"F","f","N","n","X","x"};
				 
			}
		
			else if(strActionValue.equals("S"))
			{
				arrTemp = new String [] {"S","s","X","x"};
			}
		
			else if(strActionValue.equals("ABS"))
			{
				arrTemp = new String [] {"S","s"};
			}
			else if(strActionValue.equals(""))
			{
				System.out.println("Update SKIP Column in Data Table - Blank Action is not valid, the valid actions to be performed are A, F, S, or ABS");
				return;
			}else 
			{
				System.out.println("Update SKIP Column in Data Table - The Action: " + strActionValue + " is not valid, the valid actions to be performed are A, F, S, or ABS");
				return;
			}

		    //Get the Column Index for the Skip Column
		    iSkip = fGetColumnIndex(Environment.get("CURRENTEXECUTIONDATASHEET"), strJUnitTestName, "SKIP");
		    
		    //Check if the index value is proper
		    if (iSkip == -1 ){
		    	System.out.println("Failed to find the Skip Column in the file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
		    	return;
		    }
		    
		    
			//Create the FileInputStream obhect			
			FileInputStream file = new FileInputStream(new File(Environment.get("CURRENTEXECUTIONDATASHEET")));		     
		    //Get the workbook instance for XLS file 
		    HSSFWorkbook workbook = new HSSFWorkbook(file);
		 
		    //Get first sheet from the workbook
		    HSSFSheet sheet = workbook.getSheet(strJUnitTestName);
		    
		    //Get the RowNum
		    int iRowNum = sheet.getLastRowNum();
		    
		    String strTemp = "";
		    
		    for (int iRow = 1; iRow <= iRowNum; iRow ++)
		    {
		    	strTemp = "";
        		//Check if the String is empty or equal to null
        		if (sheet.getRow(iRow).getCell(iSkip, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null)
        		{
        			strTemp = "";
        		}else{
        			strTemp = sheet.getRow(iRow).getCell(iSkip).getStringCellValue();
        		}
        		
        		//If the Action is A then
        		if (strActionValue.equals("A"))
        		{
        			//Set the value as blank in the Skip column
        			sheet.getRow(iRow).getCell(iSkip).setCellValue("");
        			
        		}else if (strActionValue.equals("ABS"))
        		{
        			//Set the value as blank in the Skip column
        			if (!strTemp.equalsIgnoreCase(("S")))
        			{
        				sheet.getRow(iRow).getCell(iSkip).setCellValue("");
        			}
        			
        		}else
        		{
        			//Loop through the array of keys and replace them by null
        			for (int x = 0; x < arrTemp.length; x++)
        			{
        				//If the strTemp is equal to elements in the array the replace them by ""
        				if (strTemp.equals(arrTemp[x])){
		        			//Set the value as blank in the Skip column
		        			sheet.getRow(iRow).getCell(iSkip).setCellValue("");
		        			//Exit from the loop
		        			break;
        				}
        			}
        		}
		    }
		    file.close();
		    
		    //Save the Changes made 
		    FileOutputStream outFile =new FileOutputStream(new File(Environment.get("CURRENTEXECUTIONDATASHEET")));
		    workbook.write(outFile);
		    outFile.close();
		    return;
		     
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		    return;
		} catch (IOException e) {
		    e.printStackTrace();
		    return;
		}catch (Exception e) {
		    e.printStackTrace();
		    return;
		}
	}

	  //*****************************************************************************************
	   //*	Name		    : fGetReferenceData
	   //*	Description	    : Fetch the data from keep refer sheet
	   //*	Author		    : Anil Agarwal
	   //*	Input Params	: None
	   //*	Return Values	: Boolean 
	   //*****************************************************************************************	
		public boolean fGetReferenceData()
		{
			//Declare few variables
			String key, value;
			Map.Entry me;
			
			//Get a set of the entries 
			Set set = Dictionary.entrySet(); 
	   	
		   	//Get an iterator 
		   	Iterator i = set.iterator(); 
		   	try
	   		{

			    int iRow = 0;
			    String strKeyName = "";
			    String strKeyValue = "";
			    boolean bFoundFlag = false;
			    
			    
		        //Set KEY_VALUE column as per Test Name
		        String colName = strJUnitTestName + "_KEY_VALUE";
		        
		        //Call the function to get the Column Index in the KEEP_REFER sheet
		        int iColIndex = fGetColumnIndex(Environment.get("CURRENTEXECUTIONDATASHEET"), "KEEP_REFER", colName);
		        
		        //Validate if we get the index
		        if (iColIndex == -1){
		        	System.out.println("Failed to find the " + colName + " Column in the KEEP_REFER sheet in file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
			    	return false;
		        }
		        
				//Create the FileInputStream obhect			
				FileInputStream file = new FileInputStream(new File(Environment.get("CURRENTEXECUTIONDATASHEET")));		     
			    //Get the workbook instance for XLS file 
			    HSSFWorkbook workbook = new HSSFWorkbook(file);
			 
			    //Get first sheet from the workbook
			    HSSFSheet sheet = workbook.getSheet("KEEP_REFER");
			    
			    //Get the RowNum
			    int iRowNum = sheet.getLastRowNum();

			    
			    //Looping through the iterator
			   	while(i.hasNext()) 
			   	{ 	
		   		
		    		me = (Map.Entry)i.next();
		    		key = me.getKey().toString();
		    		value = me.getValue().toString();
		    		
		    		//If we need to get data from KEEP refer sheet
		    		if (value.startsWith("&",0))
		    		{
		    			value = value.substring(1);
		    			
		    			
		    			//Loop thorugh all the rows in the KEEP_REFER sheet
		    		    for (iRow = 0; iRow <= iRowNum; iRow++)
		    		    {
		    		    	strKeyName = "";
		    		    	strKeyValue = "";
		    		    	bFoundFlag = false;
		    		    	
    		        		//Check if the key is present in the Keep_Refer sheet
    		        		try
    		        		{
	    		        		if (sheet.getRow(iRow).getCell(0, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null)
	    		        		{
	    		        			strKeyName = "";
	    		        		}else{
	    		        			strKeyName = sheet.getRow(iRow).getCell(0).getStringCellValue();
	    		        		}
    		        		}catch (NullPointerException e){
    		        			strKeyName = "";
    		        		}
    		        		
    		        		//Check if the key matches the expected key
    		        		if (strKeyName.equals(value))
    		        		{
    		        			//Set the boolean value to true
    		        			bFoundFlag = true;
    		        			
    		        			//Fetch the corresponding value
	    		        		if (sheet.getRow(iRow).getCell(iColIndex, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null)
	    		        		{
	    		        			strKeyValue = "";
	    		        		}else{
	    		        			strKeyValue = sheet.getRow(iRow).getCell(iColIndex).getStringCellValue();
	    		        		}

    		        			break;
    		        		}
		    		    }
		    		    
		    		    //If the Key is not found then check the same in environment hashmap
		    		    if (bFoundFlag){
		    		    	System.out.println("Key " + value + " found in KEEP_REFER and value is " + strKeyValue);
		    		    }else{
		    		    	//Fetch the value from the Environment Hashmap
		    		    	System.out.println("Key " + value + " not found in KEEP_REFER sheet. Checking in the Enviornment Hashmap");
		    		    	strKeyValue = Environment.get(value);
		    		    	System.out.println("Key " + value + " found in Environment hashmap and value is " + strKeyValue);
		    		    }
	    			
		    	        //Check if Key value is not null
		    		    if (strKeyValue == null)
		    		    {
		    		    	System.out.println("Value for Key " + value + " is null and not found in the KEEP_REFER sheet and in the Environment hashmap");
		    		    	return false;
		    		    }
		    		    //Assign the value to Dictionary key
		    	        me.setValue(strKeyValue);
			    	        
		    	        
		    		}else if (value.startsWith("@",0))
		    		{
		    	        me.setValue(value); 			
		    		}
		   		}
			   	
			   	//Close the file
			   	file.close();
			   	return true;
	   		}
	   		catch (Exception err)
	   		{
	        	System.out.print("Exception " + err + " occured in fGetReferenceData");
	        	err.printStackTrace();
	        	return false;
	   		}
	   }	
		
	   //*****************************************************************************************
	   //*	Name		    : fSetReferenceData
	   //*	Description	    : Set the data in keep refer sheet
	   //*	Author		    : Anil Agarwal
	   //*	Input Params	: None
	   //*	Return Values	: Boolean 
	   //*****************************************************************************************	
		public boolean fSetReferenceData()
		{
			
			//Declare few variables
			String key, value, tempKey, tempValue;
		   	Map.Entry me;
		   	int Field_Count;
	   	
			//Get a set of the entries 
		   	Set set = objGlobalDictOriginal.entrySet();
		   	
		   	//Get an iterator 
		   	Iterator i = set.iterator(); 
		   	
		   	try
	   		{	 
			    int iRow = 0;
			    String strKeyName = "";
			    boolean bFoundFlag = false;
			    
		        //Set KEY_VALUE column as per Test Name
		        String colName = strJUnitTestName + "_KEY_VALUE";
		        
		        //Call the function to get the Column Index in the KEEP_REFER sheet
		        int iColIndex = fGetColumnIndex(Environment.get("CURRENTEXECUTIONDATASHEET"), "KEEP_REFER", colName);
		        
		        //Validate if we get the index
		        if (iColIndex == -1){
		        	System.out.println("Failed to find the " + colName + " Column in the KEEP_REFER sheet in file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
			    	return false;
		        }
		        
				//Create the FileInputStream obhect			
				FileInputStream file = new FileInputStream(new File(Environment.get("CURRENTEXECUTIONDATASHEET")));		     
			    //Get the workbook instance for XLS file 
			    HSSFWorkbook workbook = new HSSFWorkbook(file);
			 
			    //Get first sheet from the workbook
			    HSSFSheet sheet = workbook.getSheet("KEEP_REFER");
			    
			    //Get the RowNum
			    int iRowNum = sheet.getLastRowNum();
			    
		        
			   	//Looping through the iterator
			   	while(i.hasNext()) 
			   	{ 	
		   		
		    		me = (Map.Entry)i.next();
		    		key = me.getKey().toString();
		    		value = me.getValue().toString();
		    		
		    		//If we need to get data from KEEP refer sheet
		    		if (value.startsWith("@",0))
		    		{
		    			tempKey = value.substring(1);
		    			
		    			//if Dictionary item has been changed from the objGlobalDictOriginal item
		    			if (!(Dictionary.get(key)).equalsIgnoreCase(objGlobalDictOriginal.get(key).substring(1)))
		    			{
		    				tempValue = Dictionary.get(key);
		    			}
		    			else
		    			{
		    				tempValue ="";
		    			}
		    		
		    			//Loop thorugh all the rows in the KEEP_REFER sheet
		    		    for (iRow = 0; iRow <= iRowNum; iRow++)
		    		    {
		    		    	strKeyName = "";
		    		    	bFoundFlag = false;
    		        		try
    		        		{
	    		        		//Check if the key is present in the Keep_Refer sheet
	    		        		if (sheet.getRow(iRow).getCell(0, org.apache.poi.ss.usermodel.Row.CREATE_NULL_AS_BLANK) == null)
	    		        		{
	    		        			strKeyName = "";
	    		        		}else
	    		        		{
	    		        			strKeyName = sheet.getRow(iRow).getCell(0).getStringCellValue();
	    		        		}
    		        		}catch (NullPointerException e)
    		        		{
    		        			strKeyName = "";
    		        		}
    		        		
    		        		//Check if the key matches the expected key
    		        		if (strKeyName.equals(tempKey))
    		        		{
    		        			//Set the boolean value to true
    		        			bFoundFlag = true;
    		        			break;
    		        		}
		    		        	
		    		    	//if the bFoundFlag is true then exit the For loop 
		    		        if (bFoundFlag)
		    		        {
		    		        	//System.out.println("Key " + tempKey + " found in KEEP_REFER and value is " + strKeyValue);
		    		        	//Exit the for Loop for the rows
		    		        	break;
		    		        }
		    		    }
		    		    
		    		    //If the Key is present then update the value else add the key value in KEEP_REFER
		    		    if (bFoundFlag){
		    		    	//Update the key value in KEEP_REFER
	    		    		sheet.getRow(iRow).createCell(iColIndex);
	    		    		sheet.getRow(iRow).getCell(iColIndex).setCellValue(tempValue);
		    		    }else{
		    		    	//Set value for iRowNum
		    		    	iRowNum = iRowNum + 1;
		    		    	sheet.createRow(iRowNum);
		    		    	sheet.getRow(iRowNum).createCell(0);
		    		    	sheet.getRow(iRowNum).createCell(iColIndex);
		    		    	//Add the key and the value in KEEP_REFER sheet
		    		    	sheet.getRow(iRowNum).getCell(0).setCellValue(tempKey);
		    		    	sheet.getRow(iRowNum).getCell(iColIndex).setCellValue(tempValue);
		    		    }
		    		}
		   		}
			   	

			   	file.close();
			   	
			   	//Save the file
			    //Save the Changes made 
			    FileOutputStream outFile =new FileOutputStream(new File(Environment.get("CURRENTEXECUTIONDATASHEET")));
			    workbook.write(outFile);
			    outFile.close();
		    			
			   	return true;
		
	   		}catch (Exception err)
	   		{
	   			System.out.print("Exception " + err + " occured in fSetReferenceData");
	   			err.printStackTrace();
	   			return false;
	   		}
			
	   }
	
		//*****************************************************************************************
	    //*	Name		    : fUpdateTestCaseRowSkip
	    //*	Description	    : Function to execute an query
	    //*	Author		    : Anil Agarwal
	    //* Input Params	: int row - Row number to skip
	    //*	Return Values	: None
	    //***********************************************************************
		public void fUpdateTestCaseRowSkip(int iRowNum) {
			// TODO Auto-generated method stub
			
			try {
			    int iSkip = -1;
			    
			    //Get the Column Index for the Skip Column
			    iSkip = fGetColumnIndex(Environment.get("CURRENTEXECUTIONDATASHEET"), strJUnitTestName, "SKIP");
			    
			    //Check if the index value is proper
			    if (iSkip == -1 ){
			    	System.out.println("Failed to find the Skip Column in the file " + Environment.get("CURRENTEXECUTIONDATASHEET"));
			    	return;
			    }
			    
			    
				//Create the FileInputStream obhect			
				FileInputStream file = new FileInputStream(new File(Environment.get("CURRENTEXECUTIONDATASHEET")));		     
			    //Get the workbook instance for XLS file 
			    HSSFWorkbook workbook = new HSSFWorkbook(file);
			 
			    //Get first sheet from the workbook
			    HSSFSheet sheet = workbook.getSheet(strJUnitTestName);
			    
			    //Create the Row object
			    Row row = sheet.getRow(iRowNum);
			    
			    //Check if row is not null
			    if (row == null){
			    	System.out.println("Failed to get a row with id " + iRowNum + " in the file" );
			    	return;
			    }

			    //Set the Skip value in the row
	        	row.getCell(iSkip).setCellValue("X");
			    file.close();

			    
			    //Save the Changes made 
			    FileOutputStream outFile =new FileOutputStream(new File(Environment.get("CURRENTEXECUTIONDATASHEET")));
			    workbook.write(outFile);
			    outFile.close();
			    return;
			     
			} catch (FileNotFoundException e) {
				System.out.println("Got exception while updating the Test Case Skip for row " + iRowNum + " Exception is " + e);
			    e.printStackTrace();
			    return;
			} catch (IOException e) {
				System.out.println("Got exception while updating the Test Case Skip for row " + iRowNum + " Exception is " + e);
			    e.printStackTrace();
			    return;
			}catch (Exception e) {
				System.out.println("Got exception while updating the Test Case Skip for row " + iRowNum + " Exception is " + e);
			    e.printStackTrace();
			    return;
			}
				
		}
		
		//*****************************************************************************************
	    //*	Name		    : fGetColumnIndex
	    //*	Description	    : Function to get the Column Index
	    //*	Author		    : Anil Agarwal
	    //* Input Params	: int row - Row number to skip
	    //*	Return Values	: None
	    //***********************************************************************
		public int fGetColumnIndex (String strXLS, String strSheetName, String strColumnName)
		{
			try
			{
				//Create the FileInputStream obhect			
				FileInputStream file = new FileInputStream(new File(strXLS));		     
			    //Get the workbook instance for XLS file 
			    HSSFWorkbook workbook = new HSSFWorkbook(file);
			 
			    //Get first sheet from the workbook
			    HSSFSheet sheet = workbook.getSheet(strSheetName);
			     
			    //Iterate through each rows from first sheet
			    Row row = sheet.getRow(0);
			    
			    //Get the Column count
			    int iColCount = row.getLastCellNum();
			    int iCell = 0;
			    int iIndex = -1;
		        String strTemp = "";

		        //Loop through all the columns
		        for (iCell = 0; iCell < iColCount; iCell ++)
		        {
	        		//Get the index for Version and Enviornment
	        		strTemp = sheet.getRow(0).getCell(iCell).getStringCellValue().trim().toUpperCase();
	        		
	        		//if the strColumnName contains Header then check for HEADER or HEADER_IND
	        		if (strColumnName.equals("HEADER_IND") || strColumnName.equals("HEADER"))
	        		{
	        			if (strTemp.equals("HEADER") || strTemp.equals("HEADER_IND"))
	        			{
	        				iIndex = iCell;
	        				//Exit the Loop
	        				break;
	        			}
	        			
	        		}else{ 
	        			if (strTemp.equals(strColumnName.trim().toUpperCase()))
	        			{
	        				iIndex = iCell;
	        				//Exit the Loop
	        				break;
	        			}
	        		}
		        }
		        //Close the file
			    file.close();
			    
			    //Validate if index is returned properly or not
			    if (iIndex != -1)
			    {
			    	//Print the Column Index
//			    	System.out.println("Column Id for Column " + strColumnName + " is " + iIndex);
			    	return iIndex;
			    	
			    }else{
			    	System.out.println("Failed to find the Column Id for Column " + strColumnName);
			    	return -1;
			    	
			    }
				
			}catch (Exception e){
				System.out.println("Got exception while finding the Index column. Exception is " + e);
				return -1;
			}
		}

		 
}
	

