package packet;

/**
 * 
 * This class wraps a packet with time information.
 *
 */
public class PacketWrapper {
	
	private Packet packet;
	private long sent;
	
	/**
	 * Create a packet wrapper for a specific packet.
	 * 
	 * This will set the sent time to now.
	 * 
	 * @param packet The packet to wrap.
	 */
	public PacketWrapper(Packet packet)
	{
		
		this.packet = packet;
		resetTime();
		
	}
	
	/**
	 * Reset the sent time to now.
	 */
	public void resetTime()
	{
		sent = System.currentTimeMillis();
	}
	
	/**
	 * Get the stored sent time.
	 * 
	 * @return The time the packet was last reset.
	 */
	public long getTime()
	{
		return sent;
	}
	
	/**
	 * Get the packet this is wrapped by.
	 * 
	 * @return The packet.
	 */
	public Packet getPacket()
	{
		return packet;
	}
	
}
