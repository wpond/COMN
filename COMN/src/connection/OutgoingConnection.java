package connection;

import packet.*;

import java.util.*;
import java.util.concurrent.*;
import java.net.*;

/**
 * 
 * Controls the outgoing packets and the thread which send them.
 *
 */
public class OutgoingConnection {
	
	/**
	 * A queue of items to be sent.  This is a concurrent linked queue.
	 * @see java.util.concurrent.ConcurrentLinkedQueue
	 */
	private Queue<Packet> queue = new ConcurrentLinkedQueue<Packet>();
	
	private Thread outgoingThread;
	
	/**
	 * 
	 * Create an outgoing connection to port at host.
	 * 
	 * @param host The host computer.
	 * @param port The port of the computer.
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public OutgoingConnection(String host, int port) throws SocketException, UnknownHostException
	{
		
		outgoingThread = new OutgoingThread(InetAddress.getByName(host),port,queue);
		outgoingThread.start();
		
	}
	
	/**
	 * Closes the outgoing thread
	 */
	public void close()
	{
		
		outgoingThread.interrupt();
		while (outgoingThread.isAlive())
		{
			
			try {
				outgoingThread.join();
			} catch (InterruptedException e) {
				
			}
			
		}
		
	}
	
	/**
	 * 
	 * Queues a packet to be sent.
	 * 
	 * @param packet Packet to be sent.
	 * @return Whether the packet was successfully queued.
	 */
	public boolean queuePacket(Packet packet)
	{
		
		return queue.offer(packet);
		
	}
	
}
