package YellowPages;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import ASAP.CommonFunctions;
import ASAP.Reporting;

public class SearchResultPage {
	
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
	public SearchResultPage(WebDriver driverTemp, Reporting ReporterTemp, HashMap<String, String> DictionaryTemp,  HashMap<String, String> EnvironmentTemp)
	{
		Reporter = ReporterTemp;
		driver = driverTemp;
		Dictionary = DictionaryTemp;
		Environment = EnvironmentTemp;
		objCommon = new CommonFunctions(driver, Reporter);
	}
	
	//Define Objects
	//Links
	private String lnkBookATable = "xpath:=//a[text()='Book a Table']";
	private String lnkListings = "classname:=listing-name";
	
	//Buttons
	private String btnDimmiStep1Next = "xpath:=//*[@class='form-page page_0']/following-sibling::div//*[text()='Next']";
	private String btnBookATableDialogClose = "xpath:=//*[@class='lightbox-close-button glyph icon-cross']";
	
	//Drop Down
	private String drpDownDimmiMonthYear = "id:=dimmi-month-year";
	private String drpDownDimmiDay = "id:=dimmi-day";
	private String drpDownDimmiNoOfPeople = "id:=dimmi-num-people";
	private String drpDownDimmiService = "id:=dimmi-service";
	private String drpDownDimmiTime = "id:=dimmi-time";
	private String drpDownDimmiInitial = "id:=dimmi-initial";
	private String chkBoxSuburbFilter = "name:=suburb";
	private String chkBoxRefinedCategoryFilter = "xpath:=//label[contains(@for,'refinedCategory')]/span[3]";
	
	//Web Elements
	private String webElmt1stPOI = "xpath:=//*[@class='poi' and text()='1']";
	private String webElmtSelectedPOI = "xpath:=//div[@class='selected emsMarker']";
	
	//*****************************************************************************************
    //*	Name		    : bookATableStep1
    //*	Description	    : Performs the 1st Step of Books A Table(doesn't enters details)
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: boolean
    //*****************************************************************************************
	public boolean bookATableStep1(){
		
		//Click on Book a table link for the 1st listing
		List<WebElement> lst = objCommon.getObjects(lnkBookATable);
		if(lst.isEmpty()) {
			Reporter.fnWriteToHtmlOutput("Click on Book A Table", "Book a Table link should be displayed", "No Book a Table link was found", "Fail");
			return false;
		}
		else{
			if(objCommon.fGuiClick(lst.get(0)) == false) return false;
		}
		
		//Select Month and Year on the Book A Table Dialog, format eg: 'January, 14' (Month, YY)
		if(objCommon.fGuiSelectOptionFromList(drpDownDimmiMonthYear, Dictionary.get("DIMMI_MONTH_YEAR")) == false) return false;
		
		//Select Day on the Book A Table Dialog, format eg: '2' (d)
		if(objCommon.fGuiSelectOptionFromList(drpDownDimmiDay, Dictionary.get("DIMMI_DAY")) == false) return false;

		//Delay to let other fields get updated
		objCommon.delayBy(1000);
		
		//Select Number of People on the Book A Table Dialog, format eg: '4' (n)
		if(objCommon.fGuiSelectOptionFromList(drpDownDimmiNoOfPeople, Dictionary.get("DIMMI_NO_OF_PEOPLE")) == false) return false;

		//Select Service on the Book A Table Dialog, format eg: 'Lunch' (Lunch/Dinner)
		if(objCommon.fGuiSelectOptionFromList(drpDownDimmiService, Dictionary.get("DIMMI_SERVICE")) == false) return false;

		//Select Time on the Book A Table Dialog, format eg: '3:00 PM' (H:MM AM/PM) 
		//Timings restricted to: Lunch(12:00, 12:30, 1:00, 1:30, 2:00, 2:30) same with Dinner(5:30 to 9:30)
		if(objCommon.fGuiSelectOptionFromList(drpDownDimmiTime, Dictionary.get("DIMMI_TIME")) == false) return false;

		//Click on Next Button
		if(objCommon.fGuiClick(btnDimmiStep1Next) == false) return false;

		//Validate the next step - with initials is displayed
		if(objCommon.fGuiIsDisplayed(drpDownDimmiInitial) == false){
			Reporter.fnWriteToHtmlOutput("Next Step", "Validate Next Step - Having initials dropdown is displayed", "Next Step - Having initials dropdown is not displayed", "Fail");
			return false;
		}
		
		//Report Success
		Reporter.fnWriteToHtmlOutput("Proceed to Step 2 of Book A Table", "Step 1 of Book A Table should be completed", "Step 1 of Book A Table is completed Successfully", "Pass");
		return true;
	}
	
	//*****************************************************************************************
    //*	Name		    : closeBookATableDialog
    //*	Description	    : Performs the 1st Step of Books A Table(doesn't enters details)
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: boolean
    //*****************************************************************************************
	public boolean closeBookATableDialog(){
		
		//Click on the Close x button of the Book A Table Dialog
		if(objCommon.fGuiClick(btnBookATableDialogClose) == false) return false;
		
		//Report Closure of the Dialog
		Reporter.fnWriteToHtmlOutput("Close Button", "Book A Table Dialog Close button should be clicked", "Close button clicked successfully", "Pass");
		return true;
	}

	//*****************************************************************************************
    //*	Name		    : checkSuburbFilters
    //*	Description	    : Checks if the suburb filter contains the expected location
    //*	Author		    : Anil Agarwal
    //*	Input Params	: None
    //*	Return Values	: boolean
    //*****************************************************************************************
	public boolean checkSuburbFilters(){
		
		//Get a list of all suburb filters
		List<WebElement> lst = objCommon.getObjects(chkBoxSuburbFilter);
		if(lst == null) {
			Reporter.fnWriteToHtmlOutput("Get List of Suburb Filter", "List of Suburb should be returned", "No objects found matching the decription " + chkBoxSuburbFilter, "Fail");
			return false;
		}
		String strSuburbFilter = "";
		String strTemp = "";
		//Loop through the list to get list of all Suburbs
		for (int i = 0; i < lst.size(); i++){
			strTemp = lst.get(i).getAttribute("value");
			strSuburbFilter = strSuburbFilter + ";" + strTemp; 
		}
		//Get the final string
		strSuburbFilter = strSuburbFilter.substring(1);
		
		//Declare an int variable
		int j = 0;
		String strSuburb = "";
		boolean bErrorFlag = false;
		//Loop through all the Suburbs that needs to be checked
		while (Dictionary.containsKey("SUBURB_FILTER_" + j) && Dictionary.get("SUBURB_FILTER_" + j).isEmpty()==false){
			strSuburb = Dictionary.get("SUBURB_FILTER_" + j);
			
			//Check if the suburb value is present in the application
			if (strSuburbFilter.contains(strSuburb)==false){
				Reporter.fnWriteToHtmlOutput("Check Suburb Filter", "Suburb Filter " + strSuburb + " should be present in the application", "Suburb Filter " + strSuburb + " is not present in the application", "Fail");
				bErrorFlag = true;
			}else{
				Reporter.fnWriteToHtmlOutput("Check Suburb Filter", "Suburb Filter " + strSuburb + " should be present in the application", "Suburb Filter " + strSuburb + " is present in the application", "Done");
			}
			j++;
		}
		
		//Validate if bErrorFlag is true
		if (bErrorFlag){
			Reporter.fnWriteToHtmlOutput("Check Suburb Filter", "All Suburb Filters should be present in the application", "All Suburb Filter are not present in the application", "Fail");
			return false;	
		}else{
			Reporter.fnWriteToHtmlOutput("Check Suburb Filter", "All Suburb Filters should be present in the application", "All Suburb Filter are present in the application", "Pass");
			return true;				
		}
		
	}

	//*****************************************************************************************
    //*	Name		    : checkCategoryFilters
    //*	Description	    : Checks if the Category filter contains the expected location
    //*	Author		    : Anil Agarwal
    //*	Input Params	: None
    //*	Return Values	: boolean
    //*****************************************************************************************
	public boolean checkCategoryFilters(){
		
		//Get a list of all Category filters
		List<WebElement> lst = objCommon.getObjects(chkBoxRefinedCategoryFilter);
		if(lst == null) {
			Reporter.fnWriteToHtmlOutput("Get List of Category Filter", "List of Category should be returned", "No objects found matching the decription " + chkBoxRefinedCategoryFilter, "Fail");
			return false;
		}
		String strCategoryFilter = "";
		String strTemp = "";
		//Loop through the list to get list of all Category
		for (int i = 0; i < lst.size(); i++){
			//strTemp = objCommon.fGuiGetText(lst.get(i));
			strTemp = lst.get(i).getAttribute("title");
			strCategoryFilter = strCategoryFilter + ";" + strTemp; 
		}
		//Get the final string
		strCategoryFilter = strCategoryFilter.substring(1);
		
		//Declare an int variable
		int j = 0;
		String strCategory = "";
		boolean bErrorFlag = false;
		//Loop through all the Category that needs to be checked
		while (Dictionary.containsKey("CATEGORY_FILTER_" + j) && Dictionary.get("CATEGORY_FILTER_" + j).isEmpty()==false){
			strCategory = Dictionary.get("CATEGORY_FILTER_" + j);
			
			//Check if the Category value is present in the application
			if (strCategoryFilter.contains(strCategory)==false){
				Reporter.fnWriteToHtmlOutput("Check Category Filter", "Category Filter " + strCategory + " should be present in the application", "Category Filter " + strCategory + " is not present in the application", "Fail");
				bErrorFlag = true;
			}else{
				Reporter.fnWriteToHtmlOutput("Check Category Filter", "Category Filter " + strCategory + " should be present in the application", "Category Filter " + strCategory + " is present in the application", "Done");
			}
			j++;
		}
		
		//Validate if bErrorFlag is true
		if (bErrorFlag){
			Reporter.fnWriteToHtmlOutput("Check Category Filter", "All Category Filters should be present in the application", "All Category Filter are not present in the application", "Fail");
			return false;	
		}else{
			Reporter.fnWriteToHtmlOutput("Check Category Filter", "All Category Filters should be present in the application", "All Category Filter are present in the application", "Pass");
			return true;				
		}
		
	}


	//*****************************************************************************************
    //*	Name		    : clickListing
    //*	Description	    : Performs the 1st Step of Books A Table(doesn't enters details)
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: object of ReviewsPage
    //*****************************************************************************************
	public ReviewsPage clickListing(){
		
		//Click on 1st Listing if nothing(or DEFAULT) is specified in the Dictionary
		String listing = Dictionary.get("LISTING");
		if(listing == null || listing.equals("") || listing.equalsIgnoreCase("DEFAULT")){
			List<WebElement> lst = objCommon.getObjects(lnkListings);
			if(lst.isEmpty()) {
				Reporter.fnWriteToHtmlOutput("Listings", "Ateast one listing should be displayed", "No listings found", "Fail");
				return null;
			}
			if(objCommon.fGuiClick(lst.get(0)) == false) return null;
		}
		//Click on the Listing specified in Dictionary
		else {
			if(objCommon.fGuiClick("linktext:=" + Dictionary.get("LISTING")) == false) return null;
		}
		
		//Report success
		Reporter.fnWriteToHtmlOutput("Click Listing", "Listing should be Clicked", "Listing Clicked", "Pass");
		return new ReviewsPage(driver,Reporter,Dictionary, Environment);
	}
	
	//*****************************************************************************************
    //*	Name		    : clickPOI
    //*	Description	    : Clicks the POI in 1st listing
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: object of ReviewsPage
    //*****************************************************************************************
	public boolean clickPOI(){
		
		//Click on POI of 1st Listing
		if(objCommon.fGuiClick(webElmt1stPOI) == false) return false;
		
		//Report success
		Reporter.fnWriteToHtmlOutput("Click POI", "POI in the 1st Listing should be Clicked", "POI Clicked Successfully", "Pass");
		return true;
	}
	
	//*****************************************************************************************
    //*	Name		    : clickPOI
    //*	Description	    : Clicks the POI in 1st listing
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: object of ReviewsPage
    //*****************************************************************************************
	public boolean hoverPOIMapView(){
		
		//Validate map view is displayed
		if(driver.getCurrentUrl().contains("selectedViewMode=map") == false) return false;
		
		//Hover over the selected POI on the map
		Actions action = new Actions(driver);
		action.moveToElement(objCommon.getObject(webElmtSelectedPOI));
		action.perform();
		
		//Report success
		Reporter.fnWriteToHtmlOutput("Hover Selected POI", "Hover over the selected POI and take a snapshot", "Hovered Over the POI Successfully", "Pass");
		return true;
	}
	
}
