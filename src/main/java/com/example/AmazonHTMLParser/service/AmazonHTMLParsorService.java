package com.example.AmazonHTMLParser.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.AmazonHTMLParser.model.ProductDetails;
import com.example.AmazonHTMLParser.model.SellerOfferDetail;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

@Service
public class AmazonHTMLParsorService {
	public static Logger logger = LoggerFactory.getLogger(AmazonHTMLParsorService.class);

	
	public ProductDetails getDetails(String url) {
		ProductDetails productDetails = new ProductDetails();
		List<SellerOfferDetail> sellerOfferDetails = new ArrayList<SellerOfferDetail>();
		//productDetails.setSellerOfferDetails(new ArrayList<SellerOfferDetail>());
		try{
		Document doc = Jsoup.connect(url).get();
		Element productTitleEle = doc.select("span#productTitle").first();
		Element brandNameEle = doc.select("a#brand").first();
		Element ASINEle = doc.select("input[id=ASIN]").first();
		Element priceElement = doc.select("span#priceblock_ourprice").first();
		productDetails.setName(productTitleEle.html());
		productDetails.setBrand(brandNameEle.html());
		productDetails.setASIN(ASINEle.attr("value"));
		productDetails.setPrice(priceElement.html());
		try{
		WebClient webClient = new WebClient();
		webClient.getOptions().setUseInsecureSSL(true);
		
        HtmlPage page = webClient.getPage(url);
        final HtmlForm form1 = (HtmlForm) page.getForms().get(1);
        final HtmlSubmitInput button = form1.getInputByName("submit.add-to-cart");
        final HtmlPage page2 = button.click();
        DomElement elementById = page2.getElementById("huc-v2-order-row-center-inner");
        String nodeContent = elementById.getTextContent().trim();
        System.out.println("mine is"+nodeContent);
        System.out.println("mine length is"+nodeContent.length());
        String newContent = nodeContent.substring(nodeContent.indexOf("Cart subtotal (1 item):")+"Cart subtotal (1 item):".length()+3, nodeContent.length());
        productDetails.setIncartprice(newContent.trim());
		}catch(Exception e){
			productDetails.setIncartprice(priceElement.html());
        	logger.error("Unable to parse URL from sending to next page: "+url,e);
        }
		int pageNumber = 1;
		while(true){
			
			boolean endOfResults= getSellerInfo(productDetails.getASIN(),pageNumber,sellerOfferDetails);
			if(endOfResults){
				break;
			}
			pageNumber++;
		}
		}catch(Exception e){
			logger.error("Unable to parse URL: "+url,e);
		}
		productDetails.setSellerOfferDetails(sellerOfferDetails);
		return productDetails;
	}
	
	private boolean getSellerInfo(String asin, int pageNumber, List<SellerOfferDetail> sellerOfferDetails) throws IOException {
		SellerOfferDetail sellerOfferDetail;
		boolean endOfResults = false;
		String sellerOfferURL = "https://www.amazon.com/gp/offer-listing/"+asin;
		int startIndex = pageNumber*10;
		String sellerOfferPageURL = sellerOfferURL+"/ref=olp_page_"+pageNumber+"?ie=UTF8&startIndex="+startIndex;
		Document sellersURLDoc = Jsoup.connect(sellerOfferPageURL).get();
		System.out.println(sellerOfferPageURL);
		
		Elements divElements = sellersURLDoc.select("div[role='row']");
		Elements divElementsForNext =  sellersURLDoc.getElementsByClass("a-last");
		
		
		
		for(Element divElementForSeller:divElements){
			sellerOfferDetail = new SellerOfferDetail();
			
			Elements gridElements = divElementForSeller.select("div[role='gridcell']");
			for(Element gridElementForSeller:gridElements){
				Element spanTagForSeller = gridElementForSeller.select("span").first();
				String classAttr = spanTagForSeller.attr("class");
				//System.out.println(classAttr);
				if(classAttr.contains("Price")){
					String priceSeller = spanTagForSeller.html();
					sellerOfferDetail.setPrice(priceSeller);
					System.out.println(priceSeller);
				}
				else if(classAttr.contains("Condition")){
					String condition = spanTagForSeller.html();
					sellerOfferDetail.setCondition(condition);
					System.out.println(condition);
				}
				else if(classAttr.contains("a-size-medium")){
					Element anchorTagForSeller = spanTagForSeller.child(0);
					sellerOfferDetail.setSellerName(anchorTagForSeller.html());
					String sellerId = anchorTagForSeller.attr("href").substring(anchorTagForSeller.attr("href").indexOf("seller")+7);
					if(sellerId !=null){
						sellerOfferDetails.add(sellerOfferDetail);
					}
					sellerOfferDetail.setSellerId(sellerId);
					System.out.println(sellerId);
				}
			}
		}
		
		if(divElementsForNext != null && divElementsForNext.first() != null){
			String divElementsForNextClassName = divElementsForNext.first().attr("class");
			System.out.println("divElementsForNextClassName is"+divElementsForNextClassName);
			if(divElementsForNextClassName.contains("a-disabled")){
				System.out.println("Comes here");
				endOfResults = true;
			}
		}else{
			endOfResults = true;
		}
		
		return endOfResults;
	}
	
	
	
	
}
