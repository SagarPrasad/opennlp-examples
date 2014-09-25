/**
 * 
 */
package com.sagar.solr.custom;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

import com.sagar.nlp.OpenNLPUtil;

/**
 * @author A039883
 * 
 */
public class CustomQParser extends QParser {

	private Query innerQuery;
	
	// Testing loading NLP Loader
	private static OpenNLPUtil extractor;

    static {
        try {
        	System.out.println(" Initializing the OpenNLP");
            extractor = new OpenNLPUtil();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public CustomQParser(String s, SolrParams localparams, SolrParams params,
			SolrQueryRequest request) {
		super(s, localparams, params, request);
		System.out.println(" QSTR : " + qstr);
		System.out.println(" OPENNLP IN ACTION " + getTokens(qstr));
		try {
			QParser parser = getParser(qstr, "lucene", getReq());
			this.innerQuery = parser.parse();
		} catch (SyntaxError ex) {
			throw new RuntimeException("error parsing query", ex);
		}
	}

	/**
	 * 
	 * @param qstr
	 * @return
	 */
	private List<String> getTokens(String qstr) {
		System.out.println(" INside Get TOken Method");
		 for (String sentence : extractor.segmentSentences(qstr)) {
	            System.out.println("sentence: " + sentence);

	            String[] tokens = extractor.tokenizeSentence(sentence);
	            return Arrays.asList(tokens);
		 }
		 return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.solr.search.QParser#parse()
	 */
	@Override
	public Query parse() throws SyntaxError {
		System.out.println("My Custom Q Parse - " + innerQuery);
		 return new MyCustomQuery(innerQuery);
	}
}
