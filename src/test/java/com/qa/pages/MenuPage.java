package com.qa.pages;

import com.qa.base.BaseTest;
import com.qa.utils.TestUtils;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

public class MenuPage extends BaseTest {
    @iOSXCUITFindBy(id = "tab bar option menu")
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"open menu\"]/android.widget.ImageView")
    private WebElement productsPageHamburgerBtn;
    @iOSXCUITFindBy(id = "menu item log in")
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"menu item log in\"]/android.widget.TextView")
    private WebElement catalogLoginOption;

    @iOSXCUITFindBy(id = "menu item log out")
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"menu item log out\"]/android.widget.TextView")
    private WebElement catalogLogoutOption;

    @iOSXCUITFindBy(accessibility = "OK")
    private WebElement iOSConfirmationLogOut;

    @iOSXCUITFindBy(accessibility = "tab bar option catalog")
    private WebElement iOSCatalogOptionMenu;

    @iOSXCUITFindBy(id = "menu item catalog")
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"menu item catalog\"]/android.widget.TextView")
    private WebElement catalogOption;

    @iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeButton[`label == \"Log Out\"`]")
    @AndroidFindBy(id = "android:id/button1")
    private WebElement logOutConfirmationMessage;

    public void pressHamburgerBtn(){
        click(productsPageHamburgerBtn);
    }

    public void pressCatalogLoginOption(){
        click(catalogLoginOption);
    }

    public void pressCatalogLogoutOption(){
        click(catalogLogoutOption);
    }

    public void pressLogoutOptionConfirmationMessage(){
        click(logOutConfirmationMessage);
    }

    public void iOSPressOKConfirmationLogOut(){
        click(iOSConfirmationLogOut);
        new ProductsPage();
    }

    public ProductsPage pressCatalogOption(){
        click(catalogOption);
        return new ProductsPage();
    }

    public ProductsPage iOSPressCatalogOption(){
        click(iOSCatalogOptionMenu);
        return new ProductsPage();
    }
}
