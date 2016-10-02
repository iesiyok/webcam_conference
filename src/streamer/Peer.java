/**
 * iesiyok
 * represents each peer which are sending and receiving packets
 * threads: Peer, Sender, Receiver
 * 
 */

package streamer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class Peer extends Thread{
	
	String id;
	String host;
	int port = -1;
	int width = -1;
	int height = -1;
	int rate = -1;
	
	int selfWidth = -1;
	int selfHeight = -1;
	int selfRate = -1;
	boolean hasStartStream = false;
	Socket socket = null;
	
	
	public Peer(String id, Socket s, String host, int port, int width, int height, int rate, boolean hasStartStream) {
		this.id = id;
		this.host = host;
		this.port = port;
		this.width = width;
		this.height = height;
		this.rate = rate;
		this.hasStartStream = hasStartStream;
		this.socket = s;
		this.selfWidth = width;
		this.selfHeight = height;
	}

	@Override
	public void run() {
		final Socket s = this.socket;
		boolean canStart = false;
		if(this.hasStartStream){//if peers given from command line, the peer will try to connect
			JSONObject json = Helper.startStreamJSON("startstream", "raw", this.selfWidth, this.selfHeight);
			
			try {
				DataOutputStream out = new DataOutputStream( s.getOutputStream());

				String pack = json.toJSONString() + "\n";
				
			    out.write(pack.getBytes());
			    
				canStart = true;
				
			} catch ( IOException e) {
				e.printStackTrace();
			} 
			
		}else{
			canStart = true;
			//if it is not a peer given by command line, but attempting to connect later(through 'Greeter')
			//sender and receiver threads can start to progress
			//otherwise sender and receiver will wait the start of the other peer to progress
		}
	
		//Viewer will open a new frame for eah connection	
		Viewer myViewer = new Viewer(this.width, this.height);
	    JFrame frame = new JFrame("Simple Stream Viewer");
	    frame.setVisible(true);
	    frame.setSize(this.width, this.height);
	    //frame.setDefaultCloseOperation(3);
	    frame.setTitle(this.host + " : " + this.port);
	    frame.add(myViewer);
	    Thread receiver;
	    Thread sender;
	    
	    if(canStart){
		    //receiver and sender threads start.
		    receiver = new Thread(new Receiver(s, myViewer,frame,this.width,this.height));
			receiver.start();
			sender = new Thread(new Sender(s, frame, this.selfWidth, this.selfHeight,this.rate));
			sender.start();
	    }
	    

	}

	
}
/**
 * 
 * Sender thread, takes images from Main.packQueue which is filled by Grabber thread
 *
 */
class Sender extends Thread{
	
	Socket s;
	int width= -1;
	int height = -1;
	JFrame frame;
	int rate = -1;
	
	public Sender(Socket s, JFrame frame, int width, int height,int rate) {
		this.s = s;
		this.width = width;
		this.height = height;
		this.frame = frame;
		this.rate = rate;
	}
	@Override
	public void run() {
		DataOutputStream out;
		try {
			out = new DataOutputStream( s.getOutputStream());
			while(frame.isVisible() && s.isConnected()){
				byte[] raw_image = Main.packQueue.take();
				byte[] compressed_image = Compressor.compress(raw_image);
				byte[] base64_image = Base64.encodeBase64(compressed_image);
				String data = new String(base64_image);
				JSONObject json = Helper.subJSON("image", data);
				String pack = json.toJSONString() + "\n";
				
			    out.write(pack.getBytes());
				try {
					Thread.sleep(rate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(s.isConnected()){
				JSONObject json = Helper.stopStreamJSON();//stop stream
				String pack = json.toJSONString() + "\n";
				out.write(pack.getBytes());
				frame.dispose();
				s.close();
			}
			
		} catch (IOException | InterruptedException e1 ) {
			e1.printStackTrace();
		}


	}
}
/**
 * 
 * Receiver thread, takes images from other peer and updates the image on frame
 *
 */
class Receiver extends Thread{
	
	Socket s;
	Viewer myViewer;
	JFrame frame;
	int width;
	int height;
	private static final JSONParser parser = new JSONParser();
	
	public Receiver(Socket s,Viewer myViewer,JFrame frame,int width,int height) {
		this.s = s;
		this.myViewer = myViewer;
		this.frame = frame;
		this.width = width;
		this.height = height;
	}
	@Override
	public void run() {
		try {
			
			DataInputStream in = new DataInputStream( s.getInputStream());

			while(frame.isVisible() && s.isConnected()){
			
				String inputLine = in.readLine();
				JSONObject json = (JSONObject) parser.parse(inputLine);
				if(json != null && json.get("type") !=null && json.get("type").equals("stopstream")){
					
					frame.dispose();//if stop stream then close
					s.close();
					break;
					
				}else if(json != null){
					byte[] nobase64_image = Base64.decodeBase64((String)json.get("data"));
				    byte[] decompressed_image = Compressor.decompress(nobase64_image);

					myViewer.ViewerInput(decompressed_image,width,height);
				    frame.repaint();
				}


			}

			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
}