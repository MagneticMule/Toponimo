package org.toponimo.client.models.webster;

import java.util.ArrayList;


public class WebsterEntries{
   	private int total_words;
   	private ArrayList<Word> words;

 	public int getTotal_words(){
		return this.total_words;
	}
	public void setTotal_words(int total_words){
		this.total_words = total_words;
	}
 	public ArrayList<Word> getWords(){
		return this.words;
	}
	public void setWords(ArrayList<Word> words){
		this.words = words;
	}
}
