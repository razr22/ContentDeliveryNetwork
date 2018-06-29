/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: DNSRecord.java
 * @description: DNSRecord provides structure for the storage and retrieval of DNS Records.
 */

package CDN.helperClasses;

public class DNSRecord {

	private String name;
	private String val;
	private String type;
	
	//constructor holds name,type, and value for the record.
	public DNSRecord(){
		name = "";
		val = "";
		type = "";
	}
	
	//instantiate variables for the instance of object
	public DNSRecord(String name, String value, String type){
		this.name = name;
		this.val = value;
		this.type = type;
	}
	
	//getter methods
	public String getName(){
		return(this.name);
	}
	
	public String getVal(){
		return(this.val);
	}
	
	public String getType(){
		return(this.type);
	}
	
}
