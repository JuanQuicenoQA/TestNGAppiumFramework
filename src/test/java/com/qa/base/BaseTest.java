package com.qa.base;

import com.aventstack.extentreports.Status;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Properties;

public class BaseTest {

    protected static AppiumDriver driver;
    protected static Properties props;
    protected static HashMap<String, String> strings = new HashMap<String, String>();
    protected static String platform;
    protected static String device;
    protected static String dateTime;

    static InputStream inputStream;
    static InputStream stringsIs;
    private static AppiumDriverLocalService server;
    TestUtils testUtils = new TestUtils();


    public static Properties getProps() {
        return props;
    }

    public String getPlatform() {
        return platform;
    }

    public static void setPlatform(String platform) {
        BaseTest.platform = platform;
    }

    public String getDeviceName() {
        return device;
    }

    public static void setDeviceName(String deviceName) {
        BaseTest.device = deviceName;
    }

    public BaseTest() {
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    public AppiumDriver getDriver() {
        return driver;
    }


    public AppiumDriverLocalService getAppiumService(){
        return AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .withLogFile(new File("ServerLogs/appium.log")));
    }

    @BeforeSuite
    public void beforeSuite() {
        server = getAppiumService();
        System.out.println("****** Appium server started **********");
        server.start();
    }

    @Parameters({"platformName", "platformVersion", "deviceName", "avd"})
    @BeforeTest
    public void beforeTest(String platformName, String platformVersion, String deviceName, @Optional String avd) throws Exception {
        dateTime = testUtils.dateTime();
        setPlatform(platformName);
        setDeviceName(deviceName);
        URL url;

        String strFile = "DevicesLogs" + File.separator + platformName + "_" + platformVersion + "_" + deviceName;
        File logFile = new File(strFile);
        ThreadContext.put("ROUTINGKEY", strFile);

        try {
        props = new Properties();
        String propFileName = "config.properties";
        String xmlFileName = "strings/strings.xml";
        inputStream = BaseTest.class.getClassLoader().getResourceAsStream(propFileName);
        props.load(inputStream);

        stringsIs = getClass().getClassLoader().getResourceAsStream(xmlFileName);
        strings = testUtils.parseStringXML(stringsIs);

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
        caps.setCapability("newCommandTimeout", 300);

        //For Native or Hybrid Applications
        switch (platformName) {
            case "Android" -> {
                caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, props.getProperty("androidAutomationName"));
                caps.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
                caps.setCapability(MobileCapabilityType.PLATFORM_VERSION, platformVersion);
                caps.setCapability("avd", avd);
                String androidUrl = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                        + File.separator + "resources" + File.separator + "apps" + File.separator + "Android-MyDemoAppRN.1.3.0.build-244.apk";
                caps.setCapability("appPackage", props.getProperty("androidAppPackage"));
                caps.setCapability("appActivity", props.getProperty("androidAppActivity"));
                caps.setCapability("avdLaunchTimeout", 200000);
                testUtils.log().info("Android appUrl is: " + androidUrl);
                //caps.setCapability("apps", androidUrl);

                url = new URL(props.getProperty("appiumURL"));
                driver = new AndroidDriver(url, caps);
            }
            case "iOS" -> {
                caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, props.getProperty("iOSAutomationName"));
                caps.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
                caps.setCapability(MobileCapabilityType.PLATFORM_VERSION, platformVersion);
                caps.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
                String iOSUrl = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                        + File.separator + "resources" + File.separator + "apps" + File.separator + "MyRNDemoApp.app";
                caps.setCapability("simulatorStartupTimeout", 200000);
                caps.setCapability("bundleId", props.getProperty("iOSBundleId"));
                testUtils.log().info("iOS appUrl is: " + iOSUrl);
                //caps.setCapability("apps", iOSUrl);

                url = new URL(props.getProperty("appiumURL"));
                driver = new IOSDriver(url, caps);
            }
            default -> throw new Exception("Invalid platform");}
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
            if(strings != null){
                assert stringsIs != null;
                stringsIs.close();
            }
        }
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        if (getDriver() != null) {
            getDriver().quit();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        server.stop();
        System.out.println("****** Appium server ended **********");
    }

    public void waitForVisibility(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForNoVisibility(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    public void click(WebElement element) {
        waitForVisibility(element);
        element.click();
    }

    public void click(WebElement element, String msg) {
        waitForVisibility(element);
        testUtils.log().info(msg);
        ExtentReport.getTest().log(Status.INFO, msg);
        element.click();
    }

    public void sendKeys(WebElement element, String txt, String msg) {
        waitForVisibility(element);
        clear(element);
        testUtils.log().info(msg);
        ExtentReport.getTest().log(Status.INFO, msg);
        element.sendKeys(txt);
    }

    public void clear(WebElement element){
        waitForVisibility(element);
        element.clear();
    }

    public String getAttribute(WebElement element, String attribute) {
        waitForVisibility(element);
        return element.getAttribute(attribute);
    }

    public String getText(WebElement element, String msg) {
        String txt = msg;
        switch (platform) {
            case "Android" -> {
                msg = getAttribute(element, "text");
            }
            case "iOS" -> {
                msg = getAttribute(element, "label");
            }
        };
        testUtils.log().info(txt + msg);
        ExtentReport.getTest().log(Status.INFO, txt + msg);
        return msg;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void closeApp() {
        switch (getPlatform()) {
            case "Android" -> ((InteractsWithApps) getDriver()).terminateApp(getProps().getProperty("androidAppPackage"));
            case "iOS" -> ((InteractsWithApps) getDriver()).terminateApp(getProps().getProperty("iOSBundleId"));
        }
    }

    public void launchApp() {
        switch (getPlatform()) {
            case "Android" -> ((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("androidAppPackage"));
            case "iOS" -> ((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("iOSBundleId"));
        }
    }

    public void androidScrollToElement() {
        getDriver().findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector()" + ".scrollable(true)).scrollIntoView("
                        + "new UiSelector().text(\"© 2023 Sauce Labs. All Rights Reserved. Terms of Service | Privacy Policy.\"));"));
    }

    public void iOSScrollToElement() {
//	  RemoteWebElement element = (RemoteWebElement)getDriver().findElement(By.name("test-ADD TO CART"));
//	  String elementID = element.getId();
        HashMap<String, String> scrollObject = new HashMap<String, String>();
//	  scrollObject.put("element", elementID);
        scrollObject.put("direction", "down");
//	  scrollObject.put("predicateString", "label == 'ADD TO CART'");
//	  scrollObject.put("name", "test-ADD TO CART");
//	  scrollObject.put("toVisible", "");
        getDriver().executeScript("mobile:scroll", scrollObject);
    }

    public void scrollPage(){
        switch (platform) {
            case "Android" -> androidScrollToElement();
            case "iOS" -> iOSScrollToElement();
        };
    }
}
