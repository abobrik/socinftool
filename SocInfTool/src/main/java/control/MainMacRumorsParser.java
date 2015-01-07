package control;

import parser.MacRumorsParser3;

public class MainMacRumorsParser {

	/**
	 * Application entry point
	 * @param args
	 * 			args[0]	boolean value. true=override old DB content; false=not override old DB content.
	 * 			args[1] int value. thread-id to be parsed. Use format "showthread.php?t=<thread-id>".  
	 * 					Don't start with link to first page. thread has to be ...php?t=... instead of ...php?p=...
	 * 			args[2]	int value. number of start page.
	 * 			args[3]	int value. number of pages to parse.
	 */
	public static void main(String[] args) {
		boolean overrideDB = Boolean.parseBoolean(args[0]);
		int threadID = Integer.parseInt(args[1]);
		int startPage = Integer.parseInt(args[2]);
		int numPages = Integer.parseInt(args[3]);
		String pathDB = args[4];
		
		// TODO: !!! dont start with link to first page. thread has to be ...php?t=... instead of ...php?p=...
		String thread = "showthread.php?t="+threadID; //showthread.php?t=1821262 a thread with quotes not referring to a post inside the thread but some external source
        MacRumorsParser3 parser = new MacRumorsParser3(overrideDB, pathDB);
        parser.fetchDataFromURL(thread,startPage, numPages);

        parser.applyAdditionalLabelsAndRelationships();
	    parser.shutdown();
	}

}
