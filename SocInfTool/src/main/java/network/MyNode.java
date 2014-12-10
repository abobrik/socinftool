package network;

import java.util.LinkedList;

import nlp.Topic;

import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.MultiNode;

public class MyNode extends MultiNode {

	public MyNode(AbstractGraph graph, String id) {
		super(graph, id);
		
	}

	public void addTopicsAttribute(){
		this.addAttribute("topics", new LinkedList<Topic>());
	}
	public void addUsernameAttribute(String username){
		this.addAttribute("username", username);
	}
}
