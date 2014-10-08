package com.sagar.solr.custom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.solr.client.solrj.SolrServerException;

import opennlp.tools.util.Span;

import com.sagar.nlp.OpenNLPUtil;

public class ColorHelper {
	
		/**
		 * Returns the colors if identified from the String
		 * @param query
		 * @return
		 */
		public List<String> getColors(String query, OpenNLPUtil extractor) {

			List<String> docArray = new ArrayList<String>();
	    	docArray.add(query);
			List<String> resultArr = new ArrayList<String>();

	    	for(String doc : docArray) {
	    		for (String sentence : extractor.segmentSentences(doc)) {
	                String[] tokens = extractor.tokenizeSentence(sentence.toLowerCase());
	                Span[] spans = extractor.findColor(tokens);
	                double[] spanProbs = extractor.findColorProb(spans);
	                int counter = 0;
	                for (Span span : spans) {
	                    System.out.print("color: ");
	                    String color = null;
	                    for (int i = span.getStart(); i < span.getEnd(); i++) {
	                    	color = tokens[i];
	                        System.out.print(tokens[i]);
	                        if (i < span.getEnd()) {
	                            System.out.print(" ");
	                        }
	                    }
	                    System.out.println("Probability is: "+spanProbs[counter]);
	                    if(spanProbs[counter] > 0.9) {
	                    	resultArr.add(color);
	                    }
	                    counter++;
	                }
	            }
	    	}
	    	return resultArr;
		}
	
		public static void main(String[] args) throws SolrServerException, IOException {
			BasicConfigurator.configure();
			ColorHelper colorHelper = new ColorHelper();
			OpenNLPUtil util = new OpenNLPUtil();
			System.out.println("Result value : " + colorHelper.getColors("red blue shoes",util));
			System.out.println("Result value : " + colorHelper.getColors("black t-shirt under 20$",util));
		}
}
