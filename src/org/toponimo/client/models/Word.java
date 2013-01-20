
package org.toponimo.client.models;

import java.util.List;
import org.toponimo.client.R;

public class Word{
   	private List<Dictionaryword> dictionarywords;
   	private List<Userword> userwords;

 	public List<Dictionaryword> getDictionarywords(){
		return this.dictionarywords;
	}
	public void setDictionarywords(List<Dictionaryword> dictionarywords){
		this.dictionarywords = dictionarywords;
	}
 	public List<Userword> getUserwords(){
		return this.userwords;
	}
	public void setUserwords(List<Userword> userwords){
		this.userwords = userwords;
	}
}
