package com.example.HTMLParser.model;

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

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getGTIN() {
		return GTIN;
	}

	public void setGTIN(String gTIN) {
		GTIN = gTIN;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	private String name;
	private String brand;
	private String ASIN;
	private String price;
	private String  incartprice;
	private String  partNumber;
	private String  GTIN;
	private String imageURL;
	
	private List<SellerOfferDetail> sellerOfferDetails;
	

}
