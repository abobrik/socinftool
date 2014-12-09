package nlp;

import java.util.LinkedList;

public class Topic {
	private String topic;
	private LinkedList<String> words = new LinkedList<String>();
	boolean isLemma;

	public Topic(String topic) {
		super();
		this.topic = topic;
	}
	public Topic(String topic, String word, boolean isLemma) {
		super();
		this.topic = topic;
		this.words = new LinkedList<String>();
		this.words.add(word);
		this.isLemma = isLemma;
	}

	public String getTopic() {
		return topic;
	}
	public void addWord(String word){
		if(!this.words.contains(word)){
			this.words.add(word);
		}
	}
	public boolean isLemma() {
		return isLemma;
	}
	
	public String toString(){
		return this.topic;
	}
	
}
