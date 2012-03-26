package inf.ed.ac.uk.comn.delayserver;

import inf.ed.ac.uk.comn.delayserver.config.*;
import inf.ed.ac.uk.comn.delayserver.server.*;

import java.io.*;

public class Entrypoint {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try
		{
		
			if (args.length < 1)
			{
				System.out.println("Required argument: pipe config data");
				System.out.println("Lines should be in form:");
				System.out.println("<listen port> <send host> <send port> <delay> <drop rate (0.0 -> 1.0)> <max packet size>");
				System.exit(0);
			}
			
			Config config = new Config(args[0]);
			
			ConfigPipe[] configPipes = config.getConfig();
			Pipe[] pipes = new Pipe[configPipes.length];
			
			for (int i = 0; i < configPipes.length; i++)
			{
				
				ConfigPipe p = configPipes[i];
				
				System.out.println("==========");
				System.out.println("Creating pipe:");
				System.out.println("\tListen: 0.0.0.0:" + p.listenPort);
				System.out.println("\tForward: " + p.sendHost + ":" + p.sendPort);
				System.out.println("\tDelay: " + p.delay);
				System.out.println("\tDrop rate: " + p.dropRate);
				
				pipes[i] = new Pipe(p);
				
			}
			
			System.out.println("==========");
			System.out.println();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println("Press <enter> to quit");

			// wait for enter to be pressed
			try 
			{
				reader.readLine();
			} 
			catch (Exception e)
			{
				System.out.println("Read line exception, quitting");
			}
			
			for (int i = 0; i < pipes.length; i++)
			{
				
				pipes[i].close();
				
			}
		
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
			
		
	}

}
