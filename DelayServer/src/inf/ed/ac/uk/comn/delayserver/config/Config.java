package inf.ed.ac.uk.comn.delayserver.config;

import java.util.*;
import java.io.*;

public class Config {
	
	ConfigPipe[] pipes;
	
	public Config(String filename) throws IOException, FileNotFoundException
	{
		
		File f = new File(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		
		load(reader);
		
	}
	
	private void load(BufferedReader r) throws IOException
	{
		
		ArrayList<ConfigPipe> pipes = new ArrayList<ConfigPipe>();
		
		while (true)
		{
			
			String l;
			l = r.readLine();
			
			if (l == null)
				break;
			
			if (l.startsWith("//") || l.isEmpty())
				continue;
			
			String[] values = l.split(" ");
			if (values.length < 6)
			{
				System.out.println("Malformed line: \"" + l + "\"");
				continue;
			}
			
			int listenPort = Integer.parseInt(values[0]);
			String sendHost = values[1];
			int sendPort = Integer.parseInt(values[2]);
			int delay = Integer.parseInt(values[3]);
			double dropRate = Double.parseDouble(values[4]);
			int maxSize = Integer.parseInt(values[5]);
			
			pipes.add(new ConfigPipe(listenPort, sendHost, sendPort, delay, dropRate, maxSize));
			
		}
		
		this.pipes = new ConfigPipe[pipes.size()];
		for (int i = 0; i < pipes.size(); i++)
		{
			
			this.pipes[i] = pipes.get(i);
			
		}
		
	}
	
	public ConfigPipe[] getConfig()
	{
		
		return pipes;
		
	}
	
}
