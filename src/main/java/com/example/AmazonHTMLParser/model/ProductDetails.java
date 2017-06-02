package com.example.AmazonHTMLParser.model;

import java.util.List;

public class ProductDetails {
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getASIN() {
		return ASIN;
	}

	public void setASIN(String aSIN) {
		ASIN = aSIN;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getIncartprice() {
		return incartprice;
	}

	public void setIncartprice(String incartprice) {
		this.incartprice = incartprice;
	}

	public List<SellerOfferDetail> getSellerOfferDetails() {
		return sellerOfferDetails;
	}

	public void setSellerOfferDetails(List<SellerOfferDetail> sellerOfferDetails) {
		this.sellerOfferDetails = sellerOfferDetails;
	}

	private String name;
	private String brand;
	private String ASIN;
	private String price;
	private String  incartprice;
	
	private List<SellerOfferDetail> sellerOfferDetails;
	

}
