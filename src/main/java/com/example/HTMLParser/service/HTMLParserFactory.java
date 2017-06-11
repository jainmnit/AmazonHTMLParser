package com.example.HTMLParser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.HTMLParser.parser.AmazonHTMLParser;
import com.example.HTMLParser.parser.GNCHTMLParser;
import com.example.HTMLParser.parser.GoogleHTMLParser;
import com.example.HTMLParser.parser.HTMLParser;
import com.example.HTMLParser.parser.WalMartHTMLParser;

@Service
public class HTMLParserFactory {

    @Autowired
    private AmazonHTMLParser amazonHTMLParser;

    @Autowired
    private GoogleHTMLParser googleHTMLParser;

    @Autowired
    private WalMartHTMLParser walMartHTMLParser;

    @Autowired
    private GNCHTMLParser gnchtmlParser;

    public HTMLParser getHTMLParser(String url) {

        if (url.startsWith("https://www.amazon.com")) {
            return amazonHTMLParser;
        } else if (url.startsWith("https://www.google.com/shopping")) {
            return googleHTMLParser;
        } else if (url.startsWith("https://www.walmart.com")) {
            return walMartHTMLParser;
        } else if (url.startsWith("http://www.gnc.com")) {
            return gnchtmlParser;
        }else{
        	return amazonHTMLParser;
        }
    }
}
