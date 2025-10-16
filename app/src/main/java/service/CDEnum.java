package service;

public enum CDEnum {

    CAR_DEKHO_EXTRACT_BRAND_URL("https://www.cardekho.com/newcars"),
    CAR_DEKHO_EXTRACT_ELECTRIC_CAR_BRAND_URL("https://www.cardekho.com/electric-cars"),
    CAR_DEKHO_URL("https://www.cardekho.com"),
    CAR_DEKHO_UPCOMING_CAR_URL("https://www.cardekho.com/api/v1/model/upcoming?_format=json&country_code=in&business_unit=car&connectoid=5ac3b117-9112-0dff-b59e-d79b7b1a00ab&sessionid=81916a51ee71e0b7bc8bbecc1882c253&lang_code=en&regionId=0&page=%s&pageSize=30&otherinfo=all&url=%%2Fupcomingcars&source=web");
    

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
