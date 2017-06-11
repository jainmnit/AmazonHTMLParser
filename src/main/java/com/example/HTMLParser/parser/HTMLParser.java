package com.example.HTMLParser.parser;

import com.example.HTMLParser.model.ProductDetails;

public interface HTMLParser {
	ProductDetails getProductDetails(String url);
}
