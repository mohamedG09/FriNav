package com.example.mapstask;

public class User {

    private String nickname;
    private String email;
    private String phoneNumber;
    private String password;
    private double lan;
    private double lon;
    private String photoUrl;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public User() {
    }

    public User(String nickname, String email, String phoneNumber, String password) {
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public double getLan() {
        return lan;
    }

    public void setLan(double lan) {
        this.lan = lan;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public boolean setEmail(String email) {
        String tempEmail = email.trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.matches(emailPattern)) {
            this.email = tempEmail;
            return true;

        }
        return false;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean setPhoneNumber(String phoneNumber) {


        if(phoneNumber.length() != 11)
            return false;

        this.phoneNumber = phoneNumber;
        return true;
    }

    public String getPassword() {
        return password;
    }

    public boolean setPassword(String password) {

        if(password.length() < 6)
            return false;

        this.password = password;
        return true;
    }
}
