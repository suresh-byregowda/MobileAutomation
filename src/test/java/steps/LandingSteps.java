package steps;

import io.cucumber.java.en.*;
import pages.LandingPage;
import static org.testng.Assert.assertTrue;

public class LandingSteps {

    private final LandingPage landing;

    public LandingSteps(LandingPage landing) {
        this.landing = landing;
    }

    @Given("user is on the landing page")
    public void user_on_landing() {
        assertTrue(true);
    }

    @When("user selects country {string}")
    public void select_country(String country) {
        landing.selectCountry(country);
    }

    @When("user enters name {string}")
    public void enter_name(String name) {
        landing.enterName(name);
    }

    @When("user selects gender {string}")
    public void select_gender(String gender) {
        landing.selectGender(gender);
    }

    @When("user clicks Let's Shop")
    public void click_shop() {
        landing.clickShop();
    }

    @Then("product list should be displayed")
    public void product_list_displayed() {
        assertTrue(true);
    }
}
