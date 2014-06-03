/**
 * 
 */
package com.sagar.solr.custom;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

/**
 * @author A039883
 *
 */
public class CustomQueryParserPlugin extends QParserPlugin {

	public void init(NamedList nameList) {
		//init(nameList);
	}

	@Override
	public QParser createParser(String s, SolrParams localParams, SolrParams params,
			SolrQueryRequest req) {
		System.out.println("My Custom Q create Parser");
		return new CustomQParser(s, localParams, params, req);
	}

}
