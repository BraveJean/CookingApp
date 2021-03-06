package com.cookingapp;

public class UserInformation {

    public String userName;
    public String email;
    public String password;

    public UserInformation() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public UserInformation(String userName, String email, String password) {
        this.email = email;
        this.userName = userName;
        this.password = password;
    }
}
