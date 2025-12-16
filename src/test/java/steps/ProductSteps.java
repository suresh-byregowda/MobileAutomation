package steps;

import io.cucumber.java.en.*;
import pages.LandingPage;
import pages.ProductPage;

public class ProductSteps {

    private final LandingPage landing ;
    private final ProductPage product;

    public ProductSteps(LandingPage landing, ProductPage product) {
        this.landing = landing;
        this.product = product;
    }

    @Given("user is on product list page")
    public void on_product_page() {
        landing.selectCountry("India");
        landing.enterName("Sachi");
        landing.selectGender("Male");
        landing.clickShop();
    }

    @When("user adds first product to cart")
    public void add_product() {
        //product.addFirstProductToCart();
    }

    @When("user opens cart")
    public void open_cart() {
      //  product.openCart();
    }

    @Then("cart page should display selected product")
    public void verify_product() {
        // enhanced validations can be added
    }
}