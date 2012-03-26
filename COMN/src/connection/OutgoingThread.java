package connection;

import packet.*;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * 
 * Sends packets from a queue.
 * 
 * If interrupted this thread stop execution and closes the socket.
 * 
 */
class OutgoingThread extends Thread {
	
	private DatagramSocket socket;
	private InetAddress host;
	private int port;
	
	private Queue<Packet> queue;
	
	/**
	 * 
	 * Creates a new thread which sends packets in the queue to the port at the host.
	 * 
	 * @param host The host to send to.
	 * @param port The port to send to.
	 * @param queue The queue to take items from.
	 * @throws SocketException If unable to create the socket.
	 */
	public OutgoingThread(InetAddress host, int port, Queue<Packet> queue) throws SocketException
	{
		
		socket = new DatagramSocket();
		
		this.host = host;
		this.port = port;
		this.queue = queue;
		
	}
	
	/**
	 * This runs the thread which sends data.
	 */
	public void run()
	{
		
		while (!Thread.interrupted())
		{
			
			Packet p = queue.poll();
			
			if (p == null)
				continue;
			
			System.out.println("  > ");
			
			DatagramPacket dp = new DatagramPacket(p.getData(), p.getData().length, host, port);
			
			try {
				socket.send(dp);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				continue;
			}
			
		}
		
		socket.close();
		
	}
	
}
