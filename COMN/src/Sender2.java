
import packet.*;
import connection.*;

public class Sender2 
{
	
	public static void main(String[] args)
	{
		
		int timeout = 100;
		
		DataInputPacketManager dipm = null;
		OutgoingConnection outConn = null;
		IncomingConnection inConn = null;
		
		try
		{
			dipm = new DataInputPacketManager("/afs/inf.ed.ac.uk/user/s08/s0818057/comn/COMN/resources/cwk_testfile.jpg");
			outConn = new OutgoingConnection("localhost",9899);
			inConn = new IncomingConnection(9900);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(99);
		}
		
		short seqNum = 0;
		Packet p;
		
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
				if (ACKP == null)
					continue;
				
				if (ACKManager.getSequenceNumber(ACKP) == seqNum)
				{
					break;
				}
					
				if (sent + timeout > System.currentTimeMillis())
				{
					outConn.queuePacket(p);
					sent = System.currentTimeMillis();
				}
				
			}
			
			seqNum++;
			
		} while (!DataInputPacketManager.isFinalPacket(p));
		
		System.out.println("Sender complete");
		
	}
	
}
