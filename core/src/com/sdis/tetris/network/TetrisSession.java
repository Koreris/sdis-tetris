package com.sdis.tetris.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;

public class TetrisSession implements Runnable{
	ConcurrentHashMap<String,Integer> players;
	DatagramSocket socket;
	
	public TetrisSession(ConcurrentHashMap<String,Integer> ps) {
		players=ps;
	}
	@Override
	public void run() {
		while(true) {
			byte[] buf = new byte[TetrisServer.MAX_PACKET_SIZE];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//handlePacket(packet)
		}
	}
	
}
