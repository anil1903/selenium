package ASAP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.omg.CORBA.PRIVATE_MEMBER;
import org.openqa.selenium.WebDriver;




import com.mercury.qualitycenter.otaclient.ClassFactory;
import com.mercury.qualitycenter.otaclient.IAttachment;
import com.mercury.qualitycenter.otaclient.IAttachmentFactory;
import com.mercury.qualitycenter.otaclient.IBaseFactory;
import com.mercury.qualitycenter.otaclient.IList;
import com.mercury.qualitycenter.otaclient.IRun;
import com.mercury.qualitycenter.otaclient.IRunFactory;
import com.mercury.qualitycenter.otaclient.IStep;
import com.mercury.qualitycenter.otaclient.ISysTreeNode;
import com.mercury.qualitycenter.otaclient.ITSTest;
import com.mercury.qualitycenter.otaclient.ITest;
import com.mercury.qualitycenter.otaclient.ITestFactory;
import com.mercury.qualitycenter.otaclient.ITestSet;
import com.mercury.qualitycenter.otaclient.ITestSetFactory;
import com.mercury.qualitycenter.otaclient.ITestSetTreeManager;
import com.mercury.qualitycenter.otaclient.ITreeManager;
import com.mercury.qualitycenter.otaclient.ITDConnection;
//import com.sun.jmx.snmp.InetAddressAcl;



import com4j.Com4jObject;

public class QualityCenter {
	
	//*************************************************************
	/** List of Methods in this class **/
	//** boolean fAddTest(ITDConnection objTD, HashMap<String, String> Dictionary, HashMap<String, String> Environment,String strTestName)
	//** boolean fQCStepUpdate(ITDConnection objTD, HashMap<String, String> Dictionary, String strStepName, String strStepDesc, String strExpValue, String strActValue, String strResult)
	//** boolean fGuiIsEnabled (String strDesc)
	//** boolean fGuiIsDisabled (String strDesc)
	//** boolean fGuiClick (String strDesc)
	//** boolean fGuiEnterText (String strDesc, String strText)
	//** boolean fValidatePageDisplayed (String strExpectedTitle)
	//** boolean fGuiSelectOptionFromList (String strDesc, String strText)
	//** getObject
	/** List of Methods in this class **/
	//*************************************************************
	

    //*****************************************************************************************
    //*	Name		    : fAddTest
    //*	Description	    : Function to Add a QTP Test to Test Set
    //*	Author		    : Aniket Gadre
    //*	Input Params	: string strTestName - Name of the test to be Added
    //*	Return Values	: Bool True on Success / False on failure
    //*****************************************************************************************
    public boolean fAddTest(ITDConnection objTD, HashMap<String, String> Dictionary, HashMap<String, String> Environment,String strTestName)
    {

        //Check whether Node exist
        try
        {
            //Declare variables
        	
        	ITest test, test1;
        
        	ITreeManager objTM =  (objTD.treeManager()).queryInterface(ITreeManager.class);

    		ITestFactory objTF = (objTD.testFactory()).queryInterface(ITestFactory.class);
            //Get the test plan node
    		ISysTreeNode MySRoot = (objTM.nodeByPath(Environment.get("TESTPLANPATH"))).queryInterface(ISysTreeNode.class);
       		//String strTestFactoryFilter = "select TS_TEST_ID from TEST where TS_NAME = '" + Global.Dictionary.get("TEST_NAME") + "' and TS_SUBJECT = " + MySRoot.nodeID();
       		String strTestFactoryFilter = "select TS_TEST_ID from TEST where TS_NAME = '" + strTestName + "' and TS_SUBJECT = " + MySRoot.nodeID();
          
       		//Fetch tests from Test factory corresponding to above defined filter	
       		String TestID;
    		IList objTestList = objTF.newList(strTestFactoryFilter).queryInterface(IList.class);
    		
    		//if the TC is not found in Test Plan then we create a TC under Automation Coverage folder for Reporting purpose
    		if (objTestList.count()==0)
    		{
    			System.out.println("Manual TC " + strTestName + " is not present at the location " + Environment.get("TESTPLANPATH"));
    			//Add the dummy TC in the AUtomation Coverage folder if it is not present
    			ISysTreeNode MySRoot1 = (objTM.nodeByPath(Environment.get("AUTO_TESTPLANPATH"))).queryInterface(ISysTreeNode.class);
    			//Set all Test Attributes and Post the test to DB
                Object[] arrTestDetails = { strTestName, "QUICKTEST_TEST", System.getProperty("user.name"), (long)MySRoot1.nodeID()};
                
                test = (ITest) objTF.addItem(arrTestDetails).queryInterface(ITest.class);
                test.post();
    			TestID = test.id().toString();
    		}
            else
            {
            	//test1 = (ITest) objTestList.item(1);
            	test = ((Com4jObject) objTestList.item(1)).queryInterface(ITest.class);
                TestID = test.id().toString();
            }
    		
    		Dictionary.put("TESTID", TestID);
    		
            //Declare Test Set Factory Object
            ITestSetTreeManager objTSTM = objTD.testSetTreeManager().queryInterface(ITestSetTreeManager.class);
            ITestSetFactory objTSF = objTD.testSetFactory().queryInterface(ITestSetFactory.class);

            //Get Test Set Tree Node
            ISysTreeNode objTestSetFolder = objTSTM.nodeByPath(Environment.get("TESTSETPATH")).queryInterface(ISysTreeNode.class);
            String strTestSetFactoryFilter = "Select CY_CYCLE_ID from CYCLE where CY_CYCLE = '" + Environment.get("TESTSETNAME") + "' And CY_FOLDER_ID = " + objTestSetFolder.nodeID();

            //Fetch Test Set from Test Set Factory
            IList objTestSets = objTSF.newList(strTestSetFactoryFilter).queryInterface(IList.class);
            if (objTestSets.count() == 0)
            {
                System.out.println("No Test Set by Name " + Environment.get("TESTSETNAME") + " found in folder " + Environment.get("TESTSETPATH"));
                return false;
            }
   
            //Declare Test Set Object
            Com4jObject objTSTemp = (Com4jObject)objTestSets.item(1);;
            ITestSet objTS = (ITestSet) objTSTemp.queryInterface(ITestSet.class);
            IBaseFactory objTSTF = objTS.tsTestFactory().queryInterface(IBaseFactory.class);

            //gET TEST sET id
            String TestSetID = objTS.id().toString();
            Dictionary.put("TESTSETID", TestSetID);
            	
            ITSTest objTST;

            //Check Test in TS Test Factory
            String strTSTestFactoryFilter = "Select * from TESTCYCL where TC_CYCLE_ID = " + TestSetID + " And TC_TEST_ID = " + TestID;
            IList objTestSetTests = objTSTF.newList(strTSTestFactoryFilter).queryInterface(IList.class);
            
            if (objTestSetTests.count() == 0)
            {
                objTST = objTSTF.addItem(TestID).queryInterface(ITSTest.class);
                objTST.status("Not Completed");
                objTST.post();
            }
            else
            {	
            	Com4jObject objTSTTemp = (Com4jObject)objTestSetTests.item(1);
                objTST = objTSTTemp.queryInterface(ITSTest.class);
            }

            Dictionary.put("TSTESTID", objTST.id().toString());

            //Declare Run Factory variables
            IRunFactory objRF =  objTST.runFactory().queryInterface(IRunFactory.class);
            IRun objRun = objRF.addItem(TestID).queryInterface(IRun.class);

            objRun.name(strTestName);
            objRun.status("Not Completed");
            Dictionary.put("RUNID", objRun.id().toString());
            objRun.post();

            return true;
        }

        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }

    }
    //*****************************************************************************************
    //*	Name		    : fQCStepUpdate
    //*	Description	    : Function to Add a Step to QC Test in Test Set
    //*	Author		    : Aniket Gadre
    //*	Input Params	: string strStepName - Name of the Step
    //*                 : string strStepDesc - Description of the Step
    //*                 : string strExpValue - Expected Value
    //*                 : string strActValue - Actual value
    //*                 : string strResult   - Result
    //*	Return Values	: Bool True on Success / False on failure
    //*****************************************************************************************
    public boolean fQCStepUpdate(ITDConnection objTD, HashMap<String, String> Dictionary, String strStepName, String strStepDesc, String strExpValue, String strActValue, String strResult)
    {
        
    	//Check result
    	if(strResult.toUpperCase().equals("PASS")) strResult = "Passed";
    	else if  (strResult.toUpperCase().equals("FAIL")) strResult = "Failed";
    	
    	
    	//Declare RUn factory variables
        IRunFactory objRF = objTD.runFactory().queryInterface(IRunFactory.class);
        String strRunFactoryFilter = "Select * from RUN where RN_RUN_ID = " + Dictionary.get("RUNID");
        Object[] objStepDetails = { strStepName, strResult, strStepDesc, "", "" };

        //Create New Lisr
        IList objRuns = objRF.newList(strRunFactoryFilter).queryInterface(IList.class);

        //Check Count
        if (objRuns.count() == 0)
        {
            return false;
        }
        
        Com4jObject objRunTemp = (Com4jObject) objRuns.item(1);
        IRun objRun = objRunTemp.queryInterface(IRun.class);

        //Set Step factory variables
        IBaseFactory objSF = objRun.stepFactory().queryInterface(IBaseFactory.class);
        IStep objStep = objSF.addItem(objStepDetails).queryInterface(IStep.class);
        objStep.field("ST_EXPECTED", strExpValue);
        objStep.field("ST_ACTUAL", strActValue);
        objStep.post();

        return true;
    }
      //*****************************************************************************************
    //*	Name		    : fUpdateTestStatusInQC
    //*	Description	    : Updates the status of Current RUn and Overall execution for a particular test
    //*	Author		    : Aniket Gadre
    //*	Input Params	: string strStatus - Test Status
    //*	Return Values	: Bool True on Success / False on failure
    //*****************************************************************************************
    public boolean fUpdateTestStatusInQC(ITDConnection objTD, String strStatus, HashMap<String, String> Dictionary)
    {
        //Declare RUn factory variables
        IRunFactory objRF = objTD.runFactory().queryInterface(IRunFactory.class);
        String strRunFactoryFilter = "Select * from RUN where RN_RUN_ID = " + Dictionary.get("RUNID");

        //Create New List
        IList objRuns = objRF.newList(strRunFactoryFilter).queryInterface(IList.class);

        //Check Count
        if (objRuns.count() == 0) return false;
        Com4jObject objRunTemp = (Com4jObject) objRuns.item(1);
        IRun objRun = objRunTemp.queryInterface(IRun.class);
        objRun.status(strStatus);
        objRun.post();

        return true;
    }

    //*****************************************************************************************
    //*	Name		    : fConnectToQC
    //*	Description	    : Makes a connection with QC using Specified credentials
    //*	Author		    : Aniket Gadre
    //*	Input Params	: string strStatus - Test Status
    //*	Return Values	: Bool True on Success / False on failure
    //*****************************************************************************************
    public ITDConnection fConnectToQC(String strQCServer, String strQCUser, String strQCPassword, String strQCDomain, String strQCProject)
    {
    	ITDConnection objTD = null;
    	
		try
        {
			objTD = ClassFactory.createTDConnection();
            objTD.initConnectionEx(strQCServer);
            objTD.connectProjectEx(strQCDomain,strQCProject,strQCUser,strQCPassword);

            //Check if Connection is successfull
            if (objTD.connected() != true)
            {
                System.out.println("Unable to connect to QC");
                objTD = null;
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            objTD = null;
        }
        return objTD;
    }

    //*****************************************************************************************
    //*	Name		    : fAttachResultsToRun
    //*	Description	    : Attach the HTML Reports and the Screenshots to Current RUN
    //*	Author		    : Aniket Gadre
    //*	Input Params	: None
    //*	Return Values	: Bool True on Success / False on failure
    //*****************************************************************************************
    public boolean fAttachResultsToRun(ITDConnection objTD, HashMap<String, String> Dictionary, HashMap<String, String> Environment)
    {           
        //Set the name for the Test Case Report File
        String strTestDetails = Dictionary.get("TEST_NAME") + "-" + Dictionary.get("ACTION");        
        String strZipFilePath = Environment.get("HTMLREPORTSPATH") + "\\Report_" + strTestDetails + ".zip";

        try
        {
        	//Create a zip FIle
    		File directoryToZip = new File(Environment.get("HTMLREPORTSPATH"));

    		List<File> fileList = new ArrayList<File>();
    		//System.out.println("---Getting references to all files in: " + directoryToZip.getCanonicalPath());
    		getAllFiles(directoryToZip, fileList);
    		//System.out.println("---Creating zip file");
    		writeZipFile(directoryToZip, fileList,strZipFilePath);
    		System.out.println("---Done");


            //Declare RUn factory variables
            IRunFactory objRF = objTD.runFactory().queryInterface(IRunFactory.class);
            String strRunFactoryFilter = "Select * from RUN where RN_RUN_ID = " + Dictionary.get("RUNID");

            //Create New Lisr
            IList objRuns = objRF.newList(strRunFactoryFilter).queryInterface(IList.class);

            //Check Count
            if (objRuns.count() == 0) return false;

            //Get Current Run
            Com4jObject objRunTemp = (Com4jObject) objRuns.item(1);
            IRun objRun = objRunTemp.queryInterface(IRun.class);

            //Get Attachement Factory Object object
            
            IAttachmentFactory objAF = objRun.attachments().queryInterface(IAttachmentFactory.class);
            
            Object[] arrDetails = {strZipFilePath, 1}; 
          
            IAttachment objA = (IAttachment)objAF.addItem(arrDetails).queryInterface(IAttachment.class);
            //objA.type(1);
            //objA.fileName(strZipFilePath);
            //
            objA.post();

            //Declare the variable for Test Set Factory
            ITestSetFactory objTSF = objTD.testSetFactory().queryInterface(ITestSetFactory.class);
            String strTestSetFactoryFilter = "Select * from CYCLE where CY_CYCLE_ID = " + Dictionary.get("TESTSETID");

            //Fetch Test Set from Test Set Factory
            IList objTestSets = objTSF.newList(strTestSetFactoryFilter).queryInterface(IList.class);

            //Declare Test Set Object
            Com4jObject objTSTemp = (Com4jObject)objTestSets.item(1);
            ITestSet objTS = objTSTemp.queryInterface(ITestSet.class);
            
            //ITestSet objTSTF = (ITestSet)objTS.tsTestFactory();
            IBaseFactory objTSTF = objTS.tsTestFactory().queryInterface(IBaseFactory.class);

            //Check Test in TS Test Factory
            String strTSTestFactoryFilter = "Select * from TESTCYCL where TC_CYCLE_ID = " + Dictionary.get("TESTSETID") + " And TC_TEST_ID = " + Dictionary.get("TESTID");
            IList objTestSetTests = objTSTF.newList(strTSTestFactoryFilter).queryInterface(IList.class);
            Com4jObject objTSTTemp = (Com4jObject)objTestSetTests.item(1);
            ITSTest objTST = objTSTTemp.queryInterface(ITSTest.class);
            
            //Declare a List for attachments
            IList attachList;
            
            //if there are any existing attachments delete the same so that
            //we have only the latest attachment attached to the test
        /*    if (objTST.hasAttachment())
            {
            	objAF = (objTST.attachments()).queryInterface(IAttachmentFactory.class);
            	attachList = objAF.newList("");
            	
            	//Loop through all the attachments and remove the same
                for(int i = 1; i <= attachList.count(); i++)                  
                {
                	objAF.removeItem((attachList.item(i)));
                } 
                
                objTST.post();
            	
            }*/
            
            ////Get Attachment Factory Object
            objAF = objTST.attachments().queryInterface(IAttachmentFactory.class);
            //Object[] arrDetails = {strZipFilePath, 1}; 
            objA = objAF.addItem(arrDetails).queryInterface(IAttachment.class);
            //objA.type(1);
            //objA.fileName(strZipFilePath);
 
            objA.post();

            //Delete the zip file
            new File(strZipFilePath).delete();
            return true;

        }

        catch (Exception e)
        {
            System.out.println("Attaching results in QC Failed. Error Message: " + e.getMessage());
            return false;
        }

        
    }
    
   
    public static void getAllFiles(File dir, List<File> fileList) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory()) {
					//System.out.println("directory:" + file.getCanonicalPath());
					getAllFiles(file, fileList);
				} else {
					//System.out.println("     file:" + file.getCanonicalPath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeZipFile(File directoryToZip, List<File> fileList, String ZipFileName) {

		try {
			FileOutputStream fos = new FileOutputStream(ZipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
			IOException {

		//Initialize new file system
		FileInputStream fis = new FileInputStream(file);
		
		//Get parent folder
		
		String[] arrParentFolder = directoryToZip.getAbsolutePath().split("\\\\");
		int len = arrParentFolder.length;
		String strParentFolder = arrParentFolder[len-1];

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1, file.getCanonicalPath().length());
		System.out.println("Writing '" + zipFilePath + "' to zip file");
		ZipEntry zipEntry = new ZipEntry(strParentFolder + "\\" + zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}
	

	
}
