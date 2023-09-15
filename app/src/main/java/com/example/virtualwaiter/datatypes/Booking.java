package com.example.virtualwaiter.datatypes;


import com.google.firebase.Timestamp;

public class Booking {
    public String name;
    public Integer tableID;
    public String key;
    public Timestamp dateTime;


    public Booking(String name, Integer tableID,String key, Timestamp dateTime){
        this.name = name;
        this.tableID = tableID;
        this.key = key;
        this.dateTime = dateTime;
    }
}
