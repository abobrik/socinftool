package control;

import parser.MacRumorsParser;

public class Main {

	public static void main(String[] args) {
//		Neo4jInterface ninf = new Neo4jInterface();
//		ninf.init();
		
//        String thread = "showthread.php?t=1807791"; //120 Post, 105 nodes
//		String thread = "showthread.php?t=1821262"; 
		String thread = "showthread.php?t=1816666";
        int startPage = 1;
        MacRumorsParser parser = new MacRumorsParser("D:\\Daten\\");
        parser.initFetchDataFromURL(thread,startPage);
//        parser.displayGraph();
        
//	    TopicExtraction tex = new TopicExtraction(parser.getGraph());
//	    tex.createTopicVector();
//	    
//	    TopicSimilarityCalculation ts = new TopicSimilarityCalculation(parser.getGraph());
//	    ts.calculateLinTopicSimilarities();
//	    ninf.shutdown();
	}

}
