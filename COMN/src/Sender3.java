
import packet.*;
import connection.*;

import java.util.*;

public class Sender3 
{
	
	public static void main(String[] args)
	{
		
		if (args.length < 5)
		{
			System.out.println("Usage: <host> <port> <filename> <retry timeout> <window size>");
			System.exit(0);
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String filename = args[2];
		int timeout = Integer.parseInt(args[3]);
		int windowSize = Integer.parseInt(args[4]);
		
		DataInputPacketManager dipm = null;
		OutgoingConnection outConn = null;
		IncomingConnection inConn = null;
		
		try
		{
			dipm = new DataInputPacketManager(filename);
			outConn = new OutgoingConnection(host,port);
			inConn = new IncomingConnection(port+1);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(99);
		}
		
		short baseNum = 0;
		Packet p;
		
		long startTime = System.currentTimeMillis();
		
		LinkedList<PacketWrapper> sentQueue = new LinkedList<PacketWrapper>();
		
		System.out.println("Sending..");
		
		boolean complete = false;
		while (!complete)
		{
			
			// remove all packets for which we have an ACK
			if (!sentQueue.isEmpty())
			{
				
				while (sentQueue.peek() != null && DataOutputPacketManager.getSequenceNumber(sentQueue.peek().getPacket()) < baseNum)
				{
					sentQueue.poll();
				}
				
			}
			
			// find the next packet seq num to send 
			// base num is the next ACK we expect
			short nextSeq = baseNum;
			
			if (!sentQueue.isEmpty())
			{	
				// slide window back to base
				if (sentQueue.peek().getTime() + timeout < System.currentTimeMillis())
				{
					System.out.println(" << RESETTING WINDOW >> ");
					sentQueue.clear();
				}
				else
				{
					nextSeq = (short) (DataOutputPacketManager.getSequenceNumber(sentQueue.peekLast().getPacket()) + 1);
				}
			}
			
			// while i is within window and a packet in the file
			for (short i = nextSeq; i < baseNum + windowSize && i <= dipm.getPacketCount(); i++)
			{
				
				System.out.println("Sending packet: " + i);
				
				p = dipm.getPacket(i);
				outConn.queuePacket(p);
				sentQueue.add(new PacketWrapper(p));
				
				// TODO: remove before submission
				try
				{
					Thread.sleep(1);
				}
				catch (Exception e)
				{
					
				}
				
			}
			
			// get all responses
			while ((p = inConn.getNextPacket()) != null)
			{
				
				System.out.println("Received ACK: " + DataOutputPacketManager.getSequenceNumber(p));
				
				// set base num to be the max value (discard old ACKs)
				baseNum = (short) Math.max((DataOutputPacketManager.getSequenceNumber(p) + 1), baseNum);
				
				// only set complete to true if we're expecting the next packet to be after EoF
				if (baseNum > dipm.getPacketCount())
				{
					complete = true;
					break;
				}
				
			}
			
		}
		
		double timeDiff = (double)(System.currentTimeMillis() - startTime) / 1000d;
		double throughput = (double)dipm.getFileSize() / (double)timeDiff;
		System.out.println("Time taken: " + timeDiff + " seconds");
		System.out.println("Throughput: " + throughput + " bytes per second");
		
		System.out.println("Sender complete");
		
		inConn.close();
		outConn.close();
		
	}
	
}
