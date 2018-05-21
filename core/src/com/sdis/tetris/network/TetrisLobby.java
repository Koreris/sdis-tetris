package com.sdis.tetris.network;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TetrisLobby implements Runnable{
	String lobby_name;
	ConcurrentHashMap<String,Integer> scores; /* Save player username and score here */
	ConcurrentHashMap<String,Boolean> playersReady;
    ConcurrentHashMap<String,ClientListener> playerConnections;
    ConcurrentHashMap<String,Boolean> playersGameover;
    SSLServerSocketFactory ssl_socket_factory;
	TetrisServer master;
	ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 50, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	boolean game_started=false;

	public TetrisLobby(TetrisServer server,String name) {
		master = server;
		lobby_name = name;
		scores = new ConcurrentHashMap<>();
		playerConnections = new ConcurrentHashMap<>();
		playersReady = new ConcurrentHashMap<>();
		playersGameover = new ConcurrentHashMap<>();
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
            ClientListener newlistener =new ClientListener(player_name,socket);
            playerConnections.put(player_name,newlistener);
            playersReady.put(player_name, false);
            playersGameover.put(player_name, false);
            master.thread_pool.execute(newlistener);
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
	    InputStream in;
        OutputStream out;

	    public ClientListener(String username,SSLServerSocket ssl){
	        this.username = username;
	        this.sslsocket = ssl;
        }
	    
	    public void checkAllTrue() throws IOException {
	    	boolean alltrue=true;
	    	for(String key: playersReady.keySet()) {
	    		if(!playersReady.get(key))
	    			alltrue=false;
	    	}
	    	
	    	if(alltrue) {
	    		game_started=true;
	    		executor.execute(new Runnable() {
	    			public void run() {
	    				for(String key: playerConnections.keySet()) {
	    		    		try {
								playerConnections.get(key).startGame();
							} catch (IOException e) {
								e.printStackTrace();
							}
	    		    	}
	    			}
	    		});
	    	}
	    }
	    
	    public void startGame() throws IOException {
	    	out.write(("BEGIN "+playersReady.size()+" "+master.CRLF).getBytes());
	    }
	    
	    public void handlePacket(String packet) throws IOException {
	    	String[] packetComponents = packet.split(" ");
	    	switch(packetComponents[0]) {
		    	case "READY":
		    		playersReady.put(packetComponents[1], true);
		    		checkAllTrue();
		    		break;
		    	case "GAMESTATE":
		    		executor.execute(new Runnable() {
		    			public void run() {
		    				for(String key: playerConnections.keySet()) {
		    		    		try {
		    		    			if(!key.equals(username))
		    		    				playerConnections.get(key).forwardState(packet);
								} catch (IOException e) {
									e.printStackTrace();
								}
		    		    	}
		    			}
		    		});
		    		break;
		    	case "GAMEOVER":
		    		playersReady.put(packetComponents[1], true);
		    		boolean finished = true;
		    		for(Boolean player:playersGameover.values()){
		    			if(!player){
		    				finished = false;
		    				break;
						}
					}
					if(finished){
		    			for(ClientListener cl: playerConnections.values()){
		    				cl.out.write("GAMEENDED".getBytes());
						}
					}
		    		break;
		    	default:
		    		break;
	    	}
	    }

		protected void forwardState(String packet) throws IOException {
			out.write(packet.getBytes());
		}

		@Override
        public void run() {

	        Socket socket = null;
	       
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
                    byte[] buf = new byte[1024];

                    int read = in.read(buf);

                    if(read < 0){
                    	System.out.println("Client left the lobby : "+username);
                    	if(!game_started) {
                    		scores.remove(username);
                    		playerConnections.remove(username);
                    		playersReady.remove(username);
                    		if(scores.isEmpty())
                    			master.deleteEmptyLobby(lobby_name);
                    	}
                    	break;
					}

					byte[] buffer = Arrays.copyOfRange(buf,0,read);
					
                    String str = new String(buffer);
                    //System.out.println("Received packet in lobby " + lobby_name + ": " + str);
                    handlePacket(str);
                    
                    //TODO - send packet to other clients
                }
                catch(SocketException e) {
                	System.out.println("Client has been disconnected: "+username);
                	if(!game_started) {
                		scores.remove(username);
                		playerConnections.remove(username);
                		playersReady.remove(username);
                		if(scores.isEmpty())
                			master.deleteEmptyLobby(lobby_name);
                	}
                	break;
                } catch (IOException e) {
                	System.out.println("Client has been disconnected: "+username);
                	if(!game_started) {
                		scores.remove(username);
                		playerConnections.remove(username);
                		playersReady.remove(username);
                		if(scores.isEmpty())
                			master.deleteEmptyLobby(lobby_name);
                	}
					e.printStackTrace();
				}
            }
        }
    }
}
