package com.magneticmule.toponimo.client.structures.definitionstructure;

import java.util.List;

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
