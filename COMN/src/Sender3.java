
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
		
		TreeMap<Long,Short> timeouts = new TreeMap<Long,Short>();
		
		int resends = 0;
		
		do
		{
			
			System.out.println("Sending message (" + baseNum + ")");
			
			p = dipm.getPacket(baseNum);
			outConn.queuePacket(p);
			
			// wait for ack
			Packet ACKP;
			
			long sent = System.currentTimeMillis();
			while (true)
			{
				
				ACKP = inConn.getNextPacket();
				if (ACKP != null)
				{
					if (ACKManager.getSequenceNumber(ACKP) == baseNum)
					{
						break;
					}
				}
				
				if (sent + timeout < System.currentTimeMillis())
				{
					System.out.println("Resending...");
					resends++;
					outConn.queuePacket(p);
					sent = System.currentTimeMillis();
				}
				
			}
			
			baseNum++;
			
		} while (!DataInputPacketManager.isFinalPacket(p));
		
		System.out.println("Resends: " + resends);
		
		System.out.println("Sender complete");
		
		inConn.close();
		outConn.close();
		
	}
	
}
