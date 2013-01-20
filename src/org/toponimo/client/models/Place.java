package org.toponimo.client.models;

import java.util.List;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.toponimo.client.R;

@DatabaseTable(tableName = "places")
public class Place {
	
	@DatabaseField(generatedId=true)
	private int id;
	
	@DatabaseField
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
