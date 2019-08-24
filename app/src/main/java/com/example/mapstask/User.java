package com.example.mapstask;

import android.widget.Toast;

public class User {

    private String nickname;
    private String email;
    private String phoneNumber;
    private String password;

    public User(String nickname, String email, String phoneNumber, String password) {
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
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


        if(password.length() != 11)
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
