package packet;

/**
 * 
 * Handles creating and interpreting packets.
 *
 */
public abstract class ACKManager {
	
	/**
	 * Create a packet with a specified sequence number.
	 * 
	 * @param seqNum The sequence number to insert.
	 * @return The ACK packet.
	 */
	public static Packet getPacket(short seqNum)
	{
		
		Packet p = new Packet();
		p.insert(seqNum, 0);
		return p;
		
	}
	
	/**
	 * Interpret an ACK packet.
	 * 
	 * @param packet The packet to interpret.
	 * @return The sequence number of the packet.
	 */
	public static short getSequenceNumber(Packet packet)
	{
		
		short n;
		try
		{
			n = packet.getShort(0);
		}
		catch (PacketException e)
		{
			System.err.println(e.getMessage());
			return -1;
		}
		
		return n;
		
	}
	
}
