package utils.keys;

import utils.PageName;

public enum FormPageKey implements LocatorKey {
    RADIO_MALE("radioMale"),
    LETS_SHOP_BTN("letsShopBtn"),
    NAME_FIELD("nameField"),
    COUNTRY_SPINNER("countrySpinner"),
    RADIO_FEMALE("radioFemale");

    private final String key;
    FormPageKey(String key) { this.key = key; }
    @Override public PageName page() { return PageName.FORMPAGE; }
    @Override public String key()    { return key; }
}
