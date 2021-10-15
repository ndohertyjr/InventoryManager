package com.zybooks.neildohertyoptionone;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {


    private String mUser;
    private String mPassword;
    private String mDateAdded;
    private String mPhoneNumber;
    private int mMinQuantity;

    public User() {}



    public User(String user, String password) {
        mUser = user;
        mPassword = password;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        mDateAdded = dateFormat.format(date);
        mPhoneNumber = "";
        mMinQuantity = 0;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        mUser = user;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getDateAdded() {
        return mDateAdded;
    }

    public void setDate(String date) {
        mDateAdded = date;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public int getmMinQuantity() {
        return mMinQuantity;
    }

    public void setmMinQuantity(int quantity) {
        this.mMinQuantity = quantity;
    }
}
