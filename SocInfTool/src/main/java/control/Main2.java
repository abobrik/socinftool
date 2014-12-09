package control;

import neo4j.Neo4jInterface;
import parser.MacRumorsParser3;

public class Main2 {

	public static void main(String[] args) {
		Neo4jInterface ninf = new Neo4jInterface();
		ninf.init();
		
//        String thread = "showthread.php?t=1807791"; //120 Post, 105 nodes
		String thread = "showthread.php?p=20388501"; //120 Posts, only 21 nodes
        int numPages = 3;
        MacRumorsParser3 parser = new MacRumorsParser3();
        parser.fetchDataFromURL(thread,numPages,ninf);
//        parser.displayGraph();
        
//	    TopicExtraction tex = new TopicExtraction(parser.getGraph());
//	    tex.createTopicVector();
//	    
//	    TopicSimilarityCalculation ts = new TopicSimilarityCalculation(parser.getGraph());
//	    ts.calculateLinTopicSimilarities();
	    ninf.shutdown();
	}

}
