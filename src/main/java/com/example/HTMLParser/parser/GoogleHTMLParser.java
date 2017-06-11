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
import org.springframework.stereotype.Component;

import com.example.HTMLParser.model.ProductDetails;
import com.example.HTMLParser.model.SellerOfferDetail;

import us.codecraft.xsoup.Xsoup;

@Component
public class GoogleHTMLParser implements HTMLParser{
	
	public static Logger logger = LoggerFactory.getLogger(GoogleHTMLParser.class);

	@Override
	public ProductDetails getProductDetails(String url) {

		ProductDetails productDetails = new ProductDetails();
		List<SellerOfferDetail> sellerOfferDetails = new ArrayList<SellerOfferDetail>();
		//productDetails.setSellerOfferDetails(new ArrayList<SellerOfferDetail>());
		try{
			System.out.println(url);
		Document doc = Jsoup.connect(url).get();
		Element productTitleEle = doc.select("h1#product-name").first();
		productDetails.setName(productTitleEle.html());
		getGoogleBrandInfo(productDetails, doc);
		if(StringUtils.isEmpty(productDetails.getBrand()) || StringUtils.isEmpty(productDetails.getPartNumber())  || StringUtils.isEmpty(productDetails.getPartNumber())){
			String nextPageURL = Xsoup.compile("//div[@id='specs']/div/div[@class='pag-bottom-links']/a[@class='pag-detail-link']/@href").evaluate(doc).get();
			if(StringUtils.isNotEmpty(nextPageURL)){
				nextPageURL = "https://www.google.com"+nextPageURL;
				Document extraPageInfodoc = Jsoup.connect(nextPageURL).get();
				getGoogleBrandInfo(productDetails, extraPageInfodoc);
			}
		}
		String imageURL = Xsoup.compile("//div[@id='pp-altimg-init-main']/img/@src").evaluate(doc).get();
		productDetails.setImageURL(imageURL);
		String price = Xsoup.compile("//div[@id='summary-prices']/span/span[@class='price']/text()").evaluate(doc).get();
		
		productDetails.setPrice(price);
		Elements sellerElements = Xsoup.compile("//div[@id='os-sellers-content']/table/tbody/tr[@class='os-row']").evaluate(doc).getElements();
		SellerOfferDetail sellerOfferDetail;
		for (Element element : sellerElements) {
			sellerOfferDetail = new SellerOfferDetail();
			String sellerName = Xsoup.compile("//td[contains(@class, 'os-seller-name')]/span/a/text()").evaluate(element).get();
			sellerOfferDetail.setSellerName(sellerName);
			String sellerURL = Xsoup.compile("//td[contains(@class, 'os-seller-name')]/span/a/@href").evaluate(element).get();
			sellerURL = "https://www.google.com"+sellerURL;
			Document sellerDoc = Jsoup.connect(sellerURL).get();
			System.out.println("Base URL is"+sellerDoc.baseUri());
			sellerOfferDetail.setSellerProductURL(sellerDoc.baseUri());
			sellerOfferDetail.setSellerFQDN(sellerDoc.baseUri().split("/")[2]);
			String sellerPrice = Xsoup.compile("//td[contains(@class, 'os-price-col')]/span[@class='os-base_price']/text()").evaluate(element).get();
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
		Elements specElements = Xsoup.compile("//div[@id='specs']/div/div/div[@class='specs-row']").evaluate(doc).getElements();
		for (Element element : specElements) {
			String specsName = Xsoup.compile("//span[contains(@class, 'specs-name')]/text()").evaluate(element).get();
			System.out.println("specsName is"+specsName);
			String specsValue = Xsoup.compile("//span[contains(@class, 'specs-value')]/text()").evaluate(element).get();
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
