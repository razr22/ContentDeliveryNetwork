/*
 * @authors: Zain Quraishi, Sundar Manku 
 * @date: April 13th, 2018
 * @filename: DNSQuery.java
 * @description: DNSQuery provides structure for DNS questions and answers
 */

package CDN.helperClasses;

public class DNSQuery {

	private String identification;
	private int flag; //0 for questions and 1 for response (state of query)
	
	private String question;
	private String answer; 
	
	//constructor to provide basic info about query
	public DNSQuery(String id, int flag, String question, String answer){
		
		this.identification = id;
		this.flag = flag;
		this.question = question;
		this.answer = answer;
		
	}
	
	//set query info
	public DNSQuery(String data){
		
		String query = data;
		String delims = "[ ]+";
		String[] tokens = query.split(delims);
		
		//if query has answer
		if(tokens.length == 4){
			
			this.identification = tokens[0];
			this.flag = Integer.parseInt(tokens[1]);
			this.question = tokens[2];
			this.answer = tokens[3];
		}
		//else query has no answer yet
		if(tokens.length == 3){
			this.identification = tokens[0];
			this.flag = Integer.parseInt(tokens[1]);
			this.question = tokens[2];
			this.answer = "";
		}
		
	}
	
	//check for query properties
	public boolean isQuestion(){
		if(this.flag == 0){	return true;}
		else 
		return false;
	}
		
	public void addAnswer(String answer){
		this.answer = answer;
	}
	
	//Get query string
	public String getQuery(){
		
		//if no answer
		if(answer == ""){
			String query = this.identification + " " + this.flag + " " + this.question + " ";
			return query;
		//if there exists an answer
		} else {
			String query = this.identification + " " + this.flag + " " + this.question + " " + this.answer;
			return query;
		}
	}
	
	//getter methods
	public String getID(){
		return this.identification;
	}
	
	public int getFlag(){
		return this.flag;
	}
	
	public String getAnswer(){
		return this.answer;
	}
	
	public String getQuestion(){
		return this.question;
	}
	
}
