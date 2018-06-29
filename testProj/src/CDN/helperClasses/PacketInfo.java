/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: Packet.java
 * @description: Packet class allows structure to be provided for the creation of a packet, making it easier to manipulate packetinfo.
 */

package CDN.helperClasses;

public class PacketInfo {

	private String header;
	private String file;
	
	//packet info holds header and data info.
	public PacketInfo(){
		header = "";
		file = null;
	}
	
	//getters and setters
	public void setHeader(String header){
		this.header = header;
	}
	
	public String getHeader(){
		return header;
	}
	
	public void setFile(String file){
		this.file = file;
	}
	
	public String getFile(){
		return file;
	}

}