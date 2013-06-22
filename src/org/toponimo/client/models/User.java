package org.toponimo.client.models;

public class User {

    private static String userName = null;
    private static String userId   = null;

    public User() {

    }

    public User(String _userName, String _userId) {
        this.userName = _userName;
        this.userId = _userId;
    }

}
