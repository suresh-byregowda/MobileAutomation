package pages;

import base.BasePage;
import hooks.Hooks;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import utils.LocatorLogic;
import utils.keys.FormPageKey;

public class LandingPage extends BasePage {

    private final LocatorLogic locator;

    public LandingPage() {
        super(Hooks.getDriver());
        this.locator = new LocatorLogic(Hooks.getDriver());
    }

    /* ===================== ACTIONS ===================== */

    public void selectCountry(String country) {

        perform("Select Country: " + country, () -> {

            WebElement countryDropdown = locator.one_ele(FormPageKey.COUNTRY_SPINNER);
            countryDropdown.click();

            /* -------------------- ANDROID -------------------- */
            if (isAndroid()) {

                String uiScroll = "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                        + ".scrollIntoView(new UiSelector().text(\"" + country + "\"))";

                driver.findElement(AppiumBy.androidUIAutomator(uiScroll)).click();
                return;
            }

            /* ---------------------- iOS ---------------------- */
            if (isIOS()) {
                // iOS class chain scroll + tap
                String iosChain = "**/XCUIElementTypeStaticText[`label == \"" + country + "\"`]";

                try {
                    driver.findElement(AppiumBy.iOSClassChain(iosChain)).click();
                } catch (Exception e) {
                    // fallback to iOS scroll
                    var params = new java.util.HashMap<String, Object>();
                    params.put("predicateString", "label == '" + country + "'");
                    params.put("direction", "down");

                    ((JavascriptExecutor) driver).executeScript("mobile:scroll", params);

                    driver.findElement(AppiumBy.iOSClassChain(iosChain)).click();
                }
            }
        });
    }

    public void enterName(String name) {
        perform("Enter Name: " + name, () -> {
            WebElement nameField = locator.one_ele(FormPageKey.NAME_FIELD);
            nameField.clear();
            nameField.sendKeys(name);
        });
    }

    public void selectGender(String gender) {
        perform("Select Gender: " + gender, () -> {

            if (gender.equalsIgnoreCase("male")) {
                locator.one_ele(FormPageKey.RADIO_MALE).click();
            } else {
                locator.one_ele(FormPageKey.RADIO_FEMALE).click();
            }
        });
    }

    public void clickShop() {
        perform("Tap Let's Shop Button", () ->
                locator.one_ele(FormPageKey.LETS_SHOP_BTN).click()
        );
    }
}
