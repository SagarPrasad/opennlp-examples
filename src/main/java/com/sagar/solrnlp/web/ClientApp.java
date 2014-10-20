/**
 * 
 */
package com.sagar.solrnlp.web;

import static spark.Spark.*;

import com.sagar.solr.SolrCustomSearcher;


/**
 * @author spras3
 * 
 */
public class ClientApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		setPort(4777);
		staticFileLocation("/webapp");
		
		get("/hello-nlp", (req, res) -> "hello world from the web client");
		
		
		get("/search", (request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			String searchTerm = request.queryParams("searchTerm");
			System.out.println("Request recieved " + searchTerm);
			try {
				return SolrCustomSearcher.solrsearch(searchTerm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
		});

	}
}
