package extract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import service.CDConnect;
import service.CDEnum;


public class ElectricCarData {

    // Fields
    private final Logger logger = Logger.getLogger(ElectricCarData.class.getName());

    private CDConnect connectToCarDekho;
    private String dataFilePath;


    // Constructor
    public ElectricCarData(CDConnect connectToCarDekho, String dataFilePath) {
        this.connectToCarDekho = connectToCarDekho;
        this.dataFilePath = dataFilePath;
    }


    public void extractVehicleDataByBrand(String readElectricCarBrandFile) {

        try (BufferedReader reader = new BufferedReader(new FileReader(readElectricCarBrandFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(dataFilePath))

        ) {

            writer.write("Name,Price,Type,CC,Brand,Description");
            writer.newLine();

            String line;

            while ((line = reader.readLine()) != null) {

                String apiUrl = CDEnum.CAR_DEKHO_URL.getValue() + line;
                logger.info("Reading the URL: " + apiUrl);

                String[] urlParts = apiUrl.split("/");
                String cleanedVehicelBrand = urlParts[urlParts.length - 1];

                logger.info("Extracting the details of Car with brand: " + cleanedVehicelBrand);

                Document brandHtmlDocument = connectToCarDekho.getHtmlDocument(apiUrl);
                if (brandHtmlDocument == null) {
                    logger.info("No HTML document received from the server...");
                    return;
                }


                Elements dataElements = brandHtmlDocument.select(".append_list section.card.card_new");
                logger.info("dataElements size: " + dataElements.size());

                for (Element ele : dataElements) {
                    
                    String vehicleName = ele.select("h3 a").text(); 
                    String vehiclePrice = ele.select(".price span").text(); 
                    String vehicleType = "Electric"; 

                   
                    Elements specs = ele.select(".dotlist span");
                    String vehicleKwh = "";
                    for (Element spec : specs) {
                        if (spec.text().toLowerCase().contains("kwh")) {
                            vehicleKwh = spec.text();
                            break;
                        }
                    }
                    
                    
                    String vehicleDescription = "";
                    
                    String linkElement = ele.select("h3 a").attr("href"); 
                    
                    if (!linkElement.isBlank() || linkElement != null) 
                    {                        
                        logger.info("Vehicle Webpage URL: " + linkElement);
    
                        Document vehicleHtmlDocument = connectToCarDekho.getHtmlDocument(CDEnum.CAR_DEKHO_URL.getValue() + linkElement);
                        if (vehicleHtmlDocument == null) {
                            logger.info("No vehicle HTML received for: " + vehicleHtmlDocument);
                        }
    
                        vehicleDescription = extractVehicleDescription(vehicleHtmlDocument, vehicleName);
                    }

                    String cleanedVehicleName = escapeCsvField(vehicleName);
                    String cleanedVehiclePrice = escapeCsvField(vehiclePrice);
                    String cleanedVehicleType = escapeCsvField(vehicleType);
                    String cleanedVehicleKwh = escapeCsvField(vehicleKwh);
                    String cleanedVehicleDescription = escapeCsvField(vehicleDescription);

                    writer.write(cleanedVehicleName + "," + cleanedVehiclePrice + "," + cleanedVehicleType + ","
                            + cleanedVehicleKwh + "," + cleanedVehicelBrand + "," + cleanedVehicleDescription);
                    
                    writer.newLine();
                }


                logger.info("Data extracted successfully for the API endPoint: " + line);

                Thread.sleep(2000); // make request after 2 seconds for the next point
            }

        } catch (Exception e) {
            logger.info("Error: " + e.getMessage());
        }

        logger.info("Data extracted successfully...");
    }
    
    private String extractVehicleDescription(Document vehicleHtmlDocument, String vehicleName) {
        
        String[] selectors =
                {"#rf01 > div.app-content > div > main > div.gs_readmore.model-highlight.thcontent.carSummary.loaded",
                        "div.model-highlight.thcontent.carSummary", // fallback
                        "section.model-overview, div.model-overview" // extra fallback
                };

        for (String selector : selectors) {
            Elements descriptionElements = vehicleHtmlDocument.select(selector);
            if (!descriptionElements.isEmpty()) {
                return cleanText(descriptionElements.first().text());
            }
        }

        logger.info("Vehicle description not found for: " + vehicleName);
        return "N/A";
    }
    
    private String cleanText(String text) {

        if (text == null)
            return "";

        // Remove newlines, carriage returns, and multiple spaces
        return text.replaceAll("[\\r\\n]+", " ").replaceAll("\\s{2,}", " ").trim();
    }


    private String escapeCsvField(String field) {

        if (field == null) {
            return "\"\"";
        }

        // Check if field needs quoting
        boolean needsQuotes =
                field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r");

        if (needsQuotes) {
            // Escape internal quotes by doubling them
            String escaped = field.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        } else {
            return field;
        }
    }

}
