package com.example.HTMLParser.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.HTMLParser.model.ProductDetails;
import com.example.HTMLParser.model.SellerOfferDetail;

import us.codecraft.xsoup.Xsoup;

@Component
public class GNCHTMLParser implements HTMLParser{
	
	public static Logger logger = LoggerFactory.getLogger(GNCHTMLParser.class);

	@Override
	public ProductDetails getProductDetails(String url) {

		ProductDetails productDetails = new ProductDetails();
		List<SellerOfferDetail> sellerOfferDetails = new ArrayList<SellerOfferDetail>();
		//productDetails.setSellerOfferDetails(new ArrayList<SellerOfferDetail>());
		try{
			System.out.println(url);
		Document doc = Jsoup.connect(url).get();
		String productTitle = Xsoup.compile("//h1[@class='product-name']/text()").evaluate(doc).get();
		String producServing = Xsoup.compile("//div[@class='item-count']/text()").evaluate(doc).get();
		System.out.println(producServing.substring(6));
		productDetails.setName(productTitle+producServing.substring(6));
		
		
		String price = Xsoup.compile("//div[@class='product-price']/span[@class='price-sales']/text()").evaluate(doc).get();
		productDetails.setPrice(price);
		
		String imgSrc = Xsoup.compile("//div[@class='product-primary-image']/a/@data-href").evaluate(doc).get();
		productDetails.setImageURL(imgSrc);
		}catch(Exception e){
			logger.error("Unable to parse URL: "+url,e);
		}
		productDetails.setSellerOfferDetails(sellerOfferDetails);
		return productDetails;
	
	}

}
