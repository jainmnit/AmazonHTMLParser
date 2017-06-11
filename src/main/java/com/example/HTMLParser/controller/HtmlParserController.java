package com.example.HTMLParser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.HTMLParser.model.ProductDetails;
import com.example.HTMLParser.parser.HTMLParser;
import com.example.HTMLParser.service.HTMLParserFactory;

@RestController
public class HtmlParserController {
	
	@Autowired
	private HTMLParserFactory htmlParserFactory;
 
    @RequestMapping(method = RequestMethod.GET, value = "/productDetails")
    @ResponseBody
    public ProductDetails getDetails(@RequestParam String url) {
        HTMLParser htmlParser = htmlParserFactory.getHTMLParser(url);
        return htmlParser.getProductDetails(url);
    }
}