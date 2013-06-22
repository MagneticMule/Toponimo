package org.toponimo.client.models;

import java.util.List;

import org.toponimo.client.R;


public class Place {
	

	private int id;
	

    private List<Results> results;
    
	// Do not persist
    private String status;
    
    //Constructor (no args) for ORMLite visibility
    Place() {
    	
    }

    public List<Results> getResults() {
	return this.results;
    }

    public void setResults(List<Results> results) {
	this.results = results;
    }

    public String getStatus() {
	return this.status;
    }

    public void setStatus(String status) {
	this.status = status;
    }
}
