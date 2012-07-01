package org.toponimo.client.utils;

import org.toponimo.client.R;

public final class StringBuilder {

    public String buildString(String... args) {

	String completed = new String("");

	for (String s : args) {
	    completed = completed.concat(s);
	}

	return completed;

    }

}
