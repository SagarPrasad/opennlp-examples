package com.sagar.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import com.sagar.solr.domain.Item;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SolrServerException, IOException
    {
    	BasicConfigurator.configure();
        HttpSolrServer httpSolrServer = AppSolrServer.getInstance();
        System.out.println( "Hello World!"  + httpSolrServer);
        UpdateResponse ur = httpSolrServer.deleteByQuery( "*:*" );
        AppSolrServer.getInstance().commit();
        //System.out.println("Time " +  ur.getStatus());
        addDocument("one", new String[] { "aaa", "bbb", "ccc" }, "PowerShot Nikon camera is good but very expesive" , 2.3F, null, "red");
        addDocument("two", new String[] { "aaa", "ddd", "eee" }, "Power-shot has very bad battery life" , 5.3F, null, "blue");
        addDocument("three", new String[] { "sagar", "aac", "eee" }, "Nikon is worth for money" , 20.3F, null, "green");
        addDocument("four", new String[] { "sagar", "www", "eee" }, "Nikon PS has very high resoltion but at very high price" , 24.3F, null, "red");
        addDocument("five", new String[] { "sagar", "aac", "eee" }, "It has very bad battery life duration" , 52.3F, null, "yellow");
        addDocument("six", new String[] { "sagar", "www", "ddd" }, "Camera for professional, expensive but good" , 62.3F, null, "blue");
        updateDocument();
        readDocuments();
    }

    
	private static void updateDocument() {
		UpdateRequest req = new UpdateRequest();
		//req.add
	}


	private static void readDocuments() throws SolrServerException {
		SolrQuery query = new SolrQuery();
	    query.setQuery( "id:one" );
	    QueryResponse rsp = AppSolrServer.getInstance().query( query );
	    List<Item> beans = rsp.getBeans(Item.class);
	    
	    for(Item o : beans) {
	    	System.out.println(o.toString());
	    }
		
	}

	private static void addDocument(String id, String[] category, String description, Float price,
			List<String> features, String color) throws IOException, SolrServerException {
		
		List<String> featureslst = new ArrayList<String>();
		featureslst.add(id);
		featureslst.add(color);
		
		Item offer = new Item();
		offer.setId(id);
		offer.setTitle(id);
		offer.setDescription(description);
		offer.setPrice(price);
		offer.setFeatures(featureslst);
		offer.setCategories(category);
		offer.setColor(color);
		AppSolrServer.getInstance().addBean(offer);
		AppSolrServer.getInstance().commit();
	}
}
