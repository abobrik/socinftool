package parser;
import java.io.IOException;
import java.util.List;

import neo4j.Neo4jInterface;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neo4j.graphdb.Node;


public class MacRumorsParser3{
	public static final String ROOT_URL = "http://forums.macrumors.com";
	
	private List<MacRumorsPost> posts;
	Neo4jInterface n4jinf;

	private org.neo4j.graphdb.Node lastPost;
	
	public MacRumorsParser3(){
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

	        for (Element post : posts) {
	        	parseElement(post);
	        }
	        
	        int num_pages = document.select("#mainContainer .pagenav .alt1").size()/2;
	        
	        if(page<num_pages+1){
	        	page++;
	        	fetchDataFromURL(thread, page,n4jinf);
	        }

		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
    public void parseElement(Element element){
    	// Retrieve content properties
    	Elements body = element.select("div[id^=\"post_message_\"]");
    	String text = body.text();
    	
    	String postId = body.attr("id").replaceAll("post_message_","");

    	// improve select!!!
    	String header = element.select(".alt1 .smallfont").text();
    	String date = element.select(".tcat").get(0).text(); // TODO: transform to Date type
    	String num= element.select(".tcat").get(1).text();
    	// TODO: Remove Quotes of other posts etc. from posts
    	// several quotes?
    	// quote text?
    	if(text.contains("Quote:")){
    		String refurl = body.select("a").attr("href").toString();
    		long refPostId = Long.parseLong(refurl.substring(refurl.indexOf("#")+5,refurl.length()));
    		System.out.println(refPostId);
    		n4jinf.getPostNode(refPostId);
    	}

    	
    	Node post= n4jinf.addUniquePostNode(header, text, date, num, postId, lastPost);
    	
    	String username = element.select(".alt2 .bigusername").text();
    	Node user = n4jinf.addUniqueUserNode(username, post);

    	lastPost = post;
    }
    



}

