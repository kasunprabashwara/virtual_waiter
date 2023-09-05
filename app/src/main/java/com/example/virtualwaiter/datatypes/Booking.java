package com.example.virtualwaiter.datatypes;


import com.google.firebase.Timestamp;

public class Booking {
    public String name;
    public Integer tableID;
    public String email;
    public Timestamp dateTime;


    public Booking(String name, Integer tableID,String email, Timestamp dateTime){
        this.name = name;
        this.tableID = tableID;
        this.email = email;
        this.dateTime = dateTime;
    }
}
