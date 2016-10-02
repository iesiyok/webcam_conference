/**
 * iesiyok
 * takes parameters from command line
 * 
 */

package streamer;

import org.kohsuke.args4j.Option;

public class CmdValues {

	
	@Option(name = "-sport", usage = "server port")
	private int sport;

	@Option(name = "-remote", usage = "remote host names")
	private String remoteHosts;

	@Option(name = "-rport", usage = "remote ports")
	private String rPorts;

	@Option(name = "-width", usage = "width")
	private int width;

	@Option(name = "-height", usage = "height")
	private int height;
	
	@Option(name = "-rate", usage = "rate")
	private int rate;
	
	public CmdValues() {

	}
	
	public String getrPorts() {
		return rPorts;
	}

	public void setrPorts(String rPorts) {
		this.rPorts = rPorts;
	}


	public int getRate() {
		return rate;
	}


	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getSport() {
		return sport;
	}
	public void setSport(int sport) {
		this.sport = sport;
	}
	
	public String getRemoteHosts() {
		return remoteHosts;
	}
	public void setRemoteHosts(String remoteHosts) {
		this.remoteHosts = remoteHosts;
	}

	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}

}
