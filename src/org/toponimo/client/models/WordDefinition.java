package org.toponimo.client.models;

import java.util.List;
import org.toponimo.client.R;

public class WordDefinition {
    private List<Synset> synset;
    private int total;

    public List<Synset> getSynset() {
	return this.synset;
    }

    public void setSynset(List<Synset> synset) {
	this.synset = synset;
    }

    public int getTotal() {
	return this.total;
    }

    public void setTotal(int total) {
	this.total = total;
    }
}
