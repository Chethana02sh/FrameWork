package com.crm.genericUtilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 
 * @author SanjayBabu
 *
 */
public class JavaUtility {
	/**
	 * its used to generate a random number
	 * @return
	 */
	public long getRandomNumber(int noOfDigits) {
		String format="%0"+noOfDigits+"d";
		Random random=new Random();
		long randNum = random.nextLong(Long.parseLong("1"+String.format(format, 0)));
		return randNum;
	}
	/**
	 * its used to get systemDateAndTime in IST Format
	 * @return
	 */
	public String getSystemDateAndTimeInISTformat() {
		Date date=new Date();
		return date.toString();
	}
	/**
	 * its used to get system date and Time in required format
	 * @return
	 */
	public String getSystemDateAndTimeInFormat() {
		Date date=new Date();
		String dateAndTime = date.toString();

		String YYYY = dateAndTime.split(" ")[5];
		String DD = dateAndTime.split(" ")[2];
		int MM = date.getMonth()+1;

		String finalFormat = YYYY+" "+DD+" "+MM;
		return finalFormat;
	}
	public String getSystemDateAndTimeInFormat(String format) {
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(new Date());
	}
}
