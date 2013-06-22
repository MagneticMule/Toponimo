package org.toponimo.client.models;

import org.toponimo.client.R;


public class Location {
	

	private int id;
	

    private Double lat;
	

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
