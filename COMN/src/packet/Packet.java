package packet;

import java.nio.*;

public class Packet {
	
	/**
	 * The number of bytes used to store a boolean.
	 */
	public static final int BOOLEAN_LENGTH = 1;
	
	/**
	 * The number of bytes used to store a short.
	 */
	public static final int SHORT_LENGTH = 2;
	
	private ByteBuffer buffer = ByteBuffer.allocate(0);
	
	/**
	 * 
	 * Inserts some bytes at a given index.
	 * 
	 * @param data Data to insert.
	 * @param pos Index to copy to.
	 */
	public void insert(byte[] data, int pos)
	{
		
		if (size() < pos + data.length)
		{
			
			reallocate(pos + data.length);
			
		}
		
		buffer.position(pos);
		buffer.put(data, 0, data.length);
		
	}
	
	/**
	 * 
	 * Inserts a short value at a given index.
	 * 
	 * @param val The short value to insert.
	 * @param pos The index at which to insert it.
	 */
	public void insert(short val, int pos)
	{
		
		if (size() < pos + SHORT_LENGTH)
		{
			
			reallocate(pos + SHORT_LENGTH);
			
		}
		
		buffer.putShort(pos, val);
		
	}
	
	/**
	 * 
	 * Inserts a boolean at a given index.
	 * 
	 * @param val The boolean value to insert.
	 * @param pos The index at which to insert it.
	 */
	public void insert(boolean val, int pos)
	{
		
		if (size() < pos + BOOLEAN_LENGTH)
		{
			
			reallocate(pos + BOOLEAN_LENGTH);
			
		}
		
		buffer.put(pos, (byte)((val) ? 1 : 0));
		
	}
	
	/**
	 * 
	 * Get a short value from an index.
	 * 
	 * @param pos The index to copy the short from.
	 * @return The short value.
	 */
	public short getShort(int pos) throws PacketException
	{
		
		if (pos >= size() - SHORT_LENGTH)
			throw new PacketException("Cannot fetch short at " + pos + " buffer length is " + size());
		
		return buffer.getShort(pos);
		
	}
	
	/**
	 * 
	 * Get an array of bytes of length len starting at index pos.
	 * 
	 * @param pos Index to start at.
	 * @param len Length of array to return
	 * @return An array of bytes found. 
	 */
	public byte[] getData(int pos, int len) throws PacketException
	{
		
		if (pos >= size())
			throw new PacketException("Cannot fetch data at " + pos + " buffer length is " + size());
		
		if (pos + len > size())
			len = size() - pos;
		
		byte[] retBuffer = new byte[len];
		
		buffer.position(pos);
		buffer.get(retBuffer,0,len);
		return retBuffer;
		
	}
	
	/**
	 * 
	 * Get a boolean from an index.
	 *  
	 * @param pos The index of the boolean.
	 * @return The boolean copied.
	 */
	public boolean getBoolean(int pos) throws PacketException
	{
		
		if (pos > size() - BOOLEAN_LENGTH)
			throw new PacketException("Cannot fetch boolean at " + pos + " buffer length is " + size());
		
		return ((buffer.get(pos) == 0) ? false : true);
		
	}
	
	/**
	 * 
	 * Get all the data stored.
	 * 
	 * @return The byte array of all the data stored.
	 */
	public byte[] getData()
	{
		return buffer.array();
	}
	
	/**
	 * 
	 * The size (in bytes) of the data.
	 * 
	 * @return The number of bytes stored.
	 */
	public int size()
	{
		return buffer.capacity();
	}
	
	/**
	 * 
	 * Reallocate the buffer based on a new length.
	 * This must be longer.
	 * 
	 * @param len The new length of the buffer.
	 */
	private void reallocate(int len)
	{
		
		if (size() < len)
		{
			
			ByteBuffer tmp = ByteBuffer.allocate(len);
			tmp.put(buffer.array());
			buffer = tmp;
			
		}
		
	}
	
}
