package utils.keys;

import utils.PageName;

public enum CartPageKey implements LocatorKey {
    TOTAL_AMOUNT("totalAmount"),
    PRODUCT_PRICE("productPrice"),
    TERMS_BTN("termsBtn");

    private final String key;
    CartPageKey(String key) { this.key = key; }
    @Override public PageName page() { return PageName.CARTPAGE; }
    @Override public String key()    { return key; }
}
