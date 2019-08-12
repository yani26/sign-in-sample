package com.example.signin_sample;


public class User {
    public String phone;
    public String nickname;
    public String pushuserid;
    public String pushchannelid;
    public String modelname;
    public String emergency;


    public User(String phone, String nickname, String pushuserid, String pushchannelid, String modelname) {
        this.phone = phone;
        this.nickname = nickname;
        this.pushuserid = pushuserid;
        this.pushchannelid = pushchannelid;
        this.modelname = modelname;
        this.emergency = "NO";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPushuserid() { return pushuserid;}

    public String getPushchannelid() { return pushchannelid; }

    public String getModelname(){ return modelname; }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmergency(String y){
        this.emergency = y; // y = YES or NO
    }

    public String getEmergency() { return emergency;}
}
