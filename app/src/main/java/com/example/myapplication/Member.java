package com.example.myapplication;

import java.lang.reflect.Array;

public class Member {
    int adNum;
    String latitude;
    String longitude;
    String memberEmail;
    String memberName;
    String memberPhone;
    String userUID;
    Array favorites;

    public Member(String userUID2, String memberEmail2, String memberName2, String memberPhone2, String latitude2, String longitude2, int adNum2) {
        this.userUID = userUID2;
        this.memberEmail = memberEmail2;
        this.memberName = memberName2;
        this.memberPhone = memberPhone2;
        this.latitude = latitude2;
        this.longitude = longitude2;
        this.adNum = adNum2;
    }

    public Member(String memberId, String memberName2, String memberTel) {
        this.memberEmail = memberId;
        this.memberName = memberName2;
        this.memberPhone = memberTel;
        this.latitude = "";
        this.longitude = "";
        this.adNum = 0;
        this.userUID = "";
    }

    public Member(String memberUID, String memberId, String memberName2, String memberTel) {
        this.memberEmail = memberId;
        this.memberName = memberName2;
        this.memberPhone = memberTel;
        this.latitude = "";
        this.longitude = "";
        this.adNum = 0;
        this.userUID = memberUID;
    }

    public Member() {
        this.memberEmail = "";
        this.memberName = "";
        this.memberPhone = "";
        this.latitude = "";
        this.longitude = "";
        this.adNum = 0;
        this.userUID = "";
    }

    public String getUserUID() {
        return this.userUID;
    }

    public void setUserUID(String userUID2) {
        this.userUID = userUID2;
    }

    public String getMemberEmail() {
        return this.memberEmail;
    }

    public void setMemberEmail(String memberId) {
        this.memberEmail = memberId;
    }

    public String getMemberName() {
        return this.memberName;
    }

    public void setMemberName(String memberName2) {
        this.memberName = memberName2;
    }

    public String getMemberPhone() {
        return this.memberPhone;
    }

    public void setMemberPhone(String memberTel) {
        this.memberPhone = memberTel;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude2) {
        this.latitude = latitude2;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude2) {
        this.longitude = longitude2;
    }

    public int getAdNum() {
        return this.adNum;
    }

    public void setAdNum(int adNum2) {
        this.adNum = adNum2;
    }
}