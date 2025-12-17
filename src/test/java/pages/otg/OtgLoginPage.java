package pages.otg;

import base.BasePage;
import factory.DriverFactory;
import hooks.Hooks;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import utils.*;
import utils.keys.OnTheGoKey;

import java.util.List;
import java.util.Map;

public class OtgLoginPage extends BasePage {

    private final LocatorLogic locator;

    public OtgLoginPage() {
        super(DriverFactory.getDriver());
        AppiumDriver driver = DriverFactory.getDriver();
        this.locator = new LocatorLogic(driver);
    }

    // -------------------------------------------------------------------
    //                              LOGIN PAGE
    // -------------------------------------------------------------------
    public void waitForLoginPage() {
        perform(
                "Wait for OTG Login Page",
                () -> {
                    WebElement login =
                            locator.one_ele(OnTheGoKey.LOGIN);
                    waitForVisible(login);
                    return login; // important for Callable<T>
                },
                true   // ✅ force screenshot
        );
    }
    public void tapLogin() {
        perform("Tap Login Button", () -> {
            WebElement login = locator.one_ele(OnTheGoKey.LOGIN);
            MobileUtility.safeClick(login, 50);
            // Example of force screen shot
            clickWithScreenshot(login, "Tap Login Button");
        });


    }

    // -------------------------------------------------------------------
    //                           FULL SSO FLOW
    // -------------------------------------------------------------------


    public void excel_fetch_data() {

//        CSv_Fetch

        List<Map<String, String>> testData1 = CsvUtils.readCsv(ConfigReader.get("Csv_path"));

        for (Map<String, String> row : testData1) {
            String username = row.get("username");
            String password = row.get("password");
            System.out.println("Testing with: " + username + " | " + password);
            performSSOSignIn(username, password);
        }



//        excel_fetch
//        String excelPath = ConfigReader.get("Excel_file");
//        String Sheet_Name = ConfigReader.get("Sheet_Name");
//        ExcelUtility.setExcelFile(excelPath);
//
//        List<Map<String, String>> testData = ExcelUtility.getSheetData(Sheet_Name);
//
//        for (Map<String, String> data : testData) {
//            String username = data.get(ConfigReader.get("F_ColomnName"));
//            String password = data.get(ConfigReader.get("S_ColomnName"));
//            System.out.println(username + " | " + password);
//            performSSOSignIn(username, password);
//        }
//        int totalRows = ExcelUtility.getRowCount(Sheet_Name);
//        for (int i = 1; i < totalRows; i++) {
//
//            String username = ExcelUtility.getCellData(Sheet_Name, i, 0);
//            String password = ExcelUtility.getCellData(Sheet_Name, i, 1);
//            System.out.println("Login Attempt " + i + ": " + username + " | " + password);
//            performSSOSignIn(username, password);
//        }

//        ExcelUtility.closeExcel();


    }


    public void performSSOSignIn(String email, String password) {


        perform("Switch and wait for WebView", () ->
                MobileUtility.waitForWebView(60));

        perform("Enter Email", () ->
                locator.one_ele(OnTheGoKey.EMAIL_INPUT).sendKeys(email));

        perform("Tap Next", () -> {
            WebElement next = locator.one_ele(OnTheGoKey.NEXT_BUTTON);
            MobileUtility.safeClick(next, 40);
        });

        perform("Enter Password", () ->
                locator.one_ele(OnTheGoKey.PASSWORD_INPUT).sendKeys(password));

        perform("Tap Sign-In", () -> {
            WebElement sign = locator.one_ele(OnTheGoKey.SIGNIN_BUTTON);
            MobileUtility.safeClick(sign, 60);
        });

        perform("Switch back to Native", MobileUtility::switchToNative);
        // Handle permission pop-up
        perform("Handle permission popup", MobileUtility::handlePermissionPopup);
    }
}
