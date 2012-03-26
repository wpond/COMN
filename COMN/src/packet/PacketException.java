package packet;

public class PacketException extends Exception {
	
	public PacketException()
	{
		super("Unkown packet exception");
	}
	
	public PacketException(String err) {
		super(err);
	}

	/**
	 * Generated serial id.
	 */
	private static final long serialVersionUID = -5719335503867888146L;

}
