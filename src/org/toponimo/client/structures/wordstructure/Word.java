
package org.toponimo.client.structures.wordstructure;

import java.util.List;

public class Word{
   	private List<Dictionarywords> dictionarywords;
   	private List<Userwords> userwords;

 	public List<Dictionarywords> getDictionarywords(){
		return this.dictionarywords;
	}
	public void setDictionarywords(List<Dictionarywords> dictionarywords){
		this.dictionarywords = dictionarywords;
	}
 	public List<Userwords> getUserwords(){
		return this.userwords;
	}
	public void setUserwords(List<Userwords> userwords){
		this.userwords = userwords;
	}
}
