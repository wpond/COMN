package packet;

import java.util.*;
import java.io.*;

public class DataOutputPacketManager {
	
	/**
	 * The maximum number of bytes to store in memory before writing. (This is set to 1MB)
	 */
	private static final int MAX_BYTE_ARRAY_SIZE = 1048576;
	
	/**
	 * The sequence number of the last packet we wrote
	 */
	private short seqNum = 0;
	
	/**
	 * A map of packets we're saving for later
	 */
	private Map<Short,Packet> packets = new HashMap<Short,Packet>();
	
	private File file;
	private OutputStream os;
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	private boolean fileOutputComplete = false;
	
	/**
	 * 
	 * Create a new data output manager.
	 * 
	 * @param filename The file to write it to.
	 * @throws FileNotFoundException If the file supplied is a directory or generally cannot be written to.
	 */
	public DataOutputPacketManager(String filename) throws FileNotFoundException
	{
		
		file = new File(filename);
		os = new FileOutputStream(file);
		
	}
	
	/**
	 * 
	 * Stores a packet in memory for consumption later.
	 * 
	 * @param packet The packet to store.
	 * @return Whether the packet was the end of the file.
	 * @throws PacketException If the packet was malformed.
	 */
	public boolean storePacket(Packet packet) throws PacketException
	{
		
		if (packet == null)
			throw new PacketException("Trying to store null packet");
		
		short seqNum = packet.getShort(0);
		boolean EoF = packet.getBoolean(packet.size()-1);
		
		// add to byte array
		packets.put(seqNum, packet);
		
		// check all packets in map
		checkPacketMap();
		
		// return whether it was EoF or not
		return EoF;
		
	}
	
	/**
	 * Check for any new packets we can write out
	 */
	private void checkPacketMap()
	{
		
		while (packets.containsKey(seqNum))
		{
			
			// get packet
			Packet p = packets.get(seqNum);
			
			// extract data
			byte[] data;
			try {
				data = p.getData(2, p.size()-3);
			} catch (PacketException e) {
				System.err.println(e.getMessage());
				continue;
			}
			
			// write data to buffer
			try {
				baos.write(data);
			} catch (IOException e) {
				System.err.println("Unable to write to output buffer");
				System.exit(2);
			}
			
			// remove from packets
			packets.remove(seqNum);
			
			// move to next packet
			seqNum++;
			
			// check whether we need to output
			try {
				if (p.getBoolean(p.size()-1))
				{
					System.out.println("Writing final packet");
					flushByteArray();
					fileOutputComplete = true;
				}
				else
				{
					checkByteArray();
				}
			} catch (PacketException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				continue;
			}
			
		}
		
	}
	
	/**
	 * 
	 * Whether the final packet has been written to file.
	 * 
	 * @return The final packet written flag.
	 */
	public boolean complete()
	{
		return fileOutputComplete;
	}
	
	/**
	 * Checks if we have more data buffered than allowed.
	 * 
	 * Writes to file if so.
	 */
	private void checkByteArray()
	{
		
		if (baos.size() > MAX_BYTE_ARRAY_SIZE)
		{
			flushByteArray();
		}
		
	}
	
	/**
	 * Writes all buffered data to file.
	 */
	private void flushByteArray()
	{
		
		// write byte array to the output stream
		try {
			baos.writeTo(os);
			baos.flush();
		} catch (IOException e) {
			System.err.println("Unable to write to file");
			System.exit(1);
		}
		
		baos.reset();
		
	}
	
	/**
	 * Gets the number of the last packet written.
	 * 
	 * Note this is not the last packet we pasted to storePacket.
	 * 
	 * @return A sequence number.
	 */
	public short getLastSequenceNumber()
	{
		return seqNum;
	}
	
	/**
	 * Get the sequence number of a packet.
	 * 
	 * @param p The packet to interpret.
	 * @return The sequence number.
	 */
	public static short getSequenceNumber(Packet p)
	{
		try {
			return p.getShort(0);
		} catch (PacketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
}
