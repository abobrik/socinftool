package control;

import nlp.TopicSimilarity;
import nlp.TopicSimilarityCalculation;
import parser.MacRumorsParser3;

public class MainTopicSimilarity {

	/**
	 * Application entry point
	 * @param args
	 * 			args[0]	boolean value. true=override old DB content; false=not override old DB content.
	 * 			args[1]	String value. Path to neo4j DB.
	 * 			args[2]	int value. Type of semantic relatedness calculator, 
	 * 					e.g. 0=HirstStOnge, 1=LeacockChodorow; 2=Lesk, 3=WuPalmer, 4=Resnik, 5=JiangConrath, 
	 * 					6=Lin, 7=Path 
	 */
	public static void main(String[] args) {
		boolean overrideDB = Boolean.parseBoolean(args[0]);
		String pathDB = args[1];
		int type = Integer.parseInt(args[2]);
        TopicSimilarityCalculation tsc = new TopicSimilarityCalculation(overrideDB, pathDB);
        tsc.calcTopicSimilarities(type);
	    tsc.shutdown();
	}

}
