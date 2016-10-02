/**
 * iesiyok,
 * Helper methods for json operations
 */

package streamer;

import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bridj.Pointer;
import org.json.simple.JSONObject;

import com.github.sarxos.webcam.ds.buildin.natives.Device;
import com.github.sarxos.webcam.ds.buildin.natives.DeviceList;
import com.github.sarxos.webcam.ds.buildin.natives.OpenIMAJGrabber;

public class Helper {
	
	public static JSONObject startStreamJSON(String type, String format, int width, int height ){
		
		
		JSONObject json = new JSONObject();
		json.put("type", type);
		json.put("format", format);
		json.put("width", width);
		json.put("height", height);
		
		return json;
	}
	public static JSONObject stopStreamJSON(){
		
		
		JSONObject json = new JSONObject();
		json.put("type", "stopstream");
		
		return json;
	}
	
	public static JSONObject subJSON(String type, String encodedImage){
		JSONObject json = new JSONObject();
		json.put("type", type);
		json.put("data", encodedImage);
		return json;
	}
	
	public static OpenIMAJGrabber grabber(){
		
		OpenIMAJGrabber grabber = new OpenIMAJGrabber();

		Device device = null;
		Pointer devices = grabber.getVideoDevices();
		Iterator localIterator = ((DeviceList) devices.get()).asArrayList()
				.iterator();
		if (localIterator.hasNext()) {
			Device d = (Device) localIterator.next();
			device = d;
		}
		return grabber;
	}
	public static String createId(){
		String uniqueID = UUID.randomUUID().toString();
		return uniqueID;
	}

}
