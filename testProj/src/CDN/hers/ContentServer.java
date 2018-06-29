/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: ContentServer.java
 * @description: Content Server runs simple web server hosting content folder
 */

package CDN.hers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import CDN.helperClasses.AddressDirectory;
import CDN.his.FileSystem;
import CDN.his.WSThread;

//runs simple web server
public class ContentServer {
    public static void main(String args[]) throws IOException {
    	
        ServerSocket server = null;
        Socket socket = null;
        
        FileSystem fs = new FileSystem("CDN");
        try {
            server = new ServerSocket(AddressDirectory.CDNSERVERPORT);
            System.out.println("CDN Server Listening on port: " + server.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
       
        try {
        	while (true) {
        		socket = server.accept();
        		//processes input on server thread
        		new WSThread(socket, fs).start();
        	}
        } catch (IOException e) {
            System.out.println("I/O error: " + e);
        } finally{
        	server.close();
        }
    }
}
