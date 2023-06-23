package com.crm9woodDevBallistixcem;

import java.util.Map;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.crm.genericUtilities.BaseClass;

import crm.mainUtils.GenericUtils;

public class VerifyNonMandatoryFieldsInEditPageTest extends BaseClass {
	// errormsg -> emailable report, whether entered or not --> extent report
	@Test
	public void VerifyElements() {
		SoftAssert softAssert = new SoftAssert();// asertion
		String[] modulesList = GenericUtils.getModules(driver);// api-1
		for (String module : modulesList) {
			// if(module.equals("Accounts") || module.equals("Leads")) {
			String elementJsonSchema = GenericUtils.getElementJsonSchema(driver, module);// api-2
			try {
				Map<String, String> fieldData = eLib.getData("AllModuleFieldsValue", module).get(0);// valid data from
																									// excel
				GenericUtils.enterNonMandatoryFieldsInEditPage(driver, module, elementJsonSchema, rLib, jLib, wLib,
						fieldData);// entering data to fields
				GenericUtils.saveData(driver, rLib);
				GenericUtils.verifyErrorMessageInMandatoryField(driver, softAssert, "This field is required.");
				GenericUtils.switchBackToHome(driver, wLib);
			} catch (Exception e) {
				continue;
			}
			// }
		}
		softAssert.assertAll();
	}
}