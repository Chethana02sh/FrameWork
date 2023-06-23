package com.crm.objectRepository;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.crm.genericUtilities.WebDriverUtility;

public class HomePage {

	@FindBy(xpath = "//a[@title='Menu']")
	private WebElement menuButtonClick;

	@FindBy(xpath = "//a[@role='button']")
	private WebElement adminButtonClick;

	@FindBy(xpath = "//a[.='Sign Out']")
	private WebElement signoutBtnClick;

	public HomePage(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}

	
	public void clickOnMenuBtn(WebDriver driver)
	{
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click();",menuButtonClick);
	}

	public void signoutfromApp() {
		adminButtonClick.click();
		signoutBtnClick.click();
	}
}