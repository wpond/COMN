
import packet.*;
import connection.*;

import java.net.*;

public class Receiver3 
{
	
	public static void main(String[] args)
	{
		
		if (args.length < 2)
		{
			System.out.println("Usage: <port> <filename>");
			System.exit(0);
		}
		
		int port = Integer.parseInt(args[0]);
		String filename = args[1];
		
		DataOutputPacketManager dopm = null;
		IncomingConnection inConn = null;
		OutgoingConnection outConn = null;
		
		try
		{
			dopm = new DataOutputPacketManager(filename);
			inConn = new IncomingConnection(port);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(99);
		}
		
		System.out.println("Ready to receive");
		
		Packet p;
		do
		{
			
			p = inConn.getNextPacket();
			if (p == null)
				continue;
			
			try {
				System.out.println("Received packet (" + p.getShort(0) + ")");
			} catch (PacketException e1) {
				System.err.println(e1.getMessage());
				e1.printStackTrace();
			}
			
			// if reply connection doesn't exist
			// store details for reply connection
			if (outConn == null)
			{
				InetSocketAddress addr = inConn.getLastSender();
				try
				{
						outConn = new OutgoingConnection(addr.getHostName(), port+1);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					continue;
				}
			}
			
			if (dopm.getLastSequenceNumber()+1 < DataOutputPacketManager.getSequenceNumber(p))
			{
				System.out.println("Received out of order packet, not writing");
			}
			else
			{
				try {
					dopm.storePacket(p);
				} catch (PacketException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
					continue;
				}
			}
			
			// send ack
			System.out.println("Sending ACK: " + (dopm.getLastSequenceNumber()));
			
			outConn.queuePacket(ACKManager.getPacket((short) (dopm.getLastSequenceNumber())));
			
		}
		while (!dopm.complete());
		
		System.out.println("Receiver complete");
		
		inConn.close();
		outConn.close();
		
	}
	
}
