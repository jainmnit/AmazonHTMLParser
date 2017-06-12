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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.example.HTMLParser.model.ProductDetails;
import com.example.HTMLParser.model.SellerOfferDetail;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

import us.codecraft.xsoup.Xsoup;

@Component
@PropertySource("classpath:amazon.properties")
public class AmazonHTMLParser implements HTMLParser{
	
	@Value("${amazon.productName}")
	private String productNameVar;
	
	@Value("${amazon.brandName}")
	private String brandNameVar;
	
	@Value("${amazon.asin}")
	private String asinVar;
	
	@Value("${amazon.price}")
	private String priceVar;
	
	@Value("${amazon.hrefSeller}")
	private String hrefSellerVar;
	
	@Value("${amazon.formInput}")
	private String formInputVar;
	
	@Value("${amazon.incartPrice}")
	private String incartPriceVar;
	
	@Value("${amazon.nextPageExist}")
	private String nextPageExist;
	
	@Value("${amazon.nextPageURL}")
	private String nextPageURL;
	
	@Value("${amazon.eachSellerInfo}")
	private String eachSellerInfo;
	
	@Value("${amazon.priceSeller}")
	private String priceSellerVar;
	
	@Value("${amazon.conditionSeller}")
	private String conditionSellerVar;
	
	@Value("${amazon.sellerName}")
	private String sellerNameVar;
	
	@Value("${amazon.hrefSellerindividual}")
	private String hrefSellerindividualVar;
	
	@Value("${amazon.sellersDiv}")
	private String sellersDiv;
	
	
	
	
	public static Logger logger = LoggerFactory.getLogger(AmazonHTMLParser.class);

	@Override
	public ProductDetails getProductDetails(String url) {

		ProductDetails productDetails = new ProductDetails();
		List<SellerOfferDetail> sellerOfferDetails = new ArrayList<SellerOfferDetail>();
		//productDetails.setSellerOfferDetails(new ArrayList<SellerOfferDetail>());
		try{
			System.out.println(url);
		Document doc = Jsoup.connect(url).get();
		String productName = Xsoup.compile(productNameVar).evaluate(doc).get();
		String brandName = Xsoup.compile(brandNameVar).evaluate(doc).get();
		String asinValue =  Xsoup.compile(asinVar).evaluate(doc).get();
		String price = Xsoup.compile(priceVar).evaluate(doc).get();
		String hrefSeller= Xsoup.compile(hrefSellerVar).evaluate(doc).get();
		System.out.println(hrefSeller);
		productDetails.setName(productName);
		productDetails.setBrand(brandName);
		productDetails.setASIN(asinValue);
		productDetails.setPrice(price);
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
        final HtmlSubmitInput button = form1.getInputByName(formInputVar);
        final HtmlPage page2 = button.click();
        String incartPrice = page2.getFirstByXPath(incartPriceVar);
        productDetails.setIncartprice(incartPrice);
		}catch(Exception e){
			productDetails.setIncartprice(price);
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
		Elements divElementsForNext =  Xsoup.compile(nextPageExist).evaluate(sellersURLDoc).getElements();
		String nextPage = Xsoup.compile(nextPageURL).evaluate(sellersURLDoc).get();
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
		Elements divElements = sellersURLDoc.select(sellersDiv);
		for(Element divElementForSeller:divElements){
			sellerOfferDetail = new SellerOfferDetail();
			Elements gridElements = divElementForSeller.select(eachSellerInfo);
			for(Element gridElementForSeller:gridElements){
				String priceSeller= Xsoup.compile(priceSellerVar).evaluate(gridElementForSeller).get();
				String cnditionSeller= Xsoup.compile(conditionSellerVar).evaluate(gridElementForSeller).get();
				String sellerName= Xsoup.compile(sellerNameVar).evaluate(gridElementForSeller).get();
				String hrefSeller= Xsoup.compile(hrefSellerindividualVar).evaluate(gridElementForSeller).get();
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
