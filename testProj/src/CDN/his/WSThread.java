/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: WSThread.java
 * @description: Processes Web Server socket input, and parses input for http messages.
 */

package CDN.his;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class WSThread extends Thread {
    
	protected Socket socket;
    private SendHTTPMessage http;
    private FileSystem fs;
    
    public WSThread(Socket clientSocket, FileSystem fs) {
        this.socket = clientSocket;
        this.fs = fs;
    }

    //main thread runs on web server processing input
    public void run() {
    	System.out.println("Client Connected...");

    	//Try/catch to retrieve socket input
        try {
        	
        	//get socket input
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            //initialize output stream to write to socket
            OutputStream os = socket.getOutputStream();
            http = new SendHTTPMessage(os);
            
           
            String str = ".";
            String request = "";
            
            //read incoming data from socket
            while (!str.equals("")){
              str = in.readLine();
              request += str + "\n";
            }
            
            System.out.println(request);
            
            //parse input for http request
            parseHTTP(request);
            
            os.close();
            socket.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    //parses input for http strings
    private void parseHTTP(String request){
    	
    	boolean isvalid = false;
    	
    	//split request on newline
    	String[] lines = request.split("\n");
    	
    	
    	for(int i =0; i < lines.length; i++){
    		//check for GET request
    		if(lines[i].length() > 2 && lines[i].substring(0, 3).equals("GET")){
    			isvalid = true;
    			
    			//split GET on blank
    			String[] get = lines[i].split(" ");
    			
    			//check for HTTP version
    			if(!get[2].equals(SendHTTPMessage.HTTPVERSION)){
    				http.sendMessage(505);
    			}else{
    				//send requested file
        			fs.printFile(get[1], http);
    			}
    		}
    	}
    	//the request is invalid
    	if(!isvalid){
    		http.sendMessage(400);
    	}
    }
    
}
