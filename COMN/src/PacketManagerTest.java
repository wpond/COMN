
import packet.*;

public class PacketManagerTest {
	
	public static void main(String[] args)
	{
		
		DataOutputPacketManager dopm = null;
		DataInputPacketManager dipm = null;
		try
		{
			dopm = new DataOutputPacketManager("C:\\Users\\Will\\comn\\COMN\\resources\\incoming.jpg");
			dipm = new DataInputPacketManager("C:\\Users\\Will\\comn\\COMN\\resources\\cwk_testfile.jpg");
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(99);
		}
		
		/*
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
		*/
		
		// array of all packets
		Packet[] packets = new Packet[dipm.getPacketCount()+1];
		
		// load all packets
		for (int i = 0; i < packets.length; i++)
		{
			
			packets[i] = dipm.getPacket((short)i);
			
		}
		
		// write first 10 packets forward
		for (int i = 0; i < 10; i++)
		{
			
			System.out.println("1st run: " + i);
			
			try {
				dopm.storePacket(packets[i]);
			} catch (PacketException e) {
				System.err.println(e.getMessage());
				System.exit(98);
			}
			
		}
		
		// write ~most~ packets backwards
		for (int i = packets.length-11; i >= 10; i--)
		{
			
			System.out.println("2nd run: " + i);
			
			try {
				dopm.storePacket(packets[i]);
			} catch (PacketException e) {
				System.err.println(e.getMessage());
				System.exit(97);
			}
			
		}
		
		// write 8 packets forward
		for (int i = packets.length-10; i < packets.length - 2; i++)
		{
			
			System.out.println("3rd run: " + i);
			
			try {
				dopm.storePacket(packets[i]);
			} catch (PacketException e) {
				System.err.println(e.getMessage());
				System.exit(96);
			}
			
		}
		
		// write last 2 packets backwards
		for (int i = packets.length - 2; i < packets.length; i++)
		{
			
			System.out.println("4th run: " + i);
			
			try {
				dopm.storePacket(packets[i]);
			} catch (PacketException e) {
				System.err.println(e.getMessage());
				System.exit(95);
			}
			
		}
			
		
		
	}
	
}
