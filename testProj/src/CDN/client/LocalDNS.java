/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: LocalDNS.java
 * @description: *Very similar to Auth DNS for his and her* LocalDNS
 */
package CDN.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import CDN.client.MessageSource;
import CDN.helperClasses.AddressDirectory;
import  CDN.helperClasses.DNSQuery;
import  CDN.helperClasses.DNSRecord;

public class LocalDNS {
	//given a host, find the dns with the ip
	public static DNSRecord findDNS(String host, ArrayList<DNSRecord> records){
		for(DNSRecord r : records){
			if(r.getName().equals(host)){
				//if type A
				if(r.getType().equals("A"))	{
					return r;
				//if type R
				}
				else if(r.getType().equals("R")){
					return r;
				}
				//else recursively unwrap record till base case A or R are hit.s
				else{
					return findDNS(r.getVal() ,records);
				}
				
			}
		}
		return null;
	}
		
	public static void main(String[] args) {
		//create records
		ArrayList<DNSRecord> records = new ArrayList<DNSRecord>(); 
		
		//populate records table
		records.add(new DNSRecord("herCDN.com", "NSherCDN.com", "NS"));
		records.add(new DNSRecord("NSherCDN.com", AddressDirectory.HERDNSIP, "A"));
		records.add(new DNSRecord("hiscinema.com", "NShiscinema.com", "NS"));
		records.add(new DNSRecord("NShiscinema.com", AddressDirectory.HISDNSIP, "A"));
		
		ArrayList<MessageSource> questions = new ArrayList<MessageSource>();
		
		DatagramSocket sock = null;
        
        try
        {
        	//create ldns socket
            sock = new DatagramSocket(AddressDirectory.LOCALDNSPORT);
             
            byte[] buffer = new byte[1024];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            
            System.out.println("LocalDNS Server socket created. Waiting for incoming data...");
             
            //while data being received
            while(true)
            {
                sock.receive(incoming);
                byte[] data = incoming.getData();
                
                //create record
                String s = new String(data, 0, incoming.getLength());
                DNSQuery queryRecord = new DNSQuery(s);
                
                
                //get sender info
                String senderAddress = incoming.getAddress().getHostAddress();
                int senderPort = incoming.getPort();
                
                InetAddress queryIP;
                int sendPort = 0;
                byte[] sendData;
                
                //if question: create new question
                if(queryRecord.isQuestion()){
                	MessageSource q = new MessageSource(senderAddress, senderPort,queryRecord);
                	questions.add(q);
                	
                	String question = queryRecord.getQuestion();
                	String[] hostType = question.split(";");
                	
                	String qHost = hostType[0];
                	
                	//get hostnames
                	String com = qHost.substring(qHost.lastIndexOf("."));
                	String hostName = qHost.substring(0, qHost.lastIndexOf("."));
                	String hostName2 = hostName.substring(hostName.lastIndexOf(".")+1);
                	
                	String fHostName = hostName2 + com;

                	//query dns for records
                	DNSRecord whoToQuery = findDNS(fHostName, records);
                	
                	//get address
                    queryIP = InetAddress.getByName(whoToQuery.getVal());
                    
                    if(AddressDirectory.ONEMACHINE){
                    	sendPort = AddressDirectory.HISDNSPORT;
                    }else{
                    	sendPort = AddressDirectory.ADNSPORT;
                    }
                    
                    sendData = queryRecord.getQuery().getBytes();
                    
                    System.out.println("Sending DNS query to: " + queryIP +":"+ sendPort + " with attached data: " + queryRecord.getQuery());
                    
                }
                //if answer received
                else {
                	
                	String answer = queryRecord.getAnswer();
                	String[] valType = answer.split(";"); // get value and type
                	if(valType[1].equals("NS")){
                		
                		//send query to the redirect link
                		System.out.println("NS reply received by: "+ senderAddress +":" + senderPort + " containing: " + answer);
                		
                		DNSRecord dnsToQuery = findDNS(valType[0], records);
                		
                		queryIP= InetAddress.getByName(dnsToQuery.getVal());
                		
                		//single or multi thread
                		if(AddressDirectory.ONEMACHINE){
                			sendPort = AddressDirectory.HERDNSPORT;
                		}else{
                			sendPort = AddressDirectory.ADNSPORT;
                		}
                		//Construct query
                		DNSQuery toSend = new DNSQuery(queryRecord.getID(), 0, valType[0]+";A", "");
                		
                		sendData = toSend.getQuery().getBytes();
                		
                		System.out.println("Sending DNS query to: " + queryIP + ":" + sendPort + " with data: " +queryRecord.getQuery());
                		
                	}
                	//type == A
                	else{
                		//send answer back to the question and remove from arrayList
                		System.out.println("An A reply received by: "+ senderAddress + ":" + sendPort + " containing: "+ answer);
                		
                		MessageSource qToSendBackTo= null;
                		
                		//remove question
                		for(MessageSource q : questions){
                    		if(q.getQuery().getID().equals(queryRecord.getID())){//if ids are equals
                    			qToSendBackTo = q;
                    			questions.remove(q);
                    			break;
                    		}
                    	}
                		
                		queryIP = InetAddress.getByName(qToSendBackTo.getIpOfSender());
                		sendPort = qToSendBackTo.getPortOfSender();
                		
                		sendData = queryRecord.getQuery().getBytes();
                		System.out.println("Sending reply back to client: "+ queryIP + ":" + sendPort + " with data: " + queryRecord.getQuery());
                	}
                }
                //send data
                DatagramPacket  datagram = new DatagramPacket(sendData , sendData.length, queryIP, sendPort);
                sock.send(datagram);
            }
        }
        
        catch(IOException e)
        {
            System.err.println("IOException " + e);
        }
    }
}
