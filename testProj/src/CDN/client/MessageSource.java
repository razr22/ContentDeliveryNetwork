/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: MessageSource.java
 * @description: Message Source holds information about the query in question.
 */
package CDN.client;

import CDN.helperClasses.DNSQuery;

public class MessageSource {
	
	private String ipOfSender;
	private int portOfSender;
	private DNSQuery query;
	
	//set ip and port of sender along with the associated question.
	public MessageSource(String ipOfSender, int portOfSender, DNSQuery question){
		this.ipOfSender = ipOfSender;
		this.portOfSender = portOfSender;
		this.query = question;
	}

	//getter methods
	public String getIpOfSender() {
		return ipOfSender;
	}

	public int getPortOfSender() {
		return portOfSender;
	}

	public DNSQuery getQuery() {
		return query;
	}
}
