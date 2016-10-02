/**
 * iesiyok,
 * Main class for the program,
 * takes arguments from command line, 
 * creates new peers if -remote option given,
 * runs grabber, greeter and self threads 
 * 
 */

package streamer;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {

	private static int DEFAULT_PORT_NUMBER = 6262;
	private static int DEFAULT_WIDTH = 320;
	private static int DEFAULT_HEIGHT = 240;
	private static int DEFAULT_RATE = 100;
	
	
	public static BlockingQueue<byte[]> 
		packQueue = new ArrayBlockingQueue<byte[]>(DEFAULT_RATE*1024);
/*	public static BlockingQueue<byte[]> 
		rcvQueue = new ArrayBlockingQueue<byte[]>(DEFAULT_RATE*1024);*/
	
		public static void main(String[] args) {
			
			int sport = DEFAULT_PORT_NUMBER;
			java.util.List<String> remoteHosts = null;
			java.util.List<Integer> remotePorts = null;
			CmdValues values = new CmdValues();
			int width = DEFAULT_WIDTH;
			int height = DEFAULT_HEIGHT;
			int rate = DEFAULT_RATE;
			CmdLineParser parser = new CmdLineParser(values);
			parser.setUsageWidth(80);
			
			try{
				parser.parseArgument(args);
			}catch(CmdLineException e){
				System.err.println(e.getMessage());
				System.exit(0);
			}
			//parameters from command line
			
			if(values.getSport()>0){
				sport = values.getSport();
			}
			if(values.getWidth() > 0){
				width = values.getWidth();
			}
			if(values.getHeight()>0){
				height = values.getHeight();
			}
			if(values.getRemoteHosts() !=null){
				
				remoteHosts = Arrays.asList(values.getRemoteHosts().split(","));
			}
			if(values.getrPorts() !=null ){
				List<String> rp = Arrays.asList(values.getrPorts().split(","));
				remotePorts = new ArrayList<Integer>();
				for (String string : rp) {
					remotePorts.add(Integer.valueOf(string));
				}
				
			}
			if(values.getRate() > 0){
				rate = values.getRate();
			}
						
			if(remoteHosts != null && remoteHosts.size()>0){
				
				if(remoteHosts.size() == remotePorts.size()){
					for(int i=0;i<remoteHosts.size();i++){
						String host = remoteHosts.get(i);
						int rport = remotePorts.get(i); 
						String id = Helper.createId();
						Socket s;
						
						try {
							s = new Socket(host, rport);//create new socket and new peer for each peer
							Thread p = new Thread(new Peer(id, s, host, rport, width, height, rate, true));
							p.start();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					
					
				}else{
					System.err.println("command line parser error..");
				}
				
				
			}else{
				System.out.println("No peer given.. only self camera working..");
			}
			//run threads
			Thread g = new Thread(new Grabber(width, height));
			g.start();
			Thread self = new Thread(new Self(width, height));
			self.start();
			Thread greeter = new Thread(new Greeter(sport,rate));
			greeter.start();
			
			
		}
	


}

