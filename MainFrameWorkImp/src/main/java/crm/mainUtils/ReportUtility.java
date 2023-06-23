package crm.mainUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.crm.genericUtilities.IPathConstants;

public class ReportUtility {
	private  ExtentReports extent;
	public  ExtentTest test;
	
	public  void initReports() {
		if (Objects.isNull(extent)) {
			extent = new ExtentReports();
			ExtentSparkReporter spark = new ExtentSparkReporter(IPathConstants.EXTENT_TEST_PATH+"ProjectExcecutionReport.html");
			extent.attachReporter(spark);

			// spark.config().setEncoding("utf-8");
			spark.config().setTheme(Theme.STANDARD);
			spark.config().setDocumentTitle("crm 9wood Dev Ballistixcem - ALL");
			spark.config().setReportName("crm 9wood Dev Ballistixcem -  - ALL");

			extent.setSystemInfo("Application Name", "crm 9wood Dev Ballistixcem");
			extent.setSystemInfo("Base OS","Windows");
			extent.setSystemInfo("Base Browser", "Chrome");
		}
	}

	public  void flushReports() {
		if (Objects.nonNull(extent)) {
			extent.flush();
		}
	}

	public  void createTest(String testCaseName) {
		test = extent.createTest(testCaseName);
	}


}