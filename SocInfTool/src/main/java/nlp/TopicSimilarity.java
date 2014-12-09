package nlp;

public class TopicSimilarity {
	private Topic topic1;
	private Topic topic2;
	private double similarity;
	
	public TopicSimilarity(Topic topic1, Topic topic2, double sim) {
		super();
		this.topic1 = topic1;
		this.topic2 = topic2;
		this.similarity = sim;
	}
	
	public void setSimilarity(double sim){
		this.similarity = sim;
	}
	
}
