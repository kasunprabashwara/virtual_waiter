package com.example.virtualwaiter.datatypes;

import java.util.ArrayList;

public class Session {
    public ArrayList<OrderItem> orderedItems;
    public Integer tableID;
    public Review review;
    public Integer totalBill;

    public Session(Integer tableID){
        this.tableID = tableID;
        this.orderedItems = new ArrayList<>();
        this.review = null;
        this.totalBill = 0;
    }
}
