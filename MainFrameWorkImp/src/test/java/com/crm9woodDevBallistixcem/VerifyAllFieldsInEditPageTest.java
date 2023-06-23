package com.crm9woodDevBallistixcem;

import java.util.Map;
import java.util.Random;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.crm.genericUtilities.BaseClass;

import crm.mainUtils.GenericUtils;

public class VerifyAllFieldsInEditPageTest extends BaseClass{

	@Test
	public void VerifyElements() {
		Random randN = new Random();
		int randNum = randN.nextInt(1000);
		//verification data -> emailable report, whether entered or not --> extent report
		SoftAssert softAssert=new SoftAssert();//TestNg Assertion
		String[] modulesList = GenericUtils.getModules(driver);//API-1
		for (String module : modulesList) {
			if( module.equalsIgnoreCase("Campaigns")) {
			String elementJsonSchema = GenericUtils.getElementJsonSchema(driver, module);//api-2
			try {
				Map<String, String> fieldData = eLib.getData("AllModuleFieldsValue", module).get(0); //0-> valid,1-> invalid, 2-> modified data
				GenericUtils.enterAllFieldsInEditPage(randNum,driver, module,elementJsonSchema, rLib, jLib, wLib,fieldData);//passing data to fields
				GenericUtils.saveData(driver,rLib);
				GenericUtils.verifyDataInDetailsPage(driver, softAssert, module, fieldData, false);
				GenericUtils.switchBackToHome(driver, wLib);
			}catch (Exception e) {
				GenericUtils.switchBackToHome(driver, wLib);
				GenericUtils.catchHandling(e, module,softAssert);
				continue;
			}

			}	
		}
		softAssert.assertAll();
	}
}