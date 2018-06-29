/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: AddressDirectory.java
 * @description: Contains Address and port information
 */

package CDN.helperClasses;

public class AddressDirectory {
	public final static String WEBSERVERIP = "localhost";
	public final static int WEBSERVERPORT = 5005;
	
	public final static String LOCALDNSIP = "localhost";
	public final static int LOCALDNSPORT = 5003;
	
	public final static String CDNSERVERIP = "localhost";
	public final static int CDNSERVERPORT = 5001;
	
	public final static String HISDNSIP = "localhost";
	public final static int HISDNSPORT = 5005;
	
	public final static String HERDNSIP = "localhost";
	public final static int HERDNSPORT = 5006;
	
	public final static boolean ONEMACHINE = true;
	public final static int ADNSPORT = 5007; // for two different machines/connections
	
}
