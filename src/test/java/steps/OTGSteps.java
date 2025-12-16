package steps;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.*;
import pages.otg.OtgLoginPage;
import pages.otg.OtgHomePage;
import pages.otg.OtgProjectPage;
import utils.ConfigReader;
import utils.ExcelUtility;
import utils.MobileUtility;
import utils.keys.OnTheGoKey;

public class OTGSteps {

    private final OtgLoginPage login;
    private final OtgHomePage home;
    private final OtgProjectPage project;

    // Cucumber requires zero-arg constructor
    public OTGSteps() {
        this.login = new OtgLoginPage();
        this.home = new OtgHomePage();
        this.project = new OtgProjectPage();
    }

    // ------------------------------------------------------------
    //                    LOGIN FLOW
    // ------------------------------------------------------------

    @Given("the user opens the OTG application")
    public void appLaunch() {
        login.waitForLoginPage();
    }

    @And("the user reaches the SSO login screen")
    public void reachLogin() {
        login.tapLogin();
    }

    // for test data using data table
//    @When("the user signs in using valid SSO credentials with {string} and {string}")
//    public void theUserSignsInUsingValidSSOCredentialsWithAnd(String username, String password) {
//        login.performSSOSignIn(username, password);
//    }


    @When("the user signs in using valid SSO credentials")
    public void ssoSignin() {

        //fetch data from excel
        login.excel_fetch_data();



//        ?using confi property file
//        String email = ConfigReader.get("sso.email");
//        String password = ConfigReader.get("sso.password");
//        login.performSSOSignIn(email, password);
    }

    @Then("the user should be successfully authenticated")
    public void authenticated() {
        home.verifyLoggedInUser();
    }

    @And("the user profile name should be visible on the home screen")
    public void verifyProfileName() {
        home.verifyLoggedInUser();
        home.goBackFromProfile();

    }

    // ------------------------------------------------------------
    //              PROJECT ALLOCATION FLOW
    // ------------------------------------------------------------

    @When("the user navigates to Project Allocation through Tools & Utilities")
    public void navigateToProjectAllocation() {
        home.navigateToProjectAllocation();
    }

    @Then("the user should see the current allocation details")
    public void verifyCurrentAllocation() {
        project.verifyCurrentAllocation();
    }

    @And("the user should see the past allocation details if any")
    public void verifyPastAllocation() {
        project.verifyPastAllocations();
    }

    // ------------------------------------------------------------
    //                  DIRECTORY FLOW
    // ------------------------------------------------------------

    @When("the user opens the Explore module")
    public void openExplore() {
        home.tapExplore();
    }

    @And("the user accesses the Brillio Directory section")
    public void openDirectory() {
        home.openDirectory();
    }

    @And("the user searches for a specific employee")
    public void directorySearch() {
        home.searchDirectoryPerson("Pawan");
    }

    @Then("the application should display the employee’s profile details")
    public void verifyDirectoryProfile() {
        home.verifyDirectoryUserDetails();
    }

    @And("the profile should show correct email and gender information")
    public void verifyEmailGender() {
        home.verifyDirectoryExtraDetails();
        home.smartBack(OnTheGoKey.BRILLIO_DIRECTORY_BACK_BTN);
        home.smartBack(OnTheGoKey.BRILLIO_DIRECTORY_BACK_BTN);
        home.smartBack(OnTheGoKey.BRILLIO_DIRECTORY_BACK_BTN);

    }

    // ------------------------------------------------------------
    //                      LOGOUT FLOW
    // ------------------------------------------------------------

    @When("the user opens the main menu")
    public void openMenu() {
        home.openUserMenu();
    }

    @And("the user initiates logout")
    public void clickLogout() {
        home.tapLogout();
    }

    @Then("the user should be logged out successfully")
    public void verifyLogout() {
        home.verifyLogoutSuccessful();
    }

    @And("the login button should be displayed again on the screen")
    public void verifyLoginButton() {
        login.waitForLoginPage();
    }

}
