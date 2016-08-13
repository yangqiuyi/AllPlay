package com.example.model;
/*
*
* creator: {
         uid: 2241746,
         nick: "Anna Garrison",
         portrait: "9c1564d2ed6906028e1b248809b19dcf6852d3d8_p_2241746.jpg",
         gender: 0
       }*/


import org.json.JSONObject;

public class UserInfoModel {

    public String uid;
    public String nick;
    public String portrait;
    public int gender;


    public void updateFromJson(JSONObject jsonObject) {
        if (jsonObject == null){
            return;
        }
        gender = jsonObject.optInt("gender",gender);
        nick = jsonObject.optString("nick", nick);
        portrait = jsonObject.optString("portrait",portrait);

    }

    public String getNick() {
        return nick;
    }

    public String getPortrait() {
        return portrait;
    }

    public int getGender() {
        return gender;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }




}
