package extract;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import service.CDConnect;
import service.CDEnum;


public class ElectricCarBrand {

    // Fields
    
    private final String extractElectricBrandApiEndpoint = CDEnum.CAR_DEKHO_EXTRACT_ELECTRIC_CAR_BRAND_URL.getValue();
    private final Logger logger = Logger.getLogger(ElectricCarBrand.class.getName());

    private CDConnect connectToCarDekho;
    private String electricCarBrandFile;
    
    // Constructor

    public ElectricCarBrand(String electricCarBrandFile, CDConnect connectToCarDekho) {
        this.connectToCarDekho = connectToCarDekho;
        this.electricCarBrandFile = electricCarBrandFile;
    }

    // Methods
    
    public void extractElectricCarBrandApiEndPoint() throws IOException {
        
        Document htmlDocument = connectToCarDekho.getHtmlDocument(extractElectricBrandApiEndpoint);

        Elements brandNameElements = htmlDocument.select("#rf01 > div.app-content > main > section.marginBottom20.evPopularBrands > div > div > ul > li");
        logger.info("extracting Brand");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(electricCarBrandFile))) {
            
            for (Element brand : brandNameElements) {
                String brandEndPoint = brand.getElementsByTag("a").attr("href");
                writer.write(brandEndPoint + "\n");
            }
            
        } catch (Exception e) {
            logger.info("Error: " + e.getMessage());
        }

        logger.info("File written successfully at " + electricCarBrandFile);
    }

}
