package com.llawl.tristonpang.intheloop;

public class UserData {
    private String mName;
    private String mRC;
    private String mContactNum;
    private String mEmail;

    public UserData(String name, String RC, String contactNum, String email) {
        mName = name;
        mRC = RC;
        mContactNum = contactNum;
        mEmail = email;
    }

    public UserData() {
    }

    public String getName() {
        return mName;
    }

    public String getRC() {
        return mRC;
    }

    public String getContactNum() {
        return mContactNum;
    }

    public String getEmail() {
        return mEmail;
    }
}
