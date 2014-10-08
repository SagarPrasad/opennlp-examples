/**
 * 
 */
package com.sagar.nlp.custom.model;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import opennlp.addons.modelbuilder.DefaultModelBuilderUtil;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

/**
 * @author A039883
 *
 */
public class CreateBrandModel {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/**
	     * establish a file to put sentences in
	     */
	    File sentences = new File("brandmodel/sentences.txt");

	    /**
	     * establish a file to put your NER hits in (the ones you want to keep based on prob)
	     */
	    File knownEntities = new File("brandmodel/knownentities.txt");

	    /**
	     * establish a BLACKLIST file to put your bad NER hits in (also can be based on prob)
	     */
	    File blacklistedentities = new File("brandmodel/blentities.txt");

	    /**
	     * establish a file to write your annotated sentences to
	     */
	    File annotatedSentences = new File("brandmodel/annotatedSentences.txt");

	    /**
	     * establish a file to write your model to
	     */
	    File theModel = new File("brandmodel/brandModel.bin");

	    /**
	     * THIS IS WHERE THE ADDON IS GOING TO USE THE FILES (AS IS) TO CREATE A NEW MODEL. YOU SHOULD NOT HAVE TO RUN THE FIRST PART AGAIN AFTER THIS RUNS, JUST NOW PLAY WITH THE
	     * KNOWN ENTITIES AND BLACKLIST FILES AND RUN THE METHOD BELOW AGAIN UNTIL YOU GET SOME DECENT RESULTS (A DECENT MODEL OUT OF IT).
	     */
	    DefaultModelBuilderUtil.generateModel(sentences, knownEntities, blacklistedentities, theModel, annotatedSentences, "brand", 3);
	}
}
