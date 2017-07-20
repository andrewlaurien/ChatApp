package com.andrewlaurien.chatApp.model;

/**
 * Created by andrewlaurienrsocia on 18/07/2017.
 */

public class Clan {

//Logo
//Founder
//Clan Name
//Interest
//Date Created
//Motto
//Location


    private String clanID;
    private String clanName;
    private String clanFounder;
    private String clanInterest;
    private String clanLocation;
    private double clanLatitude;
    private double clanLongitude;
    private String clanMotto;
    private long dateTime;

    public Clan() {

    }

    public Clan(String clanID, String clanName, String clanFounder, String clanInterest,
                String location, double clanLatitude, double clanLongitude, String clanMotto, long dateTime) {
        this.clanID = clanID;
        this.clanName = clanName;
        this.clanFounder = clanFounder;
        this.clanInterest = clanInterest;
        this.clanLocation = location;
        this.clanLatitude = clanLatitude;
        this.clanLongitude = clanLongitude;
        this.clanMotto = clanMotto;
        this.dateTime = dateTime;


    }

    public String getClanID() {
        return clanID;
    }

    public String getClanName() {
        return clanName;
    }

    public String getClanFounder() {
        return clanFounder;
    }

    public String getClanInterest() {
        return clanInterest;
    }

    public String getClanLocation() {
        return clanLocation;
    }

    public double getClanLatitude() {
        return clanLatitude;
    }

    public double getClanLongitude() {
        return clanLongitude;
    }

    public String getClanMotto() {
        return clanMotto;
    }

    public long getDateTime() {
        return dateTime;
    }
}
