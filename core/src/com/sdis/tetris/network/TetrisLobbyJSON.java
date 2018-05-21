package com.sdis.tetris.network;

import java.io.StringReader;
import java.util.concurrent.ConcurrentHashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;


public class TetrisLobbyJSON{
	String lobby_name;
	String[] scores; /* Save player username and score here */
	
	public TetrisLobbyJSON(String lobby_name,String[] scores) {
		this.lobby_name=lobby_name;
		this.scores=scores;
	}
	
	public TetrisLobbyJSON() {
	}
	
	public TetrisLobbyJSON setLobbyName(String name) {
		lobby_name=name;
		return this;
	}

	public TetrisLobbyJSON setScores(ConcurrentHashMap<String,Integer> map) {
		scores = new String[map.size()];
		int count = 0;
		for (String key : map.keySet()) 
		{
			scores[count]=key+":"+map.get(key);
			count++;
		}
		return this;
	}

	public JsonObject toJSON() {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for(String sc: scores) {
			builder.add(sc);
		}
		return Json.createObjectBuilder()
				.add("name", lobby_name)
				.add("scores", builder)
				.build();
	}
	
	public static TetrisLobbyJSON fromJSON(String jsonstr) {
		JsonReader jsonReader = Json.createReader(new StringReader(jsonstr));
		JsonObject obj = jsonReader.readObject();
		
		String name = obj.getString("name");
		JsonArray arr = obj.getJsonArray("scores");
		
		String[] scores = new String[arr.size()];
		int i=0;
		for(JsonValue score:arr) {
			scores[i]=score.toString().substring(1, score.toString().length()-1);
			i++;
		}
		return new TetrisLobbyJSON(name,scores);
	}
	
	public String toString() {
		String players = "";
		for(int i=0; i<scores.length; i++) {
			players+=scores[i]+"\n";
		}
		return "Lobby Name: "+lobby_name+ "\nPlayers:\n"+players+"\n";
	}
	
}
