package inf.ed.ac.uk.comn.delayserver.server.thread;

import java.util.*;

public class DelayThread extends Thread {
	
	private Queue<byte[]> incomingQueue, outgoingQueue;
	private int delay;
	private double dropRate;
	private TreeMap<Long,Queue<byte[]>> delayMap = new TreeMap<Long,Queue<byte[]>>();
	private Random random;
	
	public DelayThread(Queue<byte[]> incomingQueue, Queue<byte[]> outgoingQueue, int delay, double dropRate)
	{
		
		random = new Random();
		
		this.incomingQueue = incomingQueue;
		this.outgoingQueue = outgoingQueue;
		this.delay = delay;
		this.dropRate = dropRate;
		
	}
	
	public void run()
	{
		
		while (!Thread.interrupted())
		{
			
			long time;
			
			// check for new items on incoming queue
			byte[] packet;
			while ((packet = incomingQueue.poll()) != null)
			{
				
				time = System.currentTimeMillis();
				
				if (!delayMap.containsKey(time))
				{
					delayMap.put(time, new LinkedList<byte[]>());
				}
				
				delayMap.get(time).offer(packet);
				
			}
			
			if (delayMap.isEmpty())
				continue;
			
			// check for items in tree to move to outgoing queue
			Map.Entry<Long, Queue<byte[]>> entry;
			while (delayMap.firstKey() + delay < System.currentTimeMillis())
			{
				
				entry = delayMap.pollFirstEntry();
				
				while ((packet = entry.getValue().poll()) != null)
				{
					
					// if random double (0.0 -> 1.0) is less than dropRate, don't add packet to outgoing queue 
					double r = random.nextDouble();
					if (r < dropRate)
						continue;
					
					outgoingQueue.offer(packet);
					
				}
				
			}
			
		}
		
	}
	
}
