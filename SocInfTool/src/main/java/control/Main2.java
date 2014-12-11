package control;

import neo4j.Neo4jInterface;
import parser.MacRumorsParser3;

public class Main2 {

	public static void main(String[] args) {

		// TODO: !!! dont start with link to first page. thread has to be ...php?t=... instead of ...php?p=...
		String thread = "showthread.php?t=1821262"; // a thread with quotes not refering to a post inside the thread but some external source
        int startPage = 1;
        MacRumorsParser3 parser = new MacRumorsParser3();
        parser.fetchDataFromURL(thread,startPage);
        parser.extractTopics();
	    parser.shutdown();
	}

}
