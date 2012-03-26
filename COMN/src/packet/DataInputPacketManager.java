package packet;

import java.io.*;
import java.util.*;

public class DataInputPacketManager {
	
	private static final int SIZE = 1024;
	private static final int HEADER_SIZE = 3; // short + boolean (1 byte)
	
	private short seqNum;
	private File file;
	private InputStream is;
	
	/**
	 * A hash map of sequence number and packet data.
	 */
	private Map<Short,Packet> packets = new HashMap<Short,Packet>(); 
	
	/**
	 * 
	 * Opens the file for reading.
	 * 
	 * @param filename Filename to load.
	 * @throws FileNotFoundException 
	 */
	public DataInputPacketManager(String filename) throws FileNotFoundException
	{
		
		file = new File(filename);
		if (!file.exists())
			throw new FileNotFoundException("File not found: " + filename);
		
		open();
		
	}
	
	/**
	 * Closes the input stream
	 */
	private void close()
	{
		
		if (is == null)
			return;
		
		try {
			is.close();
		} catch (IOException e) {
			
		}
		
	}
	
	/**
	 * Open the input stream for reading.
	 * 
	 * This calls close first to stop any resources being left open.
	 */
	private void open()
	{
		
		close();
		
		seqNum = 0;
		
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			is = null;
		}
		
	}
	
	/**
	 * 
	 * Loads a packet either from memory or file.
	 * 
	 * If the file was not in memory it spools to the packet.
	 * This is done by fast forwarding if the packet is in front or 
	 * reopening the file if the packet was behind.  It should be noted
	 * any unrequested packets are not read.
	 * 
	 * Returns null if there was an error.
	 * 
	 * @param seqNum The sequence number of the packet to load.
	 * @return The packet loaded or null. 
	 */
	public Packet getPacket(short seqNum)
	{
		
		if (packetLoaded(seqNum))
		{
			return packets.get(seqNum);
		}
		
		if (seqNum < this.seqNum)
		{
			
			// reopen the file
			open();
			
		}
		
		if (seqNum > this.seqNum)
		{
			
			// move to packet (discard earlier packets)
			try {
				skip(seqNum - this.seqNum);
			} catch (IOException e) {
				// open resets the sequence number
				open();
				return null;
			}
			
		}
		
		// return the packet
		return readPacket();
		
	}
	
	/**
	 * 
	 * Skips packets on the input stream.
	 * 
	 * @param packets The number of packets to skip.
	 * @throws IOException If there was a problem.
	 */
	private void skip(int packets) throws IOException
	{
		
		is.skip((packets - seqNum) * (SIZE - HEADER_SIZE));
		seqNum += packets;
		
	}
	
	/**
	 * 
	 * Creates a packet using the current sequence number.
	 * 
	 * @return The newly created packet or null if it was unable to create it.
	 */
	private Packet readPacket()
	{
		
		byte[] data = new byte[SIZE-HEADER_SIZE];
		
		int bytesRead = -1;
		boolean EoF = false;
		
		// try and read data
		try {
			bytesRead = is.read(data);
		} catch (IOException e) {
			data = new byte[0];
		}
		
		// if EoF, truncate data array
		if (bytesRead != data.length)
		{
			
			if (bytesRead < 0)
			{
				bytesRead = 0;
			}
			
			data = Arrays.copyOf(data, bytesRead);
			EoF = true;
			
		}
		
		Packet p = new Packet();
		p.insert(seqNum, 0);
		p.insert(data, Packet.SHORT_LENGTH);
		p.insert(EoF, p.size());
		
		packets.put(seqNum, p);
		
		seqNum++;
		
		return p;
		
	}
	
	/**
	 * 
	 * Tests whether we have a packet in memory.
	 * 
	 * @param seqNum The packet's sequence number as a short.
	 * @return Whether we have the packet in memory.
	 */
	private boolean packetLoaded(short seqNum)
	{
		return packets.containsKey(seqNum);
	}
	
	/**
	 * 
	 * Drops all packets up to a sequence number from memory (inclusive).
	 * 
	 * @param seqNum Sequence number.
	 */
	public void dropPackets(short seqNum)
	{
		
		Iterator<Packet> i = packets.values().iterator();
		while (i.hasNext())
		{
			
			short num;
			try {
				num = i.next().getShort(0);
			} catch (PacketException e) {
				System.err.println(e.getMessage());
				continue;
			}
			
			if (num <= seqNum)
			{
				packets.remove(num);
			}
			
		}
		
	}
	
	public static boolean isFinalPacket(Packet packet)
	{
		try {
			return packet.getBoolean(packet.size()-1);
		} 
		catch (PacketException e) 
		{
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			System.exit(5);
		}
		catch (Exception e)
		{
			
		}
		
		return false;
	}
	
}
