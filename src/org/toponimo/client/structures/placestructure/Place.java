package org.toponimo.client.structures.placestructure;

import java.util.List;

public class Place {
    private List<Results> results;
    private String status;

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
