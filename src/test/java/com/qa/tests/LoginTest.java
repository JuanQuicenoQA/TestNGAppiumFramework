package com.qa.tests;

import com.aventstack.extentreports.Status;
import com.qa.base.BaseTest;
import com.qa.common.CommonMenuActions;
import com.qa.pages.LoginPage;
import com.qa.pages.MenuPage;
import com.qa.pages.ProductsPage;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class LoginTest extends BaseTest {
    LoginPage loginPage;
    MenuPage menuPage;
    ProductsPage productsPage;
    InputStream data;
    JSONObject loginUsers;
    TestUtils testUtils;
    CommonMenuActions commonMenuActions;


    @BeforeMethod
    public void beforeMethod(Method m) throws InterruptedException {
        launchApp();
        menuPage = new MenuPage();
        testUtils = new TestUtils();
        commonMenuActions = new CommonMenuActions();

        switch (getPlatform()) {
            case "Android" -> loginPage = commonMenuActions.logOutFromAndroidApp();
            case "iOS" -> loginPage = commonMenuActions.logOutFromIOSApp();
        }

        testUtils.log().info("\n" + "************* Start Test ******************");
        testUtils.log().info("Test name: " + m.getName());
    }

    @BeforeClass
    public void beforeClass() throws IOException {
        try {
            String dataFileName = "data/loginUsers.json";
            data = getClass().getClassLoader().getResourceAsStream(dataFileName);
            assert data != null;
            JSONTokener jSONTokener = new JSONTokener(data);
            loginUsers = new JSONObject(jSONTokener);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (data != null) {
                data.close();
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        testUtils = new TestUtils();
        testUtils.log().info("\n" + "************* End Test ******************" + "\n");
        closeApp();
    }

    @AfterClass
    public void afterClass() {
    }

    @Test
    public void invalidUserName() {
        loginPage.enterUsername(loginUsers.getJSONObject("invalidUserName").getString("username"));
        loginPage.enterPassword(loginUsers.getJSONObject("invalidUserName").getString("password"));
        productsPage = loginPage.pressLoginBtn();

        String actualErrorTxt = loginPage.getErrorText();
        String expectedErrorTxt = strings.get("error_invalid_username_or_password");

        Assert.assertEquals(actualErrorTxt, expectedErrorTxt);
    }

    @Test
    public void invalidUserPassword() {
        loginPage.enterUsername(loginUsers.getJSONObject("invalidUserPassword").getString("username"));
        loginPage.enterPassword(loginUsers.getJSONObject("invalidUserPassword").getString("password"));
        productsPage = loginPage.pressLoginBtn();

        String actualErrorTxt = loginPage.getErrorText();
        String expectedErrorTxt = strings.get("error_invalid_username_or_password");

        Assert.assertEquals(actualErrorTxt, expectedErrorTxt);
    }

    @Test
    public void successfullyLogin() {
        try {
            switch (getPlatform()) {
                case "Android" -> {
                    menuPage.pressHamburgerBtn();
                    productsPage = menuPage.pressCatalogOption();

                    loginPage.noLoginButtonPresent();
                    String titlePageTxt = productsPage.getTitle();
                    String expectedErrorTxt = strings.get("product_title");

                    //Just an example of how to scroll
                    productsPage.scrollPage();

                    Assert.assertEquals(titlePageTxt, expectedErrorTxt);
                }
                case "iOS" -> {
                    productsPage = menuPage.iOSPressCatalogOption();
                    String titlePageTxt = productsPage.getTitle();
                    String expectedErrorTxt = strings.get("product_title");

                    //Just an example of how to scroll
                    productsPage.scrollPage();

                    Assert.assertEquals(titlePageTxt, expectedErrorTxt);
                }
            }
        }
        catch (AssertionError assertionError) {
            testUtils.log().error("Assertion failed due to: ", assertionError);
            ExtentReport.getTest().log(Status.FAIL, "Assertion failed due to: " + assertionError);
            Assert.fail();
        }
    }
}
