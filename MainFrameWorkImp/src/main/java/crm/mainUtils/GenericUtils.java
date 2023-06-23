
package crm.mainUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.asserts.SoftAssert;

import com.crm.genericUtilities.ExcelUtility;
import com.crm.genericUtilities.IPathConstants;
import com.crm.genericUtilities.JavaUtility;
import com.crm.genericUtilities.WebDriverUtility;
import com.jayway.jsonpath.JsonPath;

public class GenericUtils {

			
	public static String[] getModules(WebDriver driver) {
		navigateLink(driver, "https://9wood-dev.ballistixcrm.com/testsuite/api/1.php"); //api 1
		return driver.findElement(By.xpath("//body")).getText().replaceAll("[^A-Z^a-z^,]", "").split(",");
	}
	
	public static String getDepedentModules(WebDriver driver, String moudle) {
		navigateLink(driver, "https://9wood-dev.ballistixcrm.com/testsuite/api/3.php?module="+moudle); //api 3
		return driver.findElement(By.xpath("//body")).getText();
		}

	public static String getElementJsonSchema(WebDriver driver, String moudle) {
		navigateLink(driver, "https://9wood-dev.ballistixcrm.com/testsuite/api/2.php?module="+moudle+""); //api 2
		return driver.findElement(By.xpath("//body")).getText();
	}
	
	public static void modifyAllFieldsInDetailPage(WebDriver driver, String module,String elementJsonSchema,ReportUtility rLib,JavaUtility jLib, WebDriverUtility wLib,Map<String, String> fieldData) {
		List<String> labelsList = JsonPath.read(elementJsonSchema, "$.fields[*].label");
		List<String> namesList = JsonPath.read(elementJsonSchema, "$.fields[*].name");
		List<Boolean> editableOrNotList = JsonPath.read(elementJsonSchema, "$.fields[*].editable");
		List<String> typeNameList = JsonPath.read(elementJsonSchema, "$.fields[*].type.name");
		rLib.createTest(module+" Modified");//reporting purpose
		rLib.test.info("Total Elements Present in "+module+" modules <b>"+labelsList.size()+"</b> <br> <b>List of all Elements in "+module+" module:</b> <br>"+labelsList);


		for(int i=0;i<typeNameList.size();i++) {
			String typeName = typeNameList.get(i);
			String element=labelsList.get(i);
			String name=namesList.get(i);
			String data=fieldData.get(element+"###"+name);
			if(data==null || data.equals(""))continue;
			String xpath="//input[@name='"+name+"']";
			Boolean editableStatus =editableOrNotList.get(i);
			String elementXpath="//td[@id='"+module+"_detailView_fieldValue_"+name+"']";
			if (editableStatus==false) {
				rLib.test.skip("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is <b>NOT EDITABLE</b>");
			}
			else {
				try {	
					if (typeName.equals("string") || typeName.equals("password")||
							typeName.equals("currency") || typeName.equals("double") || 
							typeName.equals("integer") ||typeName.equals("url") ||
							typeName.equals("email") ||typeName.equals("phone")) {
						clickOnEditInDetailPage(driver, name, module, elementXpath,wLib);
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						clickSaveInDetailPage(driver, name, module, elementXpath, wLib);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("text")) {
						clickOnEditInDetailPage(driver, name, module, elementXpath,wLib);
						xpath="//textarea[@name='"+name+"']";
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						clickSaveInDetailPage(driver, name, module, elementXpath, wLib);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("reference")) {
						clickOnEditInDetailPage(driver, name, module, elementXpath,wLib);
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						clickSaveInDetailPage(driver, name, module, elementXpath, wLib);
						passReport(rLib, i, element, data, xpath, typeName);	
					}
					else if(typeName.equals("date")||typeName.equals("time")||
							typeName.equals("datetime")) {
						clickOnEditInDetailPage(driver, name, module, elementXpath,wLib);						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
						clickSaveInDetailPage(driver, name, module, elementXpath, wLib);
						passReport(rLib, i, element, data, xpath, typeName);
					}

					else if(typeName.equals("file")) {
						clickOnEditInDetailPage(driver, name, module, elementXpath,wLib);
						data=IPathConstants.TEST_FILE_PATH;
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						clickSaveInDetailPage(driver, name, module, elementXpath, wLib);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("multipicklist")) {
						clickOnEditInDetailPage(driver, name, module, elementXpath,wLib);
						xpath="//select[contains(@name,'"+name+"')]";
						try {
							driver.findElement(By.xpath("//input[contains(@name,'"+name+"') and @type='radio'][1]")).click();}
						catch (Exception e) {	}
						driver.findElement(By.xpath("//div[contains(@id,'"+name+"')]//input")).sendKeys(Keys.BACK_SPACE,Keys.BACK_SPACE);
						pauseProgram(2000);
						WebElement ele = driver.findElement(By.xpath(xpath));
						wLib.selectDropDown(data,ele);
						clickSaveInDetailPage(driver, name, module, elementXpath, wLib);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("picklist")) {
						clickOnEditInDetailPage(driver, name, module, elementXpath,wLib);
						xpath="//select[@name='"+name+"']";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						clickSaveInDetailPage(driver, name, module, elementXpath, wLib);
						passReport(rLib, i, element, data, xpath, typeName);					
					}
					else if(typeName.equals("owner")) {
						clickOnEditInDetailPage(driver, name, module, elementXpath,wLib);
						xpath="//select[@name='"+name+"']";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						clickSaveInDetailPage(driver, name, module, elementXpath, wLib);
						passReport(rLib, i, element, data, xpath, typeName);					
					}
					else if(typeName.equals("boolean")) {
						clickOnEditInDetailPage(driver, name, module, elementXpath,wLib);
						xpath="//input[@name='"+name+"' and @type='checkbox']";
						try {
							driver.findElement(By.xpath(xpath)).click();
						}catch (Exception e) {
							wLib.scrollUp(driver, 100);
							driver.findElement(By.xpath(xpath)).click();
						}
						clickSaveInDetailPage(driver, name, module, elementXpath, wLib);
						passReport(rLib, i, element, data, xpath, typeName);					}
					else {
						rLib.test.warning("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>For <b>"+element+"</b> element not written any logic <br> Please write logic for <b>"+element+"</b> element <br> TYPE of the <b>"+element+"</b> is : <b>"+typeName+"</b> <br> Name of the <b>"+element+"</b> is : <b>"+name+"</b> ");
					}
				}catch (Exception e) {
					//e.printStackTrace();
					StringWriter sw=new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					rLib.test.fail("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is EDITABLE other reson is below <br>"+sw.toString());
					try{clickSaveInDetailPage(driver, name, module, elementXpath, wLib);}catch (Exception x) {
						pauseProgram(2000);
						wLib.scrollUp(driver);
						//x.printStackTrace();
					}
				}
			}
		}
	}

	public static void clickOnEditInDetailPage(WebDriver driver, String name, String module,String elementXpath, WebDriverUtility wLib) {
		pauseProgram(1000);
		wLib.mouseOverAnElement(driver, driver.findElement(By.xpath(elementXpath)));
		String editBtnXpath=elementXpath+"//a[@class='editAction fa fa-pencil']";
		wLib.moveAndClick(driver, driver.findElement(By.xpath(editBtnXpath)));
	}

	public static void clickSaveInDetailPage(WebDriver driver, String name, String module,String elementXpath, WebDriverUtility wLib) {
		String saveBtnXpath=elementXpath+"//span[@class='pointerCursorOnHover input-group-addon input-group-addon-save inlineAjaxSave']";
		wLib.moveAndClick(driver, driver.findElement(By.xpath(saveBtnXpath)));
		pauseProgram(2000);
		wLib.scrollUp(driver);
	}

	public static void enterAllFieldsInEditPage(int randomNum,WebDriver driver, String module,String elementJsonSchema,ReportUtility rLib,JavaUtility jLib, WebDriverUtility wLib,Map<String, String> fieldData) {
	//	Random ran = new Random();
	//	int randnum = ran.nextInt(500);
		
		List<String> labelsList = JsonPath.read(elementJsonSchema, "$.fields[*].label");
		List<String> namesList = JsonPath.read(elementJsonSchema, "$.fields[*].name");
		List<Boolean> editableOrNotList = JsonPath.read(elementJsonSchema, "$.fields[*].editable");
		List<String> typeNameList = JsonPath.read(elementJsonSchema, "$.fields[*].type.name");
		rLib.createTest(module);//reporting purpose
		rLib.test.info("Total Elements Present in "+module+" modules <b>"+labelsList.size()+"</b> <br> <b>List of all Elements in "+module+" module:</b> <br>"+labelsList);

		navigateEditPage(driver, module);

		for(int i=0;i<typeNameList.size();i++) {
			String typeName = typeNameList.get(i);

			String element=labelsList.get(i);
			String name=namesList.get(i);
			String data=fieldData.get(element+"###"+name);
			if(data==null || data.equals(""))continue;
			String xpath="//input[@name='"+name+"']";
			Boolean editableStatus =editableOrNotList.get(i);
			if (editableStatus==false) {
				rLib.test.skip("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is <b>NOT EDITABLE</b>");
			}
			else {
				try {	
					if (typeName.equals("password")||
							typeName.equals("currency") || typeName.equals("double") || 
							typeName.equals("integer") ||typeName.equals("url") ||
							typeName.equals("email") ||typeName.equals("phone")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("string") ) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data+"_"+randomNum);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("text")) {
						xpath="//textarea[@name='"+name+"']";
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("reference")) {
						//						xpath="//input[@data-fieldname='"+name+"']";
						driver.findElement(By.xpath("//i[contains(@id,'"+name+"_create')]")).click();
						pauseProgram(3000);
						driver.findElement(By.xpath("//div[contains(@class,'modal-body')]//*[text()='*']/ancestor::td/following-sibling::td/input")).sendKeys(data);
						driver.findElement(By.xpath("//div[contains(@class,'modal-footer')]//*[text()='Save']")).click();
						pauseProgram(2000);
						String recordStatus = driver.findElement(By.xpath("//div[@class='notificationHeader']/following-sibling::div")).getText();
						System.out.println(element +" record status "+recordStatus);
						driver.findElement(By.xpath("//div[@class='notificationHeader']/button[contains(@class,'close')]")).click();
						//						driver.findElement(By.xpath(xpath)).clear();
						//						driver.findElement(By.xpath(xpath)).sendKeys(data);
						passReport(rLib, i, element, data, xpath, typeName);	
					}
					else if(typeName.equals("date")||typeName.equals("time")||
							typeName.equals("datetime")) {
						//						date=jLib.getSystemDateAndTimeInFormat("yyyy-MM-dd"); 
						//						time=jLib.getSystemDateAndTimeInFormat("hh:00 a");
						//						datetime=jLib.getSystemDateAndTimeInFormat("yyyy-MM-dd hh:mm aa");
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						passReport(rLib, i, element, data, xpath, typeName);
						driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
					}

					else if(typeName.equals("file")) {
						data=IPathConstants.TEST_FILE_PATH;
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("multipicklist")) {
						xpath="//select[contains(@name,'"+name+"')]";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("owner")||typeName.equals("picklist")) {
						xpath="//select[@name='"+name+"']";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						passReport(rLib, i, element, data, xpath, typeName);
					}

					else if(typeName.equals("boolean")) {
						xpath="//input[@data-fieldname='"+name+"']";
						wLib.scrollTillElement(driver, driver.findElement(By.xpath(xpath+"/ancestor::div[@class='fieldBlockContainer']")));
						try {
							driver.findElement(By.xpath(xpath)).click();
						}catch (Exception e) {
							wLib.scrollUp(driver, 100);
							driver.findElement(By.xpath(xpath)).click();
						}
						passReport(rLib, i, element, data, xpath, typeName);	
					}
					else {
						rLib.test.warning("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>For <b>"+element+"</b> element not written any logic <br> Please write logic for <b>"+element+"</b> element <br> TYPE of the <b>"+element+"</b> is : <b>"+typeName+"</b> <br> Name of the <b>"+element+"</b> is : <b>"+name+"</b> ");
					}
				}catch (Exception e) {
					StringWriter sw=new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					rLib.test.fail("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is EDITABLE other reson is below <br>"+sw.toString());
				}
			}
		}
	}
	
	public static void enterAllFieldsInEditPageForRelatedModule(int randNum,WebDriver driver, String module,String elementJsonSchema,ReportUtility rLib,JavaUtility jLib, WebDriverUtility wLib,Map<String, String> fieldData) {
		List<String> labelsList = JsonPath.read(elementJsonSchema, "$.fields[*].label");
		List<String> namesList = JsonPath.read(elementJsonSchema, "$.fields[*].name");
		List<Boolean> editableOrNotList = JsonPath.read(elementJsonSchema, "$.fields[*].editable");
		List<String> typeNameList = JsonPath.read(elementJsonSchema, "$.fields[*].type.name");
		rLib.createTest(module);//reporting purpose
		rLib.test.info("Total Elements Present in "+module+" modules <b>"+labelsList.size()+"</b> <br> <b>List of all Elements in "+module+" module:</b> <br>"+labelsList);


		for(int i=0;i<typeNameList.size();i++) {
			String typeName = typeNameList.get(i);

			String element=labelsList.get(i);
			String name=namesList.get(i);
			String data=fieldData.get(element+"###"+name);
			if(data==null || data.equals(""))continue;
			String xpath="//input[@name='"+name+"']";
			Boolean editableStatus =editableOrNotList.get(i);
			if (editableStatus==false) {
				rLib.test.skip("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is <b>NOT EDITABLE</b>");
			}
			else {
				try {	
					if (typeName.equals("string") || typeName.equals("password")||
							typeName.equals("currency") || typeName.equals("double") || 
							typeName.equals("integer") ||typeName.equals("url") ||
							typeName.equals("email") ||typeName.equals("phone")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data+"_"+randNum);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("text")) {
						xpath="//textarea[@name='"+name+"']";
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("reference")) {
						//						xpath="//input[@data-fieldname='"+name+"']";
						driver.findElement(By.xpath("//i[contains(@id,'"+name+"_create')]")).click();
						pauseProgram(3000);
						driver.findElement(By.xpath("//div[contains(@class,'modal-body')]//*[text()='*']/ancestor::td/following-sibling::td/input")).sendKeys(data);
						driver.findElement(By.xpath("//div[contains(@class,'modal-footer')]//*[text()='Save']")).click();
						pauseProgram(2000);
						String recordStatus = driver.findElement(By.xpath("//div[@class='notificationHeader']/following-sibling::div")).getText();
						System.out.println(element +" record status "+recordStatus);
						driver.findElement(By.xpath("//div[@class='notificationHeader']/button[contains(@class,'close')]")).click();
						//						driver.findElement(By.xpath(xpath)).clear();
						//						driver.findElement(By.xpath(xpath)).sendKeys(data);
						passReport(rLib, i, element, data, xpath, typeName);	
					}
					else if(typeName.equals("date")||typeName.equals("time")||
							typeName.equals("datetime")) {
						//						date=jLib.getSystemDateAndTimeInFormat("yyyy-MM-dd"); 
						//						time=jLib.getSystemDateAndTimeInFormat("hh:00 a");
						//						datetime=jLib.getSystemDateAndTimeInFormat("yyyy-MM-dd hh:mm aa");
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						passReport(rLib, i, element, data, xpath, typeName);
						driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
					}

					else if(typeName.equals("file")) {
						data=IPathConstants.TEST_FILE_PATH;
						driver.findElement(By.xpath(xpath)).sendKeys(data);
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("multipicklist")) {
						xpath="//select[contains(@name,'"+name+"')]";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("owner")||typeName.equals("picklist")) {
						xpath="//select[@name='"+name+"']";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						passReport(rLib, i, element, data, xpath, typeName);
					}

					else if(typeName.equals("boolean")) {
						xpath="//input[@data-fieldname='"+name+"']";
						wLib.scrollTillElement(driver, driver.findElement(By.xpath(xpath+"/ancestor::div[@class='fieldBlockContainer']")));
						try {
							driver.findElement(By.xpath(xpath)).click();
						}catch (Exception e) {
							wLib.scrollUp(driver, 100);
							driver.findElement(By.xpath(xpath)).click();
						}
						passReport(rLib, i, element, data, xpath, typeName);	
					}
					else {
						rLib.test.warning("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>For <b>"+element+"</b> element not written any logic <br> Please write logic for <b>"+element+"</b> element <br> TYPE of the <b>"+element+"</b> is : <b>"+typeName+"</b> <br> Name of the <b>"+element+"</b> is : <b>"+name+"</b> ");
					}
				}catch (Exception e) {
					StringWriter sw=new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					rLib.test.fail("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is EDITABLE other reson is below <br>"+sw.toString());
				}
			}
		}
	}
	public static void enterAndVerifyInvalidDataInEditPage(int randNum, WebDriver driver,SoftAssert softAssert,String elementJsonSchema, String module,ReportUtility rLib,JavaUtility jLib, WebDriverUtility wLib,Map<String, String> fieldData) {
		List<String> labelsList = JsonPath.read(elementJsonSchema, "$.fields[*].label");
		List<String> namesList = JsonPath.read(elementJsonSchema, "$.fields[*].name");
		List<Boolean> editableOrNotList = JsonPath.read(elementJsonSchema, "$.fields[*].editable");
		List<String> typeNameList = JsonPath.read(elementJsonSchema, "$.fields[*].type.name");
		rLib.createTest(module+" with invalid data");//reporting purpose
		rLib.test.info("Total Elements Present in "+module+" modules <b>"+labelsList.size()+"</b> <br> <b>List of all Elements in "+module+" module:</b> <br>"+labelsList);
		Map<String, String> errorMsg = new ExcelUtility().getData("ErrorMsg");
		navigateEditPage(driver, module);

		for(int i=0;i<typeNameList.size();i++) {
			String typeName = typeNameList.get(i);

			String element=labelsList.get(i);
			String name=namesList.get(i);
			String data=fieldData.get(element+"###"+name);
			if(data==null || data.equals(""))continue;
			String xpath="//input[@name='"+name+"']";
			Boolean editableStatus =editableOrNotList.get(i);
			if (editableStatus==false) {
				rLib.test.skip("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is <b>NOT EDITABLE</b>");
			}
			else {
				try {	
					if (typeName.equals("string") ) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB+"_"+randNum);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("password")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("integer")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, errorMsg.get(typeName));
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("double")||typeName.equals("currency")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, errorMsg.get(typeName));
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("email") ) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, errorMsg.get(typeName));
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("url")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, errorMsg.get(typeName));
						passReport(rLib, i, element, data, xpath, typeName);
					}else if (typeName.equals("phone")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("text")) {
						xpath="//textarea[@name='"+name+"']";
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("reference")) {
						xpath="//input[@data-fieldname='"+name+"']";
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);	
					}
					else if(typeName.equals("date")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, errorMsg.get(typeName));
						passReport(rLib, i, element, data, xpath, typeName);
						driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
					}
					else if(typeName.equals("time")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
						driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
					}
					else if(typeName.equals("datetime")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
						driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
					}

					else if(typeName.equals("file")) {
						data=IPathConstants.TEST_FILE_PATH;
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("multipicklist")) {
						xpath="//select[contains(@name,'"+name+"')]";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("picklist")) {
						xpath="//select[@name='"+name+"']";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("owner")) {
						xpath="//select[@name='"+name+"']";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}

					else if(typeName.equals("boolean")) {
						xpath="//input[@data-fieldname='"+name+"']";
						wLib.scrollTillElement(driver, driver.findElement(By.xpath(xpath+"/ancestor::div[@class='fieldBlockContainer']")));
						try {
							driver.findElement(By.xpath(xpath)).click();
						}catch (Exception e) {
							wLib.scrollUp(driver, 100);
							driver.findElement(By.xpath(xpath)).click();
						}
						passReport(rLib, i, element, data, xpath, typeName);	
					}
					else {
						rLib.test.warning("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>For <b>"+element+"</b> element not written any logic <br> Please write logic for <b>"+element+"</b> element <br> TYPE of the <b>"+element+"</b> is : <b>"+typeName+"</b> <br> Name of the <b>"+element+"</b> is : <b>"+name+"</b> ");
					}
				}catch (Exception e) {
					StringWriter sw=new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					rLib.test.fail("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is EDITABLE other reson is below <br>"+sw.toString());
				}
			}
		}
	}
	public static void enterNonMandatoryFieldsInEditPageForRelatedModule(WebDriver driver, String module,String elementJsonSchema,ReportUtility rLib,JavaUtility jLib, WebDriverUtility wLib,Map<String, String> fieldData) {

		List<String> labelsList = JsonPath.read(elementJsonSchema, "$.fields[*].label");
		List<String> namesList = JsonPath.read(elementJsonSchema, "$.fields[*].name");
		List<Boolean> editableOrNotList = JsonPath.read(elementJsonSchema, "$.fields[*].editable");
		List<String> typeNameList = JsonPath.read(elementJsonSchema, "$.fields[*].type.name");
		List<Boolean> mandatoryOrNotList= JsonPath.read(elementJsonSchema, "$.fields[*].mandatory");


		rLib.createTest(module);//reporting purpose
		rLib.test.info("Total Elements Present in "+module+" modules <b>"+labelsList.size()+"</b> <br> <b>List of all Elements in "+module+" module:</b> <br>"+labelsList);


		for(int i=0;i<typeNameList.size();i++) {
			String typeName = typeNameList.get(i);

			String element=labelsList.get(i);
			String name=namesList.get(i);
			String data=fieldData.get(element+"###"+name);
			if(data==null || data.equals(""))continue;
			String xpath="//input[@name='"+name+"']";
			Boolean editableStatus =editableOrNotList.get(i);
			Boolean mandatoryStatus =mandatoryOrNotList.get(i);
			if(mandatoryStatus==false) {
				if (editableStatus==false) {
					rLib.test.skip("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is <b>NOT EDITABLE</b>");
				}
				else {
					try {	
						if (typeName.equals("string") || typeName.equals("password")||
								typeName.equals("currency") || typeName.equals("double") || 
								typeName.equals("integer") ||typeName.equals("url") ||

								typeName.equals("email") ||typeName.equals("phone")) {
							driver.findElement(By.xpath(xpath)).clear();
							driver.findElement(By.xpath(xpath)).sendKeys(data);
							passReport(rLib, i, element, data, xpath, typeName);
						}
						else if(typeName.equals("text")) {
							xpath="//textarea[@name='"+name+"']";
							driver.findElement(By.xpath(xpath)).clear();
							driver.findElement(By.xpath(xpath)).sendKeys(data);
							passReport(rLib, i, element, data, xpath, typeName);
						}
						else if(typeName.equals("reference")) {
							xpath="//input[@data-fieldname='"+name+"']";
							driver.findElement(By.xpath(xpath)).clear();
							driver.findElement(By.xpath(xpath)).sendKeys(data);
							passReport(rLib, i, element, data, xpath, typeName);	
						}
						else if(typeName.equals("date")||typeName.equals("time")||
								typeName.equals("datetime")) {
							//						date=jLib.getSystemDateAndTimeInFormat("yyyy-MM-dd"); 
							//						time=jLib.getSystemDateAndTimeInFormat("hh:00 a");
							//						datetime=jLib.getSystemDateAndTimeInFormat("yyyy-MM-dd hh:mm aa");
							driver.findElement(By.xpath(xpath)).clear();
							driver.findElement(By.xpath(xpath)).sendKeys(data);
							passReport(rLib, i, element, data, xpath, typeName);
							driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
						}

						else if(typeName.equals("file")) {
							data=IPathConstants.TEST_FILE_PATH;
							driver.findElement(By.xpath(xpath)).sendKeys(data);
							passReport(rLib, i, element, data, xpath, typeName);
						}
						else if(typeName.equals("multipicklist")) {
							xpath="//select[contains(@name,'"+name+"')]";
							wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
							passReport(rLib, i, element, data, xpath, typeName);
						}
						else if(typeName.equals("owner")||typeName.equals("picklist")) {
							xpath="//select[@name='"+name+"']";
							wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
							passReport(rLib, i, element, data, xpath, typeName);
						}

						else if(typeName.equals("boolean")) {
							xpath="//input[@data-fieldname='"+name+"']";
							wLib.scrollTillElement(driver, driver.findElement(By.xpath(xpath+"/ancestor::div[@class='fieldBlockContainer']")));
							try {
								driver.findElement(By.xpath(xpath)).click();
							}catch (Exception e) {
								wLib.scrollUp(driver, 100);
								driver.findElement(By.xpath(xpath)).click();
							}
							passReport(rLib, i, element, data, xpath, typeName);	
						}
						else {
							rLib.test.warning("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>For <b>"+element+"</b> element not written any logic <br> Please write logic for <b>"+element+"</b> element <br> TYPE of the <b>"+element+"</b> is : <b>"+typeName+"</b> <br> Name of the <b>"+element+"</b> is : <b>"+name+"</b> ");
						}
					}catch (Exception e) {
						StringWriter sw=new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						rLib.test.fail("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is EDITABLE other reson is below <br>"+sw.toString());
					}
				}
			}
		}
	}
	
	public static void enterAndVerifyInvalidDataInEditPageForRelatedModule(int randNum,WebDriver driver,SoftAssert softAssert,String elementJsonSchema, String module,ReportUtility rLib,JavaUtility jLib, WebDriverUtility wLib,Map<String, String> fieldData) {
		List<String> labelsList = JsonPath.read(elementJsonSchema, "$.fields[*].label");
		List<String> namesList = JsonPath.read(elementJsonSchema, "$.fields[*].name");
		List<Boolean> editableOrNotList = JsonPath.read(elementJsonSchema, "$.fields[*].editable");
		List<String> typeNameList = JsonPath.read(elementJsonSchema, "$.fields[*].type.name");
		rLib.createTest(module+" with invalid data");//reporting purpose
		rLib.test.info("Total Elements Present in "+module+" modules <b>"+labelsList.size()+"</b> <br> <b>List of all Elements in "+module+" module:</b> <br>"+labelsList);
		Map<String, String> errorMsg = new ExcelUtility().getData("ErrorMsg");

		for(int i=0;i<typeNameList.size();i++) {
			String typeName = typeNameList.get(i);

			String element=labelsList.get(i);
			String name=namesList.get(i);
			String data=fieldData.get(element+"###"+name);
			if(data==null || data.equals(""))continue;
			String xpath="//input[@name='"+name+"']";
			Boolean editableStatus =editableOrNotList.get(i);
			if (editableStatus==false) {
				rLib.test.skip("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is <b>NOT EDITABLE</b>");
			}
			else {
				try {	
					if (typeName.equals("string") ) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB+"_"+randNum);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("password")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("integer")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, errorMsg.get(typeName));
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("double")||typeName.equals("currency")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, errorMsg.get(typeName));
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("email") ) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, errorMsg.get(typeName));
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if (typeName.equals("url")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, errorMsg.get(typeName));
						passReport(rLib, i, element, data, xpath, typeName);
					}else if (typeName.equals("phone")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("text")) {
						xpath="//textarea[@name='"+name+"']";
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("reference")) {
						xpath="//input[@data-fieldname='"+name+"']";
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);	
					}
					else if(typeName.equals("date")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, errorMsg.get(typeName));
						passReport(rLib, i, element, data, xpath, typeName);
						driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
					}
					else if(typeName.equals("time")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
						driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
					}
					else if(typeName.equals("datetime")) {
						driver.findElement(By.xpath(xpath)).clear();
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
						driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
					}

					else if(typeName.equals("file")) {
						data=IPathConstants.TEST_FILE_PATH;
						driver.findElement(By.xpath(xpath)).sendKeys(data,Keys.TAB);
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("multipicklist")) {
						xpath="//select[contains(@name,'"+name+"')]";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("picklist")) {
						xpath="//select[@name='"+name+"']";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}
					else if(typeName.equals("owner")) {
						xpath="//select[@name='"+name+"']";
						wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
						errorMsgVerification(driver, xpath, softAssert, "");
						passReport(rLib, i, element, data, xpath, typeName);
					}

					else if(typeName.equals("boolean")) {
						xpath="//input[@data-fieldname='"+name+"']";
						wLib.scrollTillElement(driver, driver.findElement(By.xpath(xpath+"/ancestor::div[@class='fieldBlockContainer']")));
						try {
							driver.findElement(By.xpath(xpath)).click();
						}catch (Exception e) {
							wLib.scrollUp(driver, 100);
							driver.findElement(By.xpath(xpath)).click();
						}
						passReport(rLib, i, element, data, xpath, typeName);	
					}
					else {
						rLib.test.warning("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>For <b>"+element+"</b> element not written any logic <br> Please write logic for <b>"+element+"</b> element <br> TYPE of the <b>"+element+"</b> is : <b>"+typeName+"</b> <br> Name of the <b>"+element+"</b> is : <b>"+name+"</b> ");
					}
				}catch (Exception e) {
					StringWriter sw=new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					rLib.test.fail("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is EDITABLE other reson is below <br>"+sw.toString());
				}
			}
		}
	}
	public static void errorMsgVerification(WebDriver driver,String xpath,SoftAssert softAssert, String expErrorMsg) {
		pauseProgram(3000);
		if(driver.findElement(By.xpath(xpath)).getAttribute("aria-invalid").equals("true")) {
			String qTipID = driver.findElement(By.xpath(xpath)).getAttribute("aria-describedby");
			String actualErrorMsg = driver.findElement(By.xpath("//div[@id='"+qTipID+"-content']")).getText();
			softAssert.assertEquals(actualErrorMsg, expErrorMsg);
			System.out.println("Error message verified");
		}
	}


	public static void enterNonMandatoryFieldsInEditPage(WebDriver driver, String module,String elementJsonSchema,ReportUtility rLib,JavaUtility jLib, WebDriverUtility wLib,Map<String, String> fieldData) {

		List<String> labelsList = JsonPath.read(elementJsonSchema, "$.fields[*].label");
		List<String> namesList = JsonPath.read(elementJsonSchema, "$.fields[*].name");
		List<Boolean> editableOrNotList = JsonPath.read(elementJsonSchema, "$.fields[*].editable");
		List<String> typeNameList = JsonPath.read(elementJsonSchema, "$.fields[*].type.name");
		List<Boolean> mandatoryOrNotList= JsonPath.read(elementJsonSchema, "$.fields[*].mandatory");


		rLib.createTest(module);//reporting purpose
		rLib.test.info("Total Elements Present in "+module+" modules <b>"+labelsList.size()+"</b> <br> <b>List of all Elements in "+module+" module:</b> <br>"+labelsList);

		navigateEditPage(driver, module);

		for(int i=0;i<typeNameList.size();i++) {
			String typeName = typeNameList.get(i);

			String element=labelsList.get(i);
			String name=namesList.get(i);
			String data=fieldData.get(element+"###"+name);
			if(data==null || data.equals(""))continue;
			String xpath="//input[@name='"+name+"']";
			Boolean editableStatus =editableOrNotList.get(i);
			Boolean mandatoryStatus =mandatoryOrNotList.get(i);
			if(mandatoryStatus==false) {
				if (editableStatus==false) {
					rLib.test.skip("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is <b>NOT EDITABLE</b>");
				}
				else {
					try {	
						if (typeName.equals("string") || typeName.equals("password")||
								typeName.equals("currency") || typeName.equals("double") || 
								typeName.equals("integer") ||typeName.equals("url") ||

								typeName.equals("email") ||typeName.equals("phone")) {
							driver.findElement(By.xpath(xpath)).clear();
							driver.findElement(By.xpath(xpath)).sendKeys(data);
							passReport(rLib, i, element, data, xpath, typeName);
						}
						else if(typeName.equals("text")) {
							xpath="//textarea[@name='"+name+"']";
							driver.findElement(By.xpath(xpath)).clear();
							driver.findElement(By.xpath(xpath)).sendKeys(data);
							passReport(rLib, i, element, data, xpath, typeName);
						}
						else if(typeName.equals("reference")) {
							xpath="//input[@data-fieldname='"+name+"']";
							driver.findElement(By.xpath(xpath)).clear();
							driver.findElement(By.xpath(xpath)).sendKeys(data);
							passReport(rLib, i, element, data, xpath, typeName);	
						}
						else if(typeName.equals("date")||typeName.equals("time")||
								typeName.equals("datetime")) {
							//						date=jLib.getSystemDateAndTimeInFormat("yyyy-MM-dd"); 
							//						time=jLib.getSystemDateAndTimeInFormat("hh:00 a");
							//						datetime=jLib.getSystemDateAndTimeInFormat("yyyy-MM-dd hh:mm aa");
							driver.findElement(By.xpath(xpath)).clear();
							driver.findElement(By.xpath(xpath)).sendKeys(data);
							passReport(rLib, i, element, data, xpath, typeName);
							driver.findElement(By.xpath(xpath)).sendKeys(Keys.ESCAPE);
						}

						else if(typeName.equals("file")) {
							data=IPathConstants.TEST_FILE_PATH;
							driver.findElement(By.xpath(xpath)).sendKeys(data);
							passReport(rLib, i, element, data, xpath, typeName);
						}
						else if(typeName.equals("multipicklist")) {
							xpath="//select[contains(@name,'"+name+"')]";
							wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
							passReport(rLib, i, element, data, xpath, typeName);
						}
						else if(typeName.equals("owner")||typeName.equals("picklist")) {
							xpath="//select[@name='"+name+"']";
							wLib.selectDropDown(data,driver.findElement(By.xpath(xpath)));
							passReport(rLib, i, element, data, xpath, typeName);
						}

						else if(typeName.equals("boolean")) {
							xpath="//input[@data-fieldname='"+name+"']";
							wLib.scrollTillElement(driver, driver.findElement(By.xpath(xpath+"/ancestor::div[@class='fieldBlockContainer']")));
							try {
								driver.findElement(By.xpath(xpath)).click();
							}catch (Exception e) {
								wLib.scrollUp(driver, 100);
								driver.findElement(By.xpath(xpath)).click();
							}
							passReport(rLib, i, element, data, xpath, typeName);	
						}
						else {
							rLib.test.warning("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>For <b>"+element+"</b> element not written any logic <br> Please write logic for <b>"+element+"</b> element <br> TYPE of the <b>"+element+"</b> is : <b>"+typeName+"</b> <br> Name of the <b>"+element+"</b> is : <b>"+name+"</b> ");
						}
					}catch (Exception e) {
						StringWriter sw=new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						rLib.test.fail("<u><b>Element "+(i+1)+" : "+element+"</b></u><br>Data not Entered in <b>"+element+"</b> element <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> <br> EDITABLE STATUS of the <b>"+element+"</b> element is : <b>"+editableStatus+"</b> <br> <b>Reason for Failing to enter data is : </b> "+element+" element is EDITABLE other reson is below <br>"+sw.toString());
					}
				}
			}
		}
	}


	public static void passReport(ReportUtility rLib,int index, String element,String data, String xpath, String typeName) {
		rLib.test.pass("<u><b>Element "+(index+1)+" : "+element+"</b></u><br>Data Entered successfully in <b>"+element+"</b> element as <b>'"+data+"'</b> <br> XPATH of the <b>"+element+"</b> element is : <b>"+xpath+"</b> <br> TYPE of the <b>"+element+"</b> element is : <b>"+typeName+"</b> ");

	}



	private static void navigateLink(WebDriver driver, String url) {
		driver.get(url);
	}
	public static void navigateEditPage(WebDriver driver, String module) {
		navigateLink(driver, "https://9wood-dev.ballistixcrm.com/index.php?module="+module+"&view=Edit");
	}
	
	public static void navigateViewPage(WebDriver driver, String module) {
		navigateLink(driver, "https://9wood-dev.ballistixcrm.com/index.php?module="+module+"&view=List&app=MARKETING");
	}
	public static void navigateRelatedModuleTabPage(WebDriver driver, String module) {
		navigateLink(driver, "https://9wood-dev.ballistixcrm.com/index.php?module="+module+"&view=List&app=MARKETING");
	}
	public static void addDataAndVerify(WebDriver driver, JavaUtility jLib, Map<String, String> elementData, String module,SoftAssert softAssert) {
		long randNum = jLib.getRandomNumber(3);
		List<String> expDatas=new ArrayList<>();
		for (Map.Entry<String, String> entry : elementData.entrySet()) {
			driver.findElement(By.id(entry.getKey())).clear();
			driver.findElement(By.id(entry.getKey())).sendKeys(entry.getValue()+randNum);
			expDatas.add(entry.getValue()+randNum);
		}	
		saveData(driver);

		pauseProgram(3000);

		try {	
			List<String> actDatas = driver.findElements(By.xpath("//span[@class='recordLabel pushDown']/span")).stream().map(e-> e.getText().trim()).toList();
			for (String actData : actDatas) {
				softAssert.assertTrue(expDatas.contains(actData),actData+" not present in verification page");
			}
		}
		catch (Throwable e) {
			List<String> actDatas1 = driver.findElements(By.xpath("//span[@class='recordLabel  pushDown']/span")).stream().map(e1-> e1.getText().trim()).toList();
			for (String actData : actDatas1) {
				softAssert.assertTrue(expDatas.contains(actData),actData+" not present in verification page");
			}
		}

		expDatas.clear();
		Reporter.log(module+" is edited successfully", true);
	}
	public static void editDataAndVerify(WebDriver driver, JavaUtility jLib, Map<String, String> elementData, String module,SoftAssert softAssert) {
		driver.findElement(By.xpath("//button[.='Edit']")).click();
		addDataAndVerify(driver, jLib, elementData, module,softAssert);
	}
	public static void delete(WebDriver driver, String module)
	{
		driver.findElement(By.xpath("//button[contains(.,'More')]//i")).click();
		driver.findElement(By.xpath("//a[contains(.,'Delete')]")).click();
		driver.findElement(By.xpath("//button[.='Yes']")).click();
		Reporter.log(module+" is deleted",true);
		pauseProgram(5000);
	}
	public static void pauseProgram(long milliSecond) {
		try {
			Thread.sleep(milliSecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void saveData(WebDriver driver, ReportUtility rLib) {
			driver.findElement(By.xpath("//button[.='Save']")).click();
		if(driver.getCurrentUrl().contains("view=Detail")) rLib.test.pass("Data Saved Successfully");
		else rLib.test.fail("Data Not Saved");

	}
	public static void catchHandling(Exception e, String module, SoftAssert softAssert) {

		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		e.printStackTrace(pw);
		String exce = sw.toString().split(":")[0];
		if(exce.contains("NoSuchElementException")) {
			softAssert.assertTrue(false,"Mandatory fields valid data are not entered inside Excel for "+module);
		}else if(exce.contains("NullPointerException")){
			softAssert.assertTrue(false,module+" not present in Excel");
		}
		else {
			softAssert.assertTrue(false,module+" is having other issue");
		}
	}


	public static void saveData(WebDriver driver) {
		driver.findElement(By.xpath("//button[.='Save']")).click();
	}
	public static void verifyErrorMessageInMandatoryField(WebDriver driver, SoftAssert softAssert,String expMandToolTip) {
		pauseProgram(4000);
		String actualMandToolTip = driver.findElement(By.xpath("//div[@class='qtip-content']")).getText();
		softAssert.assertEquals(actualMandToolTip, expMandToolTip);	
	}


	public static void verifyDataInDetailsPage(WebDriver driver, SoftAssert softAssert,String module, Map<String, String> fielData,boolean modifiedData) {
		pauseProgram(5000);
		try{driver.findElement(By.xpath("//strong[text()='Details']")).click();}catch (Exception e) {
			driver.findElement(By.xpath("//button[text()='Details']")).click();
			}
		pauseProgram(5000);
		List<WebElement> list = driver.findElements(By.xpath("//h4/img[@data-mode='hide']"));
		for(int i=0;i<list.size();i++) {
			try{list.get(i).click();}catch (Exception e) {			}
		}
		for (Entry<String, String> set : fielData.entrySet()) {
			String name = set.getKey().split("###")[1];
			name=name.contains("#")?name.replace("#", ""):name;
			if (set.getValue().equals("")) continue;
			String expValue=set.getValue();
			String xpath="//td[@id='"+module+"_detailView_fieldValue_"+name+"']";
			String value ="";
			try{ 
				value = driver.findElement(By.xpath(xpath)).getText();
			}catch (Exception e) {
				softAssert.fail("NO SUCH ELEMENT EXCEPTION ===> Module Name -> "+module+", Element Name -> "+set.getKey().split("###")[0]+", Xpath of the Element -> "+xpath+" Reason For Assertion Fail ====> No Such Element exception, Xpath of Element ");
			}

			String actualValue=value.contains("$")?value.replace("$ ", ""):value;
			if(modifiedData) softAssert.assertTrue(actualValue.contains(expValue),"Assertion Error ===> Actual Value -> "+actualValue+" ,Expected Value -> "+expValue+" === Verification of Modified data ==> Module Name -> "+module+", Element Name -> "+set.getKey().split("###")[0]+", Xpath of the Element -> "+xpath+" Reason For Assertion Fail ====> ");
			else softAssert.assertTrue(actualValue.contains(expValue),"Assertion Error ===> Actual Value -> "+actualValue+" ,Expected Value -> "+expValue+" === Module Name -> "+module+", Element Name -> "+set.getKey().split("###")[0]+", Xpath of the Element -> "+xpath+" Reason For Assertion Fail ====> ");
		}
	}
	public static void switchBackToHome(WebDriver driver, WebDriverUtility wLib) {
		navigateLink(driver, "https://9wood-dev.ballistixcrm.com/");
		try{
			wLib.switchToAlertPopUpWindowAndAccept(driver);
		}
		catch(Exception e)
		{}	
	}

	public static void enterAndVerifyInvalidDataInEditPageForRelatedModule(WebDriver driver,
			SoftAssert softAssert, String elementJsonSchema, String module, ReportUtility rLib, JavaUtility jLib,
			WebDriverUtility wLib, Map<String, String> fieldInvalidData) {
		// TODO Auto-generated method stub
		
	}
}