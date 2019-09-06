package com.example.messenger.User;

import com.google.firebase.database.Exclude;

public class userInfo {
    public String nickname = "No Name";
    public String email = "No Email";
    @Exclude
    private String userUID;
    public userInfo() {
    }

    public userInfo(String _nickname, String _email, String _userUID) {
        this.nickname = _nickname;
        this.email = _email;
        this.userUID = _userUID;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    @Exclude
    public String getUserUID() {
        return userUID;
    }
}
