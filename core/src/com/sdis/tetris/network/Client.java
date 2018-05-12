package com.sdis.tetris.network;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
    protected static String CRLF = "\r\n";
    public static String Client_name= "jogador 1";
    static SSLSocket socket;
    static ArrayList<String> running_lobbies;
    static SocketFactory sslsocket;
    public static InetAddress player_address;

    static {
        try {
            player_address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static int player_port = 4500;

    public static void join_server(String server_name, InetAddress server_adress, int server_port) throws IOException {

        byte[] msg = ("ASKLIST " + server_name + " " + player_address + " " + player_port + " " + CRLF + CRLF).getBytes();

        OutputStream out = null;
        InputStream in = null;
        sslsocket = SSLSocketFactory.getDefault();
        socket = (SSLSocket) sslsocket.createSocket(server_adress, server_port);
        out = socket.getOutputStream();
        in = socket.getInputStream();
        try {
            out.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] read = null;
        String srt;
        running_lobbies = new ArrayList<>();
        in.read(read);
        srt = String.valueOf(read);
        String[] temp = srt.split(" ");
        String  dd= null;

        for(int i = 0;i<temp.length;i++){
            dd = temp[i];
            if(dd  == String.valueOf(CRLF)){
                System.out.println("msg recived");
            }
            else{
                running_lobbies.add(srt);
            }
        }
        out.close();
        in.close();
        socket.close();
    }    
}
