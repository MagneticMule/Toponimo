package org.toponimo.client.models;

import java.util.List;


import org.toponimo.client.R;

public class Results {

    private String id; //poor choice of naming here. This is the hashed ID of the location from the remote server.
	

    private Location location;

    private String name;
	

    private List<String> objects;
	
    
    private List<String> phrases;
    
    
    private List<String> types;
    
    
    private String vicinity;
    
    
    private List<String> words;
    
    
    private List<Image> images;

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public Location getLocation() {
	return this.location;
    }

    public void setLocation(Location location) {
	this.location = location;
    }

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<String> getObjects() {
	return this.objects;
    }

    public void setObjects(List<String> objects) {
	this.objects = objects;
    }

    public List<String> getPhrases() {
	return this.phrases;
    }

    public void setPhrases(List<String> phrases) {
	this.phrases = phrases;
    }

    public List<String> getTypes() {
	return this.types;
    }

    public void setTypes(List<String> types) {
	this.types = types;
    }

    public String getVicinity() {
	return this.vicinity;
    }

    public void setVicinity(String vicinity) {
	this.vicinity = vicinity;
    }

    public List<String> getWords() {
	return this.words;
    }

    public void setWords(List<String> words) {
	this.words = words;
    }

    public void addWord(String word) {
	this.words.add(word.toString());
    }

    public List<Image> getImages() {
	return images;
    }

    public void setImages(List<Image> images) {
	this.images = images;
    }
}
