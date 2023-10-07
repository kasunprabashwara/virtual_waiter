package com.example.virtualwaiter;

import android.util.Log;

import com.example.virtualwaiter.datatypes.OrderItem;
import com.example.virtualwaiter.datatypes.Review;
import com.example.virtualwaiter.recycledview.OrderListAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class SessionManager {
    private ArrayList<String> orderedItemIDs; //wil be keeping two list for ease of access. this one is for firestore references
    private ArrayList<OrderItem> orderedItems;
    private Integer tableID;
    private String sessionID;
    private Review review;
    private Integer totalBill;
    private Boolean checkedOut;
    private Boolean paid;

    public FirebaseFirestore db;

    public Integer getTotalBill() {
        return totalBill;
    }

    public interface AddOrderCallback {
        void onAddOrderReceived(Integer totalBill,OrderItem orderItem);
    }
    private final AddOrderCallback addOrderCallback;
    private final OrderListAdapter orderListAdapter;

    public SessionManager(Integer tableID, AddOrderCallback addOrderCallback,OrderListAdapter orderListAdapter){
        this.sessionID="";
        this.tableID = tableID;
        this.orderedItemIDs = new ArrayList<>();
        this.orderedItems = new ArrayList<>();
        this.review = null;
        this.totalBill = 0;
        this.checkedOut = false;
        this.paid = false;
        this.addOrderCallback = addOrderCallback;
        this.orderListAdapter = orderListAdapter;
        this.db = FirebaseFirestore.getInstance();
    }



    //this is defined seperate from the constructor because the session ID is not known at the time of construction
    // so this will be called once the session ID is known
    private void setOrderListener() {
        db.collection("orders").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("FirestoreData", "order change listen failed.", error);
                return;
            }
            if(value == null){
                return;
            }
            for (DocumentChange change : value.getDocumentChanges()) {
                if(change.getDocument().getString("sessionID")==null){
                    continue;
                }
                Log.d("FirestoreData", "order change: " + change.getDocument().getData());
                if(change.getDocument().getString("sessionID").equals(this.sessionID)){
                    DocumentSnapshot documentSnapshot = change.getDocument();
                    switch (change.getType()) {
                        case ADDED:
                            String orderID = change.getDocument().getId();
                            String name = documentSnapshot.getString("name");
                            Integer price = documentSnapshot.getLong("price").intValue();
                            Integer quantity = documentSnapshot.getLong("quantity").intValue();
                            String status = documentSnapshot.getString("status");
                            String notes = documentSnapshot.getString("notes");
                            String image = documentSnapshot.getString("url");
                            OrderItem orderItem = new OrderItem(name, price, quantity, tableID, notes, image);
                            orderItem.setOrderId(orderID);
                            orderItem.setStatus(status);
                            orderItem.setSessionID(sessionID);
                            this.orderedItems.add(orderItem);
                            this.orderedItemIDs.add(orderID);
                            this.totalBill = this.getTotalBill() + orderItem.getTotalPrice();
                            addOrderCallback.onAddOrderReceived(this.getTotalBill(), orderItem);
                            break;
                        case MODIFIED:
                            String newStatus= documentSnapshot.getString("status");
                            for(OrderItem item: this.orderedItems){
                                if(item.getOrderId().equals(change.getDocument().getId())){
                                    item.setStatus(newStatus);
                                    orderListAdapter.notifyItemChanged(this.orderedItems.indexOf(item));
                                    break;
                                }
                            }
                            Log.d("FirestoreData", "Modified order: " + change.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d("FirestoreData", "Removed order: " + change.getDocument().getData());
                            break;
                    }
                }
            }
        });
    }

    public void firebaseDownload(String sessionID) {
        this.sessionID = sessionID;
        setOrderListener();
        db.collection("sessions").document(sessionID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                this.tableID = documentSnapshot.getLong("tableID").intValue();
                this.totalBill = documentSnapshot.getLong("totalBill").intValue();
                this.checkedOut = documentSnapshot.getBoolean("checkedOut");
                this.paid = documentSnapshot.getBoolean("paid");
                this.review = null;
                this.orderedItemIDs = (ArrayList<String>) documentSnapshot.get("orders");
                this.orderedItems = new ArrayList<>();
                for (String orderID : this.orderedItemIDs) {
                    db.collection("orders").document(orderID).get().addOnSuccessListener(documentSnapshot1 -> {
                        if (documentSnapshot1.exists()) {
                            Log.d("FirestoreData", "adding order download data: " + documentSnapshot1.getData());
                            String name = documentSnapshot1.getString("name");
                            Integer price = documentSnapshot1.getLong("price").intValue();
                            Integer quantity = documentSnapshot1.getLong("quantity").intValue();
                            String status = documentSnapshot1.getString("status");
                            Integer tableID = documentSnapshot1.getLong("tableID").intValue();
                            String notes = documentSnapshot1.getString("notes");
                            String image = documentSnapshot1.getString("url");
                            OrderItem orderItem = new OrderItem(name, price, quantity, tableID, notes, image);
                            orderItem.setOrderId(orderID);
                            orderItem.setStatus(status);
                            orderItem.setSessionID(sessionID);
                            this.orderedItems.add(orderItem);
                            this.orderedItemIDs.add(orderID);
                            this.totalBill = this.getTotalBill() + orderItem.getTotalPrice();
                            addOrderCallback.onAddOrderReceived(this.getTotalBill(),orderItem);
                            Log.d("FirestoreData", "order added to list: " + orderedItems.size());
                        } else {
                            Log.d("FirestoreData", "No such document");
                        }
                    }).addOnFailureListener(e -> {
                        Log.d("FirestoreData", "get failed with ", e);
                    });
                }
            } else {
                Log.d("FirestoreData", "No such document");
            }
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "get failed with ", e);
        });
    }

    public void addOrder(OrderItem orderItem){
        Log.d("FirestoreData", "Session ID: "+this.sessionID);
        this.orderedItems.add(orderItem);
        this.orderedItemIDs.add(orderItem.getOrderId());
        if(sessionID.equals("")){
            Map<String,Object> data = new HashMap<>();
            data.put("orders", Arrays.asList());
            data.put("tableID", this.tableID);
            data.put("totalBill", this.getTotalBill());
            data.put("checkedOut", this.checkedOut);
            data.put("paid", this.paid);
            db.collection("sessions").add(data).addOnSuccessListener(documentReference -> {
                this.sessionID = documentReference.getId();
                setOrderListener();
                for(OrderItem item: this.orderedItems){
                    item.setSessionID(this.sessionID);
                    db.collection("orders").document(item.getOrderId()).update("sessionID", this.sessionID).addOnSuccessListener(documentReference1 -> {
                        Log.d("FirestoreData", "sessionID added to order");
                    }).addOnFailureListener(e -> {
                        Log.d("FirestoreData", "Error adding sessionID to order", e);
                    });
                }
                updateSessionDetails(orderItem);
            }).addOnFailureListener(e -> {
                Log.d("FirestoreData", "Error creating session", e);
            });
        }
        else{
            orderItem.setSessionID(this.sessionID);
            updateSessionDetails(orderItem);
        }
    }


    //this function is called after the order is added to the database to update the session details with the new order
    private void updateSessionDetails(OrderItem orderItem) {
        this.totalBill = this.getTotalBill() + orderItem.getTotalPrice();
        Map<String,Object> data1 = new HashMap<>();
        data1.put("orders", this.orderedItemIDs);
        data1.put("totalBill", this.getTotalBill());
        db.collection("sessions").document(this.sessionID).update(data1).addOnSuccessListener(documentReference -> {
            addOrderCallback.onAddOrderReceived(this.getTotalBill(),orderItem);
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error updating document", e);
        });
    }

    public void addReview(Integer rating, String reviewText){
        Map<String, Object> data = new HashMap<>();
        if(!(reviewText.equals(""))){
            data.put("review", review);
        }
        data.put("rating", rating);
        db.collection("reviews").add(data).addOnSuccessListener(documentReference -> {
            Log.d("FirestoreData", "review added with ID: " + documentReference.getId());
            db.collection("sessions").document(sessionID).update("review", documentReference.getId()).addOnSuccessListener(documentReference1 -> {
                Log.d("FirestoreData", "review added to session");
            }).addOnFailureListener(e -> {
                Log.d("FirestoreData", "Error adding review to session", e);
            });
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error adding review", e);
        });
    }
    public void checkOut(){
        this.checkedOut = true;
        db.collection("sessions").document(this.sessionID).update("checkedOut", true).addOnSuccessListener(documentReference -> {
            Log.d("FirestoreData", "checkedOut successfully updated!");
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error updating checkedOut", e);
        });
    }
    public void pay(){
        this.paid = true;
        db.collection("sessions").document(this.sessionID).update("paid", true).addOnSuccessListener(documentReference -> {
            Log.d("FirestoreData", "paid successfully updated!");
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error updating paid", e);
        });
    }
}
