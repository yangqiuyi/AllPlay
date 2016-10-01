package com.example.model;


import java.util.ArrayList;

public class ListViewAdapterModel {
    public   int TYPE ;
    public ArrayList<LiveItemModel> list;

    public ListViewAdapterModel(int TYPE , ArrayList<LiveItemModel> list){
        this.TYPE = TYPE;
        this.list =list;
    }


}
