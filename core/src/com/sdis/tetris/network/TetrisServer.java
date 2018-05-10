package com.sdis.tetris.network;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TetrisServer implements Runnable{
    String server_name;
    DatagramSocket server_socket;
    SSLServerSocket client_socket;
    SSLServerSocketFactory ssl_socket_factory;
    ThreadPoolExecutor thread_pool;
    ConcurrentHashMap<String,TetrisLobby> running_lobbies;
    ConcurrentHashMap<String,TetrisLobbySerializable> replicated_lobbies;
    ConcurrentHashMap<String,Integer> local_leaderboards;
    ConcurrentHashMap<String,Integer> other_servers;
    ServerReplicationService replication_service;
    public int current_port;
    protected static String CRLF = "\r\n";
    final static int MAX_PACKET_SIZE=64096;

    public TetrisServer(String name,int server_port, int client_port) {
        current_port = client_port++;
        server_name=name;
        running_lobbies = new ConcurrentHashMap<String,TetrisLobby>();
        replicated_lobbies = new ConcurrentHashMap<String,TetrisLobbySerializable>();
        local_leaderboards = new ConcurrentHashMap<String,Integer>();
        other_servers = new ConcurrentHashMap<String,Integer>();

        LinkedBlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>();
        thread_pool = new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS, queue);
        thread_pool.execute(new ParseServersFile(other_servers));

        ssl_socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            client_socket = (SSLServerSocket) ssl_socket_factory.createServerSocket(client_port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client_socket.setNeedClientAuth(true);

        try {
            server_socket = new DatagramSocket(server_port);
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

        Socket socket = null;
     
        try {
            socket = client_socket.accept();
            thread_pool.execute(new ClientConnectionHandler(socket));
        }catch(IOException e){
            e.printStackTrace();
            return;
        }
   
    }

    /* function just to test replication */
    public void simulateLobbyCreation() {
        try {
            //when actual thing is implemented, cannot allow creation of lobbies with already existing names
            TetrisLobby newlob = new TetrisLobby(this,"my_lobby");
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
                byte []buffer = new byte[512];
                in.read(buffer);
               
                String str = new String(buffer);
                String msg_tokens[] = str.split(" ");

                if(msg_tokens[0].compareTo("CREATE") == 0){
                    String lobby_name = msg_tokens[1];
                    TetrisLobby new_lob = new TetrisLobby(TetrisServer.this,lobby_name);

                    if(running_lobbies.get(lobby_name) != null){
                        //TODO - Error, room already exists
                    	return;
                    }

                    running_lobbies.put(lobby_name,new_lob);
                    String answer = "CREATED " + new_lob.getCurrentPort();
                    out.write(answer.getBytes());
                }
                else if(msg_tokens[0].compareTo("ASKLIST") == 0){

                }
                else if(msg_tokens[0].compareTo("CONNECT") == 0){

                }
                terminateConnection();	
            } 
            catch(SocketTimeoutException e) {
            	 terminateConnection();
            }
            catch (Exception e) {
                e.printStackTrace();
            } 
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
        TetrisServer serv = new TetrisServer(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        serv.run();
    }

}
