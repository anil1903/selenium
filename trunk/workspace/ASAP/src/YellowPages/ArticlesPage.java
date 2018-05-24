package YellowPages;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ASAP.CommonFunctions;
import ASAP.Reporting;

public class ArticlesPage {
	
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
	public ArticlesPage(WebDriver driverTemp, Reporting ReporterTemp, HashMap<String, String> DictionaryTemp,  HashMap<String, String> EnvironmentTemp)
	{
		super();
		Reporter = ReporterTemp;
		driver = driverTemp;
		Dictionary = DictionaryTemp;
		Environment = EnvironmentTemp;
		objCommon = new CommonFunctions(driver, Reporter);
	}
	
	//Define Objects
	private String webElmntArticles = "xpath:=//a[@class='article-card ']";
	private String webElmntDetailArticles = "xpath:=//p[@class='h3']";
 	private List <WebElement> lst;
	
	//*****************************************************************************************
    //*	Name		    : fGuiCheckArticles
    //*	Description	    : Gets no of articles displayed
    //*	Author		    : Diksha Mirajkar
    //*	Input Params	: None
    //*	Return Values	: boolean 
    //*****************************************************************************************
	public boolean fGuiCheckArticles(){
		//Get list of articles displayed
		lst = objCommon.getObjects(webElmntArticles);

		//Return false if there are no articles displayed
		if(lst.size()==0){
			Reporter.fnWriteToHtmlOutput("Check if the list of articles is displayed ","List of articles should be displayed", "List of articles not displayed", "Fail");
			return false;
		}

		//Get list of all the article titles
		List<WebElement> lst1 = objCommon.getObjects(webElmntDetailArticles);

		//Replace special characters with "-" so that it matches the URL
		String title_url=lst1.get(0).getText();
		String titleUrl = title_url.replaceAll("[^\\w]", "-");
		String Title = titleUrl.toLowerCase();
		String url=Dictionary.get("FIRST_ARTICLE_URL").trim();
		
		//Check if the  title is present in the URL
		if(url.contains(Title)==false){
			Reporter.fnWriteToHtmlOutput("Title should be present in the URL","Title should be present in the url","Title not present in the URL","Fail");
			return false;
		}
		
		//open first article
		WebElement title = lst1.get(0);
		if(objCommon.fGuiClick(title)==false){
			return false;
		}
		
		//report success
		Reporter.fnWriteToHtmlOutput("Navigate to Articles detail page","Articles detail page should be displayed","Navigation to articles detail page successful","Pass");	
		return true;
	}


	
	
}
