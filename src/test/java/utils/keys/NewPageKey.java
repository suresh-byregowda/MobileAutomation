package utils.keys;

import utils.PageName;

public enum NewPageKey implements LocatorKey {
    SEARCH_INPUT("Search_Input"),
    SUBMIT_BUTTON("Submit_Button"),
    CANCEL_BUTTON("Cancel_Button");

    private final String key;
    NewPageKey(String key) { this.key = key; }
    @Override public PageName page() { return PageName.NEWPAGE; }
    @Override public String key()    { return key; }
}
