package nlp;

public class TopicSimilarity {
	private String topic1;
	private String topic2;
	private double similarity;
	private String relatednessCalculator;
	
	public TopicSimilarity(String topic1, String topic2, double sim, String relatednessCalculator) {
		super();
		this.topic1 = topic1;
		this.topic2 = topic2;
		this.similarity = sim;
		this.relatednessCalculator = relatednessCalculator;
	}

	public String getTopic1() {
		return topic1;
	}

	public String getTopic2() {
		return topic2;
	}

	public double getSimilarity() {
		return similarity;
	}

	public String getRelatednessCalculator() {
		return relatednessCalculator;
	}
	
}
