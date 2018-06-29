/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: FileSystem.java
 * @description: File system allows auditing of source file location and printing to socket or os.
 */

package CDN.his;

import java.io.File;

public class FileSystem {

	private String dir;
	final static String DEFAULT_FILE = "/index.html";
	
	//getters and setters
	public FileSystem(String dir){
		this.dir = dir;
	}
	
	public String getDir(){
		return dir;
	}
	
	//printfile will send the file as HTTP message to OS if it exists
	public void printFile(String path, SendHTTPMessage http){
		//if root dir
		if(path.equals("/")){
			path = DEFAULT_FILE;
		}
		
		File f = new File(dir+path);
		//if file exists and is not a directory
		if(f.exists() && !f.isDirectory()){
			
			//200 ok
			http.sendMessage(200, f);
		}else{
			//404 file not found
			http.sendMessage(404);
		}
	}
}
