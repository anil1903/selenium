package YellowPages;


import java.util.List;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ASAP.CommonFunctions;
import ASAP.Reporting;

public class ReviewsPage {
	
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
	public ReviewsPage(WebDriver driverTemp, Reporting ReporterTemp, HashMap<String, String> DictionaryTemp,  HashMap<String, String> EnvironmentTemp)
	{
		Reporter = ReporterTemp;
		driver = driverTemp;
		Dictionary = DictionaryTemp;
		Environment = EnvironmentTemp;
		objCommon = new CommonFunctions(driver, Reporter);
	}
	
	//Define Objects
	//Buttons
	private String btnWriteAReview = "xpath:=//*[contains(@class, 'write-a-review button block secondary button-nowrap button-textual text-and-image')]";
	private String btnReviewSubmit = "xpath:=//*[contains(@class, 'button button-confirm submit-review button-submit')]";
	private String btnReviewCancel = "xpath:=//*[contains(@class, 'button button-cancel cancel-review')]";
	private String btnReviewCancelYes = "xpath:=//*[contains(@class, 'button button-confirm') and text()='Yes']";
	

	private String webElmntYellowReview = "xpath:=//div[@class='star-rating yellow-pages']";
	private String webElmntYelpReview = "xpath:=//div[@class='star-rating yelp']";
	private String webElmntListingNm = "xpath:=//a[@class='listing-name']";
	private String webElmntShowUserReview = "xpath:=//div[@class='closed']";
	private String webElmntReview = "xpath:=//div[@class='listing-review-summary horizontal']";
	
	
	private String webElmntYellowSummaryReview = "xpath:=//li[contains(@class,'review') and contains(@class,'yellow-pages')]";
	private String webElmntYelpSummaryReview = "xpath:=//li[contains(@class,'review') and contains(@class,'yelp')]";
	private List <WebElement> lst;

	//Edit boxes
	private String edtReviewText = "name:=reviewText";
	
	//Web Elements
	private String webElmtRatingStar3 = "xpath:=//div[@data-score='3']";
	
	
	
	private String webElmntAboutUs = "xpath:=//div[@class='paragraph']";
	private String webElmntStaff = "xpath:=//dd[@class='number-of-employees']";
	private String webElmntTrades = "xpath:=//dd[@class='trading-aliases']";
	private String webElmntLegalId = "xpath:=//dd[@class='legal-id']";
	private String webElmtTextDesc = "xpath:=//*[@class='listing-short-description']";
	private String webElmtAddress = "xpath:=//*[@class='listing-address mappable-address']";
	private String imgBrand = "xpath:=//div[@class='panel']/img[@class='brand-package-image']";
	private String imgLogo = "xpath:=//img[@class='listing-logo standard-logo large-logo']";
	
	//*****************************************************************************************
	//*	Name		    : checkReviewSummary
	//*	Description	    : Checks the category 
	//*	Author		    : Diksha Mirajkar
	//*	Input Params	: None
	//*	Return Values	: boolean 
	//*****************************************************************************************
	public boolean checkReviewSummary(){
		
		//check if the yellow review summary is present on the search result page
		lst =  objCommon.getObjects(webElmntYellowReview);
		if(lst.size()==0){
			Reporter.fnWriteToHtmlOutput("Checking yellow review summary","yellow review summary should be present","yellow review summaryis not present","Fail");
			return false;
		}

		else{
			Reporter.fnWriteToHtmlOutput("Checking yellow page  review on the search result page","yellow page  review should be present","yellow page review is present on the search result page","Pass");
			
		}
		
		objCommon.delayBy(100);
		//check if the yelp review summary is present on the search result page	
		List <WebElement> lst1 =  objCommon.getObjects(webElmntYelpReview);
		if(lst.size()==0){
			Reporter.fnWriteToHtmlOutput("Checking yelp review summary","yelp review summary should be present","yelp review summary is not present","Fail");
			return false;
		}
		else{
			Reporter.fnWriteToHtmlOutput("Checking yelp review on the search result page","yelp page review should be present","yelp page review is present on the search result page","Pass");
			
		}
		
		List <WebElement> lst2 = objCommon.getObjects(webElmntListingNm);

		//open first article
		WebElement title = lst2.get(0);
		if(objCommon.fGuiClick(title)==false){
			Reporter.fnWriteToHtmlOutput("Click on the first review","First review should be clicked","failed to click on the first review","Fail");
			return false;
		}

		//Click on show user review
		if(objCommon.fGuiClick(webElmntShowUserReview)==false){
			Reporter.fnWriteToHtmlOutput("Clicking yelp show user review","show user review should be clicked","failed to click on show user review","Fail");	
			return false;
		}
		
		String webElmntReviewYellowPages = "//div[@class='new-tab' and text()='Yellow Pages']";
		String webElmntReviewYelp = "//div[@class='new-tab' and text()='Yelp']";
		
		//Get the object for the Yellow Pages Review tab
		WebDriverWait wait = new WebDriverWait(driver, 15000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(webElmntReviewYellowPages)));

	
		//Click on the yellow pages summary
		if(objCommon.fGuiClick("xpath:=" + webElmntReviewYellowPages)==false){
			return false;
		}
		
		//check if yellow page reviews are present on the yellow page tab
		List <WebElement> lst4 = objCommon.getObjects(webElmntYellowSummaryReview);
		if(lst4.size()==0){
			Reporter.fnWriteToHtmlOutput("Clicking yellow page review","Yellow Page review should be present","Yellow page review not present","Fail");	
			return false;	
		}else{
			Reporter.fnWriteToHtmlOutput("Validate Yellow page review","Yellow Page review should be present",lst4.size() +  " Yellow page reviews are present","Pass");
		}
		
		//Check if yelp page review are present on the yelp tab
		//Get the object for the Yellow Pages Review tab
		//WebDriverWait wait = new WebDriverWait(driver, 15000);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(webElmntReviewYelp)));

	
		//Click on the yellow pages summary
		if(objCommon.fGuiClick("xpath:=" + webElmntReviewYelp)==false){
			return false;
		}
		
		List <WebElement> lst5 = objCommon.getObjects(webElmntYelpSummaryReview);
		if(lst5.size()==0){
			Reporter.fnWriteToHtmlOutput("Clicking yelp page review","Yelp Page review should be present","Yelp page review not present","Fail");	
			return false;		
		}else{
			Reporter.fnWriteToHtmlOutput("Validate Yelp review","Yelp review should be present",lst5.size() +  " Yelp reviews are present","Pass");
		}
		
		
		Reporter.fnWriteToHtmlOutput("Check review summary","Yellow page and Yepl review should be checked","Yellow page and Yelp review present","Pass");
		return true; 
	}
	
	//*****************************************************************************************
    //*	Name		    : writeAReviewAndRate
    //*	Description	    : Writes a review, rates with 3 star and cancels the review
    //*	Author		    : Surbhi Shivhare
    //*	Input Params	: None
    //*	Return Values	: boolean
    //*****************************************************************************************
	public boolean writeAReviewAndRate(){
		
		//Click on Write A Review button
		if(objCommon.fGuiClick(btnWriteAReview) == false) return false;
		
		//Enter text in the Review text area
		if(objCommon.fGuiEnterText(edtReviewText, "Automation Test") == false) return false;
		
		//Rate with 3 stars
		if(objCommon.fGuiClick(webElmtRatingStar3) == false) return false;
		
		//Take a snapshot of entered review and rating
		Reporter.fnWriteToHtmlOutput("Review and Rate", "Write a review and rate", "Review written and rated successfully", "Pass");
		
		//Click on the Cancel Review button
		if(objCommon.fGuiClick(btnReviewCancel) == false) return false;
		
		//Click on Yes Button on the Confirm Pop up
		if(objCommon.fGuiClick(btnReviewCancelYes) == false) return false;
				
		//Report success
		Reporter.fnWriteToHtmlOutput("Write A Review and Rate", "Write, Rate and Cancel", "Wrote, Rated and Cancelled Successfully", "Pass");

		return true;
	}
	
	//*****************************************************************************************
	//*	Name		    : validateIgenDetails
	//*	Description	    : Checks the IGen details in YP
	//*	Author		    : Diksha Mirajkar
	//*	Input Params	: None
	//*	Return Values	: boolean 
	//*****************************************************************************************
	public boolean validateIgenDetails(){

		Boolean flag = true;
		List <WebElement> listing = objCommon.getObjects(webElmntAboutUs);
		if(listing.size()==0){
			Reporter.fnWriteToHtmlOutput("Checking About us","About Us details should be present","About Us details are not present","Fail");
			return false;
		}
		//Fetch Enhanced text
		WebElement EnhancedTxt = listing.get(0);
		String enhTxt = objCommon.fGuiGetText(EnhancedTxt);
		
		//Check Enhanced text
		if(enhTxt.equalsIgnoreCase(Dictionary.get("ENHANCED_TEXT"))==false){
			Reporter.fnWriteToHtmlOutput("Checking Enhanced text", "Enhanced text should match with IGEN", "Actual:" + enhTxt + " Expected:" + Dictionary.get("ENHANCED_TEXT"),"Fail");
			flag = false;
		}
		
		//Fetch Business Description
		WebElement BusinessDesc = listing.get(1);
		String desc = objCommon.fGuiGetText(BusinessDesc);
		
		//check Business Description
		if(desc.equalsIgnoreCase(Dictionary.get("BUSINESS_DESCRIPTION"))==false){
			Reporter.fnWriteToHtmlOutput("Checking Business Description","Business Description should match with IGEN","Actual:" + desc + " Expected:" + Dictionary.get("BUSINESS_DESCRIPTION"),"Fail");
			flag = false;
		}
		
		//Check staff
		String staff = objCommon.fGuiGetText(webElmntStaff);
		
		if(staff.equalsIgnoreCase(Dictionary.get("STAFF"))==false){
			Reporter.fnWriteToHtmlOutput("Checking Staff","Staff should match with IGEN","Actual:" + staff + " Expected:" + Dictionary.get("STAFF"),"Fail");
			flag = false;
			
		}
		
		//Check trades
		String trades = objCommon.fGuiGetText(webElmntTrades);
		
		if(trades.equalsIgnoreCase(Dictionary.get("TRADES"))==false){
			Reporter.fnWriteToHtmlOutput("Checking Trades","Trades should match with IGEN","Actual:" + trades + " Expected:" + Dictionary.get("TRADES"),"Fail");
			flag = false;
		}
		
		//Check legal id
		String legalId = objCommon.fGuiGetText(webElmntLegalId);
		
		if(legalId.equalsIgnoreCase(Dictionary.get("LEGAL_ID"))==false){
			Reporter.fnWriteToHtmlOutput("Checking Legal Id","Legal Id should match with IGEN","Actual:" + legalId + " Expected:" + Dictionary.get("LEGAL_ID"),"Fail");
			flag = false;
		}
		
		//Check for Text Descriptor
		String txtDesc = objCommon.fGuiGetText(webElmtTextDesc);
		if(txtDesc.equalsIgnoreCase(Dictionary.get("TEXT_DESCRIPTOR")) == false) {
			Reporter.fnWriteToHtmlOutput("Checking Text Descriptor","Text Descriptor should match with IGEN","Actual:" + txtDesc + " Expected:" + Dictionary.get("TEXT_DESCRIPTOR"),"Fail");
			flag = false;
		}
		
		//Check for Address
		String address = objCommon.fGuiGetText(webElmtAddress);
		if(address.contains(Dictionary.get("HOUSE_NO")) && address.contains(Dictionary.get("STREET_NAME")) == false){
			String expAddress = Dictionary.get("STREET_NAME") + Dictionary.get("HOUSE_NO");
			Reporter.fnWriteToHtmlOutput("Checking Address","Address should match with IGEN","Actual:" + address + " Expected:" + expAddress ,"Fail");
			flag = false;
		}
		
		//Check for Brand Image
		//Get the src Attribute of the Brand Image
		String srcBrandImg = objCommon.getObject(imgBrand).getAttribute("src");
		//Get the Name from the src Attribute
		String brandImgName = srcBrandImg.substring(srcBrandImg.lastIndexOf('/') + 1);
		//Validate its presence in the path provided in Dictionary
		if(Dictionary.get("BRAND_IMAGE").contains(brandImgName) == false){
			Reporter.fnWriteToHtmlOutput("Checking Brand Image","Brand Image should match with IGEN","Brand Image did not match","Fail");
			flag = false;
		}
		
		//Check for Logo
		//Get the src Attribute of the Logo Image
		String srcLogoImg = objCommon.getObject(imgLogo).getAttribute("src");
		//Get the Name from the src Attribute
		String logoImgName = srcLogoImg.substring(srcBrandImg.lastIndexOf('/') + 1);
		//Validate its presence in the path provided in Dictionary
		if(Dictionary.get("LOGO").contains(logoImgName) == false){
			Reporter.fnWriteToHtmlOutput("Checking Logo Image","Logo Image should match with IGEN","Logo Image did not match","Fail");
			flag = false;
		}
		if(flag == false){
			Reporter.fnWriteToHtmlOutput("Validating Igen Data ","Igen Business Details should be validated in Yellow Pages","Details Mismatch","Fail");		
			return false;
		}
		Reporter.fnWriteToHtmlOutput("Validating Igen Data ","Igen Business Details should be validated in Yellow Pages","Details Correctly Matched","Pass");		
		return true;

	}
	
}
