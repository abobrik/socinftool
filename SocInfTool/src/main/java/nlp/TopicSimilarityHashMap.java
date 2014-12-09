package nlp;

import java.util.HashMap;

public class TopicSimilarityHashMap extends HashMap<Topic,HashMap<Topic,TopicSimilarity>>{
	
	
	public TopicSimilarityHashMap(HashMap<String, Topic> topicCollection) {
		super();
		for(Topic topic:topicCollection.values()){
			this.put(topic, new HashMap<Topic,TopicSimilarity>());
		}
	}

	
}
