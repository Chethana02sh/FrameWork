package com.crm.genericUtilities;

import java.io.FileInputStream;
import java.util.Properties;
/**
 * 
 * @author SanjayBabu
 *
 */
public class FileUtility {
	/**
	 * its is used get common data from property file based on the key which you have specified as a argument
	 * @return
	 */
	public String getPropertKeyValue(String key) throws Throwable{
		FileInputStream fileInputStream=new FileInputStream(IPathConstants.PROPERTY_FILE_PATH);
		Properties pres=new Properties();
		pres.load(fileInputStream);
		String value = pres.getProperty(key);
		return value;
	}
}