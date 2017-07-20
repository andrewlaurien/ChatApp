package com.andrewlaurien.chatApp.model;

/**
 * Created by andrewlaurienrsocia on 09/05/2017.
 */

public class User {

    private String UserID;
    private String firstName;
    private String lastName;
    private String middleName;
    private String codeName;
    private String profileLink;

    public User() {
        //Default constructor required for calls DataSnapshot.getValue(User.class);
    }

    public User(String userID, String firstName, String middleName, String lastName, String codeName, String profilelink) {
        this.UserID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.codeName = codeName;
        this.profileLink = profilelink;
    }


    public String getUserID() {
        return UserID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getProfileLink() {
        return profileLink;
    }
}
