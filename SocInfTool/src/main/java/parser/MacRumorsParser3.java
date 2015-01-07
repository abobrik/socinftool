package parser;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import neo4j.Neo4jInterface;
import nlp.TopicExtraction;
import nlp.TopicSimilarityCalculation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MacRumorsParser3{
	public static final String ROOT_URL = "http://forums.macrumors.com";
	
	Neo4jInterface n4jinf;
	
	private TopicExtraction tex;
	private Hashtable<String, Integer> topics;
	
	private TopicSimilarityCalculation tsc;
	
	private org.neo4j.graphdb.Node lastPost;
	private int quoteId=0;
	
	private Hashtable<String, String> months;
	
	public MacRumorsParser3(){
		n4jinf = new Neo4jInterface(true);
		
        tex = new TopicExtraction();
		tsc = new TopicSimilarityCalculation(n4jinf);
		
		months= new Hashtable<String,String>();
    	months.put("Jan","01");
    	months.put("Feb","02");
    	months.put("Mar","03");
    	months.put("Apr","04");
    	months.put("May","05");
    	months.put("Jun","06");
    	months.put("Jul","07");
    	months.put("Aug","08");
    	months.put("Sep","09");
    	months.put("Oct","10");
    	months.put("Nov","11");
    	months.put("Dec","12");
	}
	public MacRumorsParser3(boolean overrideDB){
		n4jinf = new Neo4jInterface(overrideDB);
		
        tex = new TopicExtraction();
		tsc = new TopicSimilarityCalculation(n4jinf);
		
		months= new Hashtable<String,String>();
    	months.put("Jan","01");
    	months.put("Feb","02");
    	months.put("Mar","03");
    	months.put("Apr","04");
    	months.put("May","05");
    	months.put("Jun","06");
    	months.put("Jul","07");
    	months.put("Aug","08");
    	months.put("Sep","09");
    	months.put("Oct","10");
    	months.put("Nov","11");
    	months.put("Dec","12");
	}
	public MacRumorsParser3(boolean overrideDB, String pathDB){
		n4jinf = new Neo4jInterface(overrideDB, pathDB);
		
        tex = new TopicExtraction();
		tsc = new TopicSimilarityCalculation(n4jinf);
		months= new Hashtable<String,String>();
    	months.put("Jan","01");
    	months.put("Feb","02");
    	months.put("Mar","03");
    	months.put("Apr","04");
    	months.put("May","05");
    	months.put("Jun","06");
    	months.put("Jul","07");
    	months.put("Aug","08");
    	months.put("Sep","09");
    	months.put("Oct","10");
    	months.put("Nov","11");
    	months.put("Dec","12");

	}
    public void fetchDataFromURL(String thread, int page, int numPages){
    	String url = ROOT_URL+"/"+thread;
        System.out.println("[INFO][PARSER] Parsing "+url+"&page="+page+"...");

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
	        
	        if(page<=num_pages&page<numPages){
	        	page++;
	        	fetchDataFromURL(thread, page, numPages);
	        }

		} catch (IOException e) {
			e.printStackTrace();
		} 
    }

    public void parseElement(Element element){
    	System.out.println("[INFO][PARSER] Parsing next post...");
    	
    	Elements body = element.select("div[id^=\"post_message_\"]");
    	String text = body.text();
    	
    	String postId = body.attr("id").replaceAll("post_message_","");

    	// improve select!!!
    	String header = element.select(".alt1 .smallfont").text();
    	String date = element.select(".tcat").get(0).text(); // TODO: transform to Date type
    	String num= element.select(".tcat").get(1).text();
    	num = num.substring(2,num.length());
    	System.out.println("[INFO][PARSER] Parsing next post..."+num);
    	
    	// transform date-String into Date and get milliseconds for later comparison
    	Date fDate = null;
    	long mDate = 0;
      	try {
    			fDate = getDate(date);
    			date=fDate.toString();
    			mDate = fDate.getTime();
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
    	String username = element.select(".alt2 .bigusername").text();
    	// TODO: extract quote text, remove quote text from quote??
    	if(text.contains("Quote:")){

    		header=""; // header of follow-up posts contains only text "Quote:"
  
    		try{
    			// quotes are formatted with tbody-html tag
    			Elements quotes = body.select("tbody");
    			for(Element q:quotes){
    				
    				String refurl = q.select("a").attr("href").toString();
    				if(refurl.length()>0){ // else quote does not refer to other post but quotes some other source
	    				String quote = q.text();
	    				String qUsername = q.select("strong").text();
	    				
	    				// TODO: should quotes be entirely removed from text, 
	    				// e.g. be not part of the text mining process??
	    				// remove entire quote from text
//	    				text=text.replace("Quote: "+quote,"");
	    				
	    				// remove tag "Quote:" from text
	    				text=text.replace("Quote: ","");
	    			
	    				
	    				// remove quote meta information from quote
	    				quote=quote.replace("Originally Posted by "+qUsername , "");
	    				text=text.replace("Originally Posted by "+qUsername , "");
	    				
			    		String refPostId =refurl.substring(refurl.indexOf("#")+5,refurl.length());

			    		

//			        	double sentiment = 0;/*tex.getSentiment(quote);*/
			    		n4jinf.addQuoteNode(Integer.toString(quoteId), postId, refPostId, quote, date, Long.toString(mDate));
			    		n4jinf.extractTopicsFromNode(tex, "Quote", "quoteId", Integer.toString(quoteId), text);
			    		this.quoteId++;
    				}
	    		}

    		} catch (Exception e){
    			e.printStackTrace();
    		}
    	} 
    	// create post node, user node and user-writes-post relationship
    	lastPost = n4jinf.addPostNode(postId, header, text, date, Long.toString(mDate), username, lastPost);
    	n4jinf.extractTopicsFromNode(tex, "Post", "postId", postId, text);
		
    }

    // TODO: improve
    private Date getDate(String date) throws ParseException{
    	String[] s = date.split("(\\s|\\p{Punct})+"); // splits at whitespace or any punctuation mark
    	String year = s[2];
    	String day = s[1];
    	String month = months.get(s[0]);
    	int hour = Integer.parseInt(s[3]);
    	String minutes = s[4];
    	if(s[5].equals("PM"))
    		hour = hour+12;
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    	return simpleDateFormat.parse(day+"."+month+"."+year+" "+hour+":"+minutes);
    }


	public void applyAdditionalLabelsAndRelationships() {
		applyIndirectUserCitesUserRelationship();
		applyLabelsOnNodes();
	}
	public void applyIndirectUserCitesUserRelationship(){
		System.out.println("[INFO][PARSER] Apply indirect User-cites-User relationship");
		n4jinf.applyIndirectUserRelationship();
	}
	public void applyLabelsOnNodes(){
		System.out.println("[INFO][PARSER] Apply labels for all nodes");
		n4jinf.applyUsersLabel();
		n4jinf.applyQuotesLabel();
		n4jinf.applyPostsLabel();
		n4jinf.applyTopicsLabel();
	}

	public void calcTopicSimilarities(int type){
		tsc.calcTopicSimilarities(type);
	}

	public void shutdown() {
		n4jinf.shutdown();
	}
	public Neo4jInterface getNeo4jInterface() {
		return this.n4jinf;
	}
}

