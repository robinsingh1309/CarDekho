
package org.example;

import java.io.IOException;

import org.jsoup.nodes.Document;

import service.CDConnect;
import service.CDEnum;

public class App {

    public static void main(String[] args) throws IOException {
        
        CDConnect connectToCarDekho = new CDConnect();
        
        final String carDekhoUrl = CDEnum.CAR_DEKHO_NEW_CARS_URL.getValue();
        
        Document htmlDocument = connectToCarDekho.getHtmlDocument(carDekhoUrl);
        
        System.out.println(htmlDocument);
    }
    
}