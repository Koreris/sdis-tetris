package com.sdis.tetris.network;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class TetrisLobby implements Runnable{
	String lobby_name;
	ConcurrentHashMap<InetAddress,Integer> player_addresses; /* Save ip/port combos of players here */
	ConcurrentHashMap<String,Integer> scores; /* Save player username and score here */
	/* TODO Change to SSLSockets */
	transient DatagramSocket socket1; /* A transient field will not be serialized */
	transient TetrisServer master;
	
	
	public TetrisLobby(TetrisServer server,String name,String creator_name, InetAddress creator_address, int creator_port) {
		master = server;
		lobby_name=name;
		player_addresses= new ConcurrentHashMap<InetAddress,Integer>();
		scores = new ConcurrentHashMap<String,Integer>();
		player_addresses.put(creator_address,creator_port);
		scores.put(creator_name, 0);
		//establish socket1 connection with lobby creator
	}
	
	public TetrisLobby setMaster(TetrisServer newmaster) {
		master=newmaster;
		return this;
	}
	
	public void join_lobby(String player_name, InetAddress player_address, int player_port) {
		player_addresses.put(player_address,player_port);
		scores.put(player_name, 0);
		//establish socket connection to this player
	}
	
	public void start_game() {
		//prevent other joins from this point on?
		TetrisLobbySerializable static_representation = 
				new TetrisLobbySerializable()
				.setLobbyName(lobby_name)
				.setPlayerAdresses(player_addresses)
				.setScores(scores);
		this.master.replication_service.triggerReplication(static_representation);
	}
	
	public void end_game() {
		this.master.replication_service.deleteLobby(this.lobby_name);
	}
	
	public String toString() {
		String players="";
		for (String key : scores.keySet()) 
		{
			players+= key + "\n";
		}
		return "Lobby Name: "+lobby_name+ "\nPlayers:\n"+players;
	}
	
	@Override
	public void run() {
		/* TODO this run code is useless, just placeholder */
		while(true) {
			byte[] buf = new byte[TetrisServer.MAX_PACKET_SIZE];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket1.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//handlePacket(packet)
		}
	}
	
}
