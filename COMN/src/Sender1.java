
import packet.*;
import connection.*;

public class Sender1 
{
	
	public static void main(String[] args)
	{
		
		if (args.length < 3)
		{
			System.out.println("Usage: <host> <port> <filename>");
			System.exit(0);
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String filename = args[2];
		
		DataInputPacketManager dipm = null;
		OutgoingConnection conn = null;
		
		try
		{
			dipm = new DataInputPacketManager(filename);
			conn = new OutgoingConnection(host,port);
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
			conn.queuePacket(p);
			seqNum++;
			
		} while (!DataInputPacketManager.isFinalPacket(p));
		
		System.out.println("Sender complete");
		
	}
	
}
