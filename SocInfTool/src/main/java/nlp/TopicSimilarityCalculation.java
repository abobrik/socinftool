package nlp;

import java.util.HashMap;

import parser.MyGraph;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class TopicSimilarityCalculation {
	private MyGraph graph;
	private TopicSimilarityHashMap topSimHashMap;
	
    private ILexicalDatabase db = new NictWordNet();
    private RelatednessCalculator[] rcs = {
        new HirstStOnge(db), 
        new LeacockChodorow(db), 
        new Lesk(db),  
        new WuPalmer(db),
        new Resnik(db), 
        new JiangConrath(db), 
        new Lin(db), 
        new Path(db)
        };
    
    
	public TopicSimilarityCalculation(MyGraph graph) {
		super();
		this.graph = graph;
		this.topSimHashMap = new TopicSimilarityHashMap((HashMap<String,Topic>) graph.getTopicsAttribute());
		
		
	}
	public void calculateLeskTopicSimilarities(){
		calculateTopicSimilarities(rcs[2]);
	}
	public void calculateResnikTopicSimilarities(){
		calculateTopicSimilarities(rcs[4]);
	}
	public void calculateLinTopicSimilarities(){
		calculateTopicSimilarities(rcs[6]);
	}
	/**
	 * @param rc
	 */
	private void calculateTopicSimilarities(RelatednessCalculator rc){
		// do not use only most frequent sense (setMFS(true)) but all senses to calculate maximum similarity
		WS4JConfiguration.getInstance().setMFS(false);
		// similarities werden hier noch doppelt berechnet
		HashMap<String,Topic> topics = (HashMap<String,Topic>) graph.getAttribute("topics");
		System.out.println(topics.size());
		for(Topic topic1:topics.values()){
			for(Topic topic2:topics.values()){
				// calculate semantic relatedness for two topics
			
				System.out.println(rc==null);
				double sim = rc.calcRelatednessOfWords(topic1.getTopic(), topic2.getTopic());
				
				// create new topic similarity
				TopicSimilarity topSim = new TopicSimilarity(topic1, topic2, sim);
//		        System.out.println( rc.getClass().getName()+"\t"+topic1+"\t"+topic2+"\t"+sim);
		        
				this.topSimHashMap.get(topic1).put(topic2, topSim);
			}
		}
	}
	

}
