package com.example.virtualwaiter.datatypes;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Session {
    public ArrayList<String> orderedItems;
    public Integer tableID;
    public String sessionID;
    public Review review;
    public Integer totalBill;

    private totalBillCallback callback;

    public interface totalBillCallback {
        void onTotalBillReceived(Integer totalBill);
    }

    public Session(OrderItem orderItem){
        this.tableID = orderItem.tableId;
        this.orderedItems = new ArrayList<>();
        this.orderedItems.add(orderItem.orderId);
        this.review = null;
        this.totalBill = orderItem.totalPrice;
        //upload to firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> data = new HashMap<>();
        data.put("orders", Arrays.asList(orderItem.orderId));
        data.put("tableID", this.tableID);
        data.put("totalBill", this.totalBill);
        db.collection("sessions").add(data).addOnSuccessListener(documentReference -> {
            this.sessionID = documentReference.getId();
            Log.d("FirestoreData", "DocumentSnapshot added with ID: " + this.sessionID);
        }).addOnFailureListener(e -> {
            this.sessionID = null;
            Log.d("FirestoreData", "Error adding document", e);
        });
    }
    public void addOrder(OrderItem orderItem){
        this.orderedItems.add(orderItem.orderId);
        for(String item: this.orderedItems){
            Log.d("FirestoreData", item);
        }
        this.totalBill += orderItem.totalPrice;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> data = new HashMap<>();
        data.put("orders", this.orderedItems);
        data.put("tableID", this.tableID);
        data.put("totalBill", this.totalBill);
        db.collection("sessions").document(this.sessionID).update(data).addOnSuccessListener(documentReference -> {
            Log.d("FirestoreData", "DocumentSnapshot successfully updated!");
            callback.onTotalBillReceived(this.totalBill);
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error updating document", e);
        });
    }
    public void setCallback(totalBillCallback callback) {
        this.callback = callback;
    }
}
