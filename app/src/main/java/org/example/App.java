
package org.example;

import java.io.IOException;

import extract.CarBrand;
import extract.CarData;
import extract.ElectricCarBrand;
import extract.ElectricCarData;
import extract.UpcomingCarData;
import service.CDConnect;


public class App {

    public static void main(String[] args) throws IOException {

        CDConnect connectToCarDekho = new CDConnect();

        // Extracting brand and saving it to a file
        final String carBrandFile = "/home/robin/eclipse-workspace/CarDekho/app/csv/car_brand_file.csv";

        CarBrand carBrand = new CarBrand(carBrandFile, connectToCarDekho);
        carBrand.extractCarBrandApiEndPoint();
        
        // TODO:need to review code
        // carBrand.extractCarBrandImage();


        // Car Data extracted by using the csv of car_brand_file
        final String dataFile = "/home/robin/eclipse-workspace/CarDekho/app/csv/data/car_data.csv";
        CarData carData = new CarData(connectToCarDekho, dataFile);

        carData.extractVehicleDataByBrand(carBrandFile);


        // Extracting Electric Car brand and saving it to a file
        final String electricCarBrandFile =
                "/home/robin/eclipse-workspace/CarDekho/app/csv/electric_car_brand_file.csv";

        ElectricCarBrand electricCarBrand = new ElectricCarBrand(electricCarBrandFile, connectToCarDekho);
        electricCarBrand.extractElectricCarBrandApiEndPoint();


        // Electric Car Data extracted by using the csv of electric_car_brand_file
        final String electricDataFile = "/home/robin/eclipse-workspace/CarDekho/app/csv/data/electric_car_data.csv";

        ElectricCarData electricCarData = new ElectricCarData(connectToCarDekho, electricDataFile);
        electricCarData.extractVehicleDataByBrand(electricCarBrandFile);
        
        
        // Upcoming Car data extraction and saving it to file
        final String upcomingCarDataFile = "/home/robin/eclipse-workspace/CarDekho/app/csv/data/upcoming_car_data.csv";
        
        final UpcomingCarData upcomingCarData = new UpcomingCarData(connectToCarDekho, upcomingCarDataFile);
        upcomingCarData.extractVehicleDataByBrand();
        
        
    }

}
