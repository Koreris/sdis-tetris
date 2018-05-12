package com.sdis.tetris.network;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class TetrisLobby implements Runnable{
	String lobby_name;
	ConcurrentHashMap<String,Integer> scores; /* Save player username and score here */
    ConcurrentHashMap<String,SSLServerSocket> playerSockets;
    SSLServerSocketFactory ssl_socket_factory;
	transient TetrisServer master;

	public TetrisLobby(TetrisServer server,String name) {
		master = server;
		lobby_name = name;
		scores = new ConcurrentHashMap<>();
		playerSockets = new ConcurrentHashMap<>();

        ssl_socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		//establish socket1 connection with lobby creator
	}
	
	public TetrisLobby setMaster(TetrisServer newmaster) {
		master=newmaster;
		return this;
	}
	
	public int join_lobby(String player_name) {
		scores.put(player_name, 0);
        SSLServerSocket socket;
        try {
            socket = (SSLServerSocket) ssl_socket_factory.createServerSocket(0);
            playerSockets.put(player_name,socket);
            master.thread_pool.execute(new ClientListener(player_name));
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return socket.getLocalPort();
	}

	public void start_game() {
		//prevent other joins from this point on?
		TetrisLobbySerializable static_representation = 
				new TetrisLobbySerializable()
				.setLobbyName(lobby_name)
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
			players += key + "\n";
		}
		return "Lobby Name: "+lobby_name+ "\nPlayers:\n"+players;
	}
	
	@Override
	public void run() {
        //TODO - Might not be needed, implement this method or remove Runnable implementation
	}

	class ClientListener implements Runnable {

	    String username;
	    SSLServerSocket sslsocket;

	    public ClientListener(String username){
	        this.username = username;
	        this.sslsocket = playerSockets.get(username);
        }

        @Override
        public void run() {

	        Socket socket = null;
	        InputStream in = null;
	        OutputStream out = null;

	        try{
                socket = sslsocket.accept();
				System.out.println("Starting socket in port " + socket.getLocalPort());
				out = socket.getOutputStream();
                in = socket.getInputStream();
            }catch (IOException e){
	            e.printStackTrace();
	            return;
            }

	        while(true) {
                try {
                    byte[] buf = new byte[256];

                    int read = in.read(buf);

                    if(read < 0){
                    	break; //Means error or end of connection
					}

					byte[] buffer = Arrays.copyOfRange(buf,0,read);

                    String str = new String(buffer);

                    System.out.println("Received packet in lobby " + lobby_name + ": " + str);
                    //TODO - send packet to other clients
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
