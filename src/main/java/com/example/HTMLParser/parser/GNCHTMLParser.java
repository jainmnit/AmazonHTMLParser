package com.example.HTMLParser.parser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.example.HTMLParser.model.ProductDetails;
import com.example.HTMLParser.model.SellerOfferDetail;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@Component
@PropertySource("classpath:gnc.properties")
public class GNCHTMLParser implements HTMLParser{
	
	public static Logger logger = LoggerFactory.getLogger(GNCHTMLParser.class);
	
	@Value("${gnc.productName}")
	private String productNameVar;
	
	
	@Value("${gnc.imgProduct}")
	private String imgProductVar;
	
	@Value("${gnc.price}")
	private String priceVar;
	
	@Override
	public ProductDetails getProductDetails(String url) {

		ProductDetails productDetails = new ProductDetails();
		List<SellerOfferDetail> sellerOfferDetails = new ArrayList<SellerOfferDetail>();
		//productDetails.setSellerOfferDetails(new ArrayList<SellerOfferDetail>());
		try{
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
	        System.out.println(productTitle);
	        productDetails.setName(productTitle);
	        String price = page.getFirstByXPath(priceVar);
	        productDetails.setPrice(price);
	        String imgSrc = page.getFirstByXPath(imgProductVar);
	        productDetails.setImageURL(imgSrc);
		}catch(Exception e){
			logger.error("Unable to parse URL: "+url,e);
		}
			productDetails.setSellerOfferDetails(sellerOfferDetails);
			return productDetails;
	
	}
	
	


}
