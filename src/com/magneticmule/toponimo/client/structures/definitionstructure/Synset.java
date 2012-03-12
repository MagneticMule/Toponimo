package com.magneticmule.toponimo.client.structures.definitionstructure;


public class Synset {
    private String definitions;
    private String lex;
    private String sample;
    private String synsetno;

    public String getDefinitions() {
	return this.definitions;
    }

    public void setDefinitions(String definitions) {
	this.definitions = definitions;
    }

    public String getLex() {
	return this.lex;
    }

    public void setLex(String lex) {
	this.lex = lex;
    }

    public String getSample() {
	return this.sample;
    }

    public void setSample(String sample) {
	this.sample = sample;
    }

    public String getSynsetno() {
	return this.synsetno;
    }

    public void setSynsetno(String synsetno) {
	this.synsetno = synsetno;
    }
}
