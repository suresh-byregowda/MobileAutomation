package pages;

import base.BasePage;
import factory.DriverFactory;
import hooks.Hooks;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import utils.LocatorLogic;
import utils.keys.ProductPageKey;

import java.util.HashMap;
import java.util.List;

public class ProductPage extends BasePage {

    private final LocatorLogic locator;

    public ProductPage() {
        super(DriverFactory.getDriver());
        AppiumDriver driver = DriverFactory.getDriver();
        this.locator = new LocatorLogic(driver);
    }

    /**
     * Cross-platform product add by name
     * - Android → UiScrollable
     * - iOS     → mobile:scroll
     */
    public void addProductByName(String name) {
        perform("Add Product: " + name, () -> {

            String platform = driver.getCapabilities().getPlatformName().toString().toLowerCase();
            boolean isAndroid = platform.contains("android");
            boolean isIOS     = platform.contains("ios");

            List<WebElement> productNames = locator.mul_ele(ProductPageKey.PRODUCT_NAME);
            List<WebElement> addButtons   = locator.mul_ele(ProductPageKey.ADD_CART_BTN);

            // First visible check
            for (int i = 0; i < productNames.size(); i++) {
                if (productNames.get(i).getText().trim().equalsIgnoreCase(name.trim())) {
                    addButtons.get(i).click();
                    return;
                }
            }

            // -----------------------------------
            // Android Scroll
            // -----------------------------------
            if (isAndroid) {
                String uiScroll = String.format(
                        "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                                + ".scrollIntoView(new UiSelector().text(\"%s\"))",
                        name
                );

                driver.findElement(AppiumBy.androidUIAutomator(uiScroll));
            }

            // -----------------------------------
            // iOS Scroll
            // -----------------------------------
            if (isIOS) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("predicateString", "label == '" + name + "'");
                params.put("direction", "down");

                ((JavascriptExecutor) driver).executeScript("mobile:scroll", params);
            }

            // -----------------------------------
            // Retry element search after scroll
            // -----------------------------------
            productNames = locator.mul_ele(ProductPageKey.PRODUCT_NAME);
            addButtons   = locator.mul_ele(ProductPageKey.ADD_CART_BTN);

            for (int i = 0; i < productNames.size(); i++) {
                if (productNames.get(i).getText().trim().equalsIgnoreCase(name.trim())) {
                    addButtons.get(i).click();
                    return;
                }
            }

            throw new RuntimeException("Product not found: " + name);
        });
    }

    public void goToCart() {
        perform("Go To Cart", () ->
                locator.one_ele(ProductPageKey.CART_BTN).click()
        );
    }

    public void verifyProductListDisplayed() {
        perform("Verify product list is displayed", () -> {
            List<WebElement> items = locator.mul_ele(ProductPageKey.PRODUCT_NAME);

            if (items.isEmpty() || !items.get(0).isDisplayed()) {
                throw new AssertionError("Product list not visible");
            }
        });
    }
}
