package com.sdis.tetris.network;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TetrisLobby{
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
            scores.putIfAbsent(player_name, 0);
            if(!game_started)
            	playersReady.putIfAbsent(player_name, false);
            else playersReady.putIfAbsent(player_name,true);
            playersGameover.putIfAbsent(player_name, false);
            master.thread_pool.execute(newlistener);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return socket.getLocalPort();
	}
	
	public TetrisLobby set_scores_from_replication(String[] replicated_scores) {
		for(String sc:replicated_scores) {
			String[] scoreComponents = sc.split(":");
			scores.put(scoreComponents[0], Integer.parseInt(scoreComponents[1].trim()));
		}
		return this;
	}
	
	public void start_and_replicate_game() {
		
		game_started=true;
		TetrisLobbyJSON json_representation = 
				new TetrisLobbyJSON()
				.setLobbyName(lobby_name)
				.setScores(scores);
		this.master.replication_service.triggerReplication(json_representation);
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
	    	if(playersReady.size()<2)
	    		return;
	    	boolean alltrue=true;
	    	for(String key: playersReady.keySet()) {
	    		if(!playersReady.get(key))
	    			alltrue=false;
	    	}
	    	
	    	if(alltrue) {
	    		start_and_replicate_game();
	    		executor.execute(new Runnable() {
	    			public void run() {
	    				for(String key: playerConnections.keySet()) {
	    		    		try {
								playerConnections.get(key).signalGameStarted();
							} catch (IOException e) {
								e.printStackTrace();
							}
	    		    	}
	    			}
	    		});
	    	}
	    }
	    
	    public void signalGameStarted() throws IOException {
	    	out.write(("BEGIN "+playersReady.size()+" "+TetrisServer.CRLF).getBytes());
	    }
	    
	    public void handlePacket(String packet) throws IOException {
	    	String[] packetComponents = packet.split(" ");
	    	switch(packetComponents[0]) {
		    	case "READY":
		    		playersReady.put(packetComponents[1], true);
		    		checkAllTrue();
		    		break;
		    	case "SWAP":
		    		executor.execute(new Runnable() {
		    			public void run() {
		    		    	try {
								playerConnections.get(packetComponents[4].trim()).out.write(packet.getBytes());
							} catch (IOException e) {
								e.printStackTrace();
							}
		    			}
		    		});
		    		break;
		    	case "SWAPRESPONSE":
		    		executor.execute(new Runnable() {
		    			public void run() {
		    		    	try {
								playerConnections.get(packetComponents[1].trim()).out.write(packet.getBytes());
							} catch (IOException e) {
								e.printStackTrace();
							}
		    			}
		    		});
		    		break;
		    	case "GAMESTATE":
		    		if(scores.get(packetComponents[1]) == null){
		    			System.out.println("Warning - Could not get current score for player " + packetComponents[1]);
					}
					scores.put(packetComponents[1],Integer.parseInt(packetComponents[4]));

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
		    		System.out.println("Received gameover message");
		    		playersGameover.put(packetComponents[1].split("\r\n")[0], true);
		    		boolean finished = true;
		    		for(Boolean player:playersGameover.values()){
		    			if(!player){
		    				finished = false;
		    				break;
						}
					}
					if(finished){
						String msg = "GAMEENDED";
						for(String key : scores.keySet()) {
							msg += " " + key + " " + scores.get(key);
						}
						for(ClientListener cl: playerConnections.values()){
							cl.out.write(msg.getBytes());
						}
						end_game();
					}
		    		break;
		    	default:
		    		break;
	    	}
	    }
	    
	    void closeSockets(Socket sock) {
	    	try {
				out.close();
				in.close();
	        	sock.close();
	        	sslsocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    void handlePlayerDisconnection() {
	    	playerConnections.remove(username);
	    	if(!game_started) {
        		scores.remove(username);
        		playersReady.remove(username);
        		playersGameover.remove(username);
        		if(scores.isEmpty())
        			master.deleteEmptyLobby(lobby_name);
	    	}
	    	else {
	    		if(playerConnections.isEmpty())
	    			end_game();
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
				out = socket.getOutputStream();
                in = socket.getInputStream();
            }catch (IOException e){
	            e.printStackTrace();
	            return;
            }

	        while(true) {
                try {
                    byte[] buf = new byte[10000];

                    int read = in.read(buf);

                    if(read < 0){
                    	System.out.println("Client left the lobby : "+username);
                    	handlePlayerDisconnection();
                    	closeSockets(socket);
                    	break;
					}

					
                    String str = new String(buf,0,read);
                    //System.out.println("Received packet in lobby " + lobby_name + ": " + str);
                    handlePacket(str);
                }
                catch(Exception e) {
                	System.out.println("Client has been disconnected: "+username);
                	handlePlayerDisconnection();
                	closeSockets(socket);
                	break;
                }
            }
        }
    }


}
