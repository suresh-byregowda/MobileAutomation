package pages.otg;

import base.BasePage;
import hooks.Hooks;
import org.openqa.selenium.WebElement;
import utils.LocatorLogic;
import utils.MobileUtility;
import utils.keys.OnTheGoKey;

import static utils.MobileUtility.driver;
import static utils.MobileUtility.safeClick;

public class OtgHomePage extends BasePage {

    private final LocatorLogic locator;

    public OtgHomePage() {
        super(Hooks.getDriver());
        this.locator = new LocatorLogic(Hooks.getDriver());
    }

    // -------------------------------------------------------------------
    //                         LOGIN VERIFICATION
    // -------------------------------------------------------------------
    public void verifyLoggedInUser() {
        perform("Verify logged-in username", () -> {
            MobileUtility.performAndroidBack();
            MobileUtility.safeClick(locator.one_ele(OnTheGoKey.USER_ICON_TOP_LEFT), 50);
            locator.one_ele(OnTheGoKey.USERNAME_VERIFICATION).isDisplayed();
            MobileUtility.tapAt(700,400);
        });
    }

    // -------------------------------------------------------------------
    //                     PROJECT ALLOCATION NAVIGATION
    // -------------------------------------------------------------------
    public void navigateToProjectAllocation() {

        perform("Open Tools & Utility", () -> {

            try {
                locator.one_ele(OnTheGoKey.TOOLS_UTILITY_TAIL_BAR).click();
            } catch (Exception e) {

                // scroll + retry
                MobileUtility.scrollDown();

                WebElement ele = locator.one_ele(OnTheGoKey.TOOLS_UTILITY_TAIL_BAR);
                safeClick(ele, 30);
            }
        });

        perform("Open Project Allocation", () -> {
            WebElement btn = locator.one_ele(OnTheGoKey.PROJECT_ALLOCATION_ICON);
            safeClick(btn, 30);
        });
    }


    // -------------------------------------------------------------------
    //                     EXPLORE → DIRECTORY ACCESS
    // -------------------------------------------------------------------
    public void navigateToExploreDirectory() {

        perform("Tap Explore tab", () -> {
            WebElement explore = locator.one_ele(OnTheGoKey.EXPLORE_TAIL_BAR);
            safeClick(explore, 40);
        });

        perform("Open Brillio Directory", () -> {
            WebElement directory = locator.one_ele(OnTheGoKey.BRILLIO_DIRECTORY_ICON);
            safeClick(directory, 40);
        });
    }

    // -------------------------------------------------------------------
    //                          DIRECTORY SEARCH
    // -------------------------------------------------------------------
    public void searchDirectoryPerson(String name) {

        perform("Tap Directory Search Icon", () -> {
            WebElement searchIcon = locator.one_ele(OnTheGoKey.DIRECTORY_SEARCH_ICON);
            safeClick(searchIcon, 40);
        });

        perform("Enter Search Name", () ->
                locator.one_ele(OnTheGoKey.DIRECTORY_SEARCH_BAR).sendKeys(name));

        perform("Tap First Result", () -> {
            WebElement firstResult = locator.one_ele(OnTheGoKey.SEARCH_FIRST_RESULT);
            safeClick(firstResult, 40);
        });
    }

    // -------------------------------------------------------------------
    //                      VERIFY DIRECTORY USER INFO
    // -------------------------------------------------------------------
    public void verifyDirectoryUserDetails() {

        perform("Verify Profile Info Present", () ->
                locator.one_ele(OnTheGoKey.PROFILE_INFO));

        perform("Verify Gender Present", () ->
                locator.one_ele(OnTheGoKey.GENDER_VERIFY));

        perform("Verify Email Present", () ->
                locator.one_ele(OnTheGoKey.EMAIL_VERIFY));
    }

    public void verifyDirectoryExtraDetails() {
        perform("Verify Email + Gender", () -> {
            locator.one_ele(OnTheGoKey.EMAIL_VERIFY);
            locator.one_ele(OnTheGoKey.GENDER_VERIFY);
        });
    }

    // -------------------------------------------------------------------
    //                           USER MENU + LOGOUT
    // -------------------------------------------------------------------
    public void openUserMenu() {
        perform("Open User Menu", () -> {
            WebElement menu = locator.one_ele(OnTheGoKey.USER_ICON_TOP_LEFT);
            safeClick(menu, 40);
        });
    }

    public void tapLogout() {

        perform("Click Logout", () -> {
            WebElement logout = locator.one_ele(OnTheGoKey.LOGOUT_BTN);
            safeClick(logout, 40);
        });

        perform("Confirm Logout", () -> {
            WebElement confirm = locator.one_ele(OnTheGoKey.LOGOUT_BTN_1);
            safeClick(confirm, 40);
        });
    }

    public void verifyLogoutSuccessful() {
        perform("Verify Login button after logout", () ->
                locator.one_ele(OnTheGoKey.LOGIN));
    }

    // Convenience forwarding for Steps
    public void tapExplore() {
        WebElement explore = locator.one_ele(OnTheGoKey.EXPLORE_TAIL_BAR);
        safeClick(explore, 40);
    }

    public void openDirectory() {
        WebElement dir = locator.one_ele(OnTheGoKey.BRILLIO_DIRECTORY_ICON);
        safeClick(dir, 40);
    }

    public void goBackFromProfile() {
        perform("Go back from Profile page", () -> {
            try {
                locator.one_ele(OnTheGoKey.PROFILE_BACK_BTN).click();
            } catch (Exception e) {
                // fallback
                MobileUtility.performBack();
            }
        });
    }

    public static void smartBack(OnTheGoKey backKey) {
        try {
            WebElement back = new LocatorLogic(driver()).one_ele(backKey);
            safeClick(back, 30);
            return;
        } catch (Exception ignored) {}

        // fallback
        try {
            driver().navigate().back();
        } catch (Exception ignored) {}
    }

}
