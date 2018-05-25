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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TetrisClient {
    protected static String CRLF = "\r\n";
    SSLSocket lobbySocket;
    OutputStream lsos;
    InputStream lsis;
    public String connectedLobbyName;
    SocketFactory sslsocketFactory;
    public String backupServer;
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
    	byte[] msg = ("ASKLIST " + server_name + CRLF).getBytes();
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
        byte[] msg = ("LISTPLAYERS " + connectedLobbyName + CRLF).getBytes();

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
        byte[] msg = ("CONNECT " + lobby_name + " " + player_name + " " + CRLF).getBytes();

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
        
        byte[] buf = new byte[1024];

        int read = in.read(buf);
        String string = new String(buf,0,read);        
        String [] msg_tokenized = string.split(" ");
        out.close();
        in.close();
        socket.close();
        
        lobbySocket = (SSLSocket) sslsocketFactory.createSocket(server_address,Integer.parseInt(msg_tokenized[1].trim()));
        lsos = lobbySocket.getOutputStream();
        lsis = lobbySocket.getInputStream();
        connectedLobbyName=lobby_name;
    }


    public int create_lobby(String lobby_name, String player_name) throws IOException {
    	disconnectLobby();
        byte[] msg = ("CREATE " + lobby_name + " " + player_name + " " + CRLF).getBytes();
        OutputStream out = null;
        InputStream in = null;
      
        SSLSocket socket = (SSLSocket) sslsocketFactory.createSocket(server_address, server_port);
        out = socket.getOutputStream();
        in = socket.getInputStream();
        try {
            out.write(msg);
	        byte[] buf = new byte[256];
	
	        int read = in.read(buf);
	        String string = new String(buf,0,read);	        
	        String [] msg_tokenized = string.split(" ");
	        out.close();
	        in.close();
	        socket.close();
	        
	        lobbySocket = (SSLSocket) sslsocketFactory.createSocket(server_address,Integer.parseInt(msg_tokenized[1].trim()));
	        lsos = lobbySocket.getOutputStream();
	        lsis = lobbySocket.getInputStream();
	        connectedLobbyName=lobby_name;
	        return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public void start_game(String player_name) throws IOException {
    	 byte[] msg = ("READY " + player_name + " " + CRLF).getBytes();
    	 lsos.write(msg);
    }
    
    public void send_game_state(String player_name,String server_name, ArrayList<ColorJSON> colors, int player_score) throws IOException {
	   	 String msg = "GAMESTATE " + player_name + " " + connectedLobbyName + " " + server_name + " " + player_score + " " + CRLF;
	   	 String contents = ColorJSON.toJSONfromArrayList(colors).toString();
	   	 lsos.write((msg+contents+CRLF).getBytes());
    }

    public void send_game_over(String player_name) throws IOException {
    	String msg = "GAMEOVER " + player_name;
    	lsos.write((msg+CRLF).getBytes());
	}

    public int listen_lobby_socket(GUIMultiGame game,ConcurrentHashMap<String,Integer> scores)  {
    	if(lobbySocket.isClosed())
    		return -1;
		 try {
			 byte[] buf = new byte[20000];
			 int read = lsis.read(buf);
			 byte[] buffer = Arrays.copyOfRange(buf,0,read);
			 String string = new String(buffer);
			 String[] parts = string.split(System.getProperty("line.separator"));

			 String [] header_tokenized = parts[0].split(" ");
			 if(header_tokenized[0].trim().equals("GAMESTATE")) 
			 {
				 ArrayList<ColorJSON> received=ColorJSON.fromJSONtoArrayList(parts[1].trim());
				 if(game.smallBoard1.playerName!=null && game.smallBoard1.playerName.equals(header_tokenized[1])) {
					 updateSmallBoard(game.smallBoard1,received,Integer.parseInt(header_tokenized[4]));
				 }
				 else if(game.smallBoard2.playerName!=null && game.smallBoard2.playerName.equals(header_tokenized[1])) {
					 updateSmallBoard(game.smallBoard2,received,Integer.parseInt(header_tokenized[4]));
				 }
				 else if(game.smallBoard3.playerName!=null && game.smallBoard3.playerName.equals(header_tokenized[1])) {
					 updateSmallBoard(game.smallBoard3,received,Integer.parseInt(header_tokenized[4]));
				 }
				 else {
					 if(game.smallBoard1.playerName==null) {
						 game.smallBoard1.playerName=header_tokenized[1];
						 updateSmallBoard(game.smallBoard1,received,Integer.parseInt(header_tokenized[4]));
					 }
					 else if(game.smallBoard2.playerName!=null && game.smallBoard2.playerName.equals(header_tokenized[1])) {
						 updateSmallBoard(game.smallBoard2,received,Integer.parseInt(header_tokenized[4]));
					 }
					 else if(game.smallBoard3.playerName!=null && game.smallBoard3.playerName.equals(header_tokenized[1])) {
						 updateSmallBoard(game.smallBoard3,received,Integer.parseInt(header_tokenized[4]));
					 }
					 else {
						 if(game.smallBoard1.playerName==null) {
							 game.smallBoard1.playerName=header_tokenized[1];
							 updateSmallBoard(game.smallBoard1,received,Integer.parseInt(header_tokenized[4]));
						 }
						 else if(game.smallBoard2.playerName==null) {
							 game.smallBoard2.playerName=header_tokenized[1];
							 updateSmallBoard(game.smallBoard2,received,Integer.parseInt(header_tokenized[4]));
						 }
						 else if(game.smallBoard3.playerName==null) {
							 game.smallBoard3.playerName=header_tokenized[1];
							 updateSmallBoard(game.smallBoard3,received,Integer.parseInt(header_tokenized[4]));
						 }
					 }
					 return 1;
				 }
				
			 }
			 else if(header_tokenized[0].trim().equals("GAMEENDED")){
				 String playername;
				 int points;
				 for(int i=1; i+1<header_tokenized.length;i++){
				 	playername = header_tokenized[i];
				 	points = Integer.parseInt(header_tokenized[i+1]);
				 	scores.put(playername, points);
					i++;
				 }
			 	return 0;
			 }
		 }
		catch (IOException e) {
			return -1;
		}
		 return -1;
    }
   
    public void updateSmallBoard(Board smallboard, ArrayList<ColorJSON> received, int score) {
    	smallboard.cloneBoard = new Color[smallboard.boardHeight][smallboard.boardWidth];
    	for(ColorJSON color: received) {
    		smallboard.cloneBoard[color.y][color.x]=new Color(color.r,color.g,color.b,color.a);
    	}
    	smallboard.setPlayerScore(score);
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
			 String string = new String(buf,0,read);
			 String [] msg_tokenized = string.split(" ");
			 if(msg_tokenized[0].trim().equals("BEGIN"))
				 return Integer.parseInt(msg_tokenized[1].trim());
			 disconnectLobby();
			 return -1;
		} catch (IOException e) {
			disconnectLobby();
			return -1;
		}
    }
    
    public void disconnectLobby(){
    	if(lobbySocket!=null) {
    		try {
				lsos.close();
		    	lsis.close();
		    	lobbySocket.close();
    		} 
	    	catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

	public boolean canReachAnyServer(ConcurrentHashMap<String,String> servers) {
		for(String key: servers.keySet()) {
			try {
				String[] serverInfo = servers.get(key).split(" ");
				InetAddress server_address = InetAddress.getByName(serverInfo[0]);
		        int server_port = Integer.parseInt(serverInfo[1]);
		        OutputStream out = null;
		        InputStream in = null;
		        SSLSocket socket = (SSLSocket) sslsocketFactory.createSocket(server_address, server_port);
		        out = socket.getOutputStream();
		        in = socket.getInputStream();
		        
		        out.write(("TESTCONNECTION "+CRLF).getBytes());
			
		        byte[] read = new byte[1024];

		        int readBytes = in.read(read);
		        if(readBytes>0) {
		        	out.close();
		  	        in.close();
		  	        socket.close();
		  	        this.backupServer=key;
		  	        this.server_address=server_address;
		  	        this.server_port=server_port;
		        	return true;
		        }
		        out.close();
		        in.close();
		        socket.close();
			} catch (IOException e) {
	            e.printStackTrace();   
	        }
		}
		return false;
	}

	public void reconnectLobbyOnBackupServer(String original_server,String player_name) {
		try {
			join_lobby(original_server+connectedLobbyName, player_name);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
