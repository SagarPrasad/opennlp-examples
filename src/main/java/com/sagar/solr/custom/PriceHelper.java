/**
 * 
 */
package com.sagar.solr.custom;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.util.Span;

import org.apache.log4j.BasicConfigurator;
import org.apache.solr.client.solrj.SolrServerException;

import com.sagar.nlp.OpenNLPUtil;

/**
 * @author A039883
 * 
 */
public class PriceHelper {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws SolrServerException, IOException {
		BasicConfigurator.configure();
		PriceHelper pricehelper = new PriceHelper();
		OpenNLPUtil util = new OpenNLPUtil();
		System.out.println("Result value : " + pricehelper.parseString("Camera under 10$",util));
		System.out.println("Result value : " + pricehelper.parseString("Camera above 10$",util));
		System.out.println("Result value : "
				+ pricehelper.parseString("red tshirt less than 20$",util));
		System.out.println("Result value : "
				+ pricehelper.parseString("toys price more than 20$",util));
		System.out.println("Result value : "
				+ pricehelper.parseString("Camera with good battery life",util));

	}

	public Map<String, String> parseString(String queryString, OpenNLPUtil extractor) {
		Map<String, String> priceQueryParser = new HashMap<String, String>();
		priceQueryParser.put("query", queryString);
		try {
			String money = hasMoney(queryString, extractor);
			if (null != money) {
				//String num = getNumber(money);
				//System.out.println(" NUM " +  num);
				String filter = getqueryString(queryString, money, false, extractor);
				String query = getqueryString(queryString, money, true, extractor);
				priceQueryParser.put("query", query);
				priceQueryParser.put("filter", getNumber(filter));
				return priceQueryParser;
			}
		} catch (Exception e) {
			return priceQueryParser;
		}
		return priceQueryParser;
	}

	private static String getqueryString(String queryString, String price,	boolean flag, OpenNLPUtil extractor) throws IOException {
		String[] tokens = extractor.tokenizeSentence(queryString);
		int moneyPos = 0;

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equalsIgnoreCase(price)) {
				moneyPos = i;
			}
		}

		String prevValue = null;
		if (moneyPos > 1) {
			if (tokens[moneyPos - 1].equalsIgnoreCase("than")) {
				prevValue = tokens[moneyPos - 2] + " " + tokens[moneyPos - 1];
				if (flag) {
					return createQueryString(moneyPos - 2, tokens);
				}
			} else {
				prevValue = tokens[moneyPos - 1];
				if (flag) {
					return createQueryString(moneyPos - 1, tokens);
				}
			}

			// System.out.println("prev value :" + prevValue);
			switch (prevValue) {
			case "under":
			case "less than":
				return "[* TO " + price + "]";
			case "above":
			case "more than":
				return "[" + price + " TO *]";
			default:
				return queryString;
			}
		}
		return queryString;
	}

	private static String createQueryString(int j, String[] tokens) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < j; i++) {
			buffer.append(tokens[i]).append(" ");
		}

		return buffer.toString();
	}

	private static String getNumber(String money) throws ParseException {
		money = money.replace("$", "");
		money = money.replace(" usd ", "");
		return money;
	}

	/**
	 * Finds the money from the query String e.g. "Camera under 40$" returns 40$
	 * 
	 * @param queryString
	 * @return
	 * @throws IOException
	 */
	private String hasMoney(String queryString, OpenNLPUtil extractor) throws IOException {
		for (String sentence : extractor.segmentSentences(queryString)) {
			String[] tokens = extractor.tokenizeSentence(sentence);
			Span[] spans = extractor.findMoney(tokens);
			for (Span span : spans) {
				for (int position = span.getStart(); position < span.getEnd(); position++) {
					return tokens[position];
				}
			}
		}
		return null;
	}

}
