package service;

public enum CDEnum {

    CAR_DEKHO_EXTRACT_BRAND_URL("https://www.cardekho.com/newcars"),
    CAR_DEKHO_URL("https://www.cardekho.com");
    

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
