package GuiFunctions;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;

import org.openqa.selenium.Alert;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;


import org.openqa.selenium.interactions.Actions;

import YellowPages.*;
import ASAP.CommonFunctions;
import ASAP.Reporting;



public class GuiYellowPages {
	
	//Initialize
	private WebDriver driver;
	private HashMap <String, String> Dictionary = new HashMap<String, String>();
	private HashMap <String, String> Environment = new HashMap<String, String>();	
	private Reporting Reporter;
	CommonFunctions objCommon;
	
	//Constructor
	public GuiYellowPages(WebDriver webDriver,HashMap <String, String> Dict, HashMap <String, String> Env, Reporting Report)
	{
		driver = webDriver;
		Dictionary = Dict;
		Environment = Env;
		Reporter = Report;
		objCommon = new CommonFunctions(driver, Reporter);
	}
	
	//*****************************************************************************************
    //*	Name		    : fGuiFacebookSecure
    //*	Description	    : Function to check Secure login to Facebook
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************	
	public boolean fGuiFacebookSecure(){
		try
		{
			//Create object of Home Page class
			HomePage homePage = new HomePage(driver, Reporter, Dictionary, Environment);
			
			//Call  the function to launch the application URL
			if(homePage.openApplication() == false) return false;
						
			//Maximize the browser
			driver.manage().window().maximize();
			
			//Verify that the URL is not secure
			//isURLSecure returns true if the URL is secure(https)
			if(homePage.isURLSecure()) {
				Reporter.fnWriteToHtmlOutput("http/https", "URL should not be secure", "URL is found to be secure", "Fail");
				return false;
			}
			
			//Call the function to navigate to the facebook login page
			FBLoginPage  fbLoginPage = homePage.navigateToFBLogin();
			if (fbLoginPage == null) {
				Reporter.fnWriteToHtmlOutput("Navigate to the Facebook Login Page", "Facebook Login Page should be displayed", "Facebook Login Page could not be displayed", "Fail");
				return false;
			}
			
			//Call the function to login to facebook
			homePage = fbLoginPage.loginToFB();
			if(homePage == null) {
				Reporter.fnWriteToHtmlOutput("Login to facebook", "Should be logged in to facebook", "Could not login to facebook", "Fail");
				return false;
			}
			
			//Verify that the URL is secure
			//isURLSecure returns true if the URL is secure(https)
			if(homePage.isURLSecure() == false) {
				Reporter.fnWriteToHtmlOutput("http/https", "URL should be secure", "URL is found to be not secure", "Fail");
				return false;
			}
			
			//Verify that system is logged in with a facebook user
			if(homePage.getFBUserName() == null) return false;
			
			//Change the protocol identifier to http
			if(homePage.changeProtocolIdTo("http") == false) return false;
			
			//Delay to let the system redirect to secure URL
			objCommon.delayBy(2000);
			
			//Verify that the URL is still secure
			//isURLSecure returns true if the URL is secure(https)
			if(homePage.isURLSecure() == false) {
				Reporter.fnWriteToHtmlOutput("http/https", "URL should be secure", "URL is found to be not secure", "Fail");
				return false;
			}
			
			//Logout from Facebook
			if(homePage.logoutFromFB() == false) {
				Reporter.fnWriteToHtmlOutput("FB Logout", "Should Logout from Facebook", "Could not Log out from facebook", "Fail");
				return false;
			}
			
			//Verify that the URL is not secure
			//isURLSecure returns true if the URL is secure(https)
			if(homePage.isURLSecure()) {
				Reporter.fnWriteToHtmlOutput("http/https", "URL should not be secure", "URL is found to be secure", "Fail");
				return false;
			}
			
			return true;
		}
		catch (Exception e)
		{
			System.out.print("Exception is " + e);
			return false;
		}
		
	}
		
	//*****************************************************************************************
    //*	Name		    : fGuiCheckArticles
    //*	Description	    : Gets no of articles displayed
    //*	Author		    : Diksha Mirajkar
    //*	Input Params	: None
    //*	Return Values	: boolean 
    //*****************************************************************************************
	public boolean fGuiCheckArticles(){
		//Launch URL
	    LaunchApplication launchApplication = new LaunchApplication(driver, Reporter, Dictionary, Environment);
  
	    HomePage home_page = new HomePage(driver, Reporter, Dictionary, Environment);
	    
	    //Launch Articles URL
	    if(launchApplication.openApplication()==false){
	    	Reporter.fnWriteToHtmlOutput("Navigate to specified URL", "URL: " + Dictionary.get("ARTICLES_URL"), "Navigation to URL failed" , "Fail");
	    	return false;
	    }
	    
	    //Call function to get no of articles
	    if(home_page.checkNoOfArticles()==false){
	    	return false;
	    }
		
	    Reporter.fnWriteToHtmlOutput("Check Articles", "Check if the articles are displayed", "Articles present" , "Pass");
		return true;
	}

	//*****************************************************************************************
	//*	Name		    : fGuiCheckArticleSummaryPage
	//*	Description	    : Gets no of articles displayed
	//*	Author		    : Diksha Mirajkar
	//*	Input Params	: None
	//*	Return Values	: boolean 
	//*****************************************************************************************
	public boolean fGuiCheckArticleSummaryPage(){
		//Launch URL
		LaunchApplication launchApplication = new LaunchApplication(driver, Reporter, Dictionary, Environment);

		ArticlesPage articles_page = new ArticlesPage(driver, Reporter, Dictionary, Environment);

		//Launch Articles URL
		if(launchApplication.openApplication()==false){
			Reporter.fnWriteToHtmlOutput("Navigate to specified URL", "URL: " + Dictionary.get("URL"), "Navigation to URL failed" , "Fail");
			return false;
		}

		//Call function to get no of articles
		if(articles_page.fGuiCheckArticles()==false){
			return false;
		}


		Reporter.fnWriteToHtmlOutput("Check Articles", "Check if the articles are displayed", "Articles present" , "Pass");
		return true;
	}

	//*****************************************************************************************
	//*	Name		    : fGuiCheckCategory
	//*	Description	    : Checks the category 
	//*	Author		    : Diksha Mirajkar
	//*	Input Params	: None
	//*	Return Values	: boolean 
	//*****************************************************************************************
	public boolean fGuiCheckCategory(){
		//Launch URL
		LaunchApplication launchApplication = new LaunchApplication(driver, Reporter, Dictionary, Environment);

		//Launch Articles URL
		if(launchApplication.openApplication()==false){
			Reporter.fnWriteToHtmlOutput("Navigate to specified URL", "URL: " + Dictionary.get("URL"), "Navigation to URL failed" , "Fail");
			return false;
		}
		CategoryPage category_page = new CategoryPage(driver, Reporter, Dictionary, Environment);

		if(category_page.checkCategoryDetails()==false){
			Reporter.fnWriteToHtmlOutput("Checking category details", "Category details : footer link, featured bussiness section and related articles section", "Details not present","Fail");
			return false;
		}

		return true;

	}

	//*****************************************************************************************
    //*	Name		    : fGuiDimmiBooking
    //*	Description	    : Function to check Dimmi Booking without actually completing it(to check server connectivity)
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************	
	public boolean fGuiDimmiBooking(){
		try
		{
			//Create object of Home Page class
			HomePage homePage = new HomePage(driver, Reporter, Dictionary, Environment);
			
			//Call  the function to launch the application URL
			if(homePage.openApplication() == false) return false;
						
			//Maximize the browser
			driver.manage().window().maximize();
			
			//Perform a search for what and where Business
			SearchResultPage searchResultPage = homePage.searchBusiness();
			if(searchResultPage == null) {
				Reporter.fnWriteToHtmlOutput("Search Business", "Search should be performed", "Search could not be performed", "Pass");
				return false;
			}
			
			//Check if the current URL is the specified SRP URL
			if(driver.getCurrentUrl().contains(Dictionary.get("VALIDATE_SRP_URL")) == false) {
				Reporter.fnWriteToHtmlOutput("Validate URL", "Current URL should match with the Expected SRP URL: " + Dictionary.get("VALIDATE_SRP_URL"), "Actual: " + driver.getCurrentUrl(), "Fail");
				return false;
			}
			Reporter.fnWriteToHtmlOutput("Validate URL", "Current URL should match with the Expected SRP URL: " + Dictionary.get("VALIDATE_SRP_URL"), "Matching Actual: " + driver.getCurrentUrl(), "Done");
			
			//Verify that the listing with the name "Punch Lane Wine Bar Restaurant" is displayed
			if(objCommon.fGuiIsDisplayed("linktext:=" + Dictionary.get("SEARCH_WHAT")) == false) return false;
			
			//Click on 1st Book A Table link and finish its Step 1
			if(searchResultPage.bookATableStep1() == false) return false;
			
			//Close the Book A Table Dialog Box
			if(searchResultPage.closeBookATableDialog() == false) return false;
			
			//Report Success
			Reporter.fnWriteToHtmlOutput("Dimmi Booking", "Dimmi Booking Book A Table Step 1", "Dimmi Booking Book A Table Step 1 Completed Successfully", "Pass");
			return true;
		}
		catch (Exception e)
		{
			System.out.print("Exception is " + e);
			return false;
		}
	}
	
	//*****************************************************************************************
    //*	Name		    : fGuiWriteAReview
    //*	Description	    : Function to Write A Review
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: Boolean - Depending on the success
    //*****************************************************************************************	
	public boolean fGuiWriteAReview(){
		try
		{
			//Create object of Home Page class
			HomePage homePage = new HomePage(driver, Reporter, Dictionary, Environment);
			
			//Delete facebook cookies
			driver.get("http://facebook.com");
			driver.manage().deleteAllCookies();
			
			//Call  the function to launch the application URL
			if(homePage.openApplication() == false) return false;
			
			//Maximize the browser
			driver.manage().window().maximize();
			
			//Call the function to navigate to the facebook login page
			FBLoginPage  fbLoginPage = homePage.navigateToFBLogin();
			if (fbLoginPage == null) {
				Reporter.fnWriteToHtmlOutput("Navigate to the Facebook Login Page", "Facebook Login Page should be displayed", "Facebook Login Page could not be displayed", "Fail");
				return false;
			}
			
			//Call the function to login to facebook
			homePage = fbLoginPage.loginToFB();
			if(homePage == null) {
				Reporter.fnWriteToHtmlOutput("Login to facebook", "Should be logged in to facebook", "Could not login to facebook", "Fail");
				return false;
			}
			
			//Perform a search for what and where Business
			SearchResultPage searchResultPage = homePage.searchBusiness();
			if(searchResultPage == null) {
				Reporter.fnWriteToHtmlOutput("Search Business", "Search should be performed", "Search could not be performed", "Pass");
				return false;
			}
			
			//Click on the 1st listing
			ReviewsPage reviewsPage = searchResultPage.clickListing();
			if(reviewsPage == null){
				Reporter.fnWriteToHtmlOutput("Click Listing", "Click on one of the Listings", "Could not click on the Listing", "Fail");
				return false;
			}
			
			//Write a review, rate and cancel it
			if(reviewsPage.writeAReviewAndRate() == false) return false;
			
			//Report Success
			Reporter.fnWriteToHtmlOutput("Write A Review", "Write Review and then Cancel it", "Review Written and cancelled Successfully", "Pass");
			return true;
		}
		catch (Exception e)
		{
			System.out.print("Exception is " + e);
			return false;
		}
	}
	
	//*****************************************************************************************
	//*	Name		    : fGuiValidateReview
	//*	Description	    : Checks the review
	//*	Author		    : Diksha Mirajkar
	//*	Input Params	: None
	//*	Return Values	: boolean 
	//*****************************************************************************************	
	public boolean fGuiValidateReview(){
		//Launch URL
		LaunchApplication launchApplication = new LaunchApplication(driver, Reporter, Dictionary, Environment);

		//Launch Articles URL
		if(launchApplication.openApplication()==false){
			Reporter.fnWriteToHtmlOutput("Navigate to specified URL", "URL: " + Dictionary.get("URL"), "Navigation to URL failed" , "Fail");
			return false;
		}

		//Create object of Home Page class
		HomePage homePage = new HomePage(driver, Reporter, Dictionary, Environment);

		//Maximize the browser
		driver.manage().window().maximize();

		objCommon.delayBy(100);
		//Perform a search for what and where Business
		SearchResultPage searchResultPage = homePage.searchBusiness();
		if(searchResultPage == null) {
			Reporter.fnWriteToHtmlOutput("Search Business", "Search should be performed", "Search could not be performed", "Pass");
			return false;
		}

		objCommon.delayBy(100);
		//Create object of Review Page class
		ReviewsPage review_Page = new ReviewsPage(driver, Reporter, Dictionary, Environment);

		if(review_Page.checkReviewSummary()==false){
			return false;
		}

		return true;
	}

	//*****************************************************************************************
	//*	Name		    : fGuiValidateSuburbAndCategoryFilters
	//*	Description	    : Function to validate Suburb and Category Filter
	//*	Author		    : Anil Agarwal
	//*	Input Params	: None
	//*	Return Values	: Boolean - Depending on the success
	//*****************************************************************************************	
	public boolean fGuiValidateSuburbAndCategoryFilters(){
		try
		{
			//Create object of Home Page class
			HomePage homePage = new HomePage(driver, Reporter, Dictionary, Environment);

			//Call  the function to launch the application URL
			if(homePage.openApplication() == false) return false;

			//Maximize the browser
			driver.manage().window().maximize();

			//Perform a search for what and where Business
			SearchResultPage searchResultPage = homePage.searchBusiness();
			if(searchResultPage == null) {
				Reporter.fnWriteToHtmlOutput("Search Business", "Search should be performed", "Search could not be performed", "Pass");
				return false;
			}

			//Call the function to validate Suburb Filter
			if (searchResultPage.checkSuburbFilters()==false) return false;

			//Call the function to validate Category Filter
			if (searchResultPage.checkCategoryFilters()==false) return false;

			//return true
			Reporter.fnWriteToHtmlOutput("Validate Suburb and Category Filters", "Suburb and Category Filters should be validated", "Suburb and Category Filters validated Successfully", "Pass");
			return true;
		}catch (Exception e)
		{
			System.out.print("Exception is " + e);
			return false;
		}
	}

	//*****************************************************************************************
	//*	Name		    : fGuiSeachListingsWithFullAddress
	//*	Description	    : Searches for listings with full address 
	//*	Author		    : Surbhi Shivhare
	//*	Input Params	: None
	//*	Return Values	: boolean 
	//*****************************************************************************************
	public boolean fGuiSeachListingsWithFullAddress(){
		try
		{
			//Create object of Home Page class
			HomePage homePage = new HomePage(driver, Reporter, Dictionary, Environment);

			//Call  the function to launch the application URL
			if(homePage.openApplication() == false) return false;

			//Maximize the browser
			driver.manage().window().maximize();

			//Perform a search for what and where Business
			SearchResultPage searchResultPage = homePage.searchBusiness();
			if(searchResultPage == null) {
				Reporter.fnWriteToHtmlOutput("Search Business", "Search should be performed", "Search could not be performed", "Pass");
				return false;
			}

			//Verify that the listing with the name "Punch Lane Wine Bar Restaurant" is displayed
			if(objCommon.fGuiIsDisplayed("xpath:=//a[contains(text(), '" + Dictionary.get("SEARCH_WHAT") + "')]") == false) return false;

			//Click on the 1st POI
			if(searchResultPage.clickPOI() == false) {
				Reporter.fnWriteToHtmlOutput("Click POI", "POI in the 1st Listing should be Clicked", "POI Not Clicked", "Fail");
				return false;
			}

			//Hover over the ems marker(POI)
			if(searchResultPage.hoverPOIMapView() == false) return false;

			//Report Success
			Reporter.fnWriteToHtmlOutput("With Full Address", "Search Listings with Full Address", "Test Case Completed Successfully", "Pass");
			return true;
		}
		catch (Exception e)
		{
			System.out.print("Exception is " + e);
			return false;
		}
	}	

	//*****************************************************************************************
	//*	Name		    : fGuiListingIGenDetailsVerification
	//*	Description	    : Validates the details of the Business/Listing from IGen 
	//*	Author		    : Surbhi Shivhare
	//*	Input Params	: None
	//*	Return Values	: boolean 
	//*****************************************************************************************
	public boolean fGuiListingIGenDetailsVerification(){
		try
		{
			//Create object of Home Page class
			HomePage homePage = new HomePage(driver, Reporter, Dictionary, Environment);
			
			//Call  the function to launch the application URL
			if(homePage.openApplication() == false) return false;
			
			//Maximize the browser
			driver.manage().window().maximize();
						
			//Perform a search for what and where Business
			SearchResultPage searchResultPage = homePage.searchBusiness();
			if(searchResultPage == null) {
				Reporter.fnWriteToHtmlOutput("Search Business", "Search should be performed", "Search could not be performed", "Pass");
				return false;
			}
			
			//Click on the 1st listing
			ReviewsPage reviewsPage = searchResultPage.clickListing();
			if(reviewsPage == null){
				Reporter.fnWriteToHtmlOutput("Click Listing", "Click on one of the Listings", "Could not click on the Listing", "Fail");
				return false;
			}
			
			
			objCommon.delayBy(100);
			if(reviewsPage.validateIgenDetails()==false){
				Reporter.fnWriteToHtmlOutput("Validating IGen data in Yellow Pages","IGen data should be validated in Yellow pages", "Could not validate IGen data in Yellow Pages", "Fail");
				return false;
			}
			
			
			return true;
		}
		catch (Exception e)
		{
			System.out.print("Exception is " + e);
			return false;
		}
	
		
	}
	
}

