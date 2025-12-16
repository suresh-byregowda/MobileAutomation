package utils.keys;

import utils.PageName;

public enum ProductPageKey implements LocatorKey {
    ADD_CART_BTN("addCartBtn"),
    CART_BTN("cartBtn"),
    PRODUCT_NAME("productName");

    private final String key;
    ProductPageKey(String key) { this.key = key; }
    @Override public PageName page() { return PageName.PRODUCTPAGE; }
    @Override public String key()    { return key; }
}
