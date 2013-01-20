package org.toponimo.client;

import org.toponimo.client.R;
import org.toponimo.client.ui.LoginCheckActivity;
import org.toponimo.client.ui.JournalActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class User {

    private String firstName = null;
    private String lastName = null;
    private String userId = null;
    private int status;

    
    public String getFirstName() {
	return this.firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return this.lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getId() {
	return this.userId;
    }

    public void setId(String id) {
	this.userId = id;
    }


    public int getStatus() {
	return this.status;
    }

    public void setStatus(int status) {
	this.status = status;
    }



}
