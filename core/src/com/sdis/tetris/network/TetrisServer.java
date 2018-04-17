package com.sdis.tetris.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TetrisServer implements Runnable{
	DatagramSocket main_socket;
	ThreadPoolExecutor thread_pool;
	final static int MAX_PACKET_SIZE=64096;
	
	public TetrisServer(int port) {
		LinkedBlockingQueue<Runnable> queue= new LinkedBlockingQueue<Runnable>();
		thread_pool = new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS, queue);
		try {
			main_socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true) {
			byte[] buf = new byte[MAX_PACKET_SIZE];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				main_socket.receive(packet);
				//packet for connection should contain name of the player and ip and port
				//parsePacket(packet);
				//if packet is "create new session" or whatever
				//thread_pool.execute(new TetrisSession(
				//ConcurrentHashMap<String(this string should contain ip and username),Integer>)
				//);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args)
	{
		TetrisServer serv = new TetrisServer(Integer.parseInt(args[0]));
		serv.run();
	}


}
