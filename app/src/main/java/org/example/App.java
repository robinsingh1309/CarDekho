
package org.example;

import java.io.IOException;

import extract.CarBrand;

public class App {

    public static void main(String[] args) throws IOException {
        
        final String carBrandFile = "/home/robin/eclipse-workspace/CarDekho/app/csv/car_brand_file.csv";
        
        CarBrand carBrand = new CarBrand(carBrandFile);
        carBrand.extractCarBrandApiEndPoint();
        
    }
    
}