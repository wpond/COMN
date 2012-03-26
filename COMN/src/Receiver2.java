
import packet.*;
import connection.*;

import java.net.*;

public class Receiver2 
{
	
	public static void main(String[] args)
	{
		DataOutputPacketManager dopm = null;
		IncomingConnection inConn = null;
		OutgoingConnection outConn = null;
		
		try
		{
			dopm = new DataOutputPacketManager("/afs/inf.ed.ac.uk/user/s08/s0818057/comn/COMN/resources/incoming.jpg");
			inConn = new IncomingConnection(9899);
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
			
			if (outConn == null)
			{
				InetSocketAddress addr = inConn.getLastSender();
				try
				{
						outConn = new OutgoingConnection(addr.getHostName(), 9900);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					continue;
				}
			}
			
			try {
				dopm.storePacket(p);
			} catch (PacketException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				continue;
			}
			
			// send ack
			outConn.queuePacket(ACKManager.getPacket(DataOutputPacketManager.getSequenceNumber(p)));
			
		}
		while (!dopm.complete());
		
		System.out.println("Receiver complete");
		
	}
	
}
