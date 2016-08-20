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

    public int uid;
    public String nick;
    public String portrait;
    public int gender;
    public  String location;
    public int ulevel;
    public  String description;

    public void updateFromJson(JSONObject jsonObject) {
        if (jsonObject == null){
            return;
        }
        gender = jsonObject.optInt("gender",gender);
        nick = jsonObject.optString("nick", nick);
        portrait = jsonObject.optString("portrait",portrait);
        uid = jsonObject.optInt("uid",uid);
        gender = jsonObject.optInt("gender");
        description  = jsonObject.optString("description");
        ulevel  = jsonObject.optInt("ulevel");

    }





}
