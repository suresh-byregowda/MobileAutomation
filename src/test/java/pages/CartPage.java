package pages;

import base.BasePage;
import hooks.Hooks;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import utils.LocatorLogic;
import utils.keys.CartPageKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartPage extends BasePage {

    private final LocatorLogic locator;

    public CartPage() {
        super(Hooks.getDriver());
        this.locator = new LocatorLogic(Hooks.getDriver());
    }

    /* ===========================================================
          CALCULATE SUM OF PRODUCT PRICES
       =========================================================== */
    public double sumOfProductPrices() {
        return perform("Calculate Sum of Product Prices", () -> {

            List<WebElement> prices = locator.mul_ele(CartPageKey.PRODUCT_PRICE);

            return prices.stream()
                    .mapToDouble(e -> parsePrice(e.getText()))
                    .sum();
        });
    }

    /* ===========================================================
          GET DISPLAYED TOTAL
       =========================================================== */
    public double displayedTotal() {
        return perform("Get Displayed Cart Total", () -> {
            WebElement el = locator.one_ele(CartPageKey.TOTAL_AMOUNT);
            return parsePrice(el.getText());
        });
    }

    /* ===========================================================
          OPEN TERMS (Long Press) → Cross-Platform
       =========================================================== */
    public void openTerms() {
        perform("Open Terms & Conditions", () -> {

            WebElement termsBtn = locator.one_ele(CartPageKey.TERMS_BTN);

            try {
                if (isAndroid()) {
                    // ANDROID long click gesture
                    Map<String, Object> args = new HashMap<>();
                    args.put("elementId", ((RemoteWebElement) termsBtn).getId());
                    args.put("duration", 2000);
                    driver.executeScript("mobile: longClickGesture", args);
                }
                else if (isIOS()) {
                    // iOS touch-and-hold
                    Map<String, Object> args = new HashMap<>();
                    args.put("element", ((RemoteWebElement) termsBtn).getId());
                    args.put("duration", 2.0); // seconds
                    ((JavascriptExecutor) driver).executeScript("mobile:touchAndHold", args);
                }

            } catch (Exception e) {
                // Unsupported environment (BrowserStack) → Fallback to tap
                System.out.println("[CartPage] Long press not supported. Falling back to tap.");
                termsBtn.click();
            }
        });
    }

    /* ===========================================================
          PRICE PARSER (Safe for iOS + Android)
       =========================================================== */
    private double parsePrice(String text) {
        try {
            // Remove anything except numbers and decimal points
            String clean = text.replaceAll("[^0-9.]", "");
            return Double.parseDouble(clean);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse price: " + text);
        }
    }
}
