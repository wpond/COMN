package inf.ed.ac.uk.comn.delayserver.server.thread;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class OutgoingThread extends Thread 
{
	
	private Queue<byte[]> queue;
	private DatagramSocket socket;
	
	private InetAddress host;
	private int port;
	
	public OutgoingThread(String host, int port, Queue<byte[]> queue) throws SocketException, UnknownHostException
	{
		
		socket = new DatagramSocket();
		
		this.host = InetAddress.getByName(host);
		this.queue = queue;
		this.port = port;
		
	}
	
	public void run()
	{
		
		while (!Thread.interrupted())
		{
			
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				break;
			}
			
			byte[] packet = queue.poll();
			if (packet == null)
				continue;
			
			DatagramPacket dPacket = new DatagramPacket(packet,packet.length,host,port);
			
			try {
				socket.send(dPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		socket.close();
		
	}
	
}
