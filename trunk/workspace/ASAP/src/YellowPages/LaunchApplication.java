package YellowPages;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ASAP.CommonFunctions;
import ASAP.Reporting;

public class LaunchApplication {
	
	//*************************************************************
	/** List of Methods in this class **/
	//** boolean openApplication()
	/** List of Methods in this class **/
	//*************************************************************
	
	//Initialize
	private Reporting Reporter;
	private WebDriver driver;
	private HashMap<String, String> Dictionary;
	private HashMap<String, String> Environment;
	private CommonFunctions objCommon;
	
	//Constructor
	public LaunchApplication(WebDriver driverTemp, Reporting ReporterTemp, HashMap<String, String> DictionaryTemp,  HashMap<String, String> EnvironmentTemp)
	{
		Reporter = ReporterTemp;
		driver = driverTemp;
		Dictionary = DictionaryTemp;
		Environment = EnvironmentTemp;
		objCommon = new CommonFunctions(driver, Reporter);
	}
	
	//Define Objects
	
	
	//*****************************************************************************************
    //*	Name		    : openApplication
    //*	Description	    : Function to launch the application
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: boolean
    //*****************************************************************************************
	public boolean openApplication()
	{				
		driver.get(Dictionary.get("URL"));
				
		Reporter.fnWriteToHtmlOutput("Navigate to specified URL", "URL: " + Dictionary.get("URL"), "Navigated to URL: " + Dictionary.get("URL") , "Pass");
		return true;
	}

	
	
	
}
