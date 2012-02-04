package com.magneticmule.toponimo.client.utils;

public final class StringBuilder {

    public String buildString(String... args) {

	String completed = new String("");

	for (String s : args) {
	    completed = completed.concat(s);
	}

	return completed;

    }

}
