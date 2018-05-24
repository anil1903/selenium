package YellowPages;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ASAP.CommonFunctions;
import ASAP.Reporting;

public class HomePage extends LaunchApplication{
	
	//*************************************************************
	/** List of Methods in this class **/
	//** FBLoginPage navigateToFBLogin()
	//** String getFBUserName()
	//** boolean logoutFromFB()
	//** boolean changeProtocolIdTo(String protocolId)
	//** boolean isURLSecure()
	//** boolean checkNoOfArticles()
	//** boolean searchBusiness()
	/** List of Methods in this class **/
	//*************************************************************
	
	//Initialize
	private Reporting Reporter;
	private WebDriver driver;
	private HashMap<String, String> Dictionary;
	private HashMap<String, String> Environment;
	private CommonFunctions objCommon;
	
	//Constructor
	public HomePage(WebDriver driverTemp, Reporting ReporterTemp, HashMap<String, String> DictionaryTemp,  HashMap<String, String> EnvironmentTemp)
	{
		super(driverTemp, ReporterTemp, DictionaryTemp, EnvironmentTemp);
		Reporter = ReporterTemp;
		driver = driverTemp;
		Dictionary = DictionaryTemp;
		Environment = EnvironmentTemp;
		objCommon = new CommonFunctions(driver, Reporter);
	}
	
	//Define Objects
	//Links
	private String lnkLogin = "xpath:=//*[contains(@class,'login facebook')]";
	
	//Buttons
	private String btnFBLogin = "xpath:=//*[contains(@class,'login-button login-button-facebook')]";
	private String btnSearch = "xpath:=//*[contains(@class, 'button button-search')]";
	
	//Edit Boxes
	private String edtSearchWhat = "id:=what";
	private String edtSearchWhere = "id:=where";
	
	//WebElements
	private String drpDwnUserActivator = "classname:=nav-bar-user-activator";
	private String webElmtFBLogout = "classname:=logout";
	private String webElmtFBUser = "xpath:=//*[contains(@class,'cell user-details middle-cell')]";
	private String webElmntNoOfArticles = "xpath:=.//a[contains(@class,'article-card index')]";
	private List <WebElement> lst;
	
	//*****************************************************************************************
    //*	Name		    : navigateToFBLogin
    //*	Description	    : Navigates to the Facebook Login Page
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: FBLoginPage - Object of the FBLoginPage
    //*****************************************************************************************
	public FBLoginPage navigateToFBLogin()
	{				
		//Click on Login link
		if(objCommon.fGuiClick(lnkLogin) == false) return null;
				
		//Click on Login with Facebook
		if(objCommon.fGuiClick(btnFBLogin) == false) return null;
		
		//Delay to let the Facebook page load fully
		objCommon.delayBy(4000);
		
		//Validate that Login Page is displayed
		if(driver.getTitle().equalsIgnoreCase("Facebook") == false) return null;
		
		//Report Success
		Reporter.fnWriteToHtmlOutput("Navigate to the Facebook Login Page", "Facebook Login Page should be displayed", "Facebook Login Page is displayed", "Pass");
		return new FBLoginPage(driver,Reporter,Dictionary, Environment);
	}
	
	//*****************************************************************************************
    //*	Name		    : getFBUserName
    //*	Description	    : Fetches the name of the FB logged in user
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: String
    //*****************************************************************************************
	public String getFBUserName()
	{				
		//Fetch the name
		String name = objCommon.fGuiGetText(webElmtFBUser);
				
		//Verify that a user's name is found
		if(name == null || name.equals("")) {
			Reporter.fnWriteToHtmlOutput("Fetch FB User Name", "Should Return a name", "No Name( " + name + " ) found.", "Fail");
			return null;
		}
		
		Reporter.fnWriteToHtmlOutput("Fetch FB User Name", "Should Return a name", "Name( " + name + " ) found.", "Pass");
		return name;
	}
	
	//*****************************************************************************************
    //*	Name		    : logoutFromFB
    //*	Description	    : logs out from facebook
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: boolean
    //*****************************************************************************************
	public boolean logoutFromFB()
	{				
		//Click on drop down
		if(objCommon.fGuiClick(drpDwnUserActivator) == false) return false;
				
		//Click on Logout
		if(objCommon.fGuiClick(webElmtFBLogout) == false) return false;
		
		Reporter.fnWriteToHtmlOutput("FB Logout", "Should Logout from Facebook", "Logged out from facebook successfully", "Pass");
		return true;
	}
	
	//*****************************************************************************************
    //*	Name		    : changeProtocolIdTo
    //*	Description	    : Changes the protocol Identifier to the String specified
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: String - Protocol Identifier(eg: "https" or "http")
    //*	Return Values	: boolean
    //*****************************************************************************************
	public boolean changeProtocolIdTo(String protocolId)
	{				
		//Fetch the current URL
		String actualUrl = driver.getCurrentUrl();
				
		//Fetch the URL without protocol identifier
		String newUrl = actualUrl.split("://", 2)[1];
		
		//Change the protocol to the one specified
		newUrl = protocolId + "://" + newUrl;
		driver.get(newUrl);
		
		//Report Success
		Reporter.fnWriteToHtmlOutput("Protocol Identifier Change", "Change the Protocol Identifier of the current URL", "Changed the Protocol Identifier of the current URL to: " + protocolId, "Done");
		return true;
	}
	
	//*****************************************************************************************
    //*	Name		    : isURLSecure
    //*	Description	    : Checks if the current URL is secure
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: true - if URL is secure ("https")
	//					: false - if not
    //*****************************************************************************************
	public boolean isURLSecure()
	{				
		//Fetch the current URL
		String url = driver.getCurrentUrl();
				
		//Fetch the Protocol Identifier
		String protocolId = url.split("://", 2)[0];
		
		//Validate if the protocol identifier is https
		if(protocolId.equalsIgnoreCase("https") == false) {
			Reporter.fnWriteToHtmlOutput("Verify if URL has https", "Check if URL has https or not", "URL do not have https, URL: " + url, "Done");
			return false;
		}
		
		//Report Success
		Reporter.fnWriteToHtmlOutput("Verify if URL has https", "Check if URL has https or not", "URL has https, URL: " + url, "Done");
		return true;
	}
		
	//*****************************************************************************************
    //*	Name		    : checkNoOfArticles
    //*	Description	    : Gets no of articles displayed
    //*	Author		    : Diksha Mirajkar
    //*	Input Params	: None
    //*	Return Values	: boolean 
    //*****************************************************************************************
	public boolean checkNoOfArticles(){
		
		lst = objCommon.getObjects(webElmntNoOfArticles);
		
		//Validate if the list size is 0
		if(lst.size()==0){
			Reporter.fnWriteToHtmlOutput("Check if the list of articles is displayed ","List of articles should be displayed", "List of articles not displayed", "Fail");
			return false;
		}
		//Get the no of articles displayed
		int no_of_articles=lst.size();

		//report success
		Reporter.fnWriteToHtmlOutput("Check if the list of articles is displayed ","List of articles should be displayed","No of articles displayed are : "  + no_of_articles , "Pass");		
		return true;
	}
	
	//*****************************************************************************************
    //*	Name		    : searchBusiness
    //*	Description	    : Performs search for Business with Dimmi
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: SearchResultPage - Object of the SearchResultPage
    //*****************************************************************************************
	public SearchResultPage searchBusiness(){
		
		//Enter in Search what edit box
		if(objCommon.fGuiEnterText(edtSearchWhat, Dictionary.get("SEARCH_WHAT")) == false) return null;
		
		//Enter in Search where edit box
		if(objCommon.fGuiEnterText(edtSearchWhere, Dictionary.get("SEARCH_WHERE")) == false) return null;
		
		//Click on Search Button
		if(objCommon.fGuiClick(btnSearch) == false) return null;
		
		//Report Success
		Reporter.fnWriteToHtmlOutput("Search Business", "Search should be performed", "Search performed successfully", "Pass");
		return new SearchResultPage(driver,Reporter,Dictionary, Environment);
	}
}
