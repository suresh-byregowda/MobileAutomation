package utils;

public enum PageName {
    FORMPAGE("FormPage"),
    PRODUCTPAGE("ProductPage"),
    CARTPAGE("CartPage"),
    ONTHEGO("onTheGo"),
    NEWPAGE("NewPage");


    private final String jsonKey;

    PageName(String jsonKey) {
        this.jsonKey = jsonKey;
    }

    public String jsonKey() {
        return jsonKey;
    }
}
