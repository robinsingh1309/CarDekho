package service;

public enum CDEnum {

    CAR_DEKHO_NEW_CARS_URL("https://www.cardekho.com/maruti-suzuki-cars");


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
