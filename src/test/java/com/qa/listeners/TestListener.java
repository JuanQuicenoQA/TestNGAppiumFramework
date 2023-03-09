package com.qa.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.qa.base.BaseTest;
import com.qa.reports.ExtentReport;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TestListener implements ITestListener {
    public void onTestFailure(ITestResult result) {
        if (result.getThrowable() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            result.getThrowable().printStackTrace(pw);
            System.out.println(sw);
        }

        BaseTest baseTest = new BaseTest();
        File file = baseTest.getDriver().getScreenshotAs(OutputType.FILE);

        byte[] encoded = null;
        try {
            encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Map<String, String> params;
        params = result.getTestContext().getCurrentXmlTest().getAllParameters();

        String imagePath = "Screenshots" + File.separator + params.get("platformName") + "_" + params.get("platformVersion")
                + "_" + params.get("deviceName") + File.separator + baseTest.getDateTime() +
                File.separator + result.getTestClass().getRealClass().getSimpleName() + File.separator + result.getName() + ".png";

        try {
            FileUtils.copyFile(file, new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert encoded != null;
        ExtentReport.getTest().fail("Test Failed",
                MediaEntityBuilder.createScreenCaptureFromBase64String(new String(encoded, StandardCharsets.US_ASCII)).build());
    }

    @Override
    public void onTestStart(ITestResult result) {
        BaseTest baseTest = new BaseTest();
        ExtentReport.startTest(result.getName(), result.getMethod().getDescription())
                .assignCategory(baseTest.getPlatform() + "_" + baseTest.getDeviceName())
                .assignAuthor("Juan Quiceno");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReport.getTest().log(Status.PASS, "Test Passed");

    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReport.getTest().log(Status.SKIP, "Test Skipped");

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStart(ITestContext context) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReport.getReporter().flush();
    }
}
