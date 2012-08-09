package org.toponimo.client.structures.placestructure;

import org.toponimo.client.R;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

public class Location {
	
	@DatabaseField(generatedId=true)
	private int id;
	
	@DatabaseField
    private Double lat;
	
    @DatabaseField
    private Double lng;

    public Double getLat() {
	return this.lat;
    }

    public void setLat(Double lat) {
	this.lat = lat;
    }

    public Double getLng() {
	return this.lng;
    }

    public void setLng(Double lng) {
	this.lng = lng;
    }
}
