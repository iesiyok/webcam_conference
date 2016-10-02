/**
 * iesiyok,
 * Self camera, each peer can see their camera by this thread 
 * 
 */

package streamer;

import javax.swing.JFrame;

public class Self extends Thread{

	int width = -1;
	int height = -1;
	
	public Self(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void run() {

		Viewer myViewer = new Viewer(this.width, this.height);
		JFrame frame = new JFrame("Simple Stream Viewer");
		frame.setVisible(true);
		frame.setSize(this.width, this.height);
		frame.setTitle("Self Camera..");
		frame.setDefaultCloseOperation(3);
		frame.add(myViewer);
		while(true){
			byte[] raw_image;
			try {
				raw_image = Main.packQueue.take();
				if(raw_image != null){
					myViewer.ViewerInput(raw_image, this.width, this.height);
					frame.repaint();
				}
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
					
			
		}
		

		
		
		
	}
}
