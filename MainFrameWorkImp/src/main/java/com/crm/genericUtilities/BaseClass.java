package com.crm.genericUtilities;
/**
 * 
 * @author SanjayBabu
 *
 */

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import com.crm.objectRepository.LoginPage;

import crm.mainUtils.ReportUtility;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass 
{
	public WebDriver driver;
	public DataBaseUtility dLib=new DataBaseUtility();
	public ExcelUtility eLib=new ExcelUtility();
	public FileUtility fLib=new FileUtility();
	public WebDriverUtility wLib=new WebDriverUtility();
	public JavaUtility jLib=new JavaUtility();
	public ReportUtility rLib=new ReportUtility();

	
	/**
	 * launching the browser
	 * @throws Throwable
	 */
	//@Parameters("BROWSER")
	@BeforeClass(alwaysRun = true)
	public void launchTheBrowser() throws Throwable
	{
		rLib.initReports();
		String browser = fLib.getPropertKeyValue("browser");
		String url = fLib.getPropertKeyValue("url");

		if(browser.equalsIgnoreCase("firefox"))
		{
			WebDriverManager.firefoxdriver().setup();
			driver=new FirefoxDriver();
		}else if(browser.equalsIgnoreCase("chrome"))
		{
			WebDriverManager.chromedriver().setup();
			driver=new ChromeDriver();
		}
		else {
			driver=new ChromeDriver();
		}

		System.out.println("Browser successfully launched");
		//implicitly wait

//				wLib.waitForPageToLoad(driver);
		//enter the URL of the Application
		driver.get(url);
		//maximize the screen
		driver.manage().window().maximize();

		String username = fLib.getPropertKeyValue("username");
		String password = fLib.getPropertKeyValue("password");
		LoginPage lpage=new LoginPage(driver);
		//
		lpage.loginToAppli(username, password);
		System.out.println("Login successful");
	}

	/**
	 * logout from application
	 */
	@AfterMethod(alwaysRun = true)
	public void logoutFromAppln()
	{
//		GenericUtils.switchBackToHome(driver, wLib);
	}
	/**
	 * close the browser
	 */
	@AfterClass(alwaysRun = true)
	public void closeTheBrowser()
	{
		//		HomePage hpage=new HomePage(driver);
		//		hpage.signoutfromApp();
		//		System.out.println("Logout successful");

		//		driver.quit();
		//		System.out.println("Browser successfully closed");
		rLib.flushReports();
	}

}

