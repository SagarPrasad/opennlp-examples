/**
 * 
 */
package com.sagar.solr.custom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.util.Span;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.payloads.MaxPayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.parser.QueryParser;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.LuceneQParserPlugin;
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
		System.out.println(" Actual Query String : " + qstr);
		try {
			QParser parser = getParser(qstr, "lucene", getReq());
			this.innerQuery = parser.parse();
		} catch (SyntaxError ex) {
			throw new RuntimeException("error parsing query", ex);
		}
	}

	/**
	 * Returns the individual tokens and score based on the 
	 * part of speech. For e.g. Noun is boosted manifold than the 
	 * adjective and verb.
	 * @param qstr
	 * @return
	 */
	private Map<String, Float> getTokens(String qstr) {
		//System.out.println(" INside Get TOken Method");
		 for (String sentence : extractor.segmentSentences(qstr)) {
	            System.out.println("sentence: " + sentence);

	            String[] tokens = extractor.tokenizeSentence(sentence);
	            String[] tags = extractor.tagPartOfSpeech(tokens);
	            int count = 0;
	            Map<String, Float> map = new HashMap<String, Float>();
	            for(String token : tokens) 
	            {
	            	System.out.println("Token : " + token + " POS Tag :" + tags[count]);
	            	map.put(token, extractor.posScore(tags[count++]));
	            }
	            System.out.println("The return map - " +  map);
	            return map;
		 }
		 return null;
	}

	/**
	 * Returns the colors if identified from the String
	 * @param query
	 * @return
	 */
	private List<String> getColors(String query) {

		List<String> docArray = new ArrayList<String>();
    	docArray.add(query);
		List<String> resultArr = new ArrayList<String>();

    	for(String doc : docArray) {
    		for (String sentence : extractor.segmentSentences(doc)) {
                //System.out.println("sentence: " + sentence);
                String[] tokens = extractor.tokenizeSentence(sentence.toLowerCase());
                //System.out.println("Tokens -" + Arrays.asList(tokens));
                Span[] spans = extractor.findColor(tokens);
                double[] spanProbs = extractor.findColorProb(spans);
                //System.out.println(" Span Size : " + spans.length + " - " );
                int counter = 0;
                for (Span span : spans) {
                    System.out.print("color: ");
                    String color = null;
                    for (int i = span.getStart(); i < span.getEnd(); i++) {
                    	color = tokens[i];
                        System.out.print(tokens[i]);
                        if (i < span.getEnd()) {
                            System.out.print(" ");
                        }
                    }
                    System.out.println("Probability is: "+spanProbs[counter]);
                    if(spanProbs[counter] > 0.9) {
                    	resultArr.add(color);
                    }
                    counter++;
                    //System.out.println();
                }
            }
    	}
    	return resultArr;
	}
	
	
	private List<String> getNounChunckedPhrases(String qstr) {
		List<String> docArray = new ArrayList<String>();
    	docArray.add(qstr);
		List<String> resultArr = new ArrayList<String>();
		for (String doc : docArray) {
			for (String sentence : extractor.segmentSentences(doc)) {
				String[] tokens = extractor.tokenizeSentence(sentence
						.toLowerCase());
				String[] tags = extractor.tagPartOfSpeech(tokens);
				Span[] span = extractor.getChunkedSpan(tokens, tags);
				
				for (Span s : span) {
					if (s.toString().contains("NP")) {
						StringBuffer sb = new StringBuffer();
						for (int i = s.getStart(); i < s.getEnd(); i++) {
							sb.append(tokens[i] + " ");
						}
						resultArr.add(sb.toString().trim());
					}
				}
			}
		}
		System.out.println("The List of Noun Phrase : " + resultArr);
		return resultArr;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.solr.search.QParser#parse()
	 */
	@Override
	public Query parse() throws SyntaxError {
		System.out.println("Actaul Query Parser break up - " + innerQuery);

		String qstr = getString(); 
		if (qstr == null || qstr.length()==0) return null; 
		
		// hack 1
		if(qstr.contains("red")) {
			System.out.println(" Returning Inner Query--" + innerQuery);
			return innerQuery;
		}
		
		// hack 2 - Not working
		if(qstr.contains("black")) {
			qstr = qstr + "&fq=P_OfferPrice:[* TO 20]";
	        return new LuceneQParserPlugin().createParser(qstr, localParams, params, req).parse();
		}
		
		String defaultField = req.getSchema().getDefaultSearchFieldName();
		QueryParser.Operator defaultOperator = QueryParser.Operator.valueOf(req.getSchema().getQueryParserDefaultOperator());
		BooleanClause.Occur op = (defaultOperator == QueryParser.Operator.AND) ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD;
		
	    BooleanQuery q = new BooleanQuery();
		

		if(null == defaultField) {
			defaultField =  "text";
		}
		
		//System.out.println("Default Field " + defaultField);
		//System.out.println("Boolean Clause Occur " + op);
		PayloadTermQuery btq = null;
		
		String updateQSTR = qstr;
		//** Putting the colors in P_Color field **//
		List<String> colors = getColors(qstr);
		System.out.println(" Colrs Identified : " + colors);
		if(!colors.isEmpty()) {
			for(String color : colors) {
				PayloadTermQuery col = new PayloadTermQuery(new Term("P_Color", color), new MaxPayloadFunction());
				//col.setBoost(30);
				q.add(col, BooleanClause.Occur.SHOULD);
				updateQSTR = updateQSTR.replace(color, "").trim();
			}
		}

		// Price detection and applying
		PriceHelper pricehelper = new PriceHelper();
		System.out.println(" Applying Price Helper to find price in query");
		Map<String, String> price = pricehelper.parseString(updateQSTR, extractor);
		System.out.println(" Got Value :: " +  price);
		
		if(null != price.get("filter")) {
			BytesRef bf = new BytesRef("20");
			BytesRef ef = new BytesRef("30");
			TermRangeQuery trq = new TermRangeQuery("P_OfferPrice",bf,ef,true, true);
			//PayloadTermQuery ph = new PayloadTermQuery(new Term("P_OfferPrice", price.get("filter")), new MaxPayloadFunction());
			q.add(trq, BooleanClause.Occur.SHOULD);
			System.out.println("TRQ - " + trq);
			//req.set(CommonParams.FQ, "P_OfferPrice:" + price.get("filter"));
			updateQSTR =  price.get("query");
		}
		//Removing some facets words if available -??

		
		System.out.println(" Update Query String is - " + updateQSTR);
		//*** Boosting the noun words ***//
		Map<String, Float> tokenScore = getTokens(updateQSTR);
		for(String token : tokenScore.keySet()) {
			btq = new PayloadTermQuery(new Term(defaultField, token.toLowerCase()), new MaxPayloadFunction());
			btq.setBoost(tokenScore.get(token));
			q.add(btq, BooleanClause.Occur.SHOULD);
		}
		
		//** Noun Chunkers to be searched in description field **//
		List<String> nounPharses = getNounChunckedPhrases(updateQSTR);
		System.out.println("Noun Pharses Identified : " + nounPharses);
		if(!nounPharses.isEmpty()) {
			for(String phares : nounPharses) {
				PayloadTermQuery ph = new PayloadTermQuery(new Term("P_ProductDescription", phares), new MaxPayloadFunction());
				ph.setBoost(30);
				q.add(ph, BooleanClause.Occur.SHOULD);
			}
		}
		System.out.println(" Overrided Query Parser String : " + q);
		return q;
	}

}
