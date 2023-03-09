package com.qa.pages;

import com.qa.base.BaseTest;
import com.qa.utils.TestUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class LoginPage extends BaseTest {
    @iOSXCUITFindBy(id = "Username input field")
    @AndroidFindBy(accessibility = "Username input field")
    private WebElement userNameTxtFld;
    @iOSXCUITFindBy(id = "Password input field")
    @AndroidFindBy(accessibility = "Password input field")
    private WebElement passwordTxtFld;
    @iOSXCUITFindBy(id = "Login button")
    @AndroidFindBy(accessibility = "Login button")
    private WebElement loginButton;
    @iOSXCUITFindBy(id = "Provided credentials do not match any user in this service.")
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"generic-error-message\"]/android.widget.TextView")
    private WebElement errorTxt;

    public void enterUsername(String userName){
        sendKeys(userNameTxtFld, userName, "Login with: " + userName);
    }

    public void enterPassword(String password){
        sendKeys(passwordTxtFld, password, "Password: " + password);
    }

    public ProductsPage pressLoginBtn(){
        click(loginButton, "Press login button");
        return new ProductsPage();
    }

    public String getErrorText(){
        return getText(errorTxt, "Error text is: ");
    }

    public void noLoginButtonPresent(){
        waitForNoVisibility(loginButton);
    }
}
