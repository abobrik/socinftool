package nlp;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;


public class TopicExtraction {

	private Properties props;
	private StanfordCoreNLP pipeline;

	private Hashtable<String, Integer> currtopics;
	private Vector<String> topics;
	
	public TopicExtraction(){
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization
		 props = new Properties();
		 props.put("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
		// TODO: + named entity recogintion; + multi-term words
		 pipeline = new StanfordCoreNLP(props);
		 
		 topics = new Vector<String>();
	}
	
	public Hashtable<String, Integer> retrieveTopics(String text){
		currtopics = new Hashtable<String, Integer>();
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
		    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    int sumSentiment = 0;
	    for(CoreMap sentence: sentences) {
	    	Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
	    	sumSentiment=sumSentiment+RNNCoreAnnotations.getPredictedClass(tree);
            
	    	
	    	
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        // this is the lemma of the token
	        String lemma = token.get(LemmaAnnotation.class);
//		        System.out.println(word+" "+pos+ " "+lemma);
	        
	        String topic = lemma.toLowerCase();
	        if("NN NNS NNP NNPS".contains(pos)&&topic.length()>2){
	        	addTopic(topic);
	        }
	      }
	    }
	    double avgSentiment=sumSentiment/sentences.size();
	    return currtopics;
	}
	public void addTopic(String topic){
		if(!topics.contains(topic)){
			topics.add(topic);
		}
		if(this.currtopics.containsKey(topic)){
			int count = this.currtopics.get(topic)+1;
			this.currtopics.remove(topic);
			this.currtopics.put(topic, count);
		} else {
			this.currtopics.put(topic, 1);
		}
	}
	public double getSentiment(String text){
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
		    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    int sumSentiment = 0;
	    for(CoreMap sentence: sentences) {
	    	Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
	    	sumSentiment=sumSentiment+RNNCoreAnnotations.getPredictedClass(tree);
	    }
	    System.out.println(sumSentiment/sentences.size());
	    return sumSentiment/sentences.size();
	}
	public Vector<String> getTopics() {
		return topics;
	}
	

}
