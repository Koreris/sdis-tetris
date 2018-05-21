package com.sdis.tetris.network;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TetrisLobbySerializable implements Serializable {
	private static final long serialVersionUID = 1L;
	String lobby_name;
	String[] scores; /* Save player username and score here */
	
	public TetrisLobbySerializable setLobbyName(String name) {
		lobby_name=name;
		return this;
	}

	public TetrisLobbySerializable setScores(ConcurrentHashMap<String,Integer> map) {
		scores = new String[map.size()];
		int count = 0;
		for (String key : map.keySet()) 
		{
			scores[count]=key+":"+map.get(key);
			count++;
		}
		return this;
	}
	
	public String toString() {
		String players = "";
		String addresses = "";
		for(int i=0; i<scores.length; i++) {
			players+=scores[i]+"\n";
		}
		return "Lobby Name: "+lobby_name+ "\nPlayers:\n"+players+"\nAddresses:\n"+addresses;
	}
}
