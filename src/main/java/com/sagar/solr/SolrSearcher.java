/**
 * 
 */
package com.sagar.solr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.text.ParseException;

import opennlp.tools.util.Span;

import org.apache.log4j.BasicConfigurator;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.sagar.nlp.OpenNLPUtil;

/**
 * @author A039883
 * 
 */
public class SolrSearcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws MalformedURLException,
			SolrServerException {
		BasicConfigurator.configure();
		/*
		 * HttpSolrServer solr = new
		 * HttpSolrServer("http://localhost:8983/solr/"); SolrQuery query = new
		 * SolrQuery(); query = new SolrQuery("camera");
		 * query.addFilterQuery("price:[* TO 10]"); //
		 * query.setFields("id","price","merchant","cat","store");
		 * //query.setStart(0); //query.set("defType", "edismax");
		 * 
		 * QueryResponse response = solr.query(query); SolrDocumentList results
		 * = response.getResults(); for (int i = 0; i < results.size(); ++i) {
		 * System.out.println(results.get(i)); }
		 */

		System.out.println("Result value : " + parseString("Camera under 10$"));
		System.out.println("Result value : " + parseString("Camera above 10$"));
		System.out.println("Result value : "
				+ parseString("red tshirt less than 20$"));
		System.out.println("Result value : "
				+ parseString("toys price more than 20$"));

	}

	public static String parseString(String queryString) {
		try {
			String money = hasMoney(queryString);
			if (null != money) {
				String num = getNumber(money);
				String filter = getqueryString(queryString, num, false);
				String query = getqueryString(queryString, num, true);
				return query + " | " + filter;
			}
		} catch (Exception e) {
			return queryString;
		}
		return queryString;
	}

	private static String getqueryString(String queryString, String price,
			boolean flag) throws IOException {
		OpenNLPUtil extractor = new OpenNLPUtil();
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
				return "price:[* TO " + price + "]";
			case "above":
			case "more than":
				return "price:[" + price + " TO *]";
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
		/*
		 * NumberFormat format = NumberFormat.getCurrencyInstance(); Number
		 * number = format.parse(money);
		 */
		// System.out.println(money.replaceAll("$", ""));
		
		money = money.replaceAll("$", "");
		money = money.replaceAll(" usd ", "");
		return money;
	}

	/**
	 * Finds the money from the query String e.g. "Camera under 40$" returns 40$
	 * 
	 * @param queryString
	 * @return
	 * @throws IOException
	 */
	private static String hasMoney(String queryString) throws IOException {
		OpenNLPUtil extractor = new OpenNLPUtil();
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
