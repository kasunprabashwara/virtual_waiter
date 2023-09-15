package com.example.virtualwaiter.datatypes;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrderItem {
    public String name;
    public String image;
    public Integer price;
    public Integer quantity;
    public Integer totalPrice;
    public Integer tableID;
    public String status;
    public String notes;
    public String orderId;
    public String sessionID;
    private OrderIdCallback callback;

    public interface OrderIdCallback {
        void onOrderIdReceived(String orderId);
    }

    public OrderItem(String name, Integer price, Integer quantity,Integer tableID, String notes ,String image) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = price * quantity;
        this.status = "Ordered";
        this.tableID = tableID;
        this.notes = notes;
        this.image = image;

        //upload to firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("name", this.name);
        data.put("price", this.price);
        data.put("quantity", this.quantity);
        data.put("totalPrice", this.totalPrice);
        data.put("status", this.status);
        data.put("tableID", this.tableID);
        data.put("notes", this.notes);
        data.put("url", this.image);
        db.collection("orders").add(data).addOnSuccessListener(documentReference -> {
            this.orderId = documentReference.getId();
            callback.onOrderIdReceived(this.orderId);
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error adding document", e);
        });
    }

    public void setCallback(OrderIdCallback callback) {
        this.callback = callback;
    }


}
