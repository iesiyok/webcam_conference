/**
 * iesiyok,
 * Greets new peers and creates new Peer threads for each
 * 
 */

package streamer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Greeter extends Thread{

	ServerSocket listener;
	int rate;
	private static final JSONParser jsonParser = new JSONParser();
	public Greeter(int sport, int rate) {
		try {
			ServerSocket listener = new ServerSocket(sport);
			System.out.println("Server listening for a connection");
			this.listener = listener;
			this.rate = rate;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		
		
		while(true){
			Socket clientSocket;
			try {
				clientSocket = this.listener.accept();
				
				DataInputStream in = new DataInputStream( clientSocket.getInputStream());
	            String pack = in.readLine();
	            
				JSONObject json;
				try {
					json = (JSONObject) jsonParser.parse(pack);
					String type = (String) json.get("type");
					int peerWidth = ((Long) json.get("width")).intValue();
					int peerHeight = ((Long) json.get("height")).intValue();
					
					if(type.equals("startstream")){
						String id = Helper.createId();//create new peer
						Peer p = new Peer(id, clientSocket, clientSocket
								.getInetAddress().getHostName(),
								clientSocket.getPort(), peerWidth, peerHeight,
								rate, false);
						p.start();
					}
					Thread.sleep(1);
				} catch (ParseException | InterruptedException e1) {
					e1.printStackTrace();
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			

			
		}
		
	}
	
	
}
