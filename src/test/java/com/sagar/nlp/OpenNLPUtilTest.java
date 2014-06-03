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

import java.io.IOException;

public class OpenNLPUtilTest {

    private static OpenNLPUtil extractor;

    static {
        try {
            extractor = new OpenNLPUtil();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEnglishSentences() {

        // Text from http://techcrunch.com/2013/04/25/strategy-analytics-q1-tablet-stats/ (© 2013 AOL Inc.)
//        String document =
 //           "Don’t write off Microsoft’s chances in mobile just yet. It may still be struggling to make itself count in the smartphone space but early signs are more promising for Windows plus tablets. Microsoft has gone from having no share of the global tablet OS market in Q1 last year to taking 7.4% one year later, with three million Windows 8 tablets shipped in Q1 2013, according to preliminary figures from Strategy Analytics‘ Global Tablet OS Market Share: Q1 2013 report.";

        String document ="DSLR cameras are fast-focusing, allow you to take multiple photos quickly, and compose sharp images in nearly any light. With a precision viewfinder and image sensors that are more than 8X larger than smartphone sensors, DSLR cameras let you take pictures that are more detailed and stay sharp when resized.";
        
        
        for (String sentence : extractor.segmentSentences(document)) {
            System.out.println("sentence: " + sentence);
        }
    }

    //@Test
    public void testEnglishSegmentation() {
        // Text from http://techcrunch.com/2013/04/25/strategy-analytics-q1-tablet-stats/ (© 2013 AOL Inc.)
        String document =
            "Don’t write off Microsoft’s chances in mobile just yet. It may still be struggling to make itself count in the smartphone space but early signs are more promising for Windows plus tablets. Microsoft has gone from having no share of the global tablet OS market in Q1 last year to taking 7.4% one year later, with three million Windows 8 tablets shipped in Q1 2013, according to preliminary figures from Strategy Analytics‘ Global Tablet OS Market Share: Q1 2013 report.";

        for (String sentence : extractor.segmentSentences(document)) {
            System.out.println("sentence: " + sentence);

            for (String token : extractor.tokenizeSentence(sentence)) {
                System.out.println("\t" + token);
            }
        }
    }

    @Test
    public void testEnglishNames() {
        // Text from http://techcrunch.com/2013/04/25/strategy-analytics-q1-tablet-stats/ (© 2013 AOL Inc.)
       // String document = "Adult White 'Murica USA 4th July America Independence Freedom T-Shirt Tee Product Description 5.3 Oz.,Pre-Shrunk High Quality 100% cotton Double-needle stitched neckline.";
        String document = "Steve Jobs T-shirt for Men by Levis - New For MEN Size Only";

        for (String sentence : extractor.segmentSentences(document)) {
            System.out.println("sentence: " + sentence);

            String[] tokens = extractor.tokenizeSentence(sentence);

            Span[] spans = extractor.findNames(tokens);

            for (Span span : spans) {

                System.out.print("person: ");

                for (int i = span.getStart(); i < span.getEnd(); i++) {
                    System.out.print(tokens[i]);
                    if (i < span.getEnd()) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
        }
    }

    @Test
    public void testEnglishPartOfSpeech() {
        // Text from http://techcrunch.com/2013/04/25/strategy-analytics-q1-tablet-stats/ (© 2013 AOL Inc.)
        //String document =
          //  "Microsoft, founded in 1975 by Bill Gates and Paul Allen, is a veteran software company, best known for its Microsoft Windows operating system and the Microsoft Office suite of productivity software.";

        //String document ="The technical specs which are a A6X Quad core processor";
    	String document ="The whole package. In a smaller package.iPad mini features a beautiful 7.9-inch display, iSight and FaceTime cameras, the A5 chip, ultrafast wireless, and up to 10 hours of battery life. And over 275,000 apps on the App Store made for iPad also work with iPad mini. So it’s an iPad in every way, shape, and slightly smaller form. It is available in black & slate or white & silver.";
        for (String sentence : extractor.segmentSentences(document)) {
            System.out.println("sentence: " + sentence);

            String[] tokens = extractor.tokenizeSentence(sentence);
            
            Span[] spans = extractor.findNames(tokens);
            for (Span span : spans) {

                System.out.print("person: ");

                for (int i = span.getStart(); i < span.getEnd(); i++) {
                    System.out.print(tokens[i]);
                    if (i < span.getEnd()) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
            
            String[] tags = extractor.tagPartOfSpeech(tokens);
            double[] probs = extractor.getPartOfSpeechProbabilities();

            for (int i = 0; i < tokens.length; i++) {
                System.out.print("token: " + tokens[i]);
                System.out.print("\t");
                System.out.print("pos: " + tags[i] + " - " + posValue(tags[i]));
               // System.out.print("\t");
               // System.out.print("probability: " + probs[i]);
                System.out.println();
            }
        }
    }
    
    @Test
    public void testPartOfSpeech() {
        // Text from http://techcrunch.com/2013/04/25/strategy-analytics-q1-tablet-stats/ (© 2013 AOL Inc.)
        String document = "Use the folding wheels on both vehicles for the perfect high-speed pursuit in the air or on land.";
        //String document = "mens black shoe size 10";
        
        for (String sentence : extractor.segmentSentences(document)) {
            System.out.println("sentence: " + sentence);

            String[] tokens = extractor.tokenizeSentence(sentence);

            String[] tags = extractor.tagPartOfSpeech(tokens);
            double[] probs = extractor.getPartOfSpeechProbabilities();

            for (int i = 0; i < tokens.length; i++) {
                System.out.print("token: " + tokens[i]);
                System.out.print("\t");
                System.out.print("pos: " + tags[i] + " - " + posValue(tags[i]));
                System.out.print("\t");
                System.out.print("probability: " + probs[i]);
                System.out.println();
            }
        }
    }
    
    
    @Test
    public void testMoneyFinder() {
        // Text from http://techcrunch.com/2013/04/25/strategy-analytics-q1-tablet-stats/ (© 2013 AOL Inc.)
        //String document = "Red Batman tshirt €2 under $20";
      //  String document = "Red Batman tshirt under $20";
        String document = "ipod less than hundred dollars";

        for (String sentence : extractor.segmentSentences(document)) {
            System.out.println("sentence: " + sentence);

            String[] tokens = extractor.tokenizeSentence(sentence);
            
            for(String token : tokens) {
            	System.out.println(token);
            }

            Span[] spans = extractor.findMoney(tokens);

            for (Span span : spans) {

                System.out.print("Money: ");

                for (int i = span.getStart(); i < span.getEnd(); i++) {
                    System.out.print(tokens[i]);
                    if (i < span.getEnd()) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
        }
    }
    
    
    @Test
    public void testColorFinder() {
        String document = "green Flyod tshirt under 20$";
        for (String sentence : extractor.segmentSentences(document)) {
            System.out.println("sentence: " + sentence);
            String[] tokens = extractor.tokenizeSentence(sentence);
            Span[] spans = extractor.findColor(tokens);
            for (Span span : spans) {
                System.out.print("color: ");
                for (int i = span.getStart(); i < span.getEnd(); i++) {
                    System.out.print(tokens[i]);
                    if (i < span.getEnd()) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
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
