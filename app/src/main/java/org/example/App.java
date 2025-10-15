
package org.example;

import java.io.IOException;

import extract.CarBrand;
import extract.CarData;
import service.CDConnect;

public class App {

    public static void main(String[] args) throws IOException {
        
        CDConnect connectToCarDekho = new CDConnect();
        
        final String carBrandFile = "/home/robin/eclipse-workspace/CarDekho/app/csv/car_brand_file.csv";
//        
//        CarBrand carBrand = new CarBrand(carBrandFile, connectToCarDekho);
//        carBrand.extractCarBrandApiEndPoint();
        
        final String dataFile = "/home/robin/eclipse-workspace/CarDekho/app/csv/data/car_data.csv";
        CarData carData = new CarData(connectToCarDekho, dataFile);
        
        carData.extractVehicleDataByBrand(carBrandFile);
        
    }
    
}