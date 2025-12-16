package utils.keys;

import utils.PageName;

public enum OnTheGoKey implements LocatorKey {
    DIRECTORY_SEARCH_ICON_2("directory_search_icon_2"),
    BRILLIO_DIRECTORY_ICON("brillio_Directory_icon"),
    USER_ICON_TOP_LEFT("user_icon_top_left"),
    CURRENT_ALLOCATION_DETAILS("current_allocation_details"),
    NEXT_BUTTON("Next_Button"),
    SEARCH_FIRST_RESULT("search_first_result"),
    HOME_ICON_TAIL_BAR("home_icon_tail_bar"),
    LOGIN("login"),
    EMAIL_INPUT("Email_Input"),
    PAST_ALLOCATIONS("past_allocations"),
    LOGOUT_BTN_1("logout_btn_1"),
    EXPLORE_TAIL_BAR("explore_tail_bar"),
    LOGOUT_BTN_2("logout_btn_2"),
    BRILLIO_DIRECTORY_BACK_BTN("Brillio_directory_back_btn"),
    PROFILE_BACK_BTN("profile_back_btn"),
    EMAIL_VERIFY("email_verify"),
    SIGNIN_BUTTON("Signin_Button"),
    TIMESHEET_BACKBTN("timesheet_backbtn"),
    DIRECTORY_SEARCH_BAR("directory_search_bar"),
    PROFILE_INFO("profile_info"),
    LOGOUT_BTN("logout_btn"),
    DIRECTORY_SEARCH_ICON("directory_search_icon"),
    GENDER_VERIFY("Gender_verify"),
    PASSWORD_INPUT("Password_Input"),
    TOOLS_UTILITY_TAIL_BAR("tools_utility_tail_bar"),
    PROJECT_ALLOCATION_ICON("project_allocation_icon"),
    USERNAME_VERIFICATION("username_verification");

    private final String key;
    OnTheGoKey(String key) { this.key = key; }
    @Override public PageName page() { return PageName.ONTHEGO; }
    @Override public String key()    { return key; }
}
