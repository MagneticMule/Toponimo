package com.magneticmule.toponimo.client.structures.userstructure;

public class UserDetails {
    private int status;
    private String firstName;
    private String lastName;
    private String userId;

    public void setStatus(int _status) {
	status = _status;
    }

    public int getStatus() {
	return status;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getUserId() {
	return userId;
    }

    public void setUserId(String userId) {
	this.userId = userId;
    }

}
