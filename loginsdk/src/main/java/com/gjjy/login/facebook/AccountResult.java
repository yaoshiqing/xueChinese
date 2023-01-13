package com.gjjy.login.facebook;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountResult {
    private String id;
    private String name;
    private String photoUrl;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;

    public AccountResult() {}
    public AccountResult(JSONObject json) {
        if( json == null ) return;
        try {
            id = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            name = json.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            photoUrl = json.getJSONObject("picture")
                    .getJSONObject("data")
                    .getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            firstName = json.getString("first_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            lastName = json.getString("last_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            email = json.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            gender = json.getString("gender");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "AccountResult{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    @NonNull


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}