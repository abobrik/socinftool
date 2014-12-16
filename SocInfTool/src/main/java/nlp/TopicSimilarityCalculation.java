package nlp;
import java.util.Vector;

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
       
        private static ILexicalDatabase db = new NictWordNet();
        private static RelatednessCalculator[] rcs = {
                        new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db),
                        new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
                        };
        
		private Vector<String> topics;
		private Vector<TopicSimilarity> topicSims;
		
    	public TopicSimilarityCalculation() {
    		super();

    	}
    	public Vector<TopicSimilarity> calcTopicSimilarity(Vector<String> topics, int type){
    		this.topics = topics;
    		String metric = rcs[3].getClass().getSimpleName();
    		System.out.println("[INFO] Calculate pairwise topic similarity using "+metric);
    		
    		topicSims = new Vector<TopicSimilarity>();
    		
    		for(int i=0; i<topics.size(); i++){
    		
				System.out.println("[INFO] Calculate next pairwise topic similarities for topic '"+this.topics.get(i)+"' ("+i+1+"/"+this.topics.size()+")");
	
    			for(int j=0; j<i; j++){				
    				String t1 = this.topics.get(i);
    				String t2 = this.topics.get(j);
    				double sim = TopicSimilarityCalculation.run(rcs[type],t1,t2);
    				TopicSimilarity tsim = new TopicSimilarity(t1,t2,Math.round(sim*1000)/1000.0, metric);
    				topicSims.add(tsim);
    			}
    		}
    		return topicSims;
    	}
    	
        private static double run( RelatednessCalculator rc, String word1, String word2 ) {
        	// INFO: do not use only most frequent sense (setMFS(true)) but all senses to calculate maximum similarity
            WS4JConfiguration.getInstance().setMFS(false);
            return rc.calcRelatednessOfWords(word1, word2);
        }
        public static double runResnik( String word1, String word2 ) {
        	return run(rcs[4], word1, word2);
        }
        public static double runLin( String word1, String word2 ) {
        	return run(rcs[6], word1, word2);
        }
        public static double runWuPalmer( String word1, String word2 ) {
        	return run(rcs[3], word1, word2);
        }
		public Vector<TopicSimilarity> getTopicSims() {

			return this.topicSims;
		}

}

