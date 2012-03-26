
import packet.*;

public class PacketManagerTest {
	
	public static void main(String[] args)
	{
		
		DataOutputPacketManager dopm = null;
		DataInputPacketManager dipm = null;
		try
		{
			dopm = new DataOutputPacketManager("/afs/inf.ed.ac.uk/user/s08/s0818057/comn/COMN/resources/incoming.jpg");
			dipm = new DataInputPacketManager("/afs/inf.ed.ac.uk/user/s08/s0818057/comn/COMN/resources/cwk_testfile.jpg");
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(99);
		}
		
		Packet p;
		short num = 0;
		while (!DataInputPacketManager.isFinalPacket((p = dipm.getPacket(num))))
		{
			
			System.out.println("Num: " + num);
			
			try {
				dopm.storePacket(p);
			} catch (PacketException e) {
				System.err.println(e.getMessage());
				System.exit(98);
			}
			
			num++;
			
		}
		
		System.out.println("Num: " + num);
		
		try {
			dopm.storePacket(p);
		} catch (PacketException e) {
			System.err.println(e.getMessage());
			System.exit(98);
		}
		
		
	}
	
}
