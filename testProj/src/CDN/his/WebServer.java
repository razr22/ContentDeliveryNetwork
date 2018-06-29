/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: WebServer.java
 * @description: Web Server uses a simple web server framework to serve the folder containing the index.html
 */

package CDN.his;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import CDN.his.FileSystem;
import CDN.his.WSThread;

import CDN.helperClasses.AddressDirectory;

public class WebServer {
	
	 public static void main(String args[]) throws IOException {
		 
		//creating simple web server to listen to incoming messages.
        ServerSocket server = null;
        Socket socket = null;

        //create new dir to host index.html file.
        FileSystem fs = new FileSystem("public");
        
        try {
        	//creates socket based on address given.
            server = new ServerSocket(AddressDirectory.WEBSERVERPORT);
            System.out.println("Server Listening on port: " + server.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
       
        try {
        	while (true) {
        		//run thread class to parse socket for http input
        		socket = server.accept();
        		new WSThread(socket, fs).start();    			
        	}
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
        	server.close();
        }
    }
}
