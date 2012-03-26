package inf.ed.ac.uk.comn.delayserver.server;

import inf.ed.ac.uk.comn.delayserver.config.*;
import inf.ed.ac.uk.comn.delayserver.server.thread.*;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;

public class Pipe
{
	
	private Thread incomingThread, outgoingThread, delayThread;
	
	private Queue<byte[]> incomingQueue = new ConcurrentLinkedQueue<byte[]>(),
							outgoingQueue = new ConcurrentLinkedQueue<byte[]>();
	
	public Pipe(int listenPort, String sendHost, int sendPort, int delay, double dropRate, int maxPacketSize) throws SocketException, UnknownHostException
	{
		
		incomingThread = new IncomingThread(listenPort,incomingQueue,maxPacketSize);
		outgoingThread = new OutgoingThread(sendHost, sendPort, outgoingQueue);
		
		delayThread = new DelayThread(incomingQueue, outgoingQueue, delay, dropRate);
		
		incomingThread.start();
		delayThread.start();
		outgoingThread.start();
		
	}
	
	public Pipe(ConfigPipe cPipe) throws SocketException, UnknownHostException
	{
		
		this(cPipe.listenPort, cPipe.sendHost, cPipe.sendPort, cPipe.delay, cPipe.dropRate, cPipe.maxSize);
		
	}
	
	public void close()
	{
		
		if (incomingThread != null)
			closeThread(incomingThread);
		
		if (delayThread != null)
			closeThread(delayThread);
		
		if (outgoingThread != null)
			closeThread(outgoingThread);
		
		
	}
	
	private void closeThread(Thread t)
	{
		
		t.interrupt();
		while (t.isAlive())
		{
			try
			{
				t.join();
			}
			catch (InterruptedException e)
			{
				
			}
		}
		
	}
	
}
