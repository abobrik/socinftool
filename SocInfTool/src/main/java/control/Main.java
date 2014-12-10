package control;

import parser.MacRumorsParser;

public class Main {

	public static void main(String[] args) {
		String thread = "showthread.php?t=1816666";
        int startPage = 1;
        MacRumorsParser parser = new MacRumorsParser("D:\\Daten\\");
        parser.initFetchDataFromURL(thread,startPage);
	}

}
