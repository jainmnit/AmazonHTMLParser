package com.example.AmazonHTMLParser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.AmazonHTMLParser.model.ProductDetails;
import com.example.AmazonHTMLParser.service.AmazonHTMLParsorService;

@RestController
public class AmazonHtmlParserController {
	
	@Autowired
	private AmazonHTMLParsorService amazonHTMLParsorService;
 
    @RequestMapping(method = RequestMethod.GET, value = "/productDetails")
    @ResponseBody
    public ProductDetails getDetails(@RequestParam String url) {
        return amazonHTMLParsorService.getDetails(url);
    }
}