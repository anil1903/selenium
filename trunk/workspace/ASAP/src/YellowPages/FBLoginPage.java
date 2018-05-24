package YellowPages;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ASAP.CommonFunctions;
import ASAP.Reporting;

public class FBLoginPage {
	
	//*************************************************************
	/** List of Methods in this class **/
	//** boolean createAdvertiser()
	/** List of Methods in this class **/
	//*************************************************************
	
	//Initialize
	private Reporting Reporter;
	private WebDriver driver;
	private HashMap<String, String> Dictionary;
	private HashMap<String, String> Environment;
	private CommonFunctions objCommon;
	
	//Constructor
	public FBLoginPage(WebDriver driverTemp, Reporting ReporterTemp, HashMap<String, String> DictionaryTemp,  HashMap<String, String> EnvironmentTemp)
	{
		Reporter = ReporterTemp;
		driver = driverTemp;
		Dictionary = DictionaryTemp;
		Environment = EnvironmentTemp;
		objCommon = new CommonFunctions(driver, Reporter);
	}
	
	//Define Objects
	//Edit Boxes
	private String edtEmail = "id:=email";
	private String edtpassword = "id:=pass";
	
	//Buttons
	private String btnLogIn = "name:=login";
	
	//*****************************************************************************************
    //*	Name		    : loginToFB
    //*	Description	    : Logs in to Facebook
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: HomePage - Object of the HomePage
    //*****************************************************************************************
	public HomePage loginToFB()
	{				
		//Enter the email
		if(objCommon.fGuiEnterText(edtEmail, Dictionary.get("FB_EMAIL")) == false) return null;
				
		//Enter the password
		if(objCommon.fGuiEnterText(edtpassword, Dictionary.get("FB_PASSWORD")) == false) return null;
		
		//Click on login button
		if(objCommon.fGuiClick(btnLogIn) == false) return null;
		
		//Report Success
		Reporter.fnWriteToHtmlOutput("Login to facebook", "Should be logged in to facebook", "Logged in to facebook successfully", "Pass");
		return new HomePage(driver,Reporter,Dictionary, Environment);
	}
	
}
