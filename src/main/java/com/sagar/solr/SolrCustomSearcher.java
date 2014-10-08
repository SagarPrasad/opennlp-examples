/**
 * 
 */
package com.sagar.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.sagar.nlp.OpenNLPUtil;
import com.sagar.solr.custom.BrandHelper;
import com.sagar.solr.custom.ColorHelper;
import com.sagar.solr.custom.PriceHelper;

/**
 * @author spras3
 *
 */
public class SolrCustomSearcher {

	/**
	 * @param args
	 * @throws SolrServerException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws SolrServerException, IOException {
		// TODO Auto-generated method stub
		HttpSolrServer solr = new HttpSolrServer("http://localhost:8983/solr/personalization");
	    SolrQuery query = new SolrQuery();
	    String queryString = "blue sweatert under 50$";
	    
	   // query.addFilterQuery("cat:electronics","store:amazon.com");
	    //query.setFields("id","price","merchant","cat","store");
	    query.setStart(0);    
	    //query.set("defType", "customqparser");
	    
	    System.out.println(" Query  :: " +  query);
	    OpenNLPUtil extractor = new OpenNLPUtil();
	    queryString = applyPriceFilter(queryString, extractor, query);
	    queryString = applyColorFilter(queryString, extractor, query);
	    queryString = applyBrandFilter(queryString, extractor, query);
	    query.setQuery(queryString);
	    
	    System.out.println("After Query  :: " +  query);
	    QueryResponse response = solr.query(query);
	    SolrDocumentList results = response.getResults();
	    
	    System.out.println(" No of Docs returned : " + results.size());
	    for (int i = 0; i < results.size(); ++i) {
	      System.out.println(results.get(i));
	    }
	}

	
	
	private static String applyBrandFilter(String queryString,
			OpenNLPUtil extractor, SolrQuery query) {
		BrandHelper brandHelper = new BrandHelper();
		List<String> brands = brandHelper.getBrands(queryString, extractor);
		if(null != brands && !brands.isEmpty()) {
			query.addFilterQuery("P_Brand:" + brands.get(0));
		}
		return queryString;
	}



	private static String applyColorFilter(String queryString,
			OpenNLPUtil extractor, SolrQuery query) {
		ColorHelper colorhelper = new ColorHelper();
		List<String> colors = colorhelper.getColors(queryString, extractor);
		if(null != colors && !colors.isEmpty()) {
			query.addFilterQuery("P_Color:" + colors.get(0));
			//String newQueryString  = queryString.replace(colors.get(0), "");
			//return newQueryString;
		}
		return queryString;
	}



	private static String applyPriceFilter(String queryString, OpenNLPUtil extractor, SolrQuery query) {
		String updateQSTR = queryString;
		PriceHelper pricehelper = new PriceHelper();
		System.out.println(" Applying Price Helper to find price in query");
		Map<String, String> price = pricehelper.parseString(updateQSTR, extractor);
		System.out.println(" Got Value :: " +  price);
		
		if(null != price.get("filter")) {
			updateQSTR =  price.get("query");
			query.addFilterQuery("P_OfferPrice:"+price.get("filter"));
		}
		
		return updateQSTR;
	}
}
