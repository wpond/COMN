package connection;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import packet.Packet;
import java.net.*;

/**
 * 
 * Controls incoming connections and their data.
 *
 */
public class IncomingConnection {
	
	/**
	 * A queue of items received.  This is a concurrent linked queue.
	 * @see java.util.concurrent.ConcurrentLinkedQueue
	 */
	private Queue<Packet> queue = new ConcurrentLinkedQueue<Packet>();
	
	private IncomingThread incomingThread;
	
	/**
	 * 
	 * Create a new incoming connection which listens the specified port.
	 * 
	 * @param port The port to listen on.
	 * @throws SocketException
	 */
	public IncomingConnection(int port) throws SocketException
	{
		
		incomingThread = new IncomingThread(port,queue);
		incomingThread.start();
		
	}
	
	/**
	 * Stops the thread and releases references.
	 */
	public void close()
	{
		incomingThread.interrupt();
		while (incomingThread.isAlive())
		{
			
			try {
				incomingThread.join();
			} catch (InterruptedException e) {
				
			}
			
		}
	}
	
	/**
	 * Gets the next incoming packet.
	 */
	public Packet getNextPacket()
	{
		return queue.poll();
	}
	
	/**
	 * 
	 * Get details of the origin of last packet received.
	 * 
	 * @return Socket address of last last packet received.
	 */
	public InetSocketAddress getLastSender()
	{
		return incomingThread.getLastSender();
	}
	
}
