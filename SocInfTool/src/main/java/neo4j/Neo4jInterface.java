package neo4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;
	
public class Neo4jInterface {
	GraphDatabaseService graphDb;
	private final static String DB_PATH = "D:\\Programming\\Neo4j";
	ExecutionEngine engine;
	
	public void init() {
		delete();
		
		// Instantiate a GraphDatabaseService to create a new database or open an existing one you
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		// Registers a shutdown hook for the Neo4j instance.
		registerShutdownHook( graphDb );
		
		// Use indexes if you want to retrieve users by username. 
		// Then we have to configure the database to index users by name. This only needs to be done once.
		try ( Transaction tx = graphDb.beginTx() )
		{
			
			graphDb.schema()
		            .constraintFor( DynamicLabel.label( "Post" ) )
		            .assertPropertyIsUnique( "num" )
		            .create();
		    graphDb.schema()
            .constraintFor( DynamicLabel.label( "User" ) )
            .assertPropertyIsUnique( "username" )
            .create();

		    tx.success();
		}
		engine = new ExecutionEngine(graphDb);
	}


	public Node addUniquePostNode(String header, String text, String date, String num, String postId,  Node lastPost) {
		Node post = null;
		ResourceIterator<Node> resultIterator = null;
		try ( Transaction tx = graphDb.beginTx() )
		{
		    String queryString = "MERGE (n:Post {num: {num}, post_message_id: {post_message_id}, text: {text}, header: {header}, date: {date}}) RETURN n";
		    Map<String, Object> parameters = new HashMap<>();
		    parameters.put( "post_message_id", postId );
		    parameters.put( "num", num );
		    parameters.put( "text", text );
		    parameters.put( "header", header );
		    parameters.put( "date", date );
		    resultIterator = engine.execute( queryString, parameters ).columnAs( "n" );
		    post = resultIterator.next();
		    
			if(lastPost!=null)
				post.createRelationshipTo(lastPost, RelTypes.CITES);
			
		    tx.success();
		  
		}
		return post;
	}
	public Node getPostNode(long postId) {
		Node post = null;
		ResourceIterator<Node> resultIterator = null;
		try ( Transaction tx = graphDb.beginTx() )
		{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put( "post_message_id", postId );
			String query = "MATCH (n{post_message_id:'"+postId+"'}) RETURN n";
			org.neo4j.cypher.javacompat.ExecutionResult result = engine.execute( query, params );
			
			System.out.println(result.dumpToString());
		    tx.success();
		  
		}
		return post;
	}
	public Node addUniqueUserNode(String username, Node post){
		Node user;
	
		ResourceIterator<Node> resultIterator = null;
		try ( Transaction tx = graphDb.beginTx() )
		{
		    String queryString = "MERGE (n:User {username: {username}}) RETURN n";
		    Map<String, Object> parameters = new HashMap<>();
		    parameters.put( "username", username );
		    resultIterator = engine.execute( queryString, parameters ).columnAs( "n" );
		    user = resultIterator.next();
		
			user.createRelationshipTo( post, RelTypes.WRITES );

		    tx.success();
		}
		return user;
	}
	public void shutdown(){
		graphDb.shutdown();

	}
	public static void delete(){
		try {
			FileUtils.deleteRecursively(new File(DB_PATH));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
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
	private static enum RelTypes implements RelationshipType
	{
	    WRITES, CITES
	}
	
	
	public GraphDatabaseService getGraphDb() {
		return graphDb;
	}

}
