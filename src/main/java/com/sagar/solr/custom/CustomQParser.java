/**
 * 
 */
package com.sagar.solr.custom;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.MaxPayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.parser.QueryParser;
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
	private Map<String, Float> getTokens(String qstr) {
		System.out.println(" INside Get TOken Method");
		 for (String sentence : extractor.segmentSentences(qstr)) {
	            System.out.println("sentence: " + sentence);

	            String[] tokens = extractor.tokenizeSentence(sentence);
	            String[] tags = extractor.tagPartOfSpeech(tokens);
	            int count = 0;
	            Map<String, Float> map = new HashMap<String, Float>();
	            for(String token : tokens) 
	            {
	            	map.put(token, extractor.posScore(tags[count++]));
	            }
	            System.out.println("The return map - " +  map);
	            return map;
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

		String qstr = getString(); 
		if (qstr == null || qstr.length()==0) return null; 
		

		if(qstr.contains("red")) {
			System.out.println(" Returning Inner Query--" + innerQuery);
			return innerQuery;
		}
		
		String defaultField = req.getSchema().getDefaultSearchFieldName();
		QueryParser.Operator defaultOperator = QueryParser.Operator.valueOf(req.getSchema().getQueryParserDefaultOperator());
		BooleanClause.Occur op = (defaultOperator == QueryParser.Operator.AND) ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD;
		
	    BooleanQuery q = new BooleanQuery();
		Map<String, Float> tokenScore = getTokens(qstr);

		if(null == defaultField) {
			defaultField =  "text";
		}
		
		System.out.println("Default Field " + defaultField);
		System.out.println("Boolean Clause Occur " + op);
		PayloadTermQuery btq = null;
		for(String token : tokenScore.keySet()) {
			btq = new PayloadTermQuery(new Term(defaultField, token), new MaxPayloadFunction());
			btq.setBoost(tokenScore.get(token));
			q.add(btq, BooleanClause.Occur.SHOULD);
		}
		System.out.println(" New Query : " + q);
		
		return q;
		
		/*BooleanQuery bq = new BooleanQuery(true);
	    bq.add(new TermQuery(new Term(defaultField, "")), op);
		return bq;*/
		//return new MyCustomQuery(innerQuery);
	}
}
