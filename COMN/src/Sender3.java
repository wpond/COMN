
import packet.*;
import connection.*;

public class Sender3 
{
	
	public static void main(String[] args)
	{
		
		if (args.length < 4)
		{
			System.out.println("Usage: <host> <port> <filename> <retry timeout>");
			System.exit(0);
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String filename = args[2];
		int timeout = Integer.parseInt(args[3]);
		
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
		
		short seqNum = 0;
		Packet p;

		int resends = 0;
		
		do
		{
			
			System.out.println("Sending message (" + seqNum + ")");
			
			p = dipm.getPacket(seqNum);
			outConn.queuePacket(p);
			
			// wait for ack
			Packet ACKP;
			
			long sent = System.currentTimeMillis();
			while (true)
			{
				
				ACKP = inConn.getNextPacket();
				if (ACKP != null)
				{
					if (ACKManager.getSequenceNumber(ACKP) == seqNum)
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
			
			seqNum++;
			
		} while (!DataInputPacketManager.isFinalPacket(p));
		
		System.out.println("Resends: " + resends);
		
		System.out.println("Sender complete");
		
		inConn.close();
		outConn.close();
		
	}
	
}
