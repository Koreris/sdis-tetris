package com.sdis.tetris.network;

import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TetrisServer implements Runnable{
    String server_name;
    SSLServerSocket server_socket;
    SSLServerSocket client_socket;
    SSLServerSocketFactory ssl_socket_factory;
    SocketFactory socket_factory;
    ThreadPoolExecutor thread_pool;
    ConcurrentHashMap<String,TetrisLobby> running_lobbies;
    ConcurrentHashMap<String,TetrisLobbyJSON> replicated_lobbies;
    ConcurrentHashMap<String,Integer> local_leaderboards;
    ConcurrentHashMap<String,String> other_servers;
    ServerReplicationService replication_service;
    public int current_port;
    protected static String CRLF = "\r\n";
    final static int MAX_PACKET_SIZE=64096;

    public TetrisServer(String name,int server_port, int client_port) {
    	System.setProperty("javax.net.ssl.keyStore", "server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStore", "truststore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
       // System.setProperty( "user.dir", "/path/to/dir");
        server_name=name;
        running_lobbies = new ConcurrentHashMap<String,TetrisLobby>();
        replicated_lobbies = new ConcurrentHashMap<String,TetrisLobbyJSON>();
        local_leaderboards = new ConcurrentHashMap<String,Integer>();
        other_servers = new ConcurrentHashMap<String,String>();

        LinkedBlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>();
        thread_pool = new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS, queue);
        thread_pool.execute(new ParseServersFile(other_servers));

        ssl_socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        socket_factory = SSLSocketFactory.getDefault();

        try {
            client_socket = (SSLServerSocket) ssl_socket_factory.createServerSocket(client_port);
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        client_socket.setNeedClientAuth(true);

        try {
            server_socket = (SSLServerSocket) ssl_socket_factory.createServerSocket(server_port);
            replication_service = new ServerReplicationService();
            thread_pool.execute(replication_service);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        server_socket.setNeedClientAuth(true);
    }

    @Override
    public void run() {
        while(true) {
        	Socket socket = null;
	        try {
	        	//System.out.println("Listening to client at port "+client_socket.getLocalPort()+" ip "+client_socket.getLocalSocketAddress());
	            socket = client_socket.accept();
	            thread_pool.execute(new ClientConnectionHandler(socket));
	        }catch(IOException e){
	            e.printStackTrace();
	            return;
	        }
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
    
    public void deleteEmptyLobby(String lobby_name) {
    	running_lobbies.remove(lobby_name);
    }
    
    
    class ClientConnectionHandler implements Runnable{
        Socket socket;
        InputStream in = null;
        OutputStream out = null;

        public ClientConnectionHandler(Socket connection) {
            socket=connection;
            try {
                socket.setSoTimeout(2000);
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void terminateConnection() {
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                byte []buffer = new byte[1024];
                in.read(buffer);

                String str = new String(buffer);
                String msg_tokens[] = str.split(" ");

                if(msg_tokens[0].compareTo("CREATE") == 0){
                    String lobby_name = msg_tokens[1];
                    TetrisLobby new_lob = new TetrisLobby(TetrisServer.this,lobby_name);

                    if(running_lobbies.get(lobby_name) != null){
                        System.out.println("lobby already exists");
                        terminateConnection();
                        return;
                    }

                    running_lobbies.put(lobby_name,new_lob);
                    String answer = "CREATED " + new_lob.join_lobby(msg_tokens[2].trim())+CRLF;
                    System.out.println("SERVER RESPONSE TO CREATE LOBBY  :"+answer);
                    out.write(answer.getBytes());
                }
                else if(msg_tokens[0].compareTo("ASKLIST") == 0){
                    String msg = "";
                  
                    for(Map.Entry<String,TetrisLobby> me: running_lobbies.entrySet()){
                    	if(!running_lobbies.get(me.getKey()).game_started)
                    		msg =  msg + me.getKey()+"    "+ running_lobbies.get(me.getKey()).scores.size() + " out of " + "4"+ ";";
                    }
                    
                    msg = msg + CRLF;
                    out.write(msg.getBytes());
                }
                else if(msg_tokens[0].compareTo("LISTPLAYERS") == 0){
            	   String msg = "";
                   String lobby_name = msg_tokens[1].trim();
              
                   if(running_lobbies.containsKey(lobby_name)){
                       for(String key: running_lobbies.get(lobby_name).scores.keySet()){
                    	   if(running_lobbies.get(lobby_name).playersReady.get(key))
                    		   msg= msg + key + " "+ " READY" + ";";
                    	   else  msg= msg + key + " "+ " WAITING" + ";";
                       }
                   }
                    
                   msg = msg + CRLF;
                   out.write(msg.getBytes());
                }
                else if(msg_tokens[0].compareTo("CONNECT") == 0){
                    String lobby_name = msg_tokens[1].trim();
                    String answer="";
                    if(running_lobbies.containsKey(lobby_name)){
                    	if(running_lobbies.get(lobby_name).scores.containsKey(msg_tokens[2].trim())) {
                    		answer = "RECONNECTED " + running_lobbies.get(lobby_name).join_lobby(msg_tokens[2].trim())+CRLF;
	                    	out.write(answer.getBytes());  
                    	}
                    	else if(running_lobbies.get(lobby_name).scores.size()<4 && !running_lobbies.get(lobby_name).game_started && !running_lobbies.get(lobby_name).scores.containsKey(msg_tokens[2].trim())) {
	                    	answer = "JOINED " + running_lobbies.get(lobby_name).join_lobby(msg_tokens[2].trim())+CRLF;
	                    	out.write(answer.getBytes());  
                    	}
                    }
                    else if(replicated_lobbies.containsKey(lobby_name)){
                    	running_lobbies.putIfAbsent(lobby_name, new TetrisLobby(TetrisServer.this,lobby_name).set_scores_from_replication(replicated_lobbies.get(lobby_name).scores));
                    	answer = "CHANGEDSERVER " + running_lobbies.get(lobby_name).join_lobby(msg_tokens[2].trim())+CRLF;
                    	out.write(answer.getBytes()); 
                    }   
                    System.out.println("RESPONSE TO JOIN: "+answer);
                }
                else if(msg_tokens[0].compareTo("TESTCONNECTION") == 0){
                	System.out.println("RECEIVED TEST CONNECTION");
                	String answer = "ACKNOWLEDGED "+CRLF;
                	out.write(answer.getBytes());  
                }
                terminateConnection();
            }
            catch (Exception e) {
            	e.printStackTrace();
            	terminateConnection();
            }
        }
    }

    class ServerReplicationService implements Runnable{
        @Override
        public void run() {
            while(true) {
    	        try {
    	            final Socket socket = server_socket.accept();
    	            thread_pool.execute(new Runnable() {
    	            	public void run() {
    	            		handleReplicationPacket(socket);
    	            	}
    	            });
    	        }catch(IOException e){
    	            e.printStackTrace();
    	        }
            }

        }

        public void triggerReplication(TetrisLobbyJSON lobby) {
        		running_lobbies.get(lobby.lobby_name);
                String header = "REPLICATE" +  " " + server_name + " " + lobby.lobby_name + CRLF;
                byte[] replicate = Utils.combineByteArrays(header.getBytes(),lobby.toJSON().toString().getBytes());
                for (String key : other_servers.keySet())
                {
                	if(key.equals(server_name))
                		continue;
                	String[] servDetails = other_servers.get(key).split(" ");
                    System.out.println("Sending lobby to "+servDetails[0]+":"+servDetails[1]);
                    try {
	                    SSLSocket sendSocket = (SSLSocket) socket_factory.createSocket();
	                    sendSocket.connect(new InetSocketAddress(InetAddress.getByName(servDetails[0]),Integer.parseInt(servDetails[1])), 1500);
	                    OutputStream out = sendSocket.getOutputStream();
	                    out.write(replicate);
	                    out.close();
	                    sendSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
				
        }

        public void deleteLobby(String lobby_name) {
            running_lobbies.remove(lobby_name);
            String header = "DELETE" +  " " + server_name + " " + lobby_name + CRLF;
            for (String key : other_servers.keySet())
            {
                try {
                	String[] servDetails = other_servers.get(key).split(" ");
                    System.out.println("Sending delete to "+key+":"+other_servers.get(key));
                    SSLSocket sendSocket = (SSLSocket) socket_factory.createSocket();
                    sendSocket.connect(new InetSocketAddress(InetAddress.getByName(servDetails[0]),Integer.parseInt(servDetails[1])), 1500);
                    OutputStream out = sendSocket.getOutputStream();
                    out.write(header.getBytes());
                    out.close();
                    sendSocket.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
       
        public void handleReplicationPacket(Socket sock){
			try {
				InputStream sockin = sock.getInputStream();
	        	byte[] buffer = new byte[2048];
				int read = sockin.read(buffer);
	            String packetString = new String(buffer,0,read);
	
	            String[] lines = packetString.split(System.getProperty("line.separator"));
	            String header = lines[0];
	
	            String[] headerComponents = header.split(" ");
	
	            switch(headerComponents[0]) {
	                case "REPLICATE":
                        TetrisLobbyJSON lobby = TetrisLobbyJSON.fromJSON(lines[1].trim());
                        replicated_lobbies.remove(headerComponents[1]+headerComponents[2]);
                        replicated_lobbies.put(headerComponents[1]+headerComponents[2], lobby);
                        printLobbies();
	                    break;
	                case "DELETE":
	                    replicated_lobbies.remove(headerComponents[1]+headerComponents[2]);
	                    printLobbies();
	                    break;
	                default:
	                    break;
	            }
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }

    }

    public static void main(String[] args)
    {
        TetrisServer serv = new TetrisServer(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        serv.run();
    }

}