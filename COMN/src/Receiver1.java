
import packet.*;
import connection.*;

public class Receiver1 
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
		IncomingConnection conn = null;
		
		try
		{
			dopm = new DataOutputPacketManager(filename);
			conn = new IncomingConnection(port);
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
			
			p = conn.getNextPacket();
			if (p == null)
				continue;
			
			try {
				System.out.println("Received packet (" + p.getShort(0) + ")");
			} catch (PacketException e1) {
				System.err.println(e1.getMessage());
				e1.printStackTrace();
			}
			
			try {
				dopm.storePacket(p);
			} catch (PacketException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				continue;
			}
			
		}
		while (!dopm.complete());
		
		System.out.println("Receiver complete");
		
	}
	
}
