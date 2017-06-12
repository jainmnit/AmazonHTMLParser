package com.example.HTMLParser.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
@PropertySource("classpath:google.properties")
public class GoogleHTMLParser implements HTMLParser{
	
	@Value("${google.productName}")
	private String productNameVar;
	
	@Value("${google.price}")
	private String priceVar;
	
	@Value("${google.image}")
	private String imageVar;
	
	@Value("${google.nextPageURL}")
	private String nextPageURLVar;
	
	@Value("${google.eachSellerInfo}")
	private String eachSellerInfo;
	
	@Value("${google.priceSeller}")
	private String priceSellerVar;
	
	@Value("${google.sellerName}")
	private String sellerNameVar;
	
	@Value("${google.hrefSellerindividual}")
	private String hrefSellerindividualVar;
	
	
	@Value("${google.productConfigTable}")
	private String productConfigTable;
	
	@Value("${google.productConfigValue}")
	private String productConfigValue;
	
	@Value("${google.productConfigName}")
	private String productConfigName;
	
	
	public static Logger logger = LoggerFactory.getLogger(GoogleHTMLParser.class);

	@Override
	public ProductDetails getProductDetails(String url) {

		ProductDetails productDetails = new ProductDetails();
		List<SellerOfferDetail> sellerOfferDetails = new ArrayList<SellerOfferDetail>();
		//productDetails.setSellerOfferDetails(new ArrayList<SellerOfferDetail>());
		try{
			System.out.println(url);
			WebClient webClient = new WebClient();
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setCssEnabled(false);//if you don't need css
			webClient.getOptions().setJavaScriptEnabled(false);//if you don't need js
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setActiveXNative(false);
			webClient.getOptions().setAppletEnabled(false);
	        HtmlPage page = webClient.getPage(url);
		Document doc = Jsoup.connect(url).get();
		System.out.println(productNameVar);
		String title = page.getFirstByXPath(productNameVar);
		productDetails.setName(title);
		getGoogleBrandInfo(productDetails, doc);
		if(StringUtils.isEmpty(productDetails.getBrand()) || StringUtils.isEmpty(productDetails.getPartNumber())  || StringUtils.isEmpty(productDetails.getPartNumber())){
			String nextPageURL = Xsoup.compile(nextPageURLVar).evaluate(doc).get();
			if(StringUtils.isNotEmpty(nextPageURL)){
				nextPageURL = "https://www.google.com"+nextPageURL;
				Document extraPageInfodoc = Jsoup.connect(nextPageURL).get();
				getGoogleBrandInfo(productDetails, extraPageInfodoc);
			}
		}
		String imageURL =  page.getFirstByXPath(imageVar);
		productDetails.setImageURL(imageURL);
		String price = page.getFirstByXPath(priceVar);
		
		productDetails.setPrice(price);
		Elements sellerElements = Xsoup.compile(eachSellerInfo).evaluate(doc).getElements();
		SellerOfferDetail sellerOfferDetail;
		for (Element element : sellerElements) {
			sellerOfferDetail = new SellerOfferDetail();
			String sellerName = Xsoup.compile(sellerNameVar).evaluate(element).get();
			sellerOfferDetail.setSellerName(sellerName);
			String sellerURL = Xsoup.compile(hrefSellerindividualVar).evaluate(element).get();
			sellerURL = "https://www.google.com"+sellerURL;
			Document sellerDoc = Jsoup.connect(sellerURL).get();
			System.out.println("Base URL is"+sellerDoc.baseUri());
			sellerOfferDetail.setSellerProductURL(sellerDoc.baseUri());
			sellerOfferDetail.setSellerFQDN(sellerDoc.baseUri().split("/")[2]);
			String sellerPrice = Xsoup.compile(priceSellerVar).evaluate(element).get();
			sellerOfferDetail.setPrice(sellerPrice);
			sellerOfferDetails.add(sellerOfferDetail);

		}
		}catch(Exception e){
			logger.error("Unable to parse URL: "+url,e);
		}
		productDetails.setSellerOfferDetails(sellerOfferDetails);
		return productDetails;
	
	}
	
	private void getGoogleBrandInfo(ProductDetails productDetails, Document doc) {
		Elements specElements = Xsoup.compile(productConfigTable).evaluate(doc).getElements();
		for (Element element : specElements) {
			String specsName = Xsoup.compile(productConfigName).evaluate(element).get();
			System.out.println("specsName is"+specsName);
			String specsValue = Xsoup.compile(productConfigValue).evaluate(element).get();
			if("Brand".equals(specsName)){
				productDetails.setBrand(specsValue);
			}
			if("Part Number".equals(specsName)){
				productDetails.setPartNumber(specsValue);
			}
			if("GTIN".equals(specsName)){
				productDetails.setGTIN(specsValue);
			}
		}
	}

}
