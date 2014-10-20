/**
 * 
 */
package com.sagar.solrnlp.web;

import static spark.Spark.*;


/**
 * @author spras3
 * 
 */
public class ClientApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		setPort(7777);
		staticFileLocation("/webapp");
		
		get("/hello-nlp", (req, res) -> "hello world from the web client");
		
		/*
		 * get("/hello-nlp", (req, res) -> "hello world from the web client");
		 * 
		 * get("/search", (request, response) -> {
		 * response.header("Access-Control-Allow-Origin", "*"); });
		 */

	}
}
