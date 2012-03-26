package inf.ed.ac.uk.comn.delayserver.server.thread;

import java.net.*;
import java.util.*;

public class IncomingThread extends Thread {
	
	private DatagramSocket socket;
	private Queue<byte[]> queue;
	private int maxPacketSize;
	
	public IncomingThread(int port, Queue<byte[]> queue, int maxPacketSize) throws SocketException
	{
		
		socket = new DatagramSocket(port);
		socket.setSoTimeout(10); // socket timeout
		
		this.queue = queue;
		this.maxPacketSize = maxPacketSize;
		
	}
	
	public void run()
	{
		
		while (!Thread.interrupted())
		{
			
			byte[] packet = new byte[maxPacketSize];
			DatagramPacket dPacket = new DatagramPacket(packet, packet.length);
			try
			{
				socket.receive(dPacket);
			}
			catch (Exception e)
			{
				continue;
			}
			
			if (dPacket.getLength() < packet.length)
			{
				packet = Arrays.copyOf(packet, dPacket.getLength());
			}
			
			queue.offer(packet);
			
		}
		
		socket.close();
		
	}
	
}
