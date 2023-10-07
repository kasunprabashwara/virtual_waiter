package com.example.virtualwaiter.datatypes;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrderItem {
    private String name;
    private String image;
    private Integer price;
    private Integer quantity;
    private Integer totalPrice;
    private Integer tableID;
    private String status;
    private String notes;
    private String orderId;
    private String sessionID;
    private OrderIdCallback callback;

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public Integer getTableID() {
        return tableID;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public OrderIdCallback getCallback() {
        return callback;
    }

    public interface OrderIdCallback {
        void onOrderIdReceived(String orderId);
    }

    public OrderItem(String name, Integer price, Integer quantity,Integer tableID, String notes ,String image) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = price * quantity;
        this.setStatus("Ordered");
        this.tableID = tableID;
        this.notes = notes;
        this.image = image;
    }

    public void firebaseUpload() {
        Log.d("FirestoreData", "Uploading to Firestore "+ this.getName());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("sessionID", this.getSessionID());
        data.put("name", this.getName());
        data.put("price", this.getPrice());
        data.put("quantity", this.getQuantity());
        data.put("totalPrice", this.getTotalPrice());
        data.put("status", this.getStatus());
        data.put("tableID", this.getTableID());
        data.put("notes", this.getNotes());
        data.put("url", this.getImage());
        db.collection("orders").add(data).addOnSuccessListener(documentReference -> {
            this.setOrderId(documentReference.getId());
            getCallback().onOrderIdReceived(this.getOrderId());
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error adding document", e);
        });
    }

    public void setCallback(OrderIdCallback callback) {
        this.callback = callback;
    }


}
