/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: SendHTTPMessage.java
 * @description: Class prepares http message, appends file data and sends prepared data to output stream.
 */

package CDN.his;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class SendHTTPMessage {

	private OutputStream os;
	
	private static final String DIRTOHTTPCODEFILES = "codes";
	private static final String CRLF = "\r\n";
	private HashMap<Integer, String> httpCodes = new HashMap<Integer, String>();
	public static final String HTTPVERSION = "HTTP/1.1";
	
	//push codes onto a hash map for later retrieval
	public SendHTTPMessage(OutputStream os){
		this.os = os;
		
		httpCodes.put(200, "OK");
		httpCodes.put(400, "Bad Request");
		httpCodes.put(404, "Not Found");
		httpCodes.put(505, "HTTP Version Not Supported");
	}
	
	public void sendMessage(int httpCode, File f){
		
		//create header message
		String codeMessage = httpCodes.get(httpCode);
		String header= "";
		header += HTTPVERSION + " " + httpCode + " " + codeMessage + CRLF;
		header += "Content-Length: " + f.length() + CRLF;
		header += CRLF;
		
		try {
			//write header to output stream
			os.write(header.getBytes());
			
			//read file input
			byte[] bytes = new byte[(int)f.length()];
			FileInputStream finput = new FileInputStream(f);
			
			int count;
			//write file contents to output stream
	        while ((count = finput.read(bytes)) > 0) {
	            os.write(bytes, 0, count);
	        }
	        
	        finput.close();
	        os.flush();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	//reply with http code
	public void sendMessage(int httpCode){		
		File file = new File(DIRTOHTTPCODEFILES+"/"+httpCode+".html");
		sendMessage(httpCode, file);
	}
}
