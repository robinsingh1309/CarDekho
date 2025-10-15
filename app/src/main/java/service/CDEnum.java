package service;

public enum CDEnum {

    CAR_DEKHO_EXTRACT_BRAND_URL("https://www.cardekho.com/newcars");

    // Fields

    private String url;

    // Constructor

    private CDEnum(String url) {
        this.url = url;
    }

    // Getter

    public String getValue() {
        return url;
    }

}
