package extract;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import service.CDConnect;
import service.CDEnum;


public class UpcomingCarData {

    // Fields
    private final Logger logger = Logger.getLogger(UpcomingCarData.class.getName());

    private CDConnect connectToCarDekho;
    private String dataFilePath;


    // Constructor
    public UpcomingCarData(CDConnect connectToCarDekho, String dataFilePath) {
        this.connectToCarDekho = connectToCarDekho;
        this.dataFilePath = dataFilePath;
    }

    public void extractVehicleDataByBrand() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFilePath))) {

            writer.write("Name,Price,FuelType,Image,Brand");
            writer.newLine();

            int page = 1;
            int pageCount = 1;

            while (page <= pageCount) {

                String apiUrl = String.format(CDEnum.CAR_DEKHO_UPCOMING_CAR_URL.getValue(), page);
                logger.info("Current URL: " + apiUrl);

                String jsonResponse = connectToCarDekho.getJson(apiUrl);
                if (jsonResponse == null) {
                    logger.info("Empty JSON response for Page : " + page);
                    page++;
                    continue;
                }

                JSONObject jsonResponseObject = new JSONObject(jsonResponse);

                JSONObject data = jsonResponseObject.getJSONObject("data");
                if (data == null || data.isEmpty()) {
                    logger.info("'data' object is missing from page:" + page);
                    page++;
                    continue;
                }

                // for pagination
                JSONObject meta = data.getJSONObject("_meta");

                pageCount = meta.getInt("pageCount");
                logger.info("Total pages: " + pageCount);

                JSONArray items = data.getJSONArray("items");
                if (items.isEmpty() || items.length() < 0) {
                    logger.info("'items' is either empty or it has no data present in the json Response");
                    return;
                }

                for (int i = 0; i < items.length(); i++) {

                    JSONObject car = items.optJSONObject(i);

                    if (car == null) {
                        logger.warning("Skipping null car object on page " + page);
                        continue;
                    }

                    String name = escapeCsvField(car.optString("name", "N/A"));
                    String price = escapeCsvField(car.optString("priceRange", "N/A"));
                    String fuelType = escapeCsvField(car.optString("fuelType", "N/A"));
                    String image = escapeCsvField(car.optString("image", "N/A"));
                    String brand = escapeCsvField(car.optString("brandName", "N/A"));

                    writer.write(String.join(",", name, price, fuelType, image, brand));
                    writer.newLine();
                }

                page++;
                logger.info("Page " + page + " processed successfully");

                Thread.sleep(2000);
            }

        } catch (Exception e) {
            logger.info("Error during extraction " + e.getMessage());
        }

        logger.info("All Data extracted successfully...");
    }

    // Simple CSV escaping utility
    private String escapeCsvField(String field) {

        if (field == null)
            return "\"\"";
        boolean needsQuotes = field.contains(",") || field.contains("\"") || field.contains("\n");

        if (needsQuotes) {
            field = field.replace("\"", "\"\"");
            return "\"" + field + "\"";
        }
        return field;
    }

}
