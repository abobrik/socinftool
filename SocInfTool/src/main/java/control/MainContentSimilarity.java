package control;

import nlp.ContentSimilarityCalculation;
import nlp.TopicSimilarity;
import nlp.TopicSimilarityCalculation;
import parser.MacRumorsParser;

public class MainContentSimilarity {

	/**
	 * Application entry point
	 * @param args
	 * 			args[0]	boolean value. true=override old DB content; false=not override old DB content.
	 * 			args[1]	String value. Path to neo4j DB.

	 */
	public static void main(String[] args) {
		boolean overrideDB = Boolean.parseBoolean(args[0]);
		String pathDB = args[1];
        ContentSimilarityCalculation tsc = new ContentSimilarityCalculation(overrideDB, pathDB);
        tsc.calcContentSimilarity();
	    tsc.shutdown();
	}

}
