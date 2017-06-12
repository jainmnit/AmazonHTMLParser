package com.example.HTMLParser.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.example.HTMLParser.model.ProductDetails;
import com.example.HTMLParser.model.SellerOfferDetail;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import us.codecraft.xsoup.Xsoup;

@Component
@PropertySource("classpath:walmart.properties")
public class WalMartHTMLParser implements HTMLParser{
	
	@Value("${walmart.productName}")
	private String productNameVar;
	
	
	@Value("${walmart.imgProduct}")
	private String imgProductVar;
	
	@Value("${walmart.price}")
	private String priceVar;
	
	@Value("${walmart.productConfigTable}")
	private String productConfigTable;
	
	@Value("${walmart.productConfigValue}")
	private String productConfigValue;
	
	@Value("${walmart.productConfigName}")
	private String productConfigName;

	
	public static Logger logger = LoggerFactory.getLogger(WalMartHTMLParser.class);

	@Override
	public ProductDetails getProductDetails(String url) {

		ProductDetails productDetails = new ProductDetails();
		List<SellerOfferDetail> sellerOfferDetails = new ArrayList<SellerOfferDetail>();
		//productDetails.setSellerOfferDetails(new ArrayList<SellerOfferDetail>());
		try{
			System.out.println(url);
		Document doc = Jsoup.connect(url).get();
		WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setCssEnabled(false);//if you don't need css
		webClient.getOptions().setJavaScriptEnabled(false);//if you don't need js
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setAppletEnabled(false);
        HtmlPage page = webClient.getPage(url);
		String productTitle = page.getFirstByXPath(productNameVar);
		productDetails.setName(productTitle);
		
		String price  = page.getFirstByXPath(priceVar);
		
		productDetails.setPrice(price);
		String imgSrc = page.getFirstByXPath(imgProductVar);
		productDetails.setImageURL(imgSrc);
		getWalmartBrandInfo(productDetails,doc);
		}catch(Exception e){
			logger.error("Unable to parse URL: "+url,e);
		}
		productDetails.setSellerOfferDetails(sellerOfferDetails);
		return productDetails;
	
	}
	
	private void getWalmartBrandInfo(ProductDetails productDetails, Document doc) {
		Elements rowElements = Xsoup.compile(productConfigTable).evaluate(doc).getElements();
		for (Element element : rowElements) {
			String specsName = Xsoup.compile(productConfigName).evaluate(element).get();
			System.out.println("specsName is"+specsName);
			String specsValue = Xsoup.compile(productConfigValue).evaluate(element).get();
			if("Brand".equals(specsName)){
				productDetails.setBrand(specsValue);
			}
			if("Manufacturer Part Number".equals(specsName)){
				productDetails.setPartNumber(specsValue);
			}
			if("GTIN".equals(specsName)){
				productDetails.setGTIN(specsValue);
			}
		}
	}

}
