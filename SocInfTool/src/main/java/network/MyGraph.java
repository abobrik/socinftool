package network;

import java.util.HashMap;
import java.util.LinkedList;

import nlp.Topic;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

public class MyGraph extends MultiGraph{

	public MyGraph(String id) {
		super(id);
		init();
	}
	
	private void init(){
		initTopicsAttribute();
		initPostsAttribute();
	}
	private void initTopicsAttribute(){
		this.addAttribute("topics", new HashMap<String, Topic>());
	}
	private void initPostsAttribute(){
		this.addAttribute("posts", new LinkedList<MacRumorsPost>());
	}
	
	public Node addNode(String username, long nextNodeId){
		Node node = this.getNode(username);
    	
    	if(node==null){
	    	this.addNode(username);
	    	node = this.getNode(username);
	    	node.addAttribute("username", username);
	    	node.addAttribute("id", nextNodeId);
			node.addAttribute("topics", new LinkedList<Topic>());
    	}
    	return node;
	}
	public Edge addEdge(Node node, Node lastNode, long id){
		this.addEdge(Long.toString(id), node, lastNode);
    	Edge edge = this.getEdge(Long.toString(id));
    	edge.addAttribute("topics", new LinkedList<Topic>());
    	return edge;
	}
	
	public HashMap<String, Topic> getTopicsAttribute(){
		return this.getAttribute("topics");
	}
	public LinkedList<MacRumorsPost> getPostsAttribute(){
		return this.getAttribute("posts");
	}

	
    public void displayGraph(){
    	
    	this.addAttribute("ui.stylesheet",styleSheet);
    	for (Node node : this) {
    	    node.addAttribute("ui.label", node.getAttribute("username"));
    	}
    	this.display();
    }
    // put this into local file, see http://graphstream-project.org/doc/Tutorials/Graph-Visualisation_1.1/#the-graphstream-css
    protected String styleSheet =
    	    "node {" +
    	    "       fill-color: black;" +
    	    "}" +
    	    "node.origin {" +
    	    "       fill-color: red;" +
    	    "}";

}
