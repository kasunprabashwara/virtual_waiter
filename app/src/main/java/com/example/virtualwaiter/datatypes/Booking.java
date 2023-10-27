package com.example.virtualwaiter.datatypes;


import com.google.firebase.Timestamp;

public class Booking {
    private String name;
    private Integer tableID;
    private String key;
    private Timestamp startTime;
    private Timestamp endTime;
    private String bookingID;


    public Booking(String name, Integer tableID,String key, Timestamp startTime, Timestamp endTime, String bookingID){
        this.name = name;
        this.tableID = tableID;
        this.key = key;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingID = bookingID;
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

    public Timestamp getStartTime() {
        return startTime;
    }
    public Timestamp getEndTime() { return endTime; }
    public String getBookingID() { return bookingID; }
}
