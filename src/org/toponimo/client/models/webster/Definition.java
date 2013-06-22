package org.toponimo.client.models.webster;

import android.text.Html;
import android.text.Spanned;

public class Definition {
	private String definition;

	public Definition() {

	}

	public String getDefinition() {
		return this.definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	public Spanned getSpannedDefinition() {
		Spanned def = Html.fromHtml(definition);
		return def;
	}

}
