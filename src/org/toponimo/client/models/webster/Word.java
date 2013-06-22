
package org.toponimo.client.models.webster;

import java.util.ArrayList;



public class Word{
   	private String _id;
   	private ArrayList<Definition> definitions;
   	private String inflected_forms;
   	private String lemma;
   	private String part_of_speach;
   	private String pronounciation;
   	private String syllables;

 	public String get_id(){
		return this._id;
	}
	public void set_id(String _id){
		this._id = _id;
	}
 	public ArrayList<Definition> getDefinitions(){
		return this.definitions;
	}
	public void setDefinitions(ArrayList<Definition> definitions){
		this.definitions = definitions;
	}
 	public String getInflected_forms(){
		return this.inflected_forms;
	}
	public void setInflected_forms(String inflected_forms){
		this.inflected_forms = inflected_forms;
	}
 	public String getLemma(){
		return this.lemma;
	}
	public void setLemma(String lemma){
		this.lemma = lemma;
	}
 	public String getPart_of_speach(){
		return this.part_of_speach;
	}
	public void setPart_of_speach(String part_of_speach){
		this.part_of_speach = part_of_speach;
	}
 	public String getPronounciation(){
		return this.pronounciation;
	}
	public void setPronounciation(String pronounciation){
		this.pronounciation = pronounciation;
	}
 	public String getSyllables(){
		return this.syllables;
	}
	public void setSyllables(String syllables){
		this.syllables = syllables;
	}
	
	 @Override
         public boolean equals(Object o) {
             if (o instanceof Word) {
                 return ((Word) o)._id == _id;
             }
             return false;
         }
}
