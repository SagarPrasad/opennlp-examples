/**
 * 
 */
package com.sagar.solr.custom;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

/**
 * @author A039883
 * 
 */
public class CustomQParser extends QParser {

	private Query innerQuery;

	public CustomQParser(String s, SolrParams localparams, SolrParams params,
			SolrQueryRequest request) {
		super(s, localparams, params, request);
		try {
			QParser parser = getParser(qstr, "lucene", getReq());
			this.innerQuery = parser.parse();
		} catch (SyntaxError ex) {
			throw new RuntimeException("error parsing query", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.solr.search.QParser#parse()
	 */
	@Override
	public Query parse() throws SyntaxError {
		System.out.println("My Custom Q Parse");
		 return new MyCustomQuery(innerQuery);
	}
}
