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


public class CarData {

    // Fields
    private final Logger logger = Logger.getLogger(CarData.class.getName());

    private CDConnect connectToCarDekho;
    private String dataFilePath;


    // Constructor
    public CarData(CDConnect connectToCarDekho, String dataFilePath) {
        this.connectToCarDekho = connectToCarDekho;
        this.dataFilePath = dataFilePath;
    }

    public void extractVehicleDataByBrand(String readCarBrandFile) {

        try (BufferedReader reader = new BufferedReader(new FileReader(readCarBrandFile));
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
                    continue;
                }


                Elements dataElements = brandHtmlDocument.select("ul > li.gsc_col-xs-12.gsc_col-sm-6.gsc_col-md-12.gsc_col-lg-12");
                logger.info("dataElements found: " + dataElements.size());


                for (Element ele : dataElements) {

                    String vehicleName = cleanText(ele.select("h3").text());
                    String vehiclePrice = cleanText(ele.select(".price").text());
                    
                    String vehicleDescription = "";
                    
                    // Find vehicle link safely
                    Element linkElement = ele.selectFirst("div.listView.holder.posS > a");
                    
                    if (linkElement != null) 
                    {                        
                        String vehicleWebpageUrl = linkElement.attr("href");
                        logger.info("Vehicle Webpage URL: " + vehicleWebpageUrl);
    
                        // Request vehicle webpage
                        Document vehicleHtmlDocument = connectToCarDekho.getHtmlDocument(vehicleWebpageUrl);
                        if (vehicleHtmlDocument == null) {
                            logger.info("No vehicle HTML received for: " + vehicleWebpageUrl);
                        }
    
                        vehicleDescription = extractVehicleDescription(vehicleHtmlDocument, vehicleName);
                    }else 
                    {
                        logger.info("No <a> tag found skipping...");
                    }

                    // Handle missing .dotlist gracefully
                    Elements dotLists = ele.select(".dotlist");

                    String vehicleType = "";
                    String vehicleCC = "";

                    if (dotLists.size() > 0) {
                        Element firstDotList = dotLists.get(0);
                        vehicleType = cleanText(firstDotList.select("span:nth-of-type(1)").text());
                    }

                    if (dotLists.size() > 1) {
                        Element lastDotList = dotLists.get(dotLists.size() - 1);
                        vehicleCC = cleanText(lastDotList.select("span:nth-of-type(1)").text());
                    }

                    // Escape fields for CSV
                    String cleanedVehicleName = escapeCsvField(vehicleName);
                    String cleanedVehiclePrice = escapeCsvField(vehiclePrice);
                    String cleanedVehicleType = escapeCsvField(vehicleType);
                    String cleanedVehicleCC = escapeCsvField(vehicleCC);
                    String cleanedVehicleDescription = escapeCsvField(vehicleDescription);

                    // Write data in one clean CSV line
                    writer.write(String.join(",", cleanedVehicleName, cleanedVehiclePrice, cleanedVehicleType,
                            cleanedVehicleCC, escapeCsvField(cleanedVehicelBrand),
                            escapeCsvField(cleanedVehicleDescription)));

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
