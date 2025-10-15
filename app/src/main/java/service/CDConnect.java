package service;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CDConnect {

	public Document getHtmlDocument(String webUrl) throws IOException 
	{
		Document htmlResponse = Jsoup.connect(webUrl)
								.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
								.referrer("https://www.cardekho.com/")
								.header("accept", "application/json, text/plain, */*")
								.timeout(10000)
								.method(Connection.Method.GET)
								.get();
		
		return htmlResponse;
	}
	
}