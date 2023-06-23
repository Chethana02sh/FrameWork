package com.crm.genericUtilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 
 * @author SanjayBabu
 *
 */
public class ExcelUtility {
	/**
	 *its used to read the data from excel file  
	 * @return
	 */
	
	public int getRandNum()
	{
	Random randN = new Random();
	int randNum = randN.nextInt(100);
	return randNum;
	}
	
	public String readDataFromExcel(String sheetName,int rowNum,int cellNum) throws Throwable{
		FileInputStream fileInputStream = new FileInputStream(IPathConstants.EXCEL_FILE_PATH);
		Workbook workbook = WorkbookFactory.create(fileInputStream);
		Sheet sheet = workbook.getSheet(sheetName);
		Row row = sheet.getRow(rowNum);
		Cell cell = row.getCell(cellNum);
		String data = cell.toString();
		return data;
	}
	/**
	 * its used to write data into excel file
	 * @param sheetName
	 * @return
	 * @throws Throwable
	 */
	public void writeDataIntoExcel(String sheetName,int rowNum,int cellNum,String data) throws Throwable {
		FileInputStream fileInputStream=new FileInputStream(IPathConstants.EXCEL_FILE_PATH);
		Workbook workbook = WorkbookFactory.create(fileInputStream);
		Sheet sheet = workbook.getSheet(sheetName);
		Row row = sheet.getRow(rowNum);
		Cell cell = row.createCell(cellNum);
		cell.setCellValue(data);
		FileOutputStream fileOutputStream = new FileOutputStream(".\\src\\test\\resources\\TestData.xlsx");
		workbook.write(fileOutputStream);
	}
	/**
	 * This method is used to fetch elements
	 * @param sheetName
	 * @param moduleName
	 * @return
	 * @throws Throwable
	 */
	public List<Map<String, String>> getData(String sheetName, String moduleName)  {
		DataFormatter df = new DataFormatter();
		FileInputStream fisExcel = null;
		try {
			fisExcel = new FileInputStream(IPathConstants.EXCEL_FILE_PATH);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(fisExcel);
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fisExcel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Sheet sheet = wb.getSheet(sheetName);
		int rowCount = sheet.getLastRowNum(); //index
		List<Map<String, String>> listMap=new ArrayList<>();

		for (int i = 1; i <= rowCount; i++) {
			String moduleActName = df.formatCellValue(sheet.getRow(i).getCell(0));		
			if(moduleActName.equalsIgnoreCase(moduleName)) {
				for (int x = i+1;x<i+1+3 ; x++) {
					Map<String, String> map=new HashMap<>();
					for (int j = 1; j < sheet.getRow(i).getLastCellNum(); j++) {
						String key = df.formatCellValue(sheet.getRow(i).getCell(j));
						//random no
             			String value = df.formatCellValue(sheet.getRow(x).getCell(j));
						if(key.equals("")) {
							break;
						}
						map.put(key, value);
					}
					listMap.add(map);

				}
				break;
			}

		}
		try {
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(listMap.size()==0) {
			 throw new NullPointerException(moduleName+" not added in excel");
		}
		return listMap;
	}
/**
 * 
 * @param sheetName
 * @return
 */
	public Map<String, String> getData(String sheetName)  {
		DataFormatter df = new DataFormatter();
		FileInputStream fisExcel = null;
		try {
			fisExcel = new FileInputStream(IPathConstants.EXCEL_FILE_PATH);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(fisExcel);
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fisExcel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Sheet sheet = wb.getSheet(sheetName);
		int rowCount = sheet.getLastRowNum(); //index
		Map<String, String> map=new HashMap<>();

		for (int i = 0; i <= rowCount; i++) {
			String key = df.formatCellValue(sheet.getRow(i).getCell(0));
			String value = df.formatCellValue(sheet.getRow(i).getCell(1));
			if(key.equals("")) {
				break;
			}
			map.put(key, value);
		}
		try {
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
}