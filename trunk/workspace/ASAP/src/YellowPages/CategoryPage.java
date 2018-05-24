package YellowPages;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ASAP.CommonFunctions;
import ASAP.Reporting;

public class CategoryPage {
	
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
	public CategoryPage(WebDriver driverTemp, Reporting ReporterTemp, HashMap<String, String> DictionaryTemp,  HashMap<String, String> EnvironmentTemp)
	{
		Reporter = ReporterTemp;
		driver = driverTemp;
		Dictionary = DictionaryTemp;
		Environment = EnvironmentTemp;
		objCommon = new CommonFunctions(driver, Reporter);
	}
	
	//Define Objects
	private String webElmntRelatedCategory = "xpath:=//div[@class='related-groups']";
	private String webElmntBussinessSection = "xpath:=//a[@class='related-business-link']";
	private String webElmntRelatedArticles = "xpath:=//a[@class='related-article-link']";
	private List <WebElement> lst;
	
	
	//*****************************************************************************************
	//*	Name		    : checkCategoryDetails
	//*	Description	    : Checks the category details.
	//*	Author		    : Diksha Mirajkar
	//*	Input Params	: None
	//*	Return Values	: boolean 
	//*****************************************************************************************
	public boolean checkCategoryDetails(){
		//Check if the related category footer link is present
		lst = objCommon.getObjects(webElmntRelatedCategory);
		if(lst.size()==0){
			Reporter.fnWriteToHtmlOutput("Checking related category footer link","Related category footer link should be present","Related category footer link is not present","Fail");
			return false;
		}

		//check if the featured business section is present
		List<WebElement> lst1 = objCommon.getObjects(webElmntBussinessSection);
		if(lst1.size()==0){
			Reporter.fnWriteToHtmlOutput("Checking featured bussiness section ","Featured Bussiness section should be present","Featured Bussiness section is not present","Fail");
			return false;	

		}

		//check related articles
		List<WebElement> lst2 = objCommon.getObjects(webElmntRelatedArticles);
		if(lst2.size()==0){
			Reporter.fnWriteToHtmlOutput("Checking related articles section ","related articles section should be present","related articles section is not present","Fail");
			return false;	

		}

		//report success
		Reporter.fnWriteToHtmlOutput("Check category datails", "Category details : footer link, featured bussiness section and related articles section","Category details : footer link, featured bussiness section and related articles section are present", "Pass");
		return true;
	}



}
