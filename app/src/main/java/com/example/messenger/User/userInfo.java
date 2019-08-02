package com.example.messenger.User;

public class userInfo {
    public String nickname;
    public String email;
    public String userUID;

    public userInfo() {
    }

    public userInfo(String _nickname, String _email, String _userUID) {
        this.nickname = _nickname;
        this.email = _email;
        this.userUID = _userUID;
    }
}
