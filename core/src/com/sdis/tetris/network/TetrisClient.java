package com.sdis.tetris.network;

import javax.net.SocketFactory;
import javax.net.ssl.*;

import com.badlogic.gdx.graphics.Color;
import com.sdis.tetris.gui.GUIMultiGame;
import com.sdis.tetris.logic.Board;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void start_game(String player_name) throws IOException {
    	 byte[] msg = ("READY " + player_name + " " + CRLF + CRLF).getBytes();
    	 lsos.write(msg);
    }
    
    public void send_game_state(String player_name,String server_name, ArrayList<ColorJSON> colors) throws IOException {
	   	 String msg = "GAMESTATE " + player_name + " " + connectedLobbyName + " " + server_name + " " + CRLF + CRLF;
	   	 String contents = ColorJSON.toJSONfromArrayList(colors).toString();
	   	 lsos.write((msg+contents+CRLF+CRLF).getBytes());
    }

    public void send_game_over(String player_name) throws IOException {
    	String msg = "GAMEOVER " + player_name;
    	lsos.write((msg+CRLF+CRLF).getBytes());
	}
    
    
    public int listen_lobby_socket(GUIMultiGame game)  {
		 try {
			 byte[] buf = new byte[64000];
			 int read = lsis.read(buf);
			 byte[] buffer = Arrays.copyOfRange(buf,0,read);
			 String string = new String(buffer);
			
			 String[] parts = string.split(System.getProperty("line.separator"));
			 System.out.println("Received parts: "+parts.length);
			 for(String part:parts) {
				 System.out.println("Im a part: "+part.trim());
			 }
			 String [] header_tokenized = parts[0].split(" ");
			 if(header_tokenized[0].trim().equals("GAMESTATE")) {
				 ArrayList<ColorJSON> received=ColorJSON.fromJSONtoArrayList(parts[2].trim());
				 if(game.smallBoard1.playerName!=null && game.smallBoard1.playerName.equals(header_tokenized[1])) {
					 updateSmallBoard(game.smallBoard1,received);
				 }
				 else if(game.smallBoard2.playerName!=null && game.smallBoard2.playerName.equals(header_tokenized[1])) {
					 updateSmallBoard(game.smallBoard2,received);
				 }
				 else if(game.smallBoard3.playerName!=null && game.smallBoard3.playerName.equals(header_tokenized[1])) {
					 updateSmallBoard(game.smallBoard3,received);
				 }
				 else {
					 if(game.smallBoard1.playerName==null) {
						 game.smallBoard1.playerName=header_tokenized[1];
						 updateSmallBoard(game.smallBoard1,received);
					 }
					 else if(game.smallBoard2.playerName==null) {
						 game.smallBoard2.playerName=header_tokenized[1];
						 updateSmallBoard(game.smallBoard2,received);
					 }
					 else if(game.smallBoard3.playerName==null) {
						 game.smallBoard3.playerName=header_tokenized[1];
						 updateSmallBoard(game.smallBoard3,received);
					 }
				 }
				 return 1;
			 }
			 else if(header_tokenized[0].trim().equals("GAMEENDED")){
			 	System.out.println("Received game ended message");
				 //TODO -  !!!JOSÉ!!!  - Aqui deve passar para o ecra de mostrar as pontuacoes finais de todos os jogadores (ecra de gameover)
			 }
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 return -1;
		 }
		 return -1;
    }
   
    public void updateSmallBoard(Board smallboard,ArrayList<ColorJSON> received) {
    	smallboard.cloneBoard = new Color[smallboard.boardHeight][smallboard.boardWidth];
    	for(ColorJSON color: received) {
    		smallboard.cloneBoard[color.y][color.x]=new Color(color.r,color.g,color.b,color.a);
    	}
    }
    
    public static void printColor(Color[][] colors) {
    	for(int h=0;h<colors.length;h++) {
			for(int w=0;w<colors[0].length;w++) {
				if(colors[h][w]!=null)
					System.out.println("Color in position "+h+","+w+": "+colors[h][w].toString());
				else System.out.println("Color in position "+h+","+w+": null");
			}	
		}
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
