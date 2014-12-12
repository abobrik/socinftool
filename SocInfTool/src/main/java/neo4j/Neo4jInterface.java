package neo4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import nlp.TopicExtraction;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;
	
public class Neo4jInterface {
	GraphDatabaseService graphDb;
	private final static String DB_PATH = "/Applications/neo4j-community-2.1.6/data/graph.db";
	ExecutionEngine engine;
	
	
	public Neo4jInterface() {
		super();
		init();
	}


	public void init() {
		System.out.println("[INFO][NEO4J] Init neo4j database. Path: "+DB_PATH);
		delete();
		
		// Instantiate a GraphDatabaseService to create a new database or open an existing one you
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		// Registers a shutdown hook for the Neo4j instance.
		registerShutdownHook( graphDb );
		
		// Use indexes if you want to retrieve users by username. 
		// Then we have to configure the database to index users by name. This only needs to be done once.
		try ( Transaction tx = graphDb.beginTx() )
		{
			System.out.println("[INFO][NEO4J] Create constraints.");
			// create constraints
			graphDb.schema()
		            .constraintFor( DynamicLabel.label( "Post" ) )
		            .assertPropertyIsUnique( "postId" )
		            .create();
		    graphDb.schema()
            .constraintFor( DynamicLabel.label( "User" ) )
            .assertPropertyIsUnique( "username" )
            .create();
		    graphDb.schema()
            .constraintFor( DynamicLabel.label( "Quote" ) )
            .assertPropertyIsUnique( "quoteId" )
            .create();
		    graphDb.schema()
            .constraintFor( DynamicLabel.label( "Topic" ) )
            .assertPropertyIsUnique( "topic" )
            .create();

		    tx.success();
		}
		engine = new ExecutionEngine(graphDb);
	}


	public Node addPostNode(String postId, String header, String text, String date, String mdate, String username, Node lastPost) {
		Node post;
		if(lastPost!=null){
			ResourceIterator<Node> resultIterator = null;
			try ( Transaction tx = graphDb.beginTx() )
			{
				String lastPostId = lastPost.getProperty("postId").toString();
			    String queryString = "MERGE (p:Post {postId:{postId}}) "
			    		+ "SET "
			    		+ "p.text={text}, "
			    		+ "p.date={date}, "
			    		+ "p.mdate=toInt({mdate}) "
			    		+ "MERGE (u:User {username: {username}})"
			    		+ "MERGE (p_old:Post {postId: {lastPostId}})"
			    		+ "MERGE (u)-[w:WRITES {date: {date}, mdate: toInt({mdate})}]-(p)"
			    		+ "MERGE (p)-[ia:IS_AFTER]-(p_old)"
			    		+ " RETURN p";
			    Map<String, Object> parameters = new HashMap<>();
			    parameters.put( "postId", postId );
			    parameters.put( "text", text );
			    parameters.put( "date", date );
			    parameters.put( "mdate", mdate );
			    parameters.put( "username", username );
			    parameters.put( "lastPostId", lastPostId );
			    resultIterator = engine.execute( queryString, parameters ).columnAs( "p" );
			    post = resultIterator.next();
	
			    tx.success();
			}
		} else { // Post is rootPost
			ResourceIterator<Node> resultIterator = null;
			try ( Transaction tx = graphDb.beginTx() )
			{
			    String queryString = "MERGE (p:Post "
			    		+ "{postId: {postId}}) SET "
			    		+ "p.header={header}, "
			    		+ "p.text={text}, "
			    		+ "p.date={date}, "
			    		+ "p.mdate=toInt({mdate}), "
			    		+ "p.isRoot='true'"
			    		+ "MERGE (u:User {username: {username}}) "
			    		+ "MERGE (u)-[w:WRITES {date: {date}, mdate: toInt({mdate})}]-(p)"
			    		+ " RETURN p";
			    Map<String, Object> parameters = new HashMap<>();
			    parameters.put( "postId", postId );
			    parameters.put( "header", header );
			    parameters.put( "text", text );
			    parameters.put( "date", date );
			    parameters.put( "mdate", mdate );
			    parameters.put( "username", username );
			    resultIterator = engine.execute( queryString, parameters ).columnAs( "p" );
			    post = resultIterator.next();
	
			    tx.success();
			}
		}
		return post;
	}
	
	public Node addQuoteNode(String quoteId, String postId, String refPostId, String text, String date, String mdate){
		Node quote;
	
		ResourceIterator<Node> resultIterator = null;
		try ( Transaction tx = graphDb.beginTx() )
		{
		    String queryString = "MERGE (q:Quote {"
		    		+ "quoteId: {quoteId}, "
		    		+ "postId: {postId}, "
		    		+ "refPostId: {refPostId},"
		    		+ "text: {text}, "
		    		+ "date: {date}, "
		    		+ "mdate: toInt({mdate})}) "
		    		+ "MERGE (p_ref:Post {postId: {refPostId}}) "
		    		+ "MERGE (q)-[r:REFERS_TO {date: {date}, mdate: toInt({mdate})}]-(p_ref) "
		    		+ "MERGE (p:Post {postId: {postId}}) "
		    		+ "MERGE (p)-[i:INCLUDES]-(q) "
//		    		+" MERGE (t:Topic2 {topic:{text}}) "
//		    		+" MERGE (p)-[h:HAS]-(t) "
		    		+ "RETURN q";
		    Map<String, Object> parameters = new HashMap<>();
		    parameters.put( "quoteId", quoteId );
		    parameters.put( "postId", postId );
		    parameters.put( "refPostId", refPostId );
		    parameters.put( "text", text );
		    parameters.put( "date", date );
		    parameters.put( "mdate", mdate );
		    resultIterator = engine.execute( queryString, parameters ).columnAs( "q" );
		    quote = resultIterator.next();


		    tx.success();
		}
		return quote;
	}
	
	

	public void shutdown(){
		System.out.println("[INFO][NEO4J] Finished. Shutdown database.");
		graphDb.shutdown();

	}
	public static void delete(){
		try {
			System.out.println("[INFO][NEO4J] Delete old content.");
			FileUtils.deleteRecursively(new File(DB_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
		System.out.println("[INFO][NEO4J] Register shutdown hook.");
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}

	
	public GraphDatabaseService getGraphDb() {
		return graphDb;
	}




	public ResourceIterator<Node> loadNodes(String type) {
		
		System.out.println("[INFO][NEO4J] Load all "+type+" nodes from database.");
		ResourceIterator<Node> nodes = null;
		try ( 
				Transaction tx = graphDb.beginTx() )
			{
			    String queryString = "MERGE (n:"+type+") RETURN n";
			    nodes = engine.execute( queryString ).columnAs( "n" );
			    tx.success();		    
			}
		
		return nodes;
				
	}

	public void extractTopicsFromNode(TopicExtraction tex, String label, String idKey, String idValue, String text) {

			    		Hashtable<String, Integer> topics = tex.retrieveTopics(text);
			    		addTopicNodes(topics,label,idKey, idValue);
			    		System.out.println("[INFO][TEX] topic count "+topics.size());
		    		


				
	}
	public void addTopicNodes(Hashtable<String,Integer> topics, String label, String idKey, String idValue) {
		
		
		for(Entry<String, Integer> t:topics.entrySet()){

		    try(Transaction tx = graphDb.beginTx() )
			{ 
		    	String queryString = "MERGE (t:Topic {topic:{topic}}) "
			    		+ "MERGE (n:"+label+" {"+idKey+": {id}}) "
			    		+ "MERGE (n)-[h:HAS {count:{count}}]-(t) "
			    		+ "RETURN t";
			    Map<String, Object> parameters = new HashMap<>();
			    parameters.put( "topic", t.getKey() );
			    parameters.put( "count", t.getValue() );
			    parameters.put( "id", idValue );
			    ResourceIterator<Node> resultIterator = engine.execute( queryString, parameters ).columnAs( "t" );
			    resultIterator.next();
			    tx.success();
			} 
		}
	}
}
