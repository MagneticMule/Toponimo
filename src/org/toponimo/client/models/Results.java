package org.toponimo.client.models;

import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.toponimo.client.R;

public class Results {
	
	@DatabaseField(id=true)
    private String id; //poor choice of naming here. This is the hashed ID of the location from the remote server.
	
	@DatabaseField
    private Location location;
	
	@DatabaseField
    private String name;
	
	@ForeignCollectionField
    private List<String> objects;
	
    @ForeignCollectionField
    private List<String> phrases;
    
    @ForeignCollectionField
    private List<String> types;
    
    @ForeignCollectionField
    private String vicinity;
    
    @ForeignCollectionField
    private List<String> words;
    
    @ForeignCollectionField
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
