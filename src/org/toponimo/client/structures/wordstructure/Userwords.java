
package org.toponimo.client.structures.wordstructure;

import java.util.List;

public class Userwords{
   	private String _id;
   	private String userid;
   	private String word;

 	public String get_id(){
		return this._id;
	}
	public void set_id(String _id){
		this._id = _id;
	}
 	public String getUserid(){
		return this.userid;
	}
	public void setUserid(String userid){
		this.userid = userid;
	}
 	public String getWord(){
		return this.word;
	}
	public void setWord(String word){
		this.word = word;
	}
}
