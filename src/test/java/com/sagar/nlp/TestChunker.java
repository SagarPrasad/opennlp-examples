package com.sagar.nlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

public class TestChunker {
	public static void main(String[] args) throws IOException {
		POSModel model = new POSModelLoader()
				.load(new File("models/en-pos-maxent.bin"));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		POSTaggerME tagger = new POSTaggerME(model);

		//String input = "He reckons the current account deficit will narrow to only #1.8 billion in September.";
		//String input = "Nikon camera - is good - but -very expensive- and bad battery life";
		//String input = "Camera is very handy but with a short battery life and expensive";
		String input = "Camera is very handy but with a short battery life and expensive";
		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new StringReader(input));

		perfMon.start();
		String line;
		String whitespaceTokenizerLine[] = null;

		String[] tags = null;
		while ((line = lineStream.read()) != null) {
			whitespaceTokenizerLine = WhitespaceTokenizer.INSTANCE
					.tokenize(line);
			tags = tagger.tag(whitespaceTokenizerLine);

			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			System.out.println(sample.toString());
			perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();

		// chunker
		InputStream is = new FileInputStream("models/en-chunker.bin");
		ChunkerModel cModel = new ChunkerModel(is);

		ChunkerME chunkerME = new ChunkerME(cModel);
		String result[] = chunkerME.chunk(whitespaceTokenizerLine, tags);

		System.out.println("--");
		for (String s : result)
			System.out.println(s);

		System.out.println("--");
		Span[] span = chunkerME.chunkAsSpans(whitespaceTokenizerLine, tags);
		
		for(Span s : span) {
			if(s.toString().contains("NP"))
				System.out.println(" SPANNNNNN " + s.toString());
		}
		
		for (Span s : span) {
			System.out.print(s.toString() + "  ->");
			for(int i = s.getStart(); i < s.getEnd(); i++) {
				System.out.print(whitespaceTokenizerLine[i]+ " ");
			}
			System.out.println("-<");
		}
			
			
	}
}
