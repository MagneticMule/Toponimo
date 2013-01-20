
package org.toponimo.client.models;

import java.util.List;
import org.toponimo.client.R;

public class Userword{
   	private String _id;
   	private String userid;
   	private String word;
   	private List<Image> images;
   	private List<Sound> sounds;
   	private List<Drawing> drawings;


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
 	public List<Image> getImages() {
		return this.images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
}
