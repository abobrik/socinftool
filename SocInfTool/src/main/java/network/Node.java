package network;
import java.util.HashMap;
import java.util.LinkedList;

import nlp.Topic;


public class Node {
	private long id;
	private String name;
	
	private Network network;
	
	private HashMap<Long,Event> EventCollection = new HashMap<Long,Event>();
	private LinkedList<Topic> topics;
	
	public Node(long id, String name, Network network){
		this.id=id;
		this.name=name;
		this.network = network;
		
		this.topics = new LinkedList<Topic>();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<Long, Event> getEventCollection() {
		return EventCollection;
	}

	public void setEventCollection(HashMap<Long, Event> EventCollection) {
		this.EventCollection = EventCollection;
	}
	public Event getEvent(long id) {
		return EventCollection.get(id);
	}

	public void setEventObjects(Event event) {
		if(!this.EventCollection.containsKey(event.getId())){
			this.EventCollection.put(event.getId(), event);
		} else {
			System.err.println("Event object "+event.toString()+" already exists!");
		}
	}
	public String toString() {
		return this.name;
	}
	public void setTopics(LinkedList<Topic> topics) {
		this.topics = topics;
	}

	public LinkedList<Topic> getTopics() {
		return topics;
	}
	
}
