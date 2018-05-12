package com.sdis.tetris.network;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class TetrisClient {

    public static void main(String []args) throws Exception{
        System.setProperty("javax.net.ssl.keyStore", "client.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStore", "truststore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        SSLSocket serverSocket;
        SSLSocket lobbySocket;
        SSLSocketFactory sf;

        sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

        serverSocket = (SSLSocket) sf.createSocket(args[0],Integer.parseInt(args[1]));

        OutputStream ssos = serverSocket.getOutputStream();
        InputStream ssis = serverSocket.getInputStream();

        String str="CREATE my_lobby player1"+"\r\n";
        ssos.write(str.getBytes());

        byte[] buf = new byte[256];

        int read = ssis.read(buf);

        byte[] buffer = Arrays.copyOfRange(buf,0,read);

        String string = new String(buffer);
        System.out.println("RECEIVED FROM SERVER RESPONSE FOR CREATE: " +string);
        
        String [] msg_tokenized = string.split(" ");
        
        ssos.close();
        ssis.close();
        serverSocket.close();
        

        lobbySocket = (SSLSocket) sf.createSocket(args[0],Integer.parseInt(msg_tokenized[1]));

        OutputStream lsos = lobbySocket.getOutputStream();
        InputStream lsis = lobbySocket.getInputStream();

        lsos.write("TEST MESSAGE".getBytes());

      
    }
}
