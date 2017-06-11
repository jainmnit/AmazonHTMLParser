package com.example.HTMLParser.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.HTMLParser.model.ProductDetails;
import com.example.HTMLParser.model.SellerOfferDetail;

import us.codecraft.xsoup.Xsoup;

@Component
public class WalMartHTMLParser implements HTMLParser{
	
	public static Logger logger = LoggerFactory.getLogger(WalMartHTMLParser.class);

	@Override
	public ProductDetails getProductDetails(String url) {

		ProductDetails productDetails = new ProductDetails();
		List<SellerOfferDetail> sellerOfferDetails = new ArrayList<SellerOfferDetail>();
		//productDetails.setSellerOfferDetails(new ArrayList<SellerOfferDetail>());
		try{
			System.out.println(url);
		Document doc = Jsoup.connect(url).get();
		String productTitle = Xsoup.compile("//h1[contains(@class,'ProductTitle')]/div/text()").evaluate(doc).get();
		productDetails.setName(productTitle);
		
		String currency  = Xsoup.compile("//div[@class='prod-PriceHero']/span/span/span[@class='Price-group']/span[@class='Price-currency']/text()").evaluate(doc).get();
		
		String price = Xsoup.compile("//div[@class='prod-PriceHero']/span/span/span[@class='Price-group']/span[@class='Price-characteristic']/text()").evaluate(doc).get();
		String mantissa = Xsoup.compile("//div[@class='prod-PriceHero']/span/span/span[@class='Price-group']/span[@class='Price-mantissa']/text()").evaluate(doc).get();
		productDetails.setPrice(currency+price+"."+mantissa);
		String imgSrc = Xsoup.compile("//img[contains(@class,'prod-HeroImage-image')]/@src").evaluate(doc).get();
		productDetails.setImageURL(imgSrc);
		getWalmartBrandInfo(productDetails,doc);
		}catch(Exception e){
			logger.error("Unable to parse URL: "+url,e);
		}
		productDetails.setSellerOfferDetails(sellerOfferDetails);
		return productDetails;
	
	}
	
	private void getWalmartBrandInfo(ProductDetails productDetails, Document doc) {
		Elements rowElements = Xsoup.compile("//div[@class='Specifications']/div/div/div/div/table/tbody/tr").evaluate(doc).getElements();
		for (Element element : rowElements) {
			String specsName = Xsoup.compile("//td[contains(@class, 'name')]/text()").evaluate(element).get();
			System.out.println("specsName is"+specsName);
			String specsValue = Xsoup.compile("//td[contains(@class, 'value')]/div/text()").evaluate(element).get();
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
