package parser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;

import nlp.TopicExtraction;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import au.com.bytecode.opencsv.CSVWriter;


public class MacRumorsParser{
	public static final String ROOT_URL = "http://forums.macrumors.com";

	private CSVWriter csvUOutput;
	private CSVWriter csvPOutput;
	private CSVWriter csvWOutput;
	private CSVWriter csvIAOutput;
	private CSVWriter csvQOutput;
	private CSVWriter csvHTOutput;
	private CSVWriter csvQHTOutput;
	
	private String lastPostId;

	private Hashtable<String, String> months;

	private TopicExtraction tex;
	private Hashtable<String, Integer> topics;
	
	String CSVpath;

	private CSVWriter csvTOutput;


	
	public MacRumorsParser(String path){
		tex = new TopicExtraction();
		CSVpath = path;
		
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
	
	public void initFetchDataFromURL(String thread, int page){
		initCSVFiles();
		fetchDataFromURL(thread, page);
		writeTopicsToCSV();
		closeCSVFiles();
		
	}
	private void writeTopicsToCSV(){
		for(String t:tex.getTopics()){
			csvTOutput.writeNext(new String[]{t});
		}
	}
    public void fetchDataFromURL(String thread, int page){
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
	        	fetchDataFromURL(thread, page);
	        }

		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
    private void initCSVFiles() {
		// initialize csv file for storing user nodes
		csvUOutput = initCSVFile(CSVpath+"users.csv",new String[]{"username"});
        
		// initialize csv file for storing post nodes
        csvPOutput = initCSVFile(CSVpath+"posts.csv",new String[]{"post_message_id","num","header","text","date","mdate"});
        
		// initialize csv file for storing writes relationships
        csvWOutput = initCSVFile(CSVpath+"writes.csv",new String[]{"username","post_message_id","date","mdate"});
        
		// initialize csv file for storing is_after relationships
        csvIAOutput = initCSVFile(CSVpath+"is_after.csv",new String[]{"username","post_message_id","old_post_message_id","date","mdate"});
        
		// initialize csv file for storing quotes relationships
        csvQOutput = initCSVFile(CSVpath+"quotes.csv",new String[]{"post_message_id","quote_post_message_id","quote", "date","mdate"});
	
        // initialize csv file for storing has_topics relationships
        csvTOutput = initCSVFile(CSVpath+"topics.csv",new String[]{"topic"});
	
    	// initialize csv file for storing has_topics relationships
        csvHTOutput = initCSVFile(CSVpath+"has_topic.csv",new String[]{"post_message_id","topic","count"});
	
     // initialize csv file for storing quote-has_topics relationships
        csvQHTOutput = initCSVFile(CSVpath+"quote_has_topic.csv",new String[]{"post_message_id","quote_post_message_id","topic","count"});
	
    }
    private CSVWriter initCSVFile(String path, String[] HEADER) {
    	CSVWriter csvOutput = null;
 		try {
 		// initialize csv file for storing user nodes
 		File file = new File(path);  
         if ( file.exists() )
         	file.delete();
 		file.createNewFile();
		// Use FileWriter constructor that specifies open for appending
        csvOutput = new CSVWriter(new FileWriter(file, true), ',');
        
        // Write header
        csvOutput.writeNext(HEADER);
        
		} catch (IOException e) {
			e.printStackTrace();
		}
 		return csvOutput;
 	}
    public void closeCSVFiles(){
        closeCSVFile(csvUOutput);
        closeCSVFile(csvPOutput);
        closeCSVFile(csvWOutput);
        closeCSVFile(csvIAOutput);
        closeCSVFile(csvQOutput);
        closeCSVFile(csvTOutput);
        closeCSVFile(csvHTOutput);
        closeCSVFile(csvQHTOutput);
    }
    private void closeCSVFile(CSVWriter csvOutput){
        try {
	        csvOutput.flush();
	        csvOutput.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
    	num = num.substring(2,num.length());

    	
    	// transform date-String into Date and get milliseconds for later comparison
    	Date fDate = null;
    	long mDate = 0;
      	try {
    			fDate = getDate(date);
    			date=fDate.toString();
    			mDate = fDate.getTime();
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	// TODO: extract quote text, remove quote text from quote??
    	if(text.contains("Quote:")){
    		try{
    			// quotes are formatted with tbody-html tag
    			Elements quotes = body.select("tbody");
    			for(Element q:quotes){
    				
    				String refurl = q.select("a").attr("href").toString();
    				if(refurl.length()>0){ // else quote does not refer to other post but quotes some other source
	    				String quote = q.text();
	    				String username = q.select("strong").text();
	    				
	    				// TODO: should quotes be entirely removed from text, 
	    				// e.g. be not part of the text mining process??
	    				// remove entire quote from text
//	    				text=text.replace("Quote: "+quote,"");
	    				
	    				// remove tag "Quote:" from text
	    				text=text.replace("Quote: ","");
	    				
	    				// remove quote meta information from quote
	    				quote=quote.replace("Originally Posted by "+username , "");
	    				
	    				if(refurl.length()<5) System.out.println(num);
			    		String refPostId =refurl.substring(refurl.indexOf("#")+5,refurl.length());
			    		csvQOutput.writeNext(new String[]{postId, refPostId, quote, date, mDate+""});
			    		
			    		// retrieve topics from text and store as quote-has_topic relation with post
			        	Hashtable<String, Integer> topics = tex.retrieveTopics(quote);
			        	for(Entry<String, Integer> t:topics.entrySet()){
			        		csvQHTOutput.writeNext(new String[]{postId, refPostId, t.getKey(),t.getValue().toString()});
			        	}
    				}
	    		}

	    		header=""; // header of follow-up posts contains only text "Quote:"
    		} catch (Exception e){
    			e.printStackTrace();
    		}
    	}
    	
    	csvPOutput.writeNext(new String[]{postId, num, header, text, date, mDate+""});
    	
    	// retrieve topics from text and store as has_topic relation with post
    	Hashtable<String, Integer> topics = tex.retrieveTopics(text);
    	for(Entry<String, Integer> t:topics.entrySet()){
    		csvHTOutput.writeNext(new String[]{postId,t.getKey(),t.getValue().toString()});
    	}
    	
    	String username = element.select(".alt2 .bigusername").text();
    	csvUOutput.writeNext(new String[]{username});
    	
    	csvWOutput.writeNext(new String[]{username, postId,date, mDate+""});
    	csvIAOutput.writeNext(new String[]{username, postId,lastPostId, date, mDate+""});
    	
    	lastPostId = postId;
    }
    // improve
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
}

