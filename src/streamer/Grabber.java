/**
 * iesiyok,
 * Image grabber, takes image files and adds them to 'Main.packQueue' buffer
 */

package streamer;

import java.util.Iterator;

import org.bridj.Pointer;

import com.github.sarxos.webcam.ds.buildin.natives.Device;
import com.github.sarxos.webcam.ds.buildin.natives.DeviceList;
import com.github.sarxos.webcam.ds.buildin.natives.OpenIMAJGrabber;

public class Grabber extends Thread{
	
	int width;
	int height;
		
	public Grabber(int width, int height) {
		
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void run() {
		
		OpenIMAJGrabber grabber = Helper.grabber();

		Device device = null;
		Pointer devices = grabber.getVideoDevices();
		Iterator localIterator = ((DeviceList) devices.get()).asArrayList()
				.iterator();
		if (localIterator.hasNext()) {
			Device d = (Device) localIterator.next();
			device = d;
		}
		boolean started = grabber.startSession(this.width, this.height, 30.0D,
		Pointer.pointerTo(device));
		if (!started) {
			throw new RuntimeException("Not able to start native grabber!");
		}else{
			while(true){
				grabber.nextFrame();
				byte[] raw_image = grabber.getImage().getBytes(this.width * this.height * 3);
				Main.packQueue.add(raw_image);//Main.rcvQueue.add(raw_image);
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
