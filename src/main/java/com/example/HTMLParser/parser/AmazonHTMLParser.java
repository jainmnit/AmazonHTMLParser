package com.example.HTMLParser.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

import us.codecraft.xsoup.Xsoup;

@Component
public class AmazonHTMLParser implements HTMLParser{
	
	public static Logger logger = LoggerFactory.getLogger(AmazonHTMLParser.class);

	@Override
	public ProductDetails getProductDetails(String url) {

		ProductDetails productDetails = new ProductDetails();
		List<SellerOfferDetail> sellerOfferDetails = new ArrayList<SellerOfferDetail>();
		//productDetails.setSellerOfferDetails(new ArrayList<SellerOfferDetail>());
		try{
			System.out.println(url);
		Document doc = Jsoup.connect(url).get();
		Element productTitleEle = doc.select("span#productTitle").first();
		Element brandNameEle = doc.select("a#brand").first();
		Element ASINEle = doc.select("input[id=ASIN]").first();
		Element priceElement = doc.select("span#priceblock_ourprice").first();
		String hrefSeller= Xsoup.compile("//div[@id='olp_feature_div']/div/span/a/@href").evaluate(doc).get();
		System.out.println(hrefSeller);
		productDetails.setName(productTitleEle.html());
		productDetails.setBrand(brandNameEle.html());
		productDetails.setASIN(ASINEle.attr("value"));
		productDetails.setPrice(priceElement.html());
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
        final HtmlForm form1 = (HtmlForm) page.getForms().get(1);
        final HtmlSubmitInput button = form1.getInputByName("submit.add-to-cart");
        final HtmlPage page2 = button.click();
        final HtmlSpan elementById = (HtmlSpan)page2.getByXPath("//div[@id='huc-v2-order-row-center-inner']/div/div/div/div/span/span").get(1);
        productDetails.setIncartprice(elementById.getTextContent());
		}catch(Exception e){
			productDetails.setIncartprice(priceElement.html());
        	logger.error("Unable to parse URL from sending to next page: "+url,e);
        }
		System.out.println("Comes here");
		getSellerInfo(hrefSeller,sellerOfferDetails);
		}catch(Exception e){
			logger.error("Unable to parse URL: "+url,e);
		}
		productDetails.setSellerOfferDetails(sellerOfferDetails);
		return productDetails;
	
	}
	
	private void getSellerInfo(String sellerOfferPageURL, List<SellerOfferDetail> sellerOfferDetails) throws IOException {
		if(sellerOfferPageURL == null){
			return;
		}
		sellerOfferPageURL = "https://www.amazon.com"+sellerOfferPageURL;
		Document sellersURLDoc = Jsoup.connect(sellerOfferPageURL).get();
		System.out.println(sellerOfferPageURL);
		Elements divElementsForNext =  Xsoup.compile("//li[contains(@class, 'a-last') and contains(@class, 'a-disabled')]").evaluate(sellersURLDoc).getElements();
		
		String nextPage = Xsoup.compile("//li[@class='a-last']/a/@href").evaluate(sellersURLDoc).get();
		getDetails(sellerOfferDetails, sellersURLDoc);
		
		if(divElementsForNext != null && divElementsForNext.size() >0){
			return;
		}else{
			if(StringUtils.isEmpty(nextPage)){
				return;
			}else{
				getSellerInfo(nextPage,sellerOfferDetails);
			}
		}
	}
	
	private void getDetails(List<SellerOfferDetail> sellerOfferDetails, Document sellersURLDoc) {
		SellerOfferDetail sellerOfferDetail;
		Elements divElements = sellersURLDoc.select("div[role='row']");
		
		
		sellersURLDoc.getElementsByClass("a-last");
		
		for(Element divElementForSeller:divElements){
			sellerOfferDetail = new SellerOfferDetail();
			Elements gridElements = divElementForSeller.select("div[role='gridcell']");
			for(Element gridElementForSeller:gridElements){
				String priceSeller= Xsoup.compile("//span[contains(@class, 'Price')]/text()").evaluate(gridElementForSeller).get();
				String cnditionSeller= Xsoup.compile("//span[contains(@class, 'Condition')]/text()").evaluate(gridElementForSeller).get();
				String sellerName= Xsoup.compile("//h3[contains(@class, 'Seller')]/span/a/text()").evaluate(gridElementForSeller).get();
				String hrefSeller= Xsoup.compile("//h3[contains(@class, 'Seller')]/span/a/@href").evaluate(gridElementForSeller).get();
				if(StringUtils.isNoneEmpty(priceSeller)){
					sellerOfferDetails.add(sellerOfferDetail);
					sellerOfferDetail.setPrice(priceSeller);
					System.out.println(priceSeller);
				}
				else if(StringUtils.isNoneEmpty(cnditionSeller)){
					sellerOfferDetail.setCondition(cnditionSeller);
					System.out.println(cnditionSeller);
				}else if(StringUtils.isNoneEmpty(sellerName)){
					System.out.println(sellerName);
					sellerOfferDetail.setSellerName(sellerName);
					sellerOfferDetail.setSellerId(getQueryMap(hrefSeller).get("seller"));
				}
			}
		}
	}
	
	public static Map<String, String> getQueryMap(String query)  
	{  
	    String[] params = query.split("&");  
	    Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params)  
	    {  
	        String name = param.split("=")[0];  
	        String value = param.split("=")[1];  
	        map.put(name, value);  
	    }  
	    return map;  
	}

}
