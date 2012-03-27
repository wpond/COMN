
import packet.*;
import connection.*;

import java.util.*;

public class Sender4 
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
		
		// this is a queue of items we're waiting for ACKs for
		LinkedList<PacketWrapper> sentQueue = new LinkedList<PacketWrapper>();
		
		// this is a list of items we have acks for
		ArrayList<Short> ackdNums = new ArrayList<Short>();
		
		// this is a list of items to resend
		ArrayList<Short> resends = new ArrayList<Short>();
		
		System.out.println("Sending..");
		
		boolean complete = false;
		while (!complete)
		{
			
			// fast forward baseNum, for any ACKs which have been received
			while (ackdNums.contains(baseNum))
			{
				ackdNums.remove((Short)baseNum); // cast to object so we don't try and remove item at index baseNum
				baseNum++;
			}
			
			short nextSeq = -1;
			
			// check any timeouts
			// find the next packet seq num to send
			
			PacketWrapper pw;
			if (!sentQueue.isEmpty())
			{
				
				Iterator<PacketWrapper> itr = sentQueue.iterator();
				while (itr.hasNext())
				{
					
					pw = itr.next();
					p = pw.getPacket();
					
					// update next sequence number
					nextSeq = (short) Math.max(nextSeq, DataOutputPacketManager.getSequenceNumber(p));
					
					// check for timeout, if so, add to resends
					if (pw.getTime() + timeout < System.currentTimeMillis())
					{
						itr.remove();
						resends.add(DataOutputPacketManager.getSequenceNumber(p));
					}
					
				}
				
			}
			
			// we have to check ACKqueue when looking for the next sequence number to send
			for (int i = 0; i < ackdNums.size(); i++)
			{
				nextSeq = (short) Math.max(ackdNums.get(i), nextSeq);
			}
			
			// * if we never found a next sequence number
			// set it to baseNum
			// * if we did, increment it because it's 
			// currently set to the last packet received
			if (nextSeq == -1)
			{
				nextSeq = baseNum;
			}
			else
			{
				nextSeq++;
			}
			
			// while i is within window and a packet in the file
			short i = baseNum;
			while (sentQueue.size() < windowSize)
			{
				
				// only send IFF i has already been sent and requires resend OR i hasn't been sent yet
				if ((i < nextSeq && resends.contains(i)) || i >= nextSeq)
				{
					
					System.out.println("Sending packet: " + i);
					
					p = dipm.getPacket(i);
					outConn.queuePacket(p);
					sentQueue.add(new PacketWrapper(p));
					
					// TODO: remove before submission
					try
					{
						Thread.sleep(500);
					}
					catch (Exception e)
					{
						
					}
				}
				
				i++;
				
			}
			
			// we've just processed all resends, so clear the queue
			resends.clear();
			
			// get all responses
			while ((p = inConn.getNextPacket()) != null)
			{
				
				short seqNum = DataOutputPacketManager.getSequenceNumber(p);
				
				// remove from sent list
				Iterator<PacketWrapper> itr = sentQueue.iterator();
				while (itr.hasNext())
				{
					
					pw = itr.next();
					
					// assume only one instance of this in sent queue
					if (DataOutputPacketManager.getSequenceNumber(pw.getPacket()) == DataOutputPacketManager.getSequenceNumber(p))
					{
						itr.remove();
						break;
					}
					
				}
				
				// if we received the base num
				if (seqNum == baseNum)
				{
					baseNum++;
				}
				else
				{
					// store the ACK for later
					ackdNums.add(seqNum);
				}
				
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
