package com.crm.objectRepository;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
//declaretion
	@FindBy(xpath = "//input[@id='username']")
	private WebElement usernametxtEdt;

	@FindBy(xpath = "//input[@id='password']")
	private WebElement passwordtxtEdt;

	@FindBy(xpath = "//button[.='Sign in']")
	private WebElement submitBtn;

//initialization
	public LoginPage(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}

	/**
	 * 
	 * @param username
	 * @param password click on submit button
	 */

	public void loginToAppli(String username, String password) {
		usernametxtEdt.sendKeys(username);
		passwordtxtEdt.sendKeys(password);
		submitBtn.click();
	}
}
