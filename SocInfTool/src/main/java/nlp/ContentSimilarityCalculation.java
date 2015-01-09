package nlp;


import neo4j.Neo4jInterface;

public class ContentSimilarityCalculation {
	
	private Neo4jInterface n4jinf;

	public ContentSimilarityCalculation(boolean overrideDB, String pathDB){
		n4jinf = new Neo4jInterface(overrideDB, pathDB);
	}
	
	public void calcContentSimilarity(){
		n4jinf.calcSoftCosineSimilarityForPosts();	

	}
	public void shutdown() {
		n4jinf.shutdown();
	}

}
