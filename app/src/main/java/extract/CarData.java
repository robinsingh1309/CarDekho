package extract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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


    public void testExtractVehicleDataByBrand() throws IOException {

        String apiUrl = CDEnum.CAR_DEKHO_URL.getValue() + "/cars/MG";
        String[] urlParts = apiUrl.split("/");
        String vehicelBrand = urlParts[urlParts.length - 1];
        
        logger.info("Reading the URL: " + apiUrl);

        Document brandHtmlDocument = connectToCarDekho.getHtmlDocument(apiUrl);
        if (brandHtmlDocument == null) {
            logger.info("No HTML document received from the server...");
            return;
        }

        // this will select the li where the data is stored
        Elements dataElements = brandHtmlDocument.select(
                "#rf01 > div.app-content > div > main > div > div.gsc_col-md-8.gsc_col-lg-9.gsc_col-sm-12.gsc_col-xs-12.BrandDesc > section.gsc_row.gsc_container_hold.heading.BrandPagelist.marginTop20 > ul > li");

        for (Element ele : dataElements) {
            
            String vehicleName = ele.select(".gsc_col-sm-12.gsc_col-xs-12.gsc_col-md-8.listView.holder.posS a h3").text();
            String vehiclePrice = ele.select(".gsc_col-sm-12.gsc_col-xs-12.gsc_col-md-8.listView.holder.posS .price").text();
            String vehicleType = ele.select(".clearfix > div:nth-child(1) > span:nth-child(1)").text();
            String vehicleCC = ele.select(".clearfix > div:nth-child(2) > span:nth-child(1)").text();

            System.out.println(
                                "Name: " + vehicleName + 
                                " | price: " + vehiclePrice +
                                " | type: " + vehicleType + 
                                " | CC: " + vehicleCC + 
                                " | Brand: " + vehicelBrand
                              );
        }
    }

    public void extractVehicleDataByBrand(String readCarBrandFile) {

        try 
            (   
                BufferedReader reader = new BufferedReader(new FileReader(readCarBrandFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(dataFilePath))
            
            ) {
                
            writer.write("Name,Price,Type,CC,Brand\n");
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

                
                Elements dataElements = brandHtmlDocument.select(
                        "#rf01 > div.app-content > div > main > div > div.gsc_col-md-8.gsc_col-lg-9.gsc_col-sm-12.gsc_col-xs-12.BrandDesc > section.gsc_row.gsc_container_hold.heading.BrandPagelist.marginTop20 > ul > li");
                logger.info("dataElements size: " + dataElements.size());


                for (Element ele : dataElements) {
                    
                    String vehicleName = ele.select(".gsc_col-sm-12.gsc_col-xs-12.gsc_col-md-8.listView.holder.posS a h3").text();
                    String vehiclePrice = ele.select(".gsc_col-sm-12.gsc_col-xs-12.gsc_col-md-8.listView.holder.posS .price").text();
                    String vehicleType = ele.select(".clearfix > div:nth-child(1) > span:nth-child(1)").text();
                    String vehicleCC = ele.select(".clearfix > div:nth-child(2) > span:nth-child(1)").text();
                    
                 // for Proper CSV formatting with quotes and escaping
                    String cleanedVehicleName = escapeCsvField(vehicleName);
                    String cleanedVehiclePrice = escapeCsvField(vehiclePrice);
                    String cleanedVehicleType  = escapeCsvField(vehicleType);
                    String cleanedVehicleCC = escapeCsvField(vehicleCC);
                    
                    
                    writer.write(   cleanedVehicleName + "," + 
                                    cleanedVehiclePrice + "," +
                                    cleanedVehicleType + "," + 
                                    cleanedVehicleCC + "," + 
                                    cleanedVehicelBrand + "\n"
                                );
                    
                }

                logger.info("Data extracted successfully for the API endPoint: " + line);
                
                Thread.sleep(2000); // make request after 2 seconds for the next point
            }

        } catch (Exception e) {
            logger.info("Error: " + e.getMessage());
        }

        logger.info("Data extracted successfully...");
    }
    
    private String escapeCsvField(String field) {
        
        if (field == null) {
            return "\"\"";
        }
        
        // Check if field needs quoting
        boolean needsQuotes = field.contains(",") || 
                             field.contains("\"") || 
                             field.contains("\n") || 
                             field.contains("\r");
        
        if (needsQuotes) {
            // Escape internal quotes by doubling them
            String escaped = field.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        } else {
            return field;
        }
    }

}
