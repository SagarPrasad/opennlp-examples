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

    private SentenceModel sentenceModel;

    private TokenizerModel tokenizerModel;

    private TokenNameFinderModel nameFinderModel;
    
    private TokenNameFinderModel moneyFinderModel;
    
    private TokenNameFinderModel colorFinderModel;

    private POSModel partOfSpeechModel;

    private SentenceDetectorME sentenceDetector;

    private TokenizerME tokenizer;

    private NameFinderME nameFinder;
    
    private NameFinderME moneyFinder;
    // custom NER
    private NameFinderME colorFinder;

    private POSTaggerME partOfSpeechTagger;


    public OpenNLPUtil() throws IOException {
        initModels();
        tokenizer = new TokenizerME(tokenizerModel);
        nameFinder = new NameFinderME(nameFinderModel);
        moneyFinder = new NameFinderME(moneyFinderModel);
        colorFinder  = new NameFinderME(colorFinderModel);
        partOfSpeechTagger = new POSTaggerME(partOfSpeechModel);
        sentenceDetector = new SentenceDetectorME(sentenceModel);
    }

    private void initModels() throws IOException {
        InputStream sentenceModelStream = getInputStream("models/en-sent.bin");
        InputStream tokenizereModelStream = getInputStream("models/en-token.bin");
        InputStream nameFinderModelStream = getInputStream("models/en-ner-location.bin");
        InputStream moneyFinderModelStream = getInputStream("models/en-ner-money.bin");
        InputStream partOfSpeechModelStream = getInputStream("models/en-pos-maxent.bin");
        InputStream colorFinderModelStream =  getInputStream("cmodel/colorModel.bin");
        
        sentenceModel = new SentenceModel(sentenceModelStream);;
        tokenizerModel = new TokenizerModel(tokenizereModelStream);
        nameFinderModel = new TokenNameFinderModel(nameFinderModelStream);
        moneyFinderModel = new TokenNameFinderModel(moneyFinderModelStream);
        colorFinderModel = new TokenNameFinderModel(colorFinderModelStream);
        partOfSpeechModel = new POSModel(partOfSpeechModelStream);
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
    	System.out.println(" Loaded files -- " +  inputS);
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
}