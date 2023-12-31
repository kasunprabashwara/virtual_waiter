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

    private FirebaseFirestore db;

    public Integer getTotalBill() {
        return totalBill;
    }
    public String getSessionID() {
        return sessionID;
    }

    public interface AddOrderCallback {
        void onAddOrderReceived(Integer totalBill,OrderItem orderItem);
    }
    private final AddOrderCallback addOrderCallback;
    private final OrderListAdapter orderListAdapter;

    public SessionManager(Integer tableID, AddOrderCallback addOrderCallback, OrderListAdapter orderListAdapter){
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



    //this is defined separate from the constructor because the session ID is not known at the time of construction
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
                            Log.d("FirestoreData", orderedItemIDs.toString());

                            if(orderedItemIDs.contains(orderID)){
                                Log.d("FirestoreData", "order already added");
                                break;
                            }
                            String name = documentSnapshot.getString("name");
                            Integer price = documentSnapshot.getLong("price").intValue();
                            Integer quantity = documentSnapshot.getLong("quantity").intValue();
                            String status = documentSnapshot.getString("status");
                            String notes = documentSnapshot.getString("notes");
                            String image = documentSnapshot.getString("url");
                            OrderItem orderItem = new OrderItem(name, price, quantity, tableID, notes, image);
                            orderItem.setOrderID(orderID);
                            orderItem.setStatus(status);
                            orderItem.setSessionID(sessionID);
                            this.orderedItems.add(orderItem);
                            this.orderedItemIDs.add(orderID);
                            this.totalBill += orderItem.getTotalPrice();
                            Log.d("heyyou", "total bill: "+this.totalBill);
                            Map<String,Object> data1 = new HashMap<>();
                            data1.put("orders", this.orderedItemIDs);
                            data1.put("totalBill", this.totalBill);
                            db.collection("sessions").document(this.sessionID).update(data1).addOnSuccessListener(documentReference -> {
                                addOrderCallback.onAddOrderReceived(this.totalBill,orderItem);
                            }).addOnFailureListener(e -> {
                                Log.d("FirestoreData", "Error updating document", e);
                            });
                            break;
                        case MODIFIED:
                            String newStatus= documentSnapshot.getString("status");
                            for(OrderItem item: this.orderedItems){
                                if(item.getOrderID().equals(change.getDocument().getId())){
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
                            orderItem.setOrderID(orderID);
                            orderItem.setStatus(status);
                            orderItem.setSessionID(sessionID);
                            this.orderedItems.add(orderItem);
                            addOrderCallback.onAddOrderReceived(this.totalBill,orderItem);
                            Log.d("FirestoreData", "order added to list: " + orderedItems.size());
                        } else {
                            Log.d("FirestoreData", "No such document");
                        }
                    }).addOnFailureListener(e -> {
                        Log.d("FirestoreData", "get failed with ", e);
                    });
                }
                setOrderListener();
            } else {
                Log.d("FirestoreData", "No such document");
            }
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "get failed with ", e);
        });
    }

    public void addOrder(OrderItem orderItem){
        Log.d("FirestoreData", "Session ID: "+this.sessionID);
//        this.orderedItems.add(orderItem);
//        this.orderedItemIDs.add(orderItem.getOrderID());
        if(sessionID.equals("")){
            Map<String,Object> data = new HashMap<>();
            data.put("orders", Arrays.asList());
            data.put("tableID", this.tableID);
            data.put("totalBill", this.totalBill);
            data.put("checkedOut", this.checkedOut);
            data.put("paid", this.paid);
            db.collection("sessions").add(data).addOnSuccessListener(documentReference -> {
                this.sessionID = documentReference.getId();
                orderItem.setSessionID(this.sessionID);
                orderItem.firebaseUpload();
                //the firebase upload will cause the order collection snapshot listener to be called
                //so the order will be added to the list from there

                //update the table with the session ID and ongoing status
                Map <String,Object> data1 = new HashMap<>();
                data1.put("lastSessionID", this.sessionID);
                data1.put("status", "Ongoing");
                db.collection("tables").document(tableID.toString()).update(data1).addOnSuccessListener(documentReference1 -> {
                    Log.d("FirestoreData", "lastSessionID successfully updated!");
                }).addOnFailureListener(e -> {
                    Log.d("FirestoreData", "Error updating lastSessionID", e);
                });

                setOrderListener();
            }).addOnFailureListener(e -> {
                Log.d("FirestoreData", "Error creating session", e);
            });
        }
        else{
            orderItem.setSessionID(this.sessionID);
            orderItem.firebaseUpload();
        }
    }



    public void checkOut(){
        this.checkedOut = true;
        db.collection("sessions").document(this.sessionID).update("checkedOut", true).addOnSuccessListener(documentReference -> {
            Log.d("FirestoreData", "checkedOut successfully updated!");
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error updating checkedOut", e);
        });
        db.collection("tables").document(tableID.toString()).update("status","Available").addOnSuccessListener(documentReference -> {
            Log.d("FirestoreData", "table status successfully updated!");
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error updating table status", e);
        });
    }

    public void addReview(Integer rating, String reviewText){
        Map<String, Object> data = new HashMap<>();
        if(!(reviewText.equals(""))){
            data.put("review", reviewText);
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
    public void pay(){
        this.paid = true;
        db.collection("sessions").document(this.sessionID).update("paid", true).addOnSuccessListener(documentReference -> {
            Log.d("FirestoreData", "paid successfully updated!");
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error updating paid", e);
        });
    }
}
