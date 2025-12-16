package steps;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import pages.CartPage;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

public class CartSteps {

    private final CartPage cart;

    public CartSteps(CartPage cart) {
        this.cart = cart;
    }

    @Given("user is on cart page")
    public void user_is_on_cart_page() {
        // A real check: cart icon or total text should be visible
      //  assertTrue(cart.isCartPageLoaded(), "Cart Page did not load!");
    }

    @Then("total amount should be visible")
    public void total_amount_should_be_visible() {
        double displayed = cart.displayedTotal();
        assertTrue(displayed > 0.0, "Total amount is not visible or invalid!");
    }

    @Then("total amount should match sum of products")
    public void total_amount_should_match_sum_of_products() {
        double expected = cart.sumOfProductPrices();
        double actual = cart.displayedTotal();

        assertEquals(actual, expected, "Total amount mismatch: product prices do not add up!");
    }
}
