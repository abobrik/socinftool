package parser;
import java.io.IOException;
import java.util.List;

import neo4j.Neo4jInterface;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MacRumorsParser2{
	public static final String ROOT_URL = "http://forums.macrumors.com";

	private long nextNodeId=1;
	private long nextEdgeId=1;
	private MyGraph graph;

	private Node lastNode;
	
	private List<MacRumorsPost> posts;
	Neo4jInterface n4jinf;

	private org.neo4j.graphdb.Node lastNeo4JNode;
	
	public MacRumorsParser2(){

		
		graph = new MyGraph("MacRumors-Forum");
		
		posts = graph.getPostsAttribute();
	}
    public void fetchDataFromURL(String thread, int page, Neo4jInterface ninf){
    	n4jinf = ninf;
    	String url = ROOT_URL+"/"+thread;
        System.out.println("[INFO] Parsing "+url+"&page="+page+"...");
    	
    	Document document;
		try {
			document = Jsoup.connect(url+"&page="+page)
					.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
					.referrer("http://www.google.com")
					.get(); // website tries do avoid non-human access
			
			Elements posts = document.select("#posts .page");
			lastNode = null;
	        for (Element post : posts) {
	        	createPost(post);
	        }
	        
	        System.out.println("[INFO] Number of nodes in graph: "+graph.getNodeCount());
	        System.out.println("[INFO] Number of edges in graph: "+graph.getEdgeCount());
	        System.out.println("[INFO] Number of posts in graph: "+graph.getPostsAttribute().size());
	        
	        int num_pages = document.select("#mainContainer .pagenav .alt1").size()/2;
	        
	        if(page<num_pages+1){
	        	page++;
//	        	fetchDataFromURL(thread, page,n4jinf);
	        }

		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
    public void createPost(Element post){
    	// Retrieve content properties
    	String text = post.select("div[id^=\"post_message_\"]").text();
    	String header = post.select(".alt1 .smallfont").text();
    	String date = post.select(".tcat").get(0).text(); // TODO: transform to Date type
    	String num= post.select(".tcat").get(1).text();
    	// TODO: Remove Quotes of other posts etc. from posts
    	org.neo4j.graphdb.Node n4jpost= n4jinf.addUniquePostNode(this.nextEdgeId, header, text, date, num, lastNeo4JNode);
    
    	
    	MacRumorsPost p = new MacRumorsPost(num, header, text, date);
    	posts.add(p);
    	
    	String username = post.select(".alt2 .bigusername").text();
    	org.neo4j.graphdb.Node neo4juser = n4jinf.addUniqueUserNode(username, this.nextNodeId, n4jpost);
    	
    	Node node = createNode(p, post);
    	
//    	p.setReferencedPost(lastPost);
    	
    	
    	createEdge(p, node);

    	lastNeo4JNode = n4jpost;
    	lastNode = node;
    }
    
    public Node createNode(MacRumorsPost p, Element post){
    	// Retrieve node properties
    	String username = post.select(".alt2 .bigusername").text();

    	// create new node with attributes
    	// [Q] directly creating node seems not possible with graphstream
    	System.out.println("[INFO] Parsing node "+username);
    	
    	Node node = this.graph.addNode(username, this.nextNodeId);
    	p.setNode(node);
    	return node;
    	
    }
    public void createEdge(MacRumorsPost p, Node node){
    	System.out.println("[INFO] Parsing edge "+p.getNum());
    	if(lastNode!=null){
    		Edge edge = graph.addEdge(node, lastNode, this.nextEdgeId);
	    	this.nextEdgeId++;
	       	p.setEdge(edge);
    	} else {
    		// node is thread origin
    		node.addAttribute("ui.class", "origin");
    	}
    }

    public void displayGraph(){
    	graph.displayGraph();
    }

	public MyGraph getGraph() {
		return graph;
	}

}

