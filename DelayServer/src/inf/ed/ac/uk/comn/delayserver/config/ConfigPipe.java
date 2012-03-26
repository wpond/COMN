package inf.ed.ac.uk.comn.delayserver.config;

public class ConfigPipe {
	
	public final int listenPort, sendPort, delay, maxSize;
	public final String sendHost;
	public final double dropRate;
	
	public ConfigPipe(int listenPort, String sendHost, int sendPort, int delay, double dropRate, int maxSize)
	{
		
		this.listenPort = listenPort;
		this.sendHost = sendHost;
		this.sendPort = sendPort;
		this.delay = delay;
		this.dropRate = dropRate;
		this.maxSize = maxSize;
		
	}
	
}
