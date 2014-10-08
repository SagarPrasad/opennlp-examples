package com.sagar.solr.custom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.solr.client.solrj.SolrServerException;

import opennlp.tools.util.Span;

import com.sagar.nlp.OpenNLPUtil;

public class BrandHelper {
	/**
	 * Returns the colors if identified from the String
	 * @param query
	 * @return
	 */
	public List<String> getBrands(String query, OpenNLPUtil extractor) {

		List<String> docArray = new ArrayList<String>();
    	docArray.add(query);
		List<String> resultArr = new ArrayList<String>();

    	for(String doc : docArray) {
    		System.out.println("--- "+ doc);
    		for (String sentence : extractor.segmentSentences(doc)) {
                String[] tokens = extractor.tokenizeSentence(sentence.toLowerCase());
                Span[] spans = extractor.findBrand(tokens);
                double[] spanProbs = extractor.findBrandProb(spans);
                int counter = 0;
                for (Span span : spans) {
                    System.out.print("brand: ");
                    String brand = null;
                    for (int i = span.getStart(); i < span.getEnd(); i++) {
                    	brand = tokens[i];
                        System.out.print(tokens[i]);
                        if (i < span.getEnd()) {
                            System.out.print(" ");
                        }
                    }
                    System.out.println("Probability is: "+spanProbs[counter]);
                    if(spanProbs[counter] > 0.9) {
                    	resultArr.add(brand);
                    }
                    counter++;
                }
            }
    	}
    	return resultArr;
	}
	
	
	public static void main(String[] args) throws SolrServerException, IOException {
		BasicConfigurator.configure();
		BrandHelper brandHelper = new BrandHelper();
		OpenNLPUtil util = new OpenNLPUtil();
		System.out.println("Result value : " + brandHelper.getBrands("Nike running shoes ",util));
		System.out.println("Result value : " + brandHelper.getBrands("ProGear sports red tshirt",util));
	}
	
	
}
