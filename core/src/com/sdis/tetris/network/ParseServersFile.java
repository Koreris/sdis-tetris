package com.sdis.tetris.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;


public class ParseServersFile implements Runnable {
	
	ConcurrentHashMap<String,Integer> records;
	public ParseServersFile(ConcurrentHashMap<String,Integer> other_servers) {
		records = other_servers;
	}
	@Override
	public void run() {
		try {
			File file = new File ("servers.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] lineComponents = line.split(":");
				records.put(lineComponents[0], Integer.parseInt(lineComponents[1]));
			}
			fileReader.close();
		}
		catch(Exception e) {
			System.out.println("Servers file must be located at " +new File(".").getAbsolutePath());
			e.printStackTrace();
		}
	}
}
