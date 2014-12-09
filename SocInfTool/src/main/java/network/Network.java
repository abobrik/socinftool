package network;

import java.util.HashMap;

import nlp.Topic;

public class Network {
	private HashMap<String,Node> nodeCollection;
	private long nextNodeId;
	
	private HashMap<Long,Event> EventCollection;
	private long nextEventId;
	
	private HashMap<String,Topic> topicCollection;
	
	public Network(){
		nodeCollection = new HashMap<String,Node>();
		nextNodeId=1;
		
		EventCollection = new HashMap<Long,Event>();
		nextEventId=1;
		
		topicCollection = new HashMap<String,Topic>();
	}

	public HashMap<String, Node> getNodeCollection() {
		return nodeCollection;
	}

	public void setNodeCollection(HashMap<String, Node> nodeCollection) {
		this.nodeCollection = nodeCollection;
	}

	public HashMap<Long, Event> getEventCollection() {
		return EventCollection;
	}

	public void setEventCollection(HashMap<Long, Event> EventCollection) {
		this.EventCollection = EventCollection;
	}

	public Node createNewNode(String username) {
    	Node node = this.nodeCollection.get(username);
    	if(node==null){
    		node = new Node(nextNodeId,username,this);
    		this.nodeCollection.put(username, node);
    		this.nextNodeId++;
    	}
    	return node;
	}

	public Event createNewEvent(String text, String date, String header,
			String num, Node node) {
    	Event event = new Event(nextEventId,text,date,header,num,node,this); 
    	this.EventCollection.put(nextEventId, event);
    	this.nextEventId++;
    	node.setEventObjects(event);
		return event;
	}

	public HashMap<String, Topic> getTopicCollection() {
		return topicCollection;
	}


	
	
}
