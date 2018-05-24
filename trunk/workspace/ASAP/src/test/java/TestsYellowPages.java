package test.java;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.*;
import org.junit.experimental.ParallelComputer;
import org.junit.rules.TestName;
import org.junit.runner.JUnitCore;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import ASAP.Driver;
import ASAP.QualityCenter;

import com.mercury.qualitycenter.otaclient.ITDConnection;
//import com.opera.core.systems.OperaDriver;

public class TestsYellowPages {  

	   @Test  
	    public void Test() {  
		   
	      Class[] cls={ParallelTests.class};  

	      //Parallel among classes  
	     // JUnitCore.runClasses(ParallelComputer.classes(), cls);  

	      //Parallel among methods in a class  
	      JUnitCore.runClasses(ParallelComputer.methods(), cls);  

	      //Parallel all methods in all classes  
	     // JUnitCore.runClasses(new ParallelComputer(true, true), cls);     
	    }  
	   
	   
	   
	   public static class ParallelTests
	   { 
		   
		   private WebDriver webDriver;
		   private String strEnv;
		   private String Datasheet;
		   private String RootPath;
		   private String WorkingPath;
		   private String strQCServer;
		   private String strQCUser;
		   private String strQCPassword;
		   private String strQCDomain;
		   private String strQCProject;
		   private String strTestPlanPath;
		   private String strTestSetPath;
		   private String strTestSetName;	
		   private String strGuiPackage;
		   private DesiredCapabilities dc;
		   private ITDConnection objTD;
		   private String driverType;
		   
		   @Rule public TestName name = new TestName();
		   
		    //	            
 		   @Before
		    public void setUp() throws IOException {
			   
			    //Set Common Details
			    WorkingPath = System.getProperty("user.dir");
			   	RootPath = WorkingPath.split("trunk")[0];
			   	strEnv = "STAGING";//Environment in which you need to do the execution
	            strQCServer = "http://sqc.sensis.com.au/qcbin/";//QC Server
	            strQCUser = "anilag";//QC User name
	            strQCPassword = "Sensis@1"; //QC Password
	            strQCDomain = "SENSIS_IT_TESTING_SERVICES";//QC Domain
	            strQCProject = "SFDC";// QC Project
	            strTestPlanPath = "Subject\\SalesForce_Automation\\Automation_Coverage";//QC Test Plan Path
	            strTestSetPath = "Root\\SalesForce_Automation\\";//QC Test Set Path
	            strGuiPackage = "GuiYellowPages";//*****Please note you need to change the name and give the GuiFunction file name here
	            
	            
	            //Check and delete jenkins build reports zip
	            File jenkinsBuildRep = new File(RootPath + "\\Execution\\Jenkins_Build_Report.zip");
	            if(jenkinsBuildRep.exists())
	            {
	            	//Delete and Create
	            	jenkinsBuildRep.delete();
	            	jenkinsBuildRep.createNewFile();
	            }
 		   }

		   
		    @Test public void test_Firefox() 
		   {	    	 
	    	  	//Set Test Specific Details	    	  	

	            Datasheet = "YellowPages";//Calendar name for execution    	               

	            strTestSetName = "AutoTest1";	//Test Set name in QC
	           
	            
	            //Instantiate Webdriver and QC connection
	            webDriver = new FirefoxDriver();	            
	            QualityCenter objQC = new QualityCenter();	           	           
	            //objTD = objQC.fConnectToQC(strQCServer, strQCUser, strQCPassword, strQCDomain, strQCProject);
	            objTD = null;
	            
	            driverType = name.getMethodName();
	            
	            //Driver Class
		    	Driver driver = new Driver();
		    	try {
		    		driver.main(webDriver, objTD, strEnv, Datasheet, RootPath, strTestPlanPath, strTestSetPath, strTestSetName, driverType, strGuiPackage);
					
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
		    	webDriver.quit();
		    	
/*		    	//Check if the assert returns true value
		    	org.junit.Assert.assertTrue("Messge",false);*/
		    		
	      	}
		    
			   
/*		   @Test public void test_Chrome()
		   {	    	 
	    	  	//Set Test Specific Details	    	  	
	            Datasheet = "SalesForce1";        	               
	            strTestSetName = "AutoTest2";		
	            
	            //Instantiate Webdriver and QC connection
	            System.setProperty("webdriver.chrome.driver", RootPath + "trunk\\Source\\Storage\\Executables\\chromedriver.exe");
	            webDriver= new ChromeDriver();
	            QualityCenter objQC = new QualityCenter();	           	           
	            //objTD = objQC.fConnectToQC(strQCServer, strQCUser, strQCPassword, strQCDomain, strQCProject);
	            objTD = null;
	            
	            driverType = name.getMethodName();
	            
	            //Driver Class
		    	Driver driver = new Driver();
		    	try {
					driver.main(webDriver, objTD, strEnv, Datasheet, RootPath, strTestPlanPath, strTestSetPath, strTestSetName, driverType, strGuiPackage);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	webDriver.quit();		      
	      	}
	      */

			      
	  }  	         	          	  
}   
	
