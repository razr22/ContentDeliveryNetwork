/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: AuthDNSHers.java
 * @description: *File very similar to AuthDNSHis* Class parses for DNS records on herscdn auth dns server.
 */

package CDN.hers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import CDN.helperClasses.AddressDirectory;
import CDN.helperClasses.DNSQuery;
import CDN.helperClasses.DNSRecord;

public class AuthDNSHers {
	//retrieve dns record from given host and list of records
	public static DNSRecord findDNS(String host, ArrayList<DNSRecord> records){
		for(DNSRecord r : records){
			if(r.getName().equals(host)){
				//if type is A
				if(r.getType().equals("A")){
					return r;
				}
				//if type is R
				else if(r.getType().equals("R")){
					return r;
				}
				//else further unwrap DNS records till a base case is hit
				else{
					return findDNS(r.getVal() ,records);
				}	
			}
		}
		return null;
	}
		
	public static void main(String[] args) {
		//create record list
		ArrayList<DNSRecord> records = new ArrayList<DNSRecord>(); 
		
		//add records for address and host, pointing to Content Server IP
		records.add(new DNSRecord("herCDN.com", "www.herCDN.com", "CN"));
		records.add(new DNSRecord("www.herCDN.com", AddressDirectory.CDNSERVERIP, "A"));	
		
		DatagramSocket sock = null;
        
        try
        {
        	
            //if only one thread
        	if(AddressDirectory.ONEMACHINE){
        		sock = new DatagramSocket(AddressDirectory.HERDNSPORT);
        	}else{
        		//else use multithread
        		sock = new DatagramSocket(AddressDirectory.ADNSPORT);
        	}
        	
            //buffer for incoming data
            byte[] buffer = new byte[1024];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            
            System.out.println("HerDNS Server socket created. Waiting for incoming data...");
             
            //while data is received
            while(true)
            {
                sock.receive(incoming);
                byte[] data = incoming.getData();
                
                //create a query
                String s = new String(data, 0, incoming.getLength());
                DNSQuery queryRecord = new DNSQuery(s);
                
                
                //get sender info
                String senderAddress = incoming.getAddress().getHostAddress();
                int senderPort = incoming.getPort();
                
                InetAddress queryIP;
                byte[] sendData;
                
                //get query info
            	String question = queryRecord.getQuestion();
            	String[] hostAndType = question.split(";");
            	
            	String qHost = hostAndType[0];
           
            	//parse for record
            	DNSRecord queryDNS = findDNS(qHost, records);
            	
            	//get dest address name
                queryIP = InetAddress.getByName(senderAddress);
                
                //construct answer query
                DNSQuery answer = new DNSQuery(queryRecord.getID(), 1, queryRecord.getQuestion(), queryDNS.getVal()+";A");
                
                sendData = answer.getQuery().getBytes();
                
                //send data
                DatagramPacket  datagram = new DatagramPacket(sendData , sendData.length, queryIP, senderPort);
                sock.send(datagram);
            }
        }
         
        catch(IOException e){
            System.err.println("Error: " + e);
        }
    }
}
