package com.sdis.tetris.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TetrisServer implements Runnable{
	String server_name;
	DatagramSocket server_socket;
	//SSLSocket to accept client requests
	ThreadPoolExecutor thread_pool;
	ConcurrentHashMap<String,TetrisLobby> running_lobbies;
	ConcurrentHashMap<String,TetrisLobbySerializable> replicated_lobbies;
	ConcurrentHashMap<String,Integer> local_leaderboards;
	ConcurrentHashMap<String,Integer> other_servers;
	ServerReplicationService replication_service;
	protected static String CRLF = "\r\n";
	final static int MAX_PACKET_SIZE=64096;
	
	public TetrisServer(String name,int port) {
		server_name=name;
		running_lobbies = new ConcurrentHashMap<String,TetrisLobby>();
		replicated_lobbies = new ConcurrentHashMap<String,TetrisLobbySerializable>();
		local_leaderboards = new ConcurrentHashMap<String,Integer>();
		other_servers = new ConcurrentHashMap<String,Integer>();
		
		LinkedBlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>();
		thread_pool = new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS, queue);
		thread_pool.execute(new ParseServersFile(other_servers));
		
		try {
			server_socket = new DatagramSocket(port);
			replication_service = new ServerReplicationService();
			thread_pool.execute(replication_service);
		} catch (SocketException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void run() {
		//accept client requests here to create lobbies
		//parse ip/port information from create lobby
		if(server_name.equals("test"))
			simulateLobbyCreation();
	}
	
	/* function just to test replication */
	public void simulateLobbyCreation() {
		try {
			//when actual thing is implemented, cannot allow creation of lobbies with already existing names
			TetrisLobby newlob = new TetrisLobby(this,"my_lobby","p1",InetAddress.getByName("192.168.1.65"),5555);
			running_lobbies.put("my_lobby",newlob);
			newlob.join_lobby("p2", InetAddress.getByName("192.168.1.67"), 5555);
			newlob.start_game(); //this will be triggered by lobby itself when properly implemented with client connections
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printLobbies() {
		for (String key : running_lobbies.keySet()) 
		{
			System.out.println(running_lobbies.get(key).toString());
		}
		for (String key : replicated_lobbies.keySet()) 
		{
			System.out.println(replicated_lobbies.get(key).toString());
		}
	}
	
	class ServerReplicationService implements Runnable{
		
		@Override
		public void run() {
			while(true) {
				byte[] buf = new byte[TetrisServer.MAX_PACKET_SIZE];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				try {
					server_socket.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
				handleReplicationPacket(packet);
				printLobbies();
			}
			
		}
		
		public void triggerReplication(TetrisLobbySerializable lobby) {
			running_lobbies.get(lobby.lobby_name);
			try {
				String header = "REPLICATE" +  " " + server_name + " " + lobby.lobby_name + CRLF + CRLF;
				byte[] lobby_bytes = Utils.convertToBytes(lobby);
				byte[] replicate = Utils.combineByteArrays(header.getBytes(),lobby_bytes);
				for (String key : other_servers.keySet()) 
				{
					DatagramPacket packet = new DatagramPacket(replicate, 0, replicate.length,InetAddress.getByName(key),other_servers.get(key));
					System.out.println("Sending lobby to "+key+":"+other_servers.get(key));
					server_socket.send(packet);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void deleteLobby(String lobby_name) {
			running_lobbies.remove(lobby_name);
			String header = "DELETE" +  " " + server_name + " " + lobby_name + CRLF + CRLF;
			for (String key : other_servers.keySet()) 
			{
				try {
					DatagramPacket packet = new DatagramPacket(header.getBytes(), 0, header.getBytes().length,InetAddress.getByName(key),other_servers.get(key));
					System.out.println("Sending delete to "+key+":"+other_servers.get(key));
					server_socket.send(packet);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void handleReplicationPacket(DatagramPacket data) {

			String packetString = new String(data.getData(),0,data.getLength());
		
			String[] lines = packetString.split(System.getProperty("line.separator"));
			String header = lines[0];
		
			String[] headerComponents = header.split(" ");
			
			switch(headerComponents[0]) {
				case "REPLICATE":
					try {
						TetrisLobbySerializable lobby = (TetrisLobbySerializable) Utils.convertFromBytes(lines[2].trim().getBytes());
						//Key = original server name + lobby name
						replicated_lobbies.put(headerComponents[1]+headerComponents[2], lobby);
					} catch (Exception e) {
						e.printStackTrace();
					} 
					break;
				case "DELETE":
					replicated_lobbies.remove((headerComponents[1]+headerComponents[2]));
					break;
				default:
					break;
			}
		}
		
	}
	
	public static void main(String[] args)
	{
		TetrisServer serv = new TetrisServer(args[0],Integer.parseInt(args[1]));
		serv.run();
	}

}
