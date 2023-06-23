package com.crm9woodDevBallistixcem;

import static crm.mainUtils.GenericUtils.*;

import java.util.Map;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.crm.genericUtilities.BaseClass;

public class EditandDeleteTest extends BaseClass{

	@Test
	public void editDelete() {
		String[] modulesList = getModules(driver);
		SoftAssert softAssert=new SoftAssert();
		for (String module : modulesList) {
			System.out.println(module);
			navigateEditPage(driver, module);
			Map<String, String> elementData = eLib.getData("EditAndDelete", module).get(0);
			if(elementData.size()==0) continue;
			addDataAndVerify(driver, jLib, elementData, module,softAssert);
			editDataAndVerify(driver, jLib, elementData, module,softAssert);
			delete(driver, module);
		}
		softAssert.assertAll();
	}
}