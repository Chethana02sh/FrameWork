package com.crm9woodDevBallistixcem;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openqa.selenium.By;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.crm.genericUtilities.BaseClass;

import crm.mainUtils.GenericUtils;

public class AddDepedendentDataTest extends BaseClass {

	@Test
	public void addDepedendentDataTest() {

		Random randN = new Random();
		int randNum = randN.nextInt(1000);
		// verification data -> emailable report, whether entered or not --> extent
		// report
		SoftAssert softAssert = new SoftAssert();// TestNg Assertion
		List<String> modulesList = Arrays.asList(GenericUtils.getModules(driver));// API-1

		for (String module : modulesList) {
//			System.out.println(module);
//			if (!(module.equalsIgnoreCase("Emails")||module.equalsIgnoreCase("Users")||module.equalsIgnoreCase("ModComments"))) {
	//		if (module.equals("Campaigns")) {
			System.out.println(module);
			String elementJsonSchema1 = GenericUtils.getElementJsonSchema(driver, module);//api-2
			//add valid data and verify
			
			try {
			//	fieldData+randNum;
				Map<String, String> fieldData = eLib.getData("AllModuleFieldsValue", module).get(0); //0-> valid,1-> invalid, 2-> modified data
				GenericUtils.enterAllFieldsInEditPage(randNum,driver, module,elementJsonSchema1, rLib, jLib, wLib,fieldData);//passing data to fields
				GenericUtils.saveData(driver,rLib);
				GenericUtils.verifyDataInDetailsPage(driver, softAssert, module, fieldData, false);
				//					GenericUtils.switchBackToHome(driver, wLib);
			}catch (Exception e) {
				GenericUtils.switchBackToHome(driver, wLib);
				GenericUtils.catchHandling(e, module,softAssert);
//				continue;
			}
			//invalid data
			try {
				Map<String, String> fieldData = eLib.getData("AllModuleFieldsValue", module).get(1);//1-> invalid data
				GenericUtils.enterAndVerifyInvalidDataInEditPage(randNum,driver, softAssert, elementJsonSchema1,module, rLib, jLib, wLib, fieldData);
				GenericUtils.saveData(driver,rLib);
				GenericUtils.switchBackToHome(driver, wLib);
			}catch (Exception e) {
				GenericUtils.switchBackToHome(driver, wLib);
				GenericUtils.catchHandling(e, module,softAssert);
//				continue;
			}

			//Non Mandatory
			try {
				Map<String, String> fieldData = eLib.getData("AllModuleFieldsValue", module).get(0);// valid data from
				// excel
				GenericUtils.enterNonMandatoryFieldsInEditPage(driver, module, elementJsonSchema1, rLib, jLib, wLib,
						fieldData);// entering data to fields
				GenericUtils.saveData(driver, rLib);
				GenericUtils.verifyErrorMessageInMandatoryField(driver, softAssert, "This field is required.");
				GenericUtils.switchBackToHome(driver, wLib);
			} catch (Exception e) {
//				continue;
			}

			//Inline Edit

			if (!module.equalsIgnoreCase("Calendar")) {
				try {
					Map<String, String> fieldData = eLib.getData("AllModuleFieldsValue", module).get(0); // 0 --> valid
					// data from
					// excel
					GenericUtils.enterAllFieldsInEditPage(randNum, driver, module, elementJsonSchema1, rLib, jLib, wLib,
							fieldData);// inserting data to fields
					GenericUtils.saveData(driver, rLib);// saving data
					GenericUtils.verifyDataInDetailsPage(driver, softAssert, module, fieldData, false);// verify
					Map<String, String> modifyData = eLib.getData("AllModuleFieldsValue", module).get(2);// 2-->
					// modified
					// data from
					// excel
					// sheet
					GenericUtils.modifyAllFieldsInDetailPage(driver, module, elementJsonSchema1, rLib, jLib, wLib,
							modifyData);// modifying data
					GenericUtils.verifyDataInDetailsPage(driver, softAssert, module, modifyData, true);
					//								GenericUtils.switchBackToHome(driver, wLib);
				} catch (Exception e) {
					GenericUtils.switchBackToHome(driver, wLib);
					GenericUtils.catchHandling(e, module, softAssert);
//					continue;
				}
			} 
			List<String> depedentModules = driver.findElements(By.xpath("//span[@class='tab-icon']/ancestor::li"))
					.stream().map(ele -> ele.getAttribute("data-module")).toList();

			String moduleUrl = driver.getCurrentUrl();
			int depedentModulesCount = depedentModules.size();
			for (int i = 0; i < depedentModulesCount; i++) {
				String elementJsonSchema = GenericUtils.getElementJsonSchema(driver, depedentModules.get(i));// api-2
				//valid data verification and inline verification
				driver.get(moduleUrl);
				GenericUtils.pauseProgram(2000);
				driver.findElement(By.xpath(
						"//span[@class='tab-icon']/ancestor::li[@data-module='" + depedentModules.get(i) + "']"))
				.click();
				GenericUtils.pauseProgram(2000);
				try {
					driver.findElement(By.xpath(
							"//button[@module='" + depedentModules.get(i) + "' and contains(@name,'addButton')]"))
					.click();
				} catch (Exception e) {
					continue;
				}
				GenericUtils.pauseProgram(3000);
				driver.findElement(By.xpath("//button[@id='goToFullForm']")).click();
				GenericUtils.pauseProgram(3000);

				Map<String, String> fieldData = eLib.getData("AllModuleFieldsValue", depedentModules.get(i)).get(0); // 0->
				try {
					GenericUtils.enterAllFieldsInEditPageForRelatedModule(randNum,driver, depedentModules.get(i),
							elementJsonSchema, rLib, jLib, wLib, fieldData);// passing data to fields
					GenericUtils.saveData(driver, rLib);
					driver.findElement(By.xpath(
							"(//table[@id='listview-table']/tbody/tr[1]/td/span[@class='value textOverflowEllipsis']/a)[1]"))
					.click();
					GenericUtils.pauseProgram(2000);
					GenericUtils.verifyDataInDetailsPage(driver, softAssert, depedentModules.get(i), fieldData,
							false);
					Map<String, String> modifyData = eLib.getData("AllModuleFieldsValue", depedentModules.get(i))
							.get(2);// 2--> modified data from excel
					GenericUtils.modifyAllFieldsInDetailPage(driver, depedentModules.get(i), elementJsonSchema,
							rLib, jLib, wLib, modifyData);// modifying data
					GenericUtils.verifyDataInDetailsPage(driver, softAssert, depedentModules.get(i), modifyData,
							true);
				} catch (Exception e) {
					try {
						driver.get(moduleUrl);
						wLib.switchToAlertPopUpWindowAndAccept(driver);
					} catch (Exception e1) {
					}
				}

				//invalid data verification
				driver.get(moduleUrl);
				try{
				wLib.switchToAlertPopUpWindowAndAccept(driver);
				driver.get(moduleUrl);
				}catch (Exception e) {}
				GenericUtils.pauseProgram(2000);
				driver.findElement(By.xpath(
						"//span[@class='tab-icon']/ancestor::li[@data-module='" + depedentModules.get(i) + "']"))
				.click();
				GenericUtils.pauseProgram(2000);
				try {
					driver.findElement(By.xpath(
							"//button[@module='" + depedentModules.get(i) + "' and contains(@name,'addButton')]"))
					.click();
				} catch (Exception e) {
					continue;
				}
				GenericUtils.pauseProgram(3000);
				driver.findElement(By.xpath("//button[@id='goToFullForm']")).click();
				GenericUtils.pauseProgram(3000);

				Map<String, String> fieldInvalidData = eLib.getData("AllModuleFieldsValue", depedentModules.get(i)).get(1); // 0->
				try {
					GenericUtils.enterAndVerifyInvalidDataInEditPageForRelatedModule(randNum, driver, softAssert, elementJsonSchema, depedentModules.get(i), rLib, jLib, wLib, fieldInvalidData);// passing data to fields
					GenericUtils.saveData(driver, rLib);
				} catch (Exception e) {
					try {
						driver.get(moduleUrl);
						wLib.switchToAlertPopUpWindowAndAccept(driver);
					} catch (Exception e1) {
					}
				}

				//non mandatory field verification

				driver.get(moduleUrl);
				try{
				wLib.switchToAlertPopUpWindowAndAccept(driver);
				driver.get(moduleUrl);
				}catch (Exception e) {}
				GenericUtils.pauseProgram(2000);
				driver.findElement(By.xpath(
						"//span[@class='tab-icon']/ancestor::li[@data-module='" + depedentModules.get(i) + "']"))
				.click();
				GenericUtils.pauseProgram(2000);
				try {
					driver.findElement(By.xpath(
							"//button[@module='" + depedentModules.get(i) + "' and contains(@name,'addButton')]"))
					.click();
				} catch (Exception e) {
					continue;
				}
				GenericUtils.pauseProgram(3000);
				driver.findElement(By.xpath("//button[@id='goToFullForm']")).click();
				GenericUtils.pauseProgram(3000);


				Map<String, String> validData = eLib.getData("AllModuleFieldsValue", depedentModules.get(i)).get(1); // 0->
				try {
					GenericUtils.enterNonMandatoryFieldsInEditPageForRelatedModule(driver, depedentModules.get(i), elementJsonSchema, rLib, jLib, wLib, validData);// passing data to fields
					GenericUtils.saveData(driver, rLib);
				} catch (Exception e) {
					try {
						driver.get(moduleUrl);
						wLib.switchToAlertPopUpWindowAndAccept(driver);
					} catch (Exception e1) {
					}
				}
				driver.get(moduleUrl);
				try{
				wLib.switchToAlertPopUpWindowAndAccept(driver);
				driver.get(moduleUrl);
				}catch (Exception e) {}
				
			}
			}
			GenericUtils.switchBackToHome(driver, wLib);
			//}
	//	}
		//				softAssert.assertAll();

	}
}
