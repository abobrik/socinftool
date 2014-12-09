package network;

import java.util.LinkedList;

import nlp.Topic;

/**
 * 
 * @author abobrik
 *
 */
public class Event {
	private long id;
	private String postNum;
	private String header;
	private String text;
	private String date;
	
	private Node author; //more than one?
	
	private Event refContent; //referenced Content; more than one?
	
	private Network network;
	private LinkedList<Topic> topics;


	public Event (long id, String text, String date, String header, String postNum, Node author, Network network){
		this.id = id;
		this.text = text;
		this.date = date;
		this.header = header;
		this.postNum = postNum;
		this.author = author;
		this.network = network;
		
		this.topics = new LinkedList<Topic>();
	}

	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getDate() {
		return date;
	}

	public Event getRefContent() {
		return refContent;
	}

	public void setRefEvent(Event refContent) {
		this.refContent = refContent;
	}
	public String getHeader() {
		return header;
	}

	public Node getAuthor() {
		return author;
	}

	public void setAuthor(Node author) {
		this.author = author;
	}

	public String toString(){
		return this.id+"";
	}

	public void setTopics(LinkedList<Topic> topics) {
		this.topics = topics;
	}

	public LinkedList<Topic> getTopics() {
		return this.topics;
	}
}
