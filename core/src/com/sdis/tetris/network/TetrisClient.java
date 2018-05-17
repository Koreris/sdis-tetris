package com.sdis.tetris.network;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class TetrisClient {
    protected static String CRLF = "\r\n";
    SSLSocket lobbySocket;
    OutputStream lsos;
    InputStream lsis;
    public String connectedLobbyName;
    SocketFactory sslsocketFactory;
    public ArrayList<String> list_lobbies = new ArrayList<>();
    public ArrayList<String> players = new ArrayList<>();
    InetAddress server_address;
    int server_port;

    public TetrisClient() {
    	 sslsocketFactory = SSLSocketFactory.getDefault();
    }

    public void list_lobbies(String server_name, InetAddress server_address, int server_port) throws IOException {
    	disconnectLobby();
    	list_lobbies = new ArrayList<>();
    	byte[] msg = ("ASKLIST " + server_name + CRLF + CRLF).getBytes();

        this.server_address = server_address;
        this.server_port = server_port;
        OutputStream out = null;
        InputStream in = null;
       
        SSLSocket socket = (SSLSocket) sslsocketFactory.createSocket(server_address, server_port);
        out = socket.getOutputStream();
        in = socket.getInputStream();
        try {
            out.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] read = new byte[1024];
        String readValue;
        in.read(read);
        readValue = new String(read);
        String[] serverResponseComponents = readValue.split(";");
	    if(serverResponseComponents.length>1) {
	        String responseComponent;
	        for(int i = 0;i<serverResponseComponents.length;i++){
	            responseComponent = serverResponseComponents[i];
	            if(responseComponent.equals(CRLF))
	                break;
	            list_lobbies.add(responseComponent);
	        }
        }
        out.close();
        in.close();
        socket.close();
    }
    
    public void list_players() throws IOException {
    	players = new ArrayList<>();
        byte[] msg = ("LISTPLAYERS " + connectedLobbyName + CRLF + CRLF).getBytes();

        OutputStream out = null;
        InputStream in = null;
        SSLSocket socket = (SSLSocket) sslsocketFactory.createSocket(server_address, server_port);
        out = socket.getOutputStream();
        in = socket.getInputStream();
        try {
            out.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] read = new byte[256];
        String readValue;
      
        in.read(read);
        readValue = new String(read);
        String[] serverResponseComponents = readValue.split(";");
        if(serverResponseComponents.length>1) {
	        String responseComponent;
	    
	        for(int i = 0;i<serverResponseComponents.length;i++){
	        	responseComponent = serverResponseComponents[i];
	            if(responseComponent.equals(CRLF))
	                break;
	            players.add(responseComponent);
	        }
        }
        out.close();
        in.close();
        socket.close();
    }
    
    public void join_lobby(String lobby_name, String player_name) throws IOException {
    	disconnectLobby();
        byte[] msg = ("CONNECT " + lobby_name + " " + player_name + " " + CRLF + CRLF).getBytes();

        OutputStream out = null;
        InputStream in = null;
      
        SSLSocket socket = (SSLSocket) sslsocketFactory.createSocket(server_address, server_port);
        out = socket.getOutputStream();
        in = socket.getInputStream();
        try {
            out.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        byte[] buf = new byte[256];

        int read = in.read(buf);
        String string = new String(buf);
        System.out.println("RECEIVED FROM SERVER RESPONSE FOR JOIN: " +string);
        
        String [] msg_tokenized = string.split(" ");
        out.close();
        in.close();
        socket.close();
        
        lobbySocket = (SSLSocket) sslsocketFactory.createSocket(server_address,Integer.parseInt(msg_tokenized[1].trim()));
        lsos = lobbySocket.getOutputStream();
        lsis = lobbySocket.getInputStream();
        connectedLobbyName=lobby_name;
        lsos.write("TEST MESSAGE".getBytes());
    }


    public void create_lobby(String lobby_name, String player_name) throws IOException {
    	disconnectLobby();
        byte[] msg = ("CREATE " + lobby_name + " " + player_name + " " + CRLF + CRLF).getBytes();
        OutputStream out = null;
        InputStream in = null;
      
        SSLSocket socket = (SSLSocket) sslsocketFactory.createSocket(server_address, server_port);
        out = socket.getOutputStream();
        in = socket.getInputStream();
        try {
            out.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        byte[] buf = new byte[256];

        int read = in.read(buf);
        String string = new String(buf);
        System.out.println("RECEIVED FROM SERVER RESPONSE FOR CREATE: " +string);
        
        String [] msg_tokenized = string.split(" ");
        out.close();
        in.close();
        socket.close();
        
        lobbySocket = (SSLSocket) sslsocketFactory.createSocket(server_address,Integer.parseInt(msg_tokenized[1].trim()));
        lsos = lobbySocket.getOutputStream();
        lsis = lobbySocket.getInputStream();
        connectedLobbyName=lobby_name;
        lsos.write("TEST MESSAGE".getBytes());
    }
    
    public void start_game(String player_name) throws IOException {
    	 byte[] msg = ("READY " + player_name + " " + CRLF + CRLF).getBytes();
    	 lsos.write(msg);
    }
    
    public int listen_game_begin() {
    	 byte[] buf = new byte[1024];

         try {
			 int read = lsis.read(buf);
			 String string = new String(buf);
			 String [] msg_tokenized = string.split(" ");
			 if(msg_tokenized[0].trim().equals("BEGIN"))
				 return Integer.parseInt(msg_tokenized[1].trim());
			 return -1;
	        
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
    }
    
    public void disconnectLobby() throws IOException {
    	if(lobbySocket!=null) {
    		connectedLobbyName="";
    		lsos.close();
	    	lsis.close();
	    	lobbySocket.close();
    	}
    }
}
