package ASAP;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.sql.Timestamp;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class CommonFunctions {
	
	//*************************************************************
	/** List of Methods in this class **/
	
	//** boolean fGuiCheckObjectExistence(String strDesc)
	//** boolean fGuiCheckChildObjectExistence(WebElement objParent, String strDesc)
	//** boolean fGuiCheckChildObjectNonExistence(WebElement objParent, String strDesc)
	//** boolean fGuiCheckObjectNonExistence (String strDesc)
	//** boolean fGuiIsDisplayed (String strDesc)
	//** boolean fGuiIsNotDisplayed (String strDesc)
	//** boolean fGuiIsEnabled (String strDesc)
	//** boolean fGuiIsDisabled (String strDesc)
	//** boolean fGuiClick (String strDesc)
	//** boolean fGuiEnterText (String strDesc, String strText)
	//** boolean fValidatePageDisplayed (String strExpectedTitle)
	//** boolean fGuiSelectOptionFromList (String strDesc, String strText)
	//** boolean fGuiCheckObjectExistence(String strDesc)
	//** boolean fGuiCheckObjectNotExist(String strDesc)
	//** boolean fGuiCheckChildObjectExistence(WebElement Parent, String strDesc)
	//** boolean fGuiCheckChildObjectNonExistence(WebElement Parent, String strDesc)
	//** boolean fGuiCheckCheckBox(String strDesc)
	//** boolean fGuiUncheckCheckBox(String strDesc)
	//** WebElement getObject(String strDesc)
	//** WebElement getChildObject(WebElement Parent, String strDesc)
	//** boolean switchToWindowWithName(strWindowName)
	//** String returnRandomString()
	//** String returnTimeStamp()
	//** boolean fverifyCheckBoxIsChecked(String strDesc)
	//** boolean fverifyCheckBoxIsChecked(String strDesc)
	//** String fGuiGetText(String strDesc)
	//** String fGuiGetText(WebElement objParent)
	//** String fGuiAttributeValue(String strDesc)
	//** String fGuiAttributeValue(WebElement objWebElement)
	//** boolean switchiGenFrames(String frmName)
	//** boolean validateFrame(String strFrameName, String strClassName)
	//** boolean fChkBackwardNavigation(String strDesc)
	//** boolean fChkForwardNavigation(String strDesc)
	/** List of Methods in this class **/
	//*************************************************************
	
	//Properties
	private Reporting Reporter;
	private WebDriver driver;
	
	//Constructor
	public  CommonFunctions(WebDriver driverTemp, Reporting ReporterTemp)
	{
		Reporter = ReporterTemp;
		driver = driverTemp;
		
	}
	
	public void delayBy(long milliseconds) {

        try {
             Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
        	// TODO Auto-generated catch block
            e.printStackTrace();
        }
	}


	
	//*****************************************************************************************
    //*	Name		    : switchiGenFrames
    //*	Description	    : function to switch between the frames of iGen
    //*	Author		    : Aniket Gadre
    //*	Input Params	: None
    //*	Return Values	: true - OnSuccess
    //*					: false - On Failure	
    //*****************************************************************************************
	public boolean switchiGenFrames(String frmName)
	{		
		try
		{	
			//Switch
			Thread.sleep(500);
			driver.switchTo().defaultContent();
			
			//get Frame Object
			WebElement objFrame = getObject("name:=" + frmName);

			//####
			//((JavascriptExecutor)driver).executeScript("while(document.getElementById('"+ frmName + "').contentDocument.readyState !== 'complete'){}", new Object[]{});
			//###
			
			String state = (String) ((JavascriptExecutor)driver).executeScript("return document.getElementById('"+ frmName + "').contentDocument.readyState");
			while(!state.equalsIgnoreCase("complete")){
				state = (String) ((JavascriptExecutor)driver).executeScript("return document.getElementById('"+ frmName + "').contentDocument.readyState");
			}
			
			if(objFrame == null)
			{
				Reporter.fnWriteToHtmlOutput("Switch Frame", "Switch to Frame with desc " + frmName, "Switching frame failed", "Fail");
				return false;
			}
			
			
			
			Thread.sleep(500);
			driver.switchTo().frame(frmName);
		}
		catch (Exception e)
		{
			Reporter.fnWriteToHtmlOutput("Switch Frame", "Switch to Frame with desc " + frmName, "Switching frame failed. Exception occured: " + e, "Fail");
			return false;
		}
		
		
		//return 
		Reporter.fnWriteToHtmlOutput("Switch Frame", "Switch to Frame with desc " + frmName, "Switching frame Successful", "Done");
		return true;
	}
	
	//*****************************************************************************************
    //*	Name		    : validateFrame
    //*	Description	    : Checks whether required frame is opened
    //*	Author		    : Aniket Gadre
    //*	Input Params	: None
    //*	Return Values	: true - OnSuccess
    //*					: false - On Failure	
    //*****************************************************************************************
	public boolean validateFrame(String strFrameName, String strClassName)	
	{
		//if(switchiGenFrames("WORK") == false) return false;
		if(switchiGenFrames("WORK_SPACE") == false) return false;
		
		//get Frame Object
		WebElement objFrame = getObject("xpath:=//td[@class='" + strClassName + "' and contains(text(),'" + strFrameName + "')]");
		
		//Check if 
		if(objFrame == null)
		{
			Reporter.fnWriteToHtmlOutput("Check if page opened with Frame " + strFrameName, "Navigation should be successfull", "Navigation failed", "Fail");
			return false;
		}
		
		
		//return 
		Reporter.fnWriteToHtmlOutput("Check if page opened with Frame " + strFrameName, "Navigation should be successfull", "Navigation Successful", "Pass");
		return true;
	}

	
	 //*****************************************************************************************
    //*	Name		    : fGuiCheckObjectExistence
    //*	Description	    : Check if the object exists
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: strDesc(The property of the object)
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
	public boolean fGuiCheckObjectExistence(String strDesc){
        try
        {
        	//WebElement Collection
        	List<WebElement> lst = getObjects(strDesc);

        	//Check the list size
        	if(lst.size() > 0)
        	{
        		Reporter.fnWriteToHtmlOutput("Check Existence of object " + strDesc, "Object Should exist", "Object Exist", "Pass");
        		return true;
        	}
        	else
        	{
        		Reporter.fnWriteToHtmlOutput("Check Existence of object " + strDesc, "Object Should exist", "Object does not exist", "Fail");
        		return false;
        	}
        	
        }
        //Catch Block
        catch(Exception e)
        {
        	Reporter.fnWriteToHtmlOutput("Check Existence of object " + strDesc, "Object should exist", "Exception occured while checking existence", "Fail");
        	System.out.println("Exception " + e.toString() + " occured while checking object existence");
        	return false;
        }        	        	         	   	
    }
	
	//*****************************************************************************************
    //*	Name		    : fGuiCheckChildObjectExistence
    //*	Description	    : Check if the object exists
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: objParent (Parent WebElement)
	//*					: strDesc(The property of the object)
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
	public boolean fGuiCheckChildObjectExistence(WebElement objParent, String strDesc){
        try
        {
        	//WebElement Collection
        	List<WebElement> lst = getObjects(strDesc);
        	
        	if(lst.size() > 0)
        	{
        		//Reporter.fnWriteToHtmlOutput("Check Existence of child object " + strDesc, "Object Should exist", "Object Exist", "Pass");
        		return true;
        	}
        	else
        	{
        		//Reporter.fnWriteToHtmlOutput("Check Existence of child object " + strDesc, "Object Should exist", "Object does not exist", "Fail");
        		return false;
        	}
        	
        }
        
        //Catch Block
        catch(Exception e)
        {
        	Reporter.fnWriteToHtmlOutput("Check Existence of child object " + strDesc, "Object should exist", "Exception occured while checking existence", "Fail");
        	System.out.println("Exception " + e.toString() + " occured while checking object existence");
        	return false;
        }  
    }
	
	//*****************************************************************************************
    //*	Name		    : fGuiCheckChildObjectNonExistence
    //*	Description	    : Check if the object exists
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: objParent (Parent WebElement)
	//*					: strDesc(The property of the object)
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
	public boolean fGuiCheckChildObjectNonExistence(WebElement objParent, String strDesc){
        try
        {
        	//WebElement Collection
        	List<WebElement> lst = getObjects(strDesc);
        	
        	if(lst.size() == 0)
        	{
        		Reporter.fnWriteToHtmlOutput("Check Non Existence of child object " + strDesc, "Object Should not exist", "Object does not exist", "Pass");
        		return true;
        	}
        	else
        	{
        		Reporter.fnWriteToHtmlOutput("Check Non Existence of child object " + strDesc, "Object Should not exist", "Object exist", "Fail");
        		return false;
        	}
        	
        }
        
        //Catch Block
        catch(Exception e)
        {
        	Reporter.fnWriteToHtmlOutput("Check Non Existence of child object " + strDesc, "Object should not exist", "Exception occured while checking existence", "Fail");
        	System.out.println("Exception " + e.toString() + " occured while checking object non existence");
        	return false;
        }      	
    }
	
	//*****************************************************************************************
    //*	Name		    : fGuiCheckObjectNonExistence
    //*	Description	    : Check if the object does not exists
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: strDesc(The property of the object)
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
	public boolean fGuiCheckObjectNonExistence(String strDesc){
        try
        {
        	//WebElement Collection
        	List<WebElement> lst = getObjects(strDesc);
        	if(lst.size() == 0)
        	{
        		Reporter.fnWriteToHtmlOutput("Check Non Existence of object " + strDesc, "Object Should not exist", "Object does not exist", "Pass");
        		return true;
        	}
        	else
        	{
        		Reporter.fnWriteToHtmlOutput("Check Non Existence of object " + strDesc, "Object Should not exist", "Object exist", "Fail");
        		return false;
        	}
        	
        }
        
        //Catch Block
        catch(Exception e)
        {
        	Reporter.fnWriteToHtmlOutput("Check Non Existence of object " + strDesc, "Object should not exist", "Exception occured while checking existence", "Fail");
        	System.out.println("Exception " + e.toString() + " occured while checking object non existence");
        	return false;
        }     	
    }	
	
	//*****************************************************************************************
    //*	Name		    : fGuiIsDisplayed
    //*	Description	    : Check if the object is displayed or not as per the choice
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: strDesc(The property of the object)
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
	public boolean fGuiIsDisplayed(String strDesc){
    	try{
    		
    		//Get WebElement
    		WebElement webElement = getObject(strDesc);		
    		
    		//Check if the WebElement is displayed    		
    		boolean bIsDisplayed = false;	        
	        
	        int intCount = 1;    
	        
	        //Loop for around 10 secs to check whether object is being displayed
	        while (!(bIsDisplayed) && (intCount <=10))
	        {
	        	try {
	        			bIsDisplayed = webElement.isDisplayed();
	        		
        	    }
	        	catch (Exception e){
	        		Reporter.fnWriteToHtmlOutput("Check if element with description  " + strDesc + " is displayed", "Exception occurred","Exception :" + e, "Fail");
	        		e.printStackTrace();
	    			return false;	    	    	
	    	    }	 
	        	
	        	
	        	//Sleep for a sec
	        	Thread.sleep(1000);
				intCount++;			
	        }
	
	        //Validate if the element should be displayed or not	        
        	if(bIsDisplayed){
        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is displayed" , "Object should be displayed", "Object is Displayed", "Pass");
	            return true;
        	}
        	else {
        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is displayed" , "Object should be displayed", "Object is not displayed", "Fail");
	            return false;
        	}       	
	       	        
    	}catch(Exception e){
    		Reporter.fnWriteToHtmlOutput("Check if element with description  " + strDesc + " is displayed", "Exception occurred","Exception :" + e, "Fail");
    		e.printStackTrace();
			return false;		
    	}    	
    }
	
	//*****************************************************************************************
    //*	Name		    : fGuiIsDisplayed
    //*	Description	    : Check if the object is displayed or not as per the choice
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: Webelement
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
	public boolean fGuiIsDisplayed(WebElement webElement){
		
		//Check if the WebElement is displayed    		
		boolean bIsDisplayed = false;	  
		String strDesc = webElement.toString();
        
        int intCount = 1;    
	        
	    try{
	        
	        //Loop for around 10 secs to check whether object is being displayed
	        while (!(bIsDisplayed) && (intCount <=10))
	        {
	        	try {
	        			bIsDisplayed = webElement.isDisplayed();
	        		
        	    }
	        	catch (Exception e){
	        		Reporter.fnWriteToHtmlOutput("Check if element with description  " + strDesc + " is displayed", "Exception occurred","Exception :" + e, "Fail");
	        		e.printStackTrace();
	    			return false;	    	    	
	    	    }	 
	        	
	        	
	        	//Sleep for a sec
	        	Thread.sleep(1000);
				intCount++;			
	        }
	
	        //Validate if the element should be displayed or not	        
        	if(bIsDisplayed){
        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is displayed" , "Object should be displayed", "Object is Displayed", "Pass");
	            return true;
        	}
        	else {
        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is displayed" , "Object should be displayed", "Object is not displayed", "Fail");
	            return false;
        	}       	
	       	        
    	}catch(Exception e){
    		Reporter.fnWriteToHtmlOutput("Check if element with description  " + strDesc + " is displayed", "Exception occurred","Exception :" + e, "Fail");
    		e.printStackTrace();
			return false;		
    	}    	
    }
	
	
	//*****************************************************************************************
    //*	Name		    : fGuiIsNotDisplayed
    //*	Description	    : Check if the object is displayed or not as per the choice
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: strDesc(The property of the object), choiceYesOrNo(displayed->Yes; notDisplayed->No)
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
	public boolean fGuiIsNotDisplayed(String strDesc){
    	try{
    		
    		//Get WebElement
    		WebElement webElement = getObject(strDesc);		
    		
    		//Check if the WebElement is displayed    		
    		boolean bNotIsDisplayed = false;	        
	        
	        int intCount = 1;    
	        
	        //Loop for around 10 secs to check whether object is being displayed
	        while (!(bNotIsDisplayed) && (intCount <=10))
	        {
	        	try {
	        		bNotIsDisplayed = !(webElement.isDisplayed());
	        		
        	    }
	        	catch (Exception e){
	        		Reporter.fnWriteToHtmlOutput("Check if element with description  " + strDesc + " is not displayed", "Exception occurred","Exception :" + e, "Fail");
	        		e.printStackTrace();
	    			return false;	    	    	
	    	    }	 
	        	
	        	
	        	//Sleep for a sec
	        	Thread.sleep(1000);
				intCount++;			
	        }
	
	        //Validate if the element should be displayed or not	        
        	if(bNotIsDisplayed){
        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is not displayed" , "Object should not be displayed", "Object is not displayed", "Pass");
	            return true;
        	}
        	else {
        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is not displayed" , "Object should not be displayed", "Object is Displayed", "Fail");
	            return false;
        	}       	
	       	        
    	}catch(Exception e){
    		Reporter.fnWriteToHtmlOutput("Check if element with description  " + strDesc + " is displayed", "Exception occurred","Exception :" + e, "Fail");
    		e.printStackTrace();
			return false;		
    	}    	
    }
	
	//*****************************************************************************************
    //*	Name		    : fGuiIsNotDisplayed
    //*	Description	    : Check if the object is displayed or not as per the choice
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: WebElement
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
	public boolean fGuiIsNotDisplayed(WebElement webElement){
		
		//Check if the WebElement is displayed    		
		boolean bNotIsDisplayed = false;	
		String strDesc = webElement.toString();
        
		//Counter
        int intCount = 1;  
        
        
        try{      
	        //Loop for around 10 secs to check whether object is being displayed
	        while (!(bNotIsDisplayed) && (intCount <=10))
	        {
	        	try {
	        		bNotIsDisplayed = !(webElement.isDisplayed());
	        		
        	    }
	        	catch (Exception e){
	        		Reporter.fnWriteToHtmlOutput("Check if element with description  " + strDesc + " is not displayed", "Exception occurred","Exception :" + e, "Fail");
	        		e.printStackTrace();
	    			return false;	    	    	
	    	    }	 
	        	
	        	
	        	//Sleep for a sec
	        	Thread.sleep(1000);
				intCount++;			
	        }
	
	        //Validate if the element should be displayed or not	        
        	if(bNotIsDisplayed){
        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is not displayed" , "Object should not be displayed", "Object is not displayed", "Pass");
	            return true;
        	}
        	else {
        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is not displayed" , "Object should not be displayed", "Object is Displayed", "Fail");
	            return false;
        	}       	
	       	        
    	}catch(Exception e){
    		Reporter.fnWriteToHtmlOutput("Check if element with description  " + strDesc + " is displayed", "Exception occurred","Exception :" + e, "Fail");
    		e.printStackTrace();
			return false;		
    	}    	
    }
	
	
	//*****************************************************************************************
    //*	Name		    : fGuiIsEnabled
    //*	Description	    : Check if the object is enabled or not
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: strDesc - The property of the object which is expected to be  enabled
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiIsEnabled(String strDesc){
    	try{
    		//Get WebElement
    		WebElement webElement = getObject(strDesc);
    		
    		//Check if the WebElement is Enabled
	        boolean bIsEnabled = false;
	        
	        int intCount = 1;    
	        
	        while (!(bIsEnabled) && (intCount <=10)){
	        	try {	        				
	        		bIsEnabled = webElement.isEnabled();
        	    }
	        	catch (Exception e){
	        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is enabled", "Exception occurred","Exception :" + e, "Fail");
	        		e.printStackTrace();
	    			return false;	    	    	
	    	    }   
	        	
	        	//Sleep for a sec, increment the counter
	        	Thread.sleep(1000);
				intCount++;			
	        }
	        
	        //Validate if the WebElement is Enabled
	        if (!(bIsEnabled)){
	        	Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is enabled", "Object should be enabled", "Object is not enabled", "Fail");
	            return false;
	        }
	        Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is enabled", "Object should be enabled", "Object is enabled", "Pass");
	        return true;
	        
    	}catch (Exception e) {
			Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is enabled","Exception Occured", "Exception: " + e, "Fail");
			return false;
		}  
    }	
    
  //*****************************************************************************************
    //*	Name		    : fGuiIsEnabled
    //*	Description	    : Check if the object is enabled or not
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: strDesc - The property of the object which is expected to be  enabled
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiIsEnabled(WebElement webElement){
    	
    	//Check if the WebElement is Enabled
        boolean bIsEnabled = false;
        String strDesc = webElement.toString();
        int intCount = 1;
    	
    	
    	try{
    	    		  	        
	        while (!(bIsEnabled) && (intCount <=10)){
	        	try {	        				
	        		bIsEnabled = webElement.isEnabled();
        	    }
	        	catch (Exception e){
	        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is enabled", "Exception occurred","Exception :" + e, "Fail");
	        		e.printStackTrace();
	    			return false;	    	    	
	    	    }   
	        	
	        	//Sleep for a sec, increment the counter
	        	Thread.sleep(1000);
				intCount++;			
	        }
	        
	        //Validate if the WebElement is Enabled
	        if (!(bIsEnabled)){
	        	Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is enabled", "Object should be enabled", "Object is not enabled", "Fail");
	            return false;
	        }
	        Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is enabled", "Object should be enabled", "Object is enabled", "Pass");
	        return true;
	        
    	}catch (Exception e) {
			Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is enabled","Exception Occured", "Exception: " + e, "Fail");
			return false;
		}  
    }	
    
  //*****************************************************************************************
    //*	Name		    : fGuiIsDisabled
    //*	Description	    : Check if the object is enabled or not
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: strDesc - The property of the object which is expected to be  enabled
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiIsDisabled(String strDesc){
    	try{
    		//Get WebElement
    		WebElement webElement = getObject(strDesc);
    		
    		//Check if the WebElement is Enabled
	        boolean bIsNotEnabled = false;
	        
	        int intCount = 1;    
	        
	        while (!(bIsNotEnabled) && (intCount <=10)){
	        	try {	        				
	        		bIsNotEnabled = !(webElement.isEnabled());
        	    }
	        	catch (Exception e){
	        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is disabled", "Exception occurred","Exception :" + e, "Fail");
	        		e.printStackTrace();
	    			return false;	    	    	
	    	    }   
	        	
	        	//Sleep for a sec, increment the counter
	        	Thread.sleep(1000);
				intCount++;			
	        }
	        
	        //Validate if the WebElement is Enabled
	        if (!(bIsNotEnabled)){
	        	Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is disabled", "Object should be disabled", "Object is not disabled", "Fail");
	            return false;
	        }
	        Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is disabled", "Object should be disabled", "Object is disabled", "Pass");
	        return true;
	        
    	}catch (Exception e) {
			Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is disabled","Exception Occured", "Exception: " + e, "Fail");
			return false;
		}  
    }
    
  //*****************************************************************************************
    //*	Name		    : fGuiIsDisabled
    //*	Description	    : Check if the object is enabled or not
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: strDesc - The property of the object which is expected to be  enabled
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiIsDisabled(WebElement webElement){
    	
    	//Check if the WebElement is Enabled
        boolean bIsNotEnabled = false;
        String strDesc = webElement.toString();
        int intCount = 1; 
    	
    	
    	try{    		    		  
	        
	        while (!(bIsNotEnabled) && (intCount <=10)){
	        	try {	        				
	        		bIsNotEnabled = !(webElement.isEnabled());
        	    }
	        	catch (Exception e){
	        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is disabled", "Exception occurred","Exception :" + e, "Fail");
	        		e.printStackTrace();
	    			return false;	    	    	
	    	    }   
	        	
	        	//Sleep for a sec, increment the counter
	        	Thread.sleep(1000);
				intCount++;			
	        }
	        
	        //Validate if the WebElement is Enabled
	        if (!(bIsNotEnabled)){
	        	Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is disabled", "Object should be disabled", "Object is not disabled", "Fail");
	            return false;
	        }
	        Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is disabled", "Object should be disabled", "Object is disabled", "Pass");
	        return true;
	        
    	}catch (Exception e) {
			Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is disabled","Exception Occured", "Exception: " + e, "Fail");
			return false;
		}  
    }
	

	//*****************************************************************************************
    //*	Name		    : fGuiClick
    //*	Description	    : Click on the passed object
    //*	Author		    : Anil Agarwal
    //*	Input Params	: strDesc - The description of the object to click
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiClick(String strDesc)
    {
    	try
    	{
        	//Initialize
    		WebElement objClick;
        	//
        	//Call the function to get the webelement based on the description
        	objClick = getObject(strDesc);
        	
        	//if null is returned
        	if (objClick==null) return false;
        	
    		//Check if the object is enabled, if yes click the same
    		if (objClick.isEnabled())
    		{
    			//Click on the object
    			objClick.click();
    		}
    		else
    		{
        		Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
            
            Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "Click operation should be successful", "Successfully clicked the object", "Done");
            return true;
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "Click operation failed", "Exception occured while click object", "Fail");
    		return false;
    	}
    }	
    
  //*****************************************************************************************
    //*	Name		    : fGuiClick
    //*	Description	    : Click on the passed object
    //*	Author		    : Anil Agarwal
    //*	Input Params	: strDesc - The description of the object to click
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiClick(WebElement objClick)
    {
    	try
    	{
        	
    		//Check if the object is enabled, if yes click the same
    		if (objClick.isEnabled())
    		{
    			//Click on the object
    			objClick.click();
    		}
    		else
    		{
        		//Reporter.fnWriteToHtmlOutput("Check if object is enabled " + objClick.toString(), "Object with description " + objClick.toString() + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
            
            //Reporter.fnWriteToHtmlOutput("Click object matching description " + objClick.toString(), "Click operation should be successful", "Successfully clicked the object", "Done");
            return true;
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		Reporter.fnWriteToHtmlOutput("Click object matching description " + objClick.toString(), "Click operation failed", "Exception occured while click object", "Fail");
    		return false;
    	}
    }	

	//*****************************************************************************************
    //*	Name		    : fGuiEnterText
    //*	Description	    : Click on the passed object
    //*	Author		    : Anil Agarwal
    //*	Input Params	: WebElement - The webelement to click, strText = Text to enter
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiEnterText(String strDesc, String strText)
    {
    	try
    	{
        	WebElement objEdit;
        	
        	//Call the function to get the webelement based on the description
        	objEdit = getObject(strDesc);
        	
        	//if null is returned
        	if (objEdit==null) return false;
        	
    		//Check if the object is enabled, if yes click the same
    		if (objEdit.isEnabled())
    		{
    			//Enter the text in the edit box
    			objEdit.clear();
    			objEdit.sendKeys(strText);
    		}
    		else
    		{
        		Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
            
            Reporter.fnWriteToHtmlOutput("Set value in object with description " + strDesc, "Value " + strText + " should be set in the edit box", "Value is set in the text field", "Done");
            return true;	
    	}
    	catch(Exception e)
    	{
    		Reporter.fnWriteToHtmlOutput("Set value in object with description " + strDesc, "Value " + strText + " should be set in the edit box", "Exception " + e + " occured while setting the value", "Fail");    		
    		return false;
    	}
    	
    }	
    
  //*****************************************************************************************
    //*	Name		    : fGuiEnterText
    //*	Description	    : Click on the passed object
    //*	Author		    : Anil Agarwal
    //*	Input Params	: WebElement - The webelement to click, strText = Text to enter
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiEnterText(WebElement objEdit, String strText)
    {
    	
    	String strDesc = objEdit.toString();
    	try
    	{        	
        	//Check if the object is enabled, if yes click the same
    		if (objEdit.isEnabled())
    		{
    			//Enter the text in the edit box
    			objEdit.sendKeys(strText);
    		}
    		else
    		{
        		Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
            
            Reporter.fnWriteToHtmlOutput("Set value in object with description " + strDesc, "Value " + strText + " should be set in the edit box", "Value is set in the text field", "Done");
            return true;	
    	}
    	catch(Exception e)
    	{
    		Reporter.fnWriteToHtmlOutput("Set value in object with description " + strDesc, "Value " + strText + " should be set in the edit box", "Exception " + e + " occured while setting the value", "Fail");    		
    		return false;
    	}
    	
    }	

       
    //*****************************************************************************************
    //*	Name		    : fValidatePageDisplayed
    //*	Description	    : Function to validate the current window title
    //*	Author		    : Anil Agarwal
    //*	Input Params	: 
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fValidatePageDisplayed(String strExpectedTitle)
    {
    	int intCount = 1;
    	String strActualTitle = "";
    	
    	//Fetch current title
    	strActualTitle = driver.getTitle();
    	
    	
    	//while(!((strActualTitle.equalsIgnoreCase(strExpectedTitle)) || (intCount >= 31)))
    	while(!((strActualTitle.equalsIgnoreCase(strExpectedTitle)) || (intCount >= 20)))
    	{
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
			strActualTitle = driver.getTitle();
			intCount++;    		
    	}
    	
        //Validate
        if (!(driver.getTitle().equalsIgnoreCase(strExpectedTitle)))
        {
            Reporter.fnWriteToHtmlOutput("Validate Title", "Title should be " + strExpectedTitle, "Title is " + strActualTitle, "Fail");
            return false;
        }

        //Reporter.fnWriteToHtmlOutput("Validate Title", "Title should be " + strTitle, "Title is " + strActualTitle, "Pass");
        Reporter.fnWriteToHtmlOutput("Validate Title", "Title should be " + strExpectedTitle, "Title is " + strActualTitle, "Pass");
        return true;
    }	
    
    
    //*****************************************************************************************
    //*	Name		    : fGuiSelectOptionFromList
    //*	Description	    : Function to select the specified option from the list
    //*	Author		    : Anil Agarwal
    //*	Input Params	: None
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiSelectOptionFromList(String strDesc, String strText)
    {
    	try
    	{
        	WebElement objSelect;
        	
        	//Call the function to get the webelement based on the description
        	objSelect = getObject(strDesc);
        	
        	//if null is returned
        	if (objSelect==null) return false;
        	
        	//Check if the object is enabled, if yes click the same
    		if (objSelect.isEnabled())
    		{
    			//Set Select Element and select required value by text
            	Select select = new Select(objSelect);
            	select.selectByVisibleText(strText); 
            	
    		}
    		else
    		{
        		Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
        	Thread.sleep(500);
        	Reporter.fnWriteToHtmlOutput("Select value from dropdown","Select value " + strText, "Value " + strText +" selected" , "Done");
        	return true;
    		
		} catch (Exception e)
		{
			Reporter.fnWriteToHtmlOutput("Select value from dropdown with description " + strDesc,"Select value " + strText, "Exception " + e + "occured while selecting the value" , "Fail");
			return false;
		}
    }
    
  //*****************************************************************************************
    //*	Name		    : fGuiSelectOptionFromList
    //*	Description	    : Function to select the specified option from the list
    //*	Author		    : Anil Agarwal
    //*	Input Params	: None
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiSelectOptionFromList(WebElement objSelect, String strText)
    {
    	String strDesc = objSelect.toString();
    	
    	try
    	{        	
        	//Check if the object is enabled, if yes click the same
    		if (objSelect.isEnabled())
    		{
    			//Set Select Element and select required value by text
            	Select select = new Select(objSelect);
            	select.selectByVisibleText(strText);    			
    		}
    		else
    		{
        		Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
        	
        	
        	Thread.sleep(500);
        	Reporter.fnWriteToHtmlOutput("Select value from dropdown","Select value " + strText, "Value " + strText +" selected" , "Done");
        	return true;
    		
		} catch (Exception e)
		{
			Reporter.fnWriteToHtmlOutput("Select value from dropdown with description " + strDesc,"Select value " + strText, "Exception " + e + "occured while selecting the value" , "Fail");
			return false;
		}
    }
    
	//*****************************************************************************************
    //*	Name		    : getObject
    //*	Description	    : Returns the webelement based on the description
    //*	Author		    : Anil Agarwal
    //*	Input Params	: objDesc - Description of the object
    //*	Return Values	: Webelement - Webelement based on the description
    //*****************************************************************************************		
    public WebElement getObject(String objDesc)
    {
        //Delimiters
        String[] delimiters = new String[] {":="};
        String[] arrFindByValues = objDesc.split(delimiters[0]);
        
        //Get Findby and Value 
        String FindBy = arrFindByValues[0];
        String val = arrFindByValues[1];

        try
        {
            //Handle all FindBy cases
        	String strElement = FindBy.toLowerCase();
        	if (strElement.equalsIgnoreCase("linktext"))
        	{
        		return driver.findElement(By.linkText(val));
        	}
        	else if (strElement.equalsIgnoreCase("partiallinktext"))
        	{
        		return driver.findElement(By.partialLinkText(val));
        	}
        	else if (strElement.equalsIgnoreCase("xpath"))
        	{
        		return driver.findElement(By.xpath(val));
        	}
        	else if (strElement.equalsIgnoreCase("name"))
        	{
        		return driver.findElement(By.name(val));
        	}
        	else if (strElement.equalsIgnoreCase("id"))
        	{
        		return driver.findElement(By.id(val));
        	}
        	else if (strElement.equalsIgnoreCase("classname"))
        	{
        		return driver.findElement(By.className(val));
        	}
        	else if (strElement.equalsIgnoreCase("cssselector"))
        	{
        		return driver.findElement(By.cssSelector(val));
        	}
        	else if (strElement.equalsIgnoreCase("tagname"))
        	{
        		return driver.findElement(By.tagName(val));
        	}
        	else
        	{
        		Reporter.fnWriteToHtmlOutput("Get object matching description " + objDesc, "Object should be found and returned", "Property "  + FindBy + " specified for object is invalid", "Fail");
        		System.out.println("Property name " + FindBy + " specified for object " + objDesc + " is invalid");
        		return null;
        	}
        	
        }
        
        //Catch Block
        catch(Exception e)
        {
        	Reporter.fnWriteToHtmlOutput("Get object matching description " + objDesc, "Object should be found and returned", "Unable to find required object", "Fail");
        	System.out.println("Exception " + e.toString() + " occured while fetching the object");
        	return null;
        }
       
    }   
    
  //*****************************************************************************************
    //*	Name		    : getObjects
    //*	Description	    : Returns the list of webelement based on the description
    //*	Author		    : Anil Agarwal
    //*	Input Params	: objDesc - Description of the object
    //*	Return Values	: Webelement - Webelement based on the description
    //*****************************************************************************************		
    public List<WebElement> getObjects(String objDesc)
    {
        //Delimiters
        String[] delimiters = new String[] {":="};
        String[] arrFindByValues = objDesc.split(delimiters[0]);
        
        //Get Findby and Value 
        String FindBy = arrFindByValues[0];
        String val = arrFindByValues[1];

        try
        {
            //Handle all FindBy cases
        	String strElement = FindBy.toLowerCase();
        	if (strElement.equalsIgnoreCase("linktext"))
        	{
        		return driver.findElements(By.linkText(val));
        	}
        	else if (strElement.equalsIgnoreCase("partiallinktext"))
        	{
        		return driver.findElements(By.partialLinkText(val));
        	}
        	else if (strElement.equalsIgnoreCase("xpath"))
        	{
        		return driver.findElements(By.xpath(val));
        	}
        	else if (strElement.equalsIgnoreCase("name"))
        	{
        		return driver.findElements(By.name(val));
        	}
        	else if (strElement.equalsIgnoreCase("id"))
        	{
        		return driver.findElements(By.id(val));
        	}
        	else if (strElement.equalsIgnoreCase("classname"))
        	{
        		return driver.findElements(By.className(val));
        	}
        	else if (strElement.equalsIgnoreCase("cssselector"))
        	{
        		return driver.findElements(By.cssSelector(val));
        	}
        	else if (strElement.equalsIgnoreCase("tagname"))
        	{
        		return driver.findElements(By.tagName(val));
        	}
        	else
        	{
        		Reporter.fnWriteToHtmlOutput("Get objects matching description " + objDesc, "Object List should be found and returned", "Property "  + FindBy + " specified for object is invalid", "Fail");
        		System.out.println("Property name " + FindBy + " specified for objects " + objDesc + " is invalid");
        		return null;
        	}
        	
        }
        
        //Catch Block
        catch(Exception e)
        {
        	Reporter.fnWriteToHtmlOutput("Get objects matching description " + objDesc, "Object should be found and returned", "Unable to find required object", "Fail");
        	System.out.println("Exception " + e.toString() + " occured while fetching the object");
        	return null;
        }
       
    }   
    
  //*****************************************************************************************
    //*	Name		    : getChildObject
    //*	Description	    : Returns the child element of webelement based on the description
    //*	Author		    : Aniket Gadre    
    //*	Input Params	: objParent - Parent WebElement
    //*					: objDesc - Description of the object
    //*	Return Values	: Webelement - Webelement based on the description
    //*****************************************************************************************		
    public WebElement getChildObject(WebElement parentElem, String objDesc)
    {
        //Delimiters
        String[] delimiters = new String[] {":="};
        String[] arrFindByValues = objDesc.split(delimiters[0]);
        
        //Get Findby and Value 
        String FindBy = arrFindByValues[0];
        String val = arrFindByValues[1];

        try
        {
            //Handle all FindBy cases
        	String strElement = FindBy.toLowerCase();
        	if (strElement.equalsIgnoreCase("linktext"))
        	{
        		return parentElem.findElement(By.linkText(val));
        	}
        	else if (strElement.equalsIgnoreCase("partiallinktext"))
        	{
        		return parentElem.findElement(By.partialLinkText(val));
        	}
        	else if (strElement.equalsIgnoreCase("xpath"))
        	{
        		return parentElem.findElement(By.xpath(val));
        	}
        	else if (strElement.equalsIgnoreCase("name"))
        	{
        		return parentElem.findElement(By.name(val));
        	}
        	else if (strElement.equalsIgnoreCase("id"))
        	{
        		return parentElem.findElement(By.id(val));
        	}
        	else if (strElement.equalsIgnoreCase("classname"))
        	{
        		return parentElem.findElement(By.className(val));
        	}
        	else if (strElement.equalsIgnoreCase("cssselector"))
        	{
        		return parentElem.findElement(By.cssSelector(val));
        	}
        	else if (strElement.equalsIgnoreCase("tagname"))
        	{
        		return parentElem.findElement(By.tagName(val));
        	}
        	else
        	{	
        		Reporter.fnWriteToHtmlOutput("Get child object matching description " + objDesc, "Object should be found and returned", "Property "  + FindBy + " specified for object is invalid", "Fail");
        		System.out.println("Property name " + FindBy + " specified for object " + objDesc + " is invalid");
        		return null;
        	}        	
        }
        
        //Catch Block
        catch(Exception e)
        {
        	Reporter.fnWriteToHtmlOutput("Get child object matching description " + objDesc, "Object should be found and returned", "Unable to find required object", "Fail");
        	System.out.println("Exception " + e.toString() + " occured while fetching the object");
        	return null;
        }
       
    }   
    
    
    //*****************************************************************************************
    //*	Name		    : fGuiCheckCheckBox
    //*	Description	    : Checks the required checkbox if its not checked already
    //*	Author		    : Aniket Gadre
    //*	Input Params	: strDesc - The description of the object to click
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiCheckCheckBox(String strDesc)
    {
    	try
    	{
        	//Initialize
    		WebElement objChkBox;
        	
        	//Call the function to get the webelement based on the description
    		objChkBox = getObject(strDesc);        	        
        	
        	//if null is returned
        	if (objChkBox==null) return false;
        	        	
    		//Check if the object is enabled, if yes click the same
    		if (objChkBox.isEnabled())
    		{
    			//Check state of check box
            	boolean isChecked = objChkBox.isSelected();
            	
            	//Check if Not Checked
            	if(isChecked == false) objChkBox.click();
            	
    		}
    		else
    		{
			if(objChkBox.isSelected()) {
    				Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "Check operation should be successful", "Object is disabled and already checked", "Done");
    				return true;
    			}
        		Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
            
            Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "CHeck operation should be successful", "Successfully checked the checkbox", "Done");
            return true;
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "Checking checkbox operation failed", "Exception occured while checking check box", "Fail");
    		return false;
    	}
    }
    
  //*****************************************************************************************
    //*	Name		    : fGuiCheckCheckBox
    //*	Description	    : Checks the required checkbox if its not checked already
    //*	Author		    : Aniket Gadre
    //*	Input Params	: WebElement - The description of the object to click
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiCheckCheckBox(WebElement objChkBox)
    {
    	String strDesc = objChkBox.toString();
    	
    	try
    	{
        	       		
        	//Check if the object is enabled, if yes click the same
    		if (objChkBox.isEnabled())
    		{
    			//Check state of check box
            	boolean isChecked = objChkBox.isSelected();
            	
            	//Check if Not Checked
            	if(isChecked == false) objChkBox.click();
            	
    		}
    		else
    		{
        		Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
            
            Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "CHeck operation should be successful", "Successfully checked the checkbox", "Done");
            return true;
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "Checking checkbox operation failed", "Exception occured while checking check box", "Fail");
    		return false;
    	}
    }
    
    //*****************************************************************************************
    //*	Name		    : fGuiUncheckCheckBox
    //*	Description	    : Unchecks the required check box if its  checked already
    //*	Author		    : Aniket Gadre
    //*	Input Params	: strDesc - The description of the object to click
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiUncheckCheckBox(String strDesc)
    {
    	try
    	{
        	//Initialize
    		WebElement objChkBox;
        	
        	//Call the function to get the webelement based on the description
    		objChkBox = getObject(strDesc);        	        
        	
        	//if null is returned
        	if (objChkBox==null) return false;
        	        	
    		//Check if the object is enabled, if yes click the same
    		if (objChkBox.isEnabled())
    		{
    			//Check state of check box
    			boolean isChecked = objChkBox.isSelected();
            	
            	//Check if Not Checked
            	if(isChecked == true) objChkBox.click();
            	
    		}
    		else
    		{
        		Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
            
            Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "Uncheck operation should be successful", "Successfully unchecked the checkbox", "Done");
            return true;
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "Unchecking checkbox operation failed", "Exception occured while unchecking check box", "Fail");
    		return false;
    	}
    }	
    
    //*****************************************************************************************
    //*	Name		    : fGuiUncheckCheckBox
    //*	Description	    : Unchecks the required check box if its  checked already
    //*	Author		    : Aniket Gadre
    //*	Input Params	: strDesc - The description of the object to click
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fGuiUncheckCheckBox(WebElement objChkBox)
    {
    	String strDesc = objChkBox.toString();
    	
    	try
    	{
                	
        	//Check if the object is enabled, if yes click the same
    		if (objChkBox.isEnabled())
    		{
    			//Check state of check box
    			boolean isChecked = objChkBox.isSelected();
            	
            	//Check if Not Checked
            	if(isChecked == true) objChkBox.click();
            	
    		}
    		else
    		{
        		Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
            
            Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "Uncheck operation should be successful", "Successfully unchecked the checkbox", "Done");
            return true;
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		Reporter.fnWriteToHtmlOutput("Click object matching description " + strDesc, "Unchecking checkbox operation failed", "Exception occured while unchecking check box", "Fail");
    		return false;
    	}
    }	
    
    
    //*****************************************************************************************
    //*	Name		    : setGeoLocation
    //*	Description	    : Checks whether required listing detail is displayed
    //*	Author		    : Aniket Gadre
    //*	Input Params	: None
    //*	Return Values	: None
    //*****************************************************************************************
	public boolean setGeoLocation(String lat, String lon)
	{
		try
		{
			String Script = "window.navigator.geolocation.getCurrentPosition =  function(success){var position = {'coords' : {'latitude': '" + lat + "', 'longitude': '" + lon + "'}}; success(position);}";
			
			//Update geolocation
			JavascriptExecutor js = (JavascriptExecutor)driver;
			Object[] args = {null};
			js.executeScript(Script, args);
		}
		catch(Exception e)
		{
			Reporter.fnWriteToHtmlOutput("Set geo location", "Should be set successfully", "Setting geo location failed. Exception " + e, "Fail");
			return false;
		}
		//return
		return true;
	}
	
	//*****************************************************************************************
    //*	Name		    : getCurrentBrowser
    //*	Description	    : Checks whether required listing detail is displayed
    //*	Author		    : Aniket Gadre
    //*	Input Params	: None
    //*	Return Values	: None
    //*****************************************************************************************
	public String getCurrentBrowser()
	{
		try
		{
			Capabilities DC = ((RemoteWebDriver)driver).getCapabilities();
			return DC.getBrowserName();
		}
		catch(Exception e)
		{
			Reporter.fnWriteToHtmlOutput("Get browser name", "Should return Browser Name", "Fetching Browser Name Failed. Exception " + e, "Fail");
			return "";
		}
	}
	
	//*****************************************************************************************
    //*	Name		    : switchToWindowWithName
    //*	Description	    : Checks whether required listing detail is displayed
    //*	Author		    : Aniket Gadre
    //*	Input Params	: None
    //*	Return Values	: None
    //*****************************************************************************************
	public boolean switchToWindowWithName(String strWindowName)
	{
		try
		{
			//driver.switchTo().window(strWindowName);
			//Switch to new window opened
			for(String winHandle : driver.getWindowHandles()){
				//Check if the name matches the Window handle
				if (driver.switchTo().window(winHandle).getTitle().toUpperCase().contains(strWindowName.toUpperCase())){
					Reporter.fnWriteToHtmlOutput("Switch Window", "Switch to new Window " + strWindowName, "Successfully switched to window " + strWindowName, "Pass");
					return true;
				}
			    
			}
			Reporter.fnWriteToHtmlOutput("Switch Window", "Switch to new Window " + strWindowName, "Failed to switchto window " + strWindowName, "Fail");
			return false;
		}
		catch(Exception e)
		{
			Reporter.fnWriteToHtmlOutput("Switch Window", "Switch to new Window ", "Exception occured : " + e, "Fail");
			return false;
		}
	}
	
	//*****************************************************************************************
    //*	Name		    : fverifyCheckBoxIsChecked
    //*	Description	    : Checks the required checkbox if its not checked already
    //*	Author		    : Aniket Gadre
    //*	Input Params	: strDesc - The description of the object to click
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fverifyCheckBoxIsChecked(String strDesc)
    {
    	try
    	{
        	//Initialize
    		WebElement objChkBox;
        	
        	//Call the function to get the webelement based on the description
    		objChkBox = getObject(strDesc);        	        
        	
        	//if null is returned
        	if (objChkBox==null) return false;
        	        	
    		//Check if the object is Checked, if yes click the same
    		if (objChkBox.isSelected())
    		{
    			 //Reporter.fnWriteToHtmlOutput("Check is checkbox is checked with description " + strDesc, "CHeckbox should be  checked", "Checkbox is checked", "Done");
    	         return true;
            	
    		}
    		else
    		{
        		//Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}
            
            
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		Reporter.fnWriteToHtmlOutput("Verify is checkbox is checked with description " + strDesc, "Verifying checkbox is checked operation failed", "Exception occured while checking check box", "Fail");
    		return false;
    	}
    }	
    
    //*****************************************************************************************
    //*	Name		    : fverifyCheckBoxIsUnchecked
    //*	Description	    : Checks the required checkbox if its not checked already
    //*	Author		    : Aniket Gadre
    //*	Input Params	: strDesc - The description of the object to click
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public boolean fverifyCheckBoxIsUnchecked(String strDesc)
    {
    	try
    	{
        	//Initialize
    		WebElement objChkBox;
        	
        	//Call the function to get the webelement based on the description
    		objChkBox = getObject(strDesc);        	        
        	
        	//if null is returned
        	if (objChkBox==null) return false;
        	        	
    		//Check if the object is Checked, if yes click the same
    		if (!objChkBox.isSelected())
    		{
    			 //Reporter.fnWriteToHtmlOutput("Check is checkbox is checked with description " + strDesc, "CHeckbox should be  checked", "Checkbox is checked", "Done");
    	         return true;            
    		}
    		else
    		{
        		//Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return false;   			
    		}                    
    		
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		Reporter.fnWriteToHtmlOutput("Verify is checkbox is unchecked with description " + strDesc, "Verifying checkbox is checked operation failed", "Exception occured while checking check box", "Fail");
    		return false;
    	}
    }
	
	//*****************************************************************************************
    //*	Name		    : returnTimeStamp
    //*	Description	    : returns current time stamp
    //*	Author		    : Aniket Gadre
    //*	Input Params	: strDesc - The description of the object to click
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public String returnTimeStamp()
    {
    	java.util.Date today = new java.util.Date();
    	Timestamp now = new java.sql.Timestamp(today.getTime());
        String timeStamp = now.toString().replaceAll(":", "").replaceAll("-", "").replaceAll(" ", "");
        timeStamp = timeStamp.split("\\.")[0];
        return timeStamp;
    }
    
    //*****************************************************************************************
    //*	Name		    : returnRandomString()
    //*	Description	    : returns current time stamp
    //*	Author		    : Aniket Gadre
    //*	Input Params	: strDesc - The description of the object to click
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public String returnRandomString()
    {
    	//Get time stamp
        String timeStamp = returnTimeStamp();
        String Random = "";
        
        int z;

        int iLen = timeStamp.length();
        for(int i=0;i<iLen;i++)
        {
        	z = (int)(timeStamp.substring(i, i+1).toCharArray()[0]) - 48 + 97;
      
        	Random = Random + (char)z;
        }
        
        
        return Random;
    }
    
    //*****************************************************************************************
    //*	Name		    : returnRandomNumber
    //*	Description	    : returns random number 
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: int
    //*****************************************************************************************
    public int returnRandomNumber()
    {
    	int random = 100000000 + (int) (899999999*Math.random());
    	return random;
    }
    
	//*****************************************************************************************
    //*	Name		    : fGuiGetText
    //*	Description	    : returns current text of the webelement
    //*	Author		    : Anil Agarwal
    //*	Input Params	: strDesc - The description of the object to whose value needs to be returned
    //*	Return Values	: String - The text()of the webelement
    //*****************************************************************************************
    public String fGuiGetText(String strDesc)
    {
    	//Initialize
		WebElement objWebElement;
		
    	//Call the function to get the webelement based on the description
		objWebElement = getObject(strDesc);        	        
    	
    	//if null is returned
    	if (objWebElement==null) return null;
    	
    	//Return the text of the webelement
    	String strValue = objWebElement.getText().trim();
    	Reporter.fnWriteToHtmlOutput("Text for Webelement matching the description " + strDesc, "Should return the text of the Webelement", "Text is " + strValue, "Done");
        return strValue;
    }
    
	//*****************************************************************************************
    //*	Name		    : fGuiGetText
    //*	Description	    : returns current text of the webelement
    //*	Author		    : Anil Agarwal
    //*	Input Params	: strDesc - The description of the object to whose value needs to be returned
    //*	Return Values	: String - The text()of the webelement
    //*****************************************************************************************
    public String fGuiGetText(WebElement objWebElement)
    {
    	//Return the text of the webelement
    	String strValue = objWebElement.getText().trim();
    	Reporter.fnWriteToHtmlOutput("Text for Webelement matching the description ", "Should return the text of the Webelement", "Text is " + strValue, "Done");
        return strValue;
    }
    
    //*****************************************************************************************
    //*	Name		    : fGuiAttributeValue
    //*	Description	    : returns current text of the webelement
    //*	Author		    : Anil Agarwal
    //*	Input Params	: strDesc - The description of the object to whose value needs to be returned
    //*	Return Values	: String - The text()of the webelement
    //*****************************************************************************************
    public String fGuiAttributeValue(String strDesc)
    {
    	//Initialize
		WebElement objWebElement;
    	
    	//Call the function to get the webelement based on the description
		objWebElement = getObject(strDesc);        	        
    	
    	//if null is returned
    	if (objWebElement==null) return null;
    	
    	//Return the text of the webelement
    	String strValue = objWebElement.getAttribute("value").trim();
    	
    	//if null is returned
    	if (strValue==null) return null;
    	
    	Reporter.fnWriteToHtmlOutput("Text for Webelement matching the description " + strDesc, "Should return the text of the Webelement", "Text is " + strValue, "Done");
        return strValue;
    }
    
	//*****************************************************************************************
    //*	Name		    : fGuiAttributeValue
    //*	Description	    : returns current text of the webelement
    //*	Author		    : Anil Agarwal
    //*	Input Params	: strDesc - The description of the object to whose value needs to be returned
    //*	Return Values	: String - The text()of the webelement
    //*****************************************************************************************
    public String fGuiAttributeValue(WebElement objWebElement)
    {
    	//Return the text of the webelement
    	String strValue = objWebElement.getAttribute("value").trim();
    	
    	//if null is returned
    	if (strValue==null) return null;
    	
    	Reporter.fnWriteToHtmlOutput("Text for Webelement matching the description ", "Should return the text of the Webelement", "Text is " + strValue, "Done");
        return strValue;
    }
    
	//*****************************************************************************************
    //*	Name		    : fWaitUntilTextToBePresentInElementValue
    //*	Description	    : returns current text of the webelement
    //*	Author		    : Anil Agarwal
    //*	Input Params	: strDesc - The description of the object to whose value needs to be returned
    //*	Return Values	: String - The text()of the webelement
    //*****************************************************************************************
    public void fWaitUntilTextToBePresentInElementValue(String strDesc, String strValue)
    {
        //Delimiters
        String[] delimiters = new String[] {":="};
        String[] arrFindByValues = strDesc.split(delimiters[0]);
        
        //Get Findby and Value 
        String FindBy = arrFindByValues[0];
        String val = arrFindByValues[1];
        
        WebDriverWait wait = new WebDriverWait(driver, 15);
        

        try
        {
            //Handle all FindBy cases
        	String strElement = FindBy.toLowerCase();
        	if (strElement.equalsIgnoreCase("linktext"))
        	{
        		wait.until(ExpectedConditions.textToBePresentInElementValue(By.linkText(val), strValue));
        	}
        	else if (strElement.equalsIgnoreCase("partiallinktext"))
        	{
        		wait.until(ExpectedConditions.textToBePresentInElementValue(By.partialLinkText(val), strValue));
        	}
        	else if (strElement.equalsIgnoreCase("xpath"))
        	{
        		wait.until(ExpectedConditions.textToBePresentInElementValue(By.xpath(val), strValue));
        	}
        	else if (strElement.equalsIgnoreCase("name"))
        	{
        		wait.until(ExpectedConditions.textToBePresentInElementValue(By.name(val), strValue));
        	}
        	else if (strElement.equalsIgnoreCase("id"))
        	{
        		wait.until(ExpectedConditions.textToBePresentInElementValue(By.id(val), strValue));
        	}
        	else if (strElement.equalsIgnoreCase("classname"))
        	{
        		wait.until(ExpectedConditions.textToBePresentInElementValue(By.className(val), strValue));
        	}
        	else if (strElement.equalsIgnoreCase("cssselector"))
        	{
        		wait.until(ExpectedConditions.textToBePresentInElementValue(By.cssSelector(val), strValue));
        	}
        	else if (strElement.equalsIgnoreCase("tagname"))
        	{
        		wait.until(ExpectedConditions.textToBePresentInElementValue(By.tagName(val), strValue));
        	}
        	else
        	{	
        		Reporter.fnWriteToHtmlOutput("Invalid object descriptio" + strDesc, "Object should be found and returned", "Property "  + FindBy + " specified for object is invalid", "Fail");
        		return;
        	}        	
        }
        
        //Catch Block
        catch(Exception e)
        {
        	Reporter.fnWriteToHtmlOutput("Get child object matching description " + strDesc, "Object should be found and returned", "Unable to find required object", "Fail");
        	System.out.println("Exception " + e.toString() + " occured while fetching the object");
        	return;
        }
       
    }   

    //*****************************************************************************************
    //*	Name		    : fGuiSelectOptionFromList
    //*	Description	    : Function to select the specified option from the list
    //*	Author		    : Anil Agarwal
    //*	Input Params	: None
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
    public String fGuiGetSelectedOptionFromList(String strDesc)
    {
    	try
    	{
        	WebElement objSelect;
        	
        	//Call the function to get the webelement based on the description
        	objSelect = getObject(strDesc);
        	
        	//if null is returned
        	if (objSelect==null) return "";
        	
        	//Check if the object is enabled, if yes click the same
    		if (objSelect.isEnabled()){
    			//Set Select Element and select required value by text
            	Select select = new Select(objSelect);
            	String strSelectedValue = select.getFirstSelectedOption().getText(); 
            	Reporter.fnWriteToHtmlOutput("Get Selected value from dropdown","Get Selected value from Drop down", "Value " + strSelectedValue +" selected" , "Done");
            	return strSelectedValue;
    		}
    		else{
        		Reporter.fnWriteToHtmlOutput("Check if object is enabled " + strDesc, "Object with description " + strDesc + " should be enabled", "Object is not enabled", "Fail");
        		return "";   			
    		}
    		
		} catch (Exception e)
		{
			Reporter.fnWriteToHtmlOutput("Get Selected value from dropdown","Get Selected value from Drop down", "Got exception while geting the selected value from dropdown", "Fail");
			return "";
		}
    }
    
  //*****************************************************************************************
    //*	Name		    : fChkForwardNavigation
    //*	Description	    : checks the forward navigation
    //*	Author		    : Diksha Mirajkar
    //*	Input Params	: strDesc - The description of the object to whose value needs to be returned
    //*	Return Values	: boolean-depends on the sucess
    //*****************************************************************************************
	public boolean fChkForwardNavigation(String strDesc)
    {
    	//Call the function to get the webelement based on the description
    	WebElement webelement=getObject(strDesc);
    	try
    	{
		//return false if null is returned
		if(webelement==null){
			return false;
		}
		//check if the webelement is enabled and click it
		if(webelement.isEnabled())
		{
			webelement.click();
		}
		//report failure
		else
		{
    		Reporter.fnWriteToHtmlOutput("Check if forward navigation is working " + webelement.toString(), "Object with description " + webelement.toString() + " should be navigated forward", "Object is not navigated", "Fail");
    		return false;   			
		}
		//report sucess
		Reporter.fnWriteToHtmlOutput("Navigation object matching description " + webelement.toString(), "Forward Navigation operation should be successful", "Successfully navigated", "Done");
	    return true;
		
	}
	 
	catch (Exception e)
	{
		e.printStackTrace();
		Reporter.fnWriteToHtmlOutput("Navigation object matching description " + webelement.toString(), "Forward Navigation operation failed", "Exception occured while navigating forward", "Fail");
		return false;
	}
}
  
  //*****************************************************************************************
    //*	Name		    : fChkBackwardNavigation
    //*	Description	    : checks the backward navigation
    //*	Author		    : Diksha Mirajkar
    //*	Input Params	: strDesc - The description of the object to whose value needs to be returned
    //*	Return Values	: boolean-depends on the sucess
    //*****************************************************************************************
    public boolean fChkBackwardNavigation(String strDesc)
    {
    	//Call the function to get the webelement based on the description
    	WebElement webelement=getObject(strDesc);
    	try
    	{
    	//return false if null is returned
		if(webelement==null){
			return false;
		}
		//check if the webelement is enabled and click it
		if(webelement.isEnabled())
		{
			webelement.click();
		}
		//report failure
		else
		{
    		Reporter.fnWriteToHtmlOutput("Check if backward navigation is working " + webelement.toString(), "Object with description " + webelement.toString() + " should be navigated forward", "Object is not navigated", "Fail");
    		return false;   			
		}
		//report sucess
		Reporter.fnWriteToHtmlOutput("Navigation object matching description " + webelement.toString(), "Backward Navigation operation should be successful", "Successfully navigated", "Done");
	    return true;
		
	}
	 
	catch (Exception e)
	{
		e.printStackTrace();
		Reporter.fnWriteToHtmlOutput("Navigation object matching description " + webelement.toString(), "Backward navigation operation failed", "Exception occured while navigating backward", "Fail");
		return false;
	}
}

	//*****************************************************************************************
    //*	Name		    : fGuiIsDisplayed
    //*	Description	    : Check if the object is displayed or not as per the choice
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: strDesc(The property of the object)
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************
	public boolean fGuiIsDisplayed1(String strDesc){
    	try{
    		
    		//Get WebElement
    		WebElement webElement = getObject(strDesc);	
    		    		
    		//Check if the WebElement is displayed    		
    		boolean bIsDisplayed = false;	        
	        
	        int intCount = 1;    
	        
	        //Loop for around 10 secs to check whether object is being displayed
	        while (!(bIsDisplayed) && (intCount <=20))
	        {
	        	try {
	        			bIsDisplayed = webElement.isDisplayed();
	        		
        	    }
	        	catch (Exception e){
	        		Reporter.fnWriteToHtmlOutput("Check if element with description  " + strDesc + " is displayed", "Exception occurred","Exception :" + e, "Fail");
	        		e.printStackTrace();
	    			return false;	    	    	
	    	    }	 
	        	
	        	
	        	//Sleep for a sec
	        	Thread.sleep(1000);
				intCount++;			
	        }
	
	        //Validate if the element should be displayed or not	        
        	if(bIsDisplayed){
        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is displayed" , "Object should be displayed", "Object is Displayed", "Pass");
	            return true;
        	}
        	else {
        		Reporter.fnWriteToHtmlOutput("Check if object with description  " + strDesc + " is displayed" , "Object should be displayed", "Object is not displayed", "Fail");
	            return false;
        	}       	
	       	        
    	}catch(Exception e){
    		Reporter.fnWriteToHtmlOutput("Check if element with description  " + strDesc + " is displayed", "Exception occurred","Exception :" + e, "Fail");
    		e.printStackTrace();
			return false;		
    	}    	
    }

	 //*****************************************************************************************
    //*    Name            : browseFileLocation
    //*    Description     : Clicking on the Browse option to locate the files
    //*    Author          : Vinayak Gaval
    //*    Input Params    : strDesc - The description of the object to click
    //*                 	: strFilePath - file path to be selected
    //*    Return Values    : Boolean - Depending on the success
    //*****************************************************************************************
    public boolean browseFileLocation(String strDesc,String strFilePath)
    {
    	 try{
        //Clicking on Browse button
        if(getObjects(strDesc).size()>0){
            if(fGuiClick(strDesc)==false){
                Reporter.fnWriteToHtmlOutput("Clicking on Browse button", "Browse button should be clicked", "Failed to click on Browse button", "Fail");
                return false;
            }
        }
        
              Thread.sleep(2000);
              StringSelection stringSelection = new StringSelection(strFilePath);//data should be absolute path to file
              Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(stringSelection, null);
              Robot robot = new Robot();
              robot.keyPress(10);
              robot.keyRelease(10);
              Thread.sleep(1000);
              robot.keyPress(17);
              robot.keyPress(86);
              robot.keyRelease(86);
              robot.keyRelease(17);
              Thread.sleep(1000);
              robot.keyPress(10);
              robot.keyRelease(10); 
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }                  
        
        Reporter.fnWriteToHtmlOutput("File selection by browsing the location", "File selection by browsing the location should be done properly", "File selection by browsing the location is done successfully", "Pass");
        return true;
            
    }

	
}
