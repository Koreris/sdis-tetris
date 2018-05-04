package com.sdis.tetris.network;

import com.sun.security.ntlm.Client;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class TetrisLobby implements Runnable{
	String lobby_name;
	ConcurrentHashMap<InetAddress,Integer> player_addresses; /* Save ip/port combos of players here */
	ConcurrentHashMap<String,Integer> scores; /* Save player username and score here */
	transient SSLServerSocket[] sockets; /* A transient field will not be serialized */
	transient TetrisServer master;
	int current_socket;
	Boolean connected_clients[];
	
	public TetrisLobby(TetrisServer server,String name) {
		master = server;
		lobby_name=name;
		player_addresses= new ConcurrentHashMap<InetAddress,Integer>();
		scores = new ConcurrentHashMap<String,Integer>();
		current_socket = 0;

        SSLServerSocketFactory ssl_socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        sockets = new SSLServerSocket[4];
        connected_clients = new Boolean[4];
        for(int i = 0; i < sockets.length; i++){
            try {
                sockets[i] = (SSLServerSocket) ssl_socket_factory.createServerSocket(master.current_port++);
            } catch (IOException e) {
                e.printStackTrace();
            }

            sockets[i].setNeedClientAuth(true);
        }
		//establish socket1 connection with lobby creator
	}
	
	public TetrisLobby setMaster(TetrisServer newmaster) {
		master=newmaster;
		return this;
	}
	
	public void join_lobby(String player_name, InetAddress player_address, int player_port) {
		player_addresses.put(player_address,player_port);
		scores.put(player_name, 0);
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
	    int i = 0;
        for (SSLServerSocket socket: sockets) {
            master.thread_pool.execute(new ClientListener(socket,connected_clients[i]));
            i++;
        }
	}

	public int getCurrentPort() {
        return sockets[current_socket].getLocalPort();
	}

	class ClientListener implements Runnable {

	    SSLServerSocket sslsocket;
	    Boolean connected;

	    public ClientListener(SSLServerSocket s, Boolean connected){
	        sslsocket = s;
	        this.connected = connected;
	        this.connected = false;
        }

        @Override
        public void run() {

	        Socket socket = null;
	        InputStream in = null;
	        OutputStream out = null;

	        try{
                socket = sslsocket.accept();
                connected = true;
                out = socket.getOutputStream();
                in = socket.getInputStream();
            }catch (IOException e){
	            e.printStackTrace();
	            return;
            }

	        while(true) {
                try {
                    byte[] buffer = new byte[256];

                    in.read(buffer);

                    String str = new String(buffer);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
