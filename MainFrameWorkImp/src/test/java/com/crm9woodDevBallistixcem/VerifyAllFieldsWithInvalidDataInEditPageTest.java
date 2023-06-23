package com.crm9woodDevBallistixcem;


import java.util.Map;
import java.util.Random;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.crm.genericUtilities.BaseClass;

import crm.mainUtils.GenericUtils;

public class VerifyAllFieldsWithInvalidDataInEditPageTest extends BaseClass{
//errormsg -> emailable report, whether entered or not --> extent report
	@Test
	public void VerifyElements() {
		Random randN = new Random();
		int randNum = randN.nextInt(1000);
		
		SoftAssert softAssert=new SoftAssert();//Assertion
		String[] modulesList = GenericUtils.getModules(driver);//api-1
		for (String module : modulesList) {
			//if(module.equals("Accounts") || module.equals("Leads")) {
		System.out.println(module);
				String elementJsonSchema = GenericUtils.getElementJsonSchema(driver, module);//api-2
          try {
        	  Map<String, String> fieldData = eLib.getData("AllModuleFieldsValue", module).get(1);//1-> invalid data
				GenericUtils.enterAndVerifyInvalidDataInEditPage(randNum,driver, softAssert, elementJsonSchema,module, rLib, jLib, wLib, fieldData);
				GenericUtils.saveData(driver,rLib);
				GenericUtils.switchBackToHome(driver, wLib);
		}catch (Exception e) {
			GenericUtils.switchBackToHome(driver, wLib);
			GenericUtils.catchHandling(e, module,softAssert);
			continue;
		}
			//}	
		}
		softAssert.assertAll();
	}
}