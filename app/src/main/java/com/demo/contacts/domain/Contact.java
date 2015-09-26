package com.demo.contacts.domain;

import android.net.Uri;

/**
 * Created by and on 26.09.15.
 */
public class Contact {
    private String email;

    public Uri getThumbUri() {
        return thumbUri;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    private String displayName;

    public void setThumbUri(Uri thumbUri) {
        this.thumbUri = thumbUri;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private String country;
    private String phoneNumber;
    private String address;
    private String street;
    private String city;
    private String postcode;
    private Uri  thumbUri;

    public String getmContactID() {
        return mContactID;
    }

    public void setmContactID(String mContactID) {
        this.mContactID = mContactID;
    }

    private String  mContactID;
}
