package connection;

import java.net.*;
import java.util.*;

import packet.*;

/**
 * 
 * A thread which listens for packets and places them in a shared queue.
 * 
 * This thread will execute until it is interrupted.  When it is interrupted it
 * could take up to SOCKET_TIMEOUT to stop.
 *
 */
class IncomingThread extends Thread {
	
	/**
	 * The maximum number of bytes of an incoming packet.
	 */
	private static final int MAX_INCOMING_LENGTH = 1024;
	
	/**
	 * The maximum amount of time to wait for a packet.
	 */
	private static final int SOCKET_TIMEOUT = 100;
	
	private Queue<Packet> queue;
	private DatagramSocket socket;
	
	private SocketAddress lastSender;
	
	/**
	 * 
	 * Creates a thread which listens on the port specified.
	 * 
	 * @param port The port to listen on.
	 * @param queue The queue to place packets.
	 * @throws SocketException
	 */
	public IncomingThread(int port, Queue<Packet> queue) throws SocketException
	{
		
		socket = new DatagramSocket(port);
		
		socket.setSoTimeout(SOCKET_TIMEOUT);
		
		this.queue = queue;
		
	}
	
	public void run()
	{
		
		while (!Thread.interrupted())
		{
			
			byte[] data = new byte[MAX_INCOMING_LENGTH];
			DatagramPacket dp = new DatagramPacket(data, data.length);
			
			try {
				socket.receive(dp);
			} 
			catch (Exception e) 
			{
				continue;
			}
			
			synchronized (this)
			{
				lastSender = dp.getSocketAddress();
				
				// trim data
				if (dp.getLength() < data.length)
				{
					
					data = Arrays.copyOf(data, dp.getLength());
					
				}
				
				System.out.println(" <  ");
				
				Packet p = new Packet();
				p.insert(data, 0);
				
				queue.offer(p);
			}
			
		}
		
		socket.close();
		
	}
	
	/**
	 * 
	 * Get details of the sender of the last packet received
	 * 
	 * @return The address of the last packet received.
	 * @see java.net.SocketAddress
	 */
	public InetSocketAddress getLastSender()
	{
		synchronized (this)
		{
			return (InetSocketAddress)lastSender;
		}
	}
	
}
