package parser;

import java.util.LinkedList;

import nlp.Topic;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

public class MacRumorsPost {
	private Node node;
	private Edge edge;
	private MacRumorsPost referencedPost;
	
	private String num;
	private String header;
	private String post;
	private String date;
	private LinkedList<Topic> topics;
	
	public MacRumorsPost(String num, String header, String post, String date) {
		super();
		this.num = num;
		this.header = header;
		this.post = post;
		this.date = date;
		this.topics = new LinkedList<Topic>();
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public Edge getEdge() {
		return edge;
	}

	public void setEdge(Edge edge) {
		this.edge = edge;
	}

	public MacRumorsPost getReferencedPost() {
		return referencedPost;
	}

	public void setReferencedPost(MacRumorsPost referencedPost) {
		this.referencedPost = referencedPost;
	}

	public String getNum() {
		return this.num;
	}

	public LinkedList<Topic> getTopics() {
		return topics;
	}

	public String getPost() {
		return post;
	}

	public String getHeader() {
		return this.header;
	}
	
	
}
