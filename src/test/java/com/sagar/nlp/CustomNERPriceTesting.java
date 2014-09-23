/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sagar.nlp;

import opennlp.tools.util.Span;

import org.junit.Test;

import com.sagar.nlp.OpenNLPUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomNERPriceTesting {

    private static OpenNLPUtil extractor;

    static {
        try {
            extractor = new OpenNLPUtil();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testColorFinder() {

    	String document = "cycle more than $500";
    	
    	List<String> docArray = new ArrayList<String>();
    	docArray.add(document);
    	docArray.add("toys under 20$");
    	docArray.add("camera less than usd 200");
    	docArray.add("I love white car model NX200 cost around more than $ 120");

    	
    	for(String doc : docArray) {
    		for (String sentence : extractor.segmentSentences(doc)) {
                System.out.println("sentence: " + sentence);
                String[] tokens = extractor.tokenizeSentence(sentence.toLowerCase());
                System.out.println("Tokens -" + Arrays.asList(tokens));
                Span[] spans = extractor.findMoney(tokens);
                double[] spanProbs = extractor.findMoneyProb(spans);
                System.out.println(" Span Size : " + spans.length + " - " );
                int counter = 0;
                for (Span span : spans) {
                    System.out.print("color: ");
                    for (int i = span.getStart(); i < span.getEnd(); i++) {
                        System.out.print(tokens[i]);
                        if (i < span.getEnd()) {
                            System.out.print(" ");
                        }
                    }
                    System.out.println("Probability is: "+spanProbs[counter++]);
                    System.out.println();
                }
            }
    	}
    }
    
    public String posValue(String k) {
    	String value = k;
    	switch(k) {
    	 case "CC": value = "Coordinating conjunction";break;
    	 case "CD": value = "Cardinal number";break;
    	 case "DT": value = "Determiner";break;
    	 case "EX": value = "Existential there";break;
    	 case "FW": value = "Foreign word";break;
    	 case "IN": value = "Preposition or subordinating conjunction";break;
    	 case "JJ": value = "Adjective";break;
    	 case "JJR": value = "Adjective, comparative";break;
    	 case "JJS": value = "Adjective, superlative";break;
    	 case "LS": value = "List item marker";break;
    	 case "MD": value = "Modal";break;
    	 case "NN": value = "Noun, singular or mass";break;
    	 case "NNS": value = "Noun, plural";break;
    	 case "NNP": value = "Proper noun, singular";break;
    	 case "NNPS": value = "Proper noun, plural";break;
    	 case "PDT": value = "Predeterminer";break;
    	 case "POS": value = "Possessive ending";break;
    	 case "PRP": value = "Personal pronoun";break;
    	 case "PRP$": value = "Possessive pronoun";break;
    	 case "RB": value = "Adverb";break;
    	 case "RBR": value = "Adverb, comparative";break;
    	 case "RBS": value = "Adverb, superlative";break;
    	 case "RP": value = "Particle";break;
    	 case "SYM": value = "Symbol";break;
    	 case "TO": value = "to";break;
    	 case "UH": value = "Interjection";break;
    	 case "VB": value = "Verb, base form";break;
    	 case "VBD": value = "Verb, past tense";break;
    	 case "VBG": value = "Verb, gerund or present participle";break;
    	 case "VBN": value = "Verb, past participle";break;
    	 case "VBP": value = "Verb, non-3rd person singular present";break;
    	 case "VBZ": value = "Verb, 3rd person singular present";break;
    	 case "WDT": value = "Wh-determiner";break;
    	 case "WP": value = "Wh-pronoun";break;
    	 case "WP$": value = "Possessive wh-pronoun";break;
    	 case "WRB": value = "Wh-adverb";break;
    	 default: break;
    	}
    	return value;
    }
    
}
