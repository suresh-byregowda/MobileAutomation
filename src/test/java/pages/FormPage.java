package pages;

import base.BasePage;
import hooks.Hooks;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import utils.LocatorLogic;
import utils.keys.FormPageKey;

import java.util.HashMap;

public class FormPage extends BasePage {

    private final LocatorLogic locator;

    public FormPage() {
        super(Hooks.getDriver());
        this.locator = new LocatorLogic(Hooks.getDriver());
    }

    /* ============================================================
                     WAIT FOR FORM READY
       ============================================================ */
    public void waitForFormReady() {
        perform("Wait for Form Page Ready", () -> locator.one_ele(FormPageKey.NAME_FIELD));
    }

    /* ============================================================
                         ENTER NAME
       ============================================================ */
    public void enterName(String name) {
        perform("Enter Name: " + name, () -> {

            WebElement nf = locator.one_ele(FormPageKey.NAME_FIELD);

            nf.clear();
            nf.sendKeys(name);

            try {
                driver.executeScript("mobile: hideKeyboard");
            } catch (Exception ignored) {}
        });
    }

    /* ============================================================
                         SELECT COUNTRY
       ============================================================ */
    public void selectCountry(String country) {

        perform("Select Country: " + country, () -> {

            locator.one_ele(FormPageKey.COUNTRY_SPINNER).click();

            /* -------------------- ANDROID -------------------- */
            if (isAndroid()) {

                String uiScroll = String.format(
                        "new UiScrollable(new UiSelector().scrollable(true).instance(0))" +
                                ".scrollIntoView(new UiSelector().text(\"%s\").instance(0));",
                        country
                );

                driver.findElement(AppiumBy.androidUIAutomator(uiScroll)).click();
                return;
            }

            /* ---------------------- iOS ---------------------- */
            if (isIOS()) {

                String iosChain = "**/XCUIElementTypeStaticText[`label == \"" + country + "\"`]";

                try {
                    driver.findElement(AppiumBy.iOSClassChain(iosChain)).click();
                } catch (Exception e) {

                    // fallback scroll
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("predicateString", "label == '" + country + "'");
                    params.put("direction", "down");

                    ((JavascriptExecutor) driver).executeScript("mobile:scroll", params);

                    // try again
                    driver.findElement(AppiumBy.iOSClassChain(iosChain)).click();
                }
            }
        });
    }

    /* ============================================================
                         TAP LET'S SHOP
       ============================================================ */
    public void tapLetsShop() {
        perform("Tap Let's Shop", () ->
                locator.one_ele(FormPageKey.LETS_SHOP_BTN).click()
        );
    }
}
