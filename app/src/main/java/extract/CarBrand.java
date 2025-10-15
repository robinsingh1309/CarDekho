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

public class CarBrand {
    
    private CDConnect connectToCarDekho;
    private final String extractBrandApiEndpoint = CDEnum.CAR_DEKHO_EXTRACT_BRAND_URL.getValue();
    private String carBrandFile;
    
    private final Logger logger = Logger.getLogger(CarBrand.class.getName());
    
    public CarBrand(String carBrandFile) {
        connectToCarDekho = new CDConnect();
        this.carBrandFile = carBrandFile;
    }
    
    public void extractCarBrandApiEndPoint() throws IOException 
    {
        Document htmlDocument = connectToCarDekho.getHtmlDocument(extractBrandApiEndpoint);
        
        Elements brandNameElements =  htmlDocument.select("#brands > div > div.contentHold > div.gsc-ta-content.gsc-ta-active > ul > li");
        logger.info("extracting Brand");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(carBrandFile)))
        {
            for(Element brand: brandNameElements) 
            {
                String brandEndPoint = brand.getElementsByTag("a").attr("href");
                writer.write(brandEndPoint + "\n");
            }
        } catch (Exception e) {
            logger.info("Error: " + e.getMessage());
        }
        
        logger.info("File written successfully at "+ carBrandFile);
    }
    
}