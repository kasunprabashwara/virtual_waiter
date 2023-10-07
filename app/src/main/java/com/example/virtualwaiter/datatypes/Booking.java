package com.example.virtualwaiter.datatypes;


import com.google.firebase.Timestamp;

public class Booking {
    private String name;
    private Integer tableID;
    private String key;
    private Timestamp dateTime;


    public Booking(String name, Integer tableID,String key, Timestamp dateTime){
        this.name = name;
        this.tableID = tableID;
        this.key = key;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public Integer getTableID() {
        return tableID;
    }

    public String getKey() {
        return key;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }
}
