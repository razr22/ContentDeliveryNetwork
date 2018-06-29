/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: ClientApplication.java
 * @description: Client Application provides the main UI for the client to interact with the CDN.
 */
package CDN.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import CDN.helperClasses.AddressDirectory;
import CDN.helperClasses.DNSQuery;
import CDN.helperClasses.PacketInfo;

public class ClientApplication {
	final static int MAX_SIZE = 65535;
	
	//helper function to create source directories for required files
	public static boolean createDir(String fname){
		File newdir = new File(fname);
		boolean result = false;
	
		if (!newdir.exists()) {
		    try{
		        newdir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        se.printStackTrace();
		    }
		}
		return result;	
	}
	
	//helper method to read data from file
	public static String readFile(String path) throws IOException{
		String data = "";
		BufferedReader buffer;
		String sCurrentLine;
		
		if(path != null){
			buffer = new BufferedReader(new FileReader(path));
			while ((sCurrentLine = buffer.readLine()) != null) {
				data +=sCurrentLine+"\n";
			}
		}
		
		return data;
	}
	
	//method allows local playback of retrieved file
	public static void playFile(String path) throws IOException{
		String filename = path;
	    String browser = "C:\\Program Files\\VideoLAN\\VLC\\vlc.exe";
	    
		if(System.getProperty("os.name").contains("Windows")){
			Runtime.getRuntime().exec(new String[] {browser, filename});
		}
	}
	
	//method retrieves specified file using a simple GET request
	private static PacketInfo getData(String wsIP, int wsPort, String url) {
	    
		//create HTTP GET request
	    String request = "GET "+ url+" HTTP/1.1\n\n";
	    
	    //Create final packet var
	    PacketInfo packetInfo = new PacketInfo();
	    
	    try {
	    	
	    	System.out.println("Retrieving: " + url);
	
			Socket socket = new Socket(wsIP, wsPort);
	
			InputStream socketIn = socket.getInputStream();
			OutputStream socketOut = socket.getOutputStream();
			
			//write the generated request to the socket for processing
			socketOut.write(request.getBytes());
			
			byte[] reply = new byte[MAX_SIZE];
			
			int count;
			boolean headerRead = false;
			String header ="";
			byte[] data = null;
			
			//creates path to store retrieved file
			String path = "retrieve/" + wsIP + wsPort;
			createDir(path);
			
			FileOutputStream out = new FileOutputStream(path+url);
			
			//while there is input to read
	        while ((count = socketIn.read(reply)) > 0) {
	        	
	        	//read header first
	        	if(!headerRead){
	        		//offset = 0
	        		String dataRead = new String(reply, 0, count);
	        		
	        		String[] lines = dataRead.split("\r\n", -1);
	        		
	        		int i = 0;
	        		//copy header to var
	        		while(!lines[i].equals("")){
	        			header += lines[i] + "\n";
	        			i++;
	        		}
	        		
	        		i++;
	        		//if there is still data left to be read after reading header
	        		if(i < lines.length){
	        			//write data
	        			data = lines[i].getBytes();
	        			out.write(data);
	        		}
	        		
	        		headerRead = true;
	        	}
	        	//if no header or past header, read/write message data
	        	else{ 
	        		data = reply;
	        		out.write(data, 0, count);
	        	}
	        	
	        }
	        //set packet header and save the file
	        packetInfo.setHeader(header);
	        if(data != null){
	        	packetInfo.setFile(path+url);
	        }
	        
	        out.close();
	        socketOut.close();
	        socketIn.close();
	        socket.close();
	
	    } catch (UnknownHostException e1) {
	    	System.err.println("Who dis? " + wsIP);
	        System.exit(1);
	
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.exit(1);
	    }
	    System.out.println("File Received: '"+ packetInfo.getFile() + "'");
	    
	    return packetInfo;
	}
			
	//method retrieves host IP from local DNS 
	public static String getIP(String ldnsIP, int ldnsPort, String host){
	  
		DNSQuery dnsReply = null;
		
	  try
	  {
	      DatagramSocket socket = new DatagramSocket();
	      
	      //get local DNS address from IP
	      InetAddress ldnsAddress = InetAddress.getByName(ldnsIP);
	     
	      //'V' indicates video file
	      String query = host+";V" ;
	      
	      //Use helper function to structure query data
	      DNSQuery queryOut = new DNSQuery("6", 0, query, "");
	      byte[] messageSend = queryOut.getQuery().getBytes();
	     
	      System.out.println("Contacting Local DNS server with query: "+queryOut.getQuery());
	     
	      //send message to ldns
	      DatagramPacket  dp = new DatagramPacket(messageSend, messageSend.length, ldnsAddress, ldnsPort);
	      socket.send(dp);
	      
	      //get reply
	      byte[] buffer = new byte[256];
	      DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
	      socket.receive(reply);
	      
	      //get IP address from reply
	      byte[] data = reply.getData();
	      String s = new String(data, 0, reply.getLength());
	      
	      //get answer from reply
	      dnsReply = new DNSQuery(s);
	      
	      socket.close();
	  }
	  catch(IOException e){
	      System.err.println("Error: " + e);
	  }
	  
	  //parse for answer
	  String answer = dnsReply.getAnswer();
	  String[] parse = answer.split(";");
	  
		System.out.println("Received answer from local DNS: " + answer);
		return parse[0];
	}
	
	//retrieves all URL links from index.html at web server
	public static String[] getURL() throws IOException {
		String links = "/index.html";
		System.out.println("Connecting to Web Server [" + AddressDirectory.WEBSERVERIP + ":" + AddressDirectory.WEBSERVERPORT + "]");
		PacketInfo p = getData(AddressDirectory.WEBSERVERIP, AddressDirectory.WEBSERVERPORT, links);
		
		String path = p.getFile();
		String data = readFile(path);
		
		String[] options = data.split("\n");
		/*for(int i = 0; i < options.length; i++){
			System.out.println("Link " + (i+1) + ": " +options[i]);
		}*/
		
		return options;
	}
	
	public static void main(String[] args) throws IOException {
		//get user input
		System.out.print("Enter Content File (1,2,3,4): ");
		Scanner in = new Scanner(System.in);
		int contentID = in.nextInt();
		in.close();
		
		String opt[] = getURL();
		URL urlSelected = new URL(opt[contentID-1]); //offset because links starts from 1
		
		System.out.println("URL Selected: " + urlSelected);
		
		//get IP address of chosen file
		String ipOfHost = getIP(AddressDirectory.LOCALDNSIP, AddressDirectory.LOCALDNSPORT, urlSelected.getHost());
		
		//get the file from the selected IP address
		PacketInfo p = getData(ipOfHost, AddressDirectory.CDNSERVERPORT, urlSelected.getFile());
	    
		String path = p.getFile();
		playFile(path);
	}
}
