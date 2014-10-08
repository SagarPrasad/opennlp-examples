/**
 * 
 */
package com.sagar.solr;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.sagar.nlp.OpenNLPUtil;
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
	    String queryString = "blue t-shirt under 50$";
	    
	   // query.addFilterQuery("cat:electronics","store:amazon.com");
	    //query.setFields("id","price","merchant","cat","store");
	    query.setStart(0);    
	    query.set("defType", "customqparser");
	    
	    System.out.println(" Query  :: " +  query);
	    OpenNLPUtil extractor = new OpenNLPUtil();
	    queryString = applyPriceFilter(queryString, extractor, query);
	    query.setQuery(queryString);
	    
	    QueryResponse response = solr.query(query);
	    SolrDocumentList results = response.getResults();
	    
	    System.out.println(" No of Docs returned : " + results.size());
	    for (int i = 0; i < results.size(); ++i) {
	      System.out.println(results.get(i));
	    }
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
