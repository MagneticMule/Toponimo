package com.magneticmule.toponimo.client.wordstructure;


import java.util.List;

public class Results{
   	private String partOfSpeech;
   	private Number score;
   	private String sourceDictionary;
   	private String text;
   	private String word;

 	public String getPartOfSpeech(){
		return this.partOfSpeech;
	}
	public void setPartOfSpeech(String partOfSpeech){
		this.partOfSpeech = partOfSpeech;
	}
 	public Number getScore(){
		return this.score;
	}
	public void setScore(Number score){
		this.score = score;
	}
 	public String getSourceDictionary(){
		return this.sourceDictionary;
	}
	public void setSourceDictionary(String sourceDictionary){
		this.sourceDictionary = sourceDictionary;
	}
 	public String getText(){
		return this.text;
	}
	public void setText(String text){
		this.text = text;
	}
 	public String getWord(){
		return this.word;
	}
	public void setWord(String word){
		this.word = word;
	}
}