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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class OpenNLPUtil {

	// Models to be loaded in memory
    private SentenceModel sentenceModel;
    private TokenizerModel tokenizerModel;
    private TokenNameFinderModel nameFinderModel;
    private POSModel partOfSpeechModel;
    private ChunkerModel chunkerModel;
    private TokenNameFinderModel moneyFinderModel;
    // custom models
    private TokenNameFinderModel colorFinderModel;
    private TokenNameFinderModel brandFinderModel;

    // 
    private ChunkerME phraseChunker;
    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;
    private NameFinderME nameFinder;
    private POSTaggerME partOfSpeechTagger;
    private NameFinderME moneyFinder;
    // custom NER
    private NameFinderME colorFinder;
    private NameFinderME brandFinder;


    public OpenNLPUtil() throws IOException {
        initModels();
        tokenizer = new TokenizerME(tokenizerModel);
        nameFinder = new NameFinderME(nameFinderModel);
        partOfSpeechTagger = new POSTaggerME(partOfSpeechModel);
        sentenceDetector = new SentenceDetectorME(sentenceModel);
        phraseChunker = new ChunkerME(chunkerModel);
        moneyFinder = new NameFinderME(moneyFinderModel);
        colorFinder  = new NameFinderME(colorFinderModel);
        brandFinder = new NameFinderME(brandFinderModel);
    }
    
    
    public OpenNLPUtil(String test) throws IOException {
    	InputStream sentenceModelStream = getInputStream("models/en-sent.bin");
        InputStream tokenizereModelStream = getInputStream("models/en-token.bin");
        InputStream partOfSpeechModelStream = getInputStream("models/en-pos-maxent.bin");
    	
        sentenceModel = new SentenceModel(sentenceModelStream);;
        tokenizerModel = new TokenizerModel(tokenizereModelStream);
        partOfSpeechModel = new POSModel(partOfSpeechModelStream);
        
    	tokenizer = new TokenizerME(tokenizerModel);
    	partOfSpeechTagger = new POSTaggerME(partOfSpeechModel);
        sentenceDetector = new SentenceDetectorME(sentenceModel);
        
    	if(test.equalsIgnoreCase("color")) {
    		InputStream colorFinderModelStream =  getInputStream("cmodel/colorModel.bin");
    		colorFinderModel = new TokenNameFinderModel(colorFinderModelStream);
    		colorFinder  = new NameFinderME(colorFinderModel);
    	}
    }
    

    private void initModels() throws IOException {
        InputStream sentenceModelStream = getInputStream("models/en-sent.bin");
        InputStream tokenizereModelStream = getInputStream("models/en-token.bin");
        InputStream nameFinderModelStream = getInputStream("models/en-ner-location.bin");
        InputStream partOfSpeechModelStream = getInputStream("models/en-pos-maxent.bin");
        InputStream chunkerrModelStream =  getInputStream("models/en-chunker.bin");
        InputStream moneyFinderModelStream = getInputStream("models/en-ner-money.bin");
        InputStream colorFinderModelStream =  getInputStream("cmodel/colorModel.bin");
        InputStream brandFinderModelStream =  getInputStream("brandmodel/brandModel.bin");
        
        sentenceModel = new SentenceModel(sentenceModelStream);;
        tokenizerModel = new TokenizerModel(tokenizereModelStream);
        nameFinderModel = new TokenNameFinderModel(nameFinderModelStream);
        moneyFinderModel = new TokenNameFinderModel(moneyFinderModelStream);
        colorFinderModel = new TokenNameFinderModel(colorFinderModelStream);
        partOfSpeechModel = new POSModel(partOfSpeechModelStream);
        chunkerModel = new ChunkerModel(chunkerrModelStream);
        brandFinderModel = new TokenNameFinderModel(brandFinderModelStream);
    }

    private InputStream getInputStream(String resource) throws FileNotFoundException  {
    	// For local Eclipse
    	InputStream inputS = null;
    	try {
			inputS = new FileInputStream(new File(resource));
		} catch (Exception e) {
			// For Jars in SOLR
			System.out.println("Inside the Exception =" + "/" + resource);
			inputS = getClass().getResourceAsStream("/" + resource);
		}
    	System.out.println(" Loaded files -- " +  inputS + " - resource " + resource);
        return inputS;
    }

    public String[] segmentSentences(String document) {
        return sentenceDetector.sentDetect(document);
    }

    public String[] tokenizeSentence(String sentence) {
        return tokenizer.tokenize(sentence);
    }

    public Span[] findNames(String[] tokens) {
        return nameFinder.find(tokens);
    }
    
    public Span[] findColor(String[] tokens) {
        return colorFinder.find(tokens);
    }
    
    public double[] findColorProb(Span[] spans) {
        return colorFinder.probs(spans);
    }
    
    
    public Span[] findBrand(String[] tokens) {
        return brandFinder.find(tokens);
    }
    
    public double[] findBrandProb(Span[] spans) {
        return brandFinder.probs(spans);
    }
    
    public Span[] findMoney(String[] tokens) {
        return moneyFinder.find(tokens);
    }

    public double[] findMoneyProb(Span[] spans) {
        return moneyFinder.probs(spans);
    }
    
    public String[] tagPartOfSpeech(String[] tokens) {
        return partOfSpeechTagger.tag(tokens);
    }

    public double[] getPartOfSpeechProbabilities() {
        return partOfSpeechTagger.probs();
    }
    
    public String[] getChunkedPhrases(String[] tokens, String[] tags) {
    	return phraseChunker.chunk(tokens, tags);
    }
    
    
    public Span[] getChunkedSpan(String[] tokens, String[] tags) {
    	return phraseChunker.chunkAsSpans(tokens, tags);
    }
    
    public String[] getChunkedPhrases(String sentence) {
    	String[] tokens = tokenizeSentence(sentence);
        String[] tags = tagPartOfSpeech(tokens);
        return getChunkedPhrases(tokens, tags);
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
 
    public float posScore(String k) {
    	float value = 1;
    	switch(k) {
    	 case "JJ": 
    	 case "JJR": 
    	 case "JJS": value = 2;break;
    	 case "NN": 
    	 case "NNS": 
    	 case "NNP": 
    	 case "NNPS": value = 20;break;
    	 case "VB": 
    	 case "VBD": 
    	 case "VBG": 
    	 case "VBN": 
    	 case "VBP": 
    	 case "VBZ": value = 4;break;
    	 default: break;
    	}
    	return value;
    }
}