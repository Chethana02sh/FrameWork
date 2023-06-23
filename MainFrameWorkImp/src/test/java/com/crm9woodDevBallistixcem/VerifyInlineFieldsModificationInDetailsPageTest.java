
package com.crm9woodDevBallistixcem;

import java.util.Map;
import java.util.Random;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.crm.genericUtilities.BaseClass;

import crm.mainUtils.GenericUtils;

public class VerifyInlineFieldsModificationInDetailsPageTest extends BaseClass {
	// verification data -> emailable report, whether entered or not --> extent
	// report
	@Test
	public void VerifyElements() {
		Random randN = new Random();
		int randNum = randN.nextInt(1000);
		
		SoftAssert softAssert = new SoftAssert();// assertion
		String[] modulesList = GenericUtils.getModules(driver);// api-1
		for (String module : modulesList) {
		if (!module.equalsIgnoreCase("Calendar")) {
				String elementJsonSchema = GenericUtils.getElementJsonSchema(driver, module);// api-2
				try {
					Map<String, String> fieldData = eLib.getData("AllModuleFieldsValue", module).get(0); // 0 --> valid
																											// data from
																											// excel
					GenericUtils.enterAllFieldsInEditPage(randNum,driver, module, elementJsonSchema, rLib, jLib, wLib,
							fieldData);// inserting data to fields
					GenericUtils.saveData(driver, rLib);// saving data
					GenericUtils.verifyDataInDetailsPage(driver, softAssert, module, fieldData, false);// verify
					Map<String, String> modifyData = eLib.getData("AllModuleFieldsValue", module).get(2);// 2-->
																											// modified
																											// data from
																											// excel
																											// sheet
					GenericUtils.modifyAllFieldsInDetailPage(driver, module, elementJsonSchema, rLib, jLib, wLib,
							modifyData);// modifying data
					GenericUtils.verifyDataInDetailsPage(driver, softAssert, module, modifyData, true);
					GenericUtils.switchBackToHome(driver, wLib);
				} catch (Exception e) {
					GenericUtils.switchBackToHome(driver, wLib);
					GenericUtils.catchHandling(e, module, softAssert);
					continue;
				}
			} // if condition ends
		}
		softAssert.assertAll();
	}
}