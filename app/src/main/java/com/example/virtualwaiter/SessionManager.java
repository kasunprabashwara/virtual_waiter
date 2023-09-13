package com.example.virtualwaiter;

import android.util.Log;
import android.widget.Toast;

import com.example.virtualwaiter.datatypes.OrderItem;
import com.example.virtualwaiter.datatypes.Review;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class SessionManager {
    public ArrayList<String> orderedItemIDs; //wil be keeping two list for ease of access. this one is for firestore references
    public ArrayList<OrderItem> orderedItems;
    public Integer tableID;
    public String sessionID;
    public Review review;
    public Integer totalBill;
    public Boolean checkedOut = false;
    public Boolean paid = false;

    public FirebaseFirestore db;


    public interface StatusChangeCallback {
        void statusChangeReceived(Integer position);
    }

    private AddOrderCallback addOrderCallback;
    private StatusChangeCallback statusChangeCallback;

    public interface AddOrderCallback {
        void onAddOrderReceived(Integer totalBill);
    }

    public SessionManager(OrderItem orderItem, ArrayList<OrderItem> orderItems){
        this.tableID = orderItem.tableID;
        this.orderedItemIDs = new ArrayList<>();
        this.orderedItemIDs.add(orderItem.orderId);
        this.orderedItems = orderItems;
        this.orderedItems.add(orderItem);
        this.review = null;
        this.totalBill = orderItem.totalPrice;

        //upload to firestore
        this.db = FirebaseFirestore.getInstance();
        Map<String,Object> data = new HashMap<>();
        data.put("orders", Arrays.asList(orderItem.orderId));
        data.put("tableID", this.tableID);
        data.put("totalBill", this.totalBill);
        data.put("checkedOut", this.checkedOut);
        data.put("paid", this.paid);
        db.collection("sessions").add(data).addOnSuccessListener(documentReference -> {
            this.sessionID = documentReference.getId();
            orderItem.sessionID = documentReference.getId();
            db.collection("orders").document(orderItem.orderId).update("sessionID", this.sessionID).addOnSuccessListener(documentReference1 -> {
                Log.d("FirestoreData", "session id successfully updated in order!");
            }).addOnFailureListener(e -> {
                Log.d("FirestoreData", "Error updating order", e);
            });
            addOrderCallback.onAddOrderReceived(this.totalBill);
            Log.d("FirestoreData", "New session created with ID: " + this.sessionID);
        }).addOnFailureListener(e -> {
            this.sessionID = null;
            Log.d("FirestoreData", "Error creating session", e);
        });

        //listen to order status updates
        db.collection("orders").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("FirestoreData", "order change listen failed.", error);
                return;
            }
            if(value == null){
                return;
            }
            for (DocumentChange change : value.getDocumentChanges()) {
                if(change.getDocument().getString("sessionID") == null){
                    continue;
                }
                if(change.getDocument().getString("sessionID").equals(this.sessionID)){
                    switch (change.getType()) {
                        case ADDED:
                            Log.d("FirestoreData", "New order: " + change.getDocument().getData());
                            break;
                        case MODIFIED:
                            String newStatus= change.getDocument().getString("status");
                            for(OrderItem item: this.orderedItems){
                                if(item.orderId.equals(change.getDocument().getId())){
                                    item.status = newStatus;
                                    statusChangeCallback.statusChangeReceived(this.orderedItems.indexOf(item));
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
    public void addOrder(OrderItem orderItem){
        this.orderedItemIDs.add(orderItem.orderId);
        this.orderedItems.add(orderItem);   //check for concurrency issues later
        this.totalBill += orderItem.totalPrice;
        Map<String,Object> data = new HashMap<>();
        data.put("orders", this.orderedItemIDs);
        data.put("tableID", this.tableID);
        data.put("totalBill", this.totalBill);
        db.collection("sessions").document(this.sessionID).update(data).addOnSuccessListener(documentReference -> {
            Log.d("FirestoreData", "DocumentSnapshot successfully updated!");
            orderItem.sessionID = this.sessionID;
            db.collection("orders").document(orderItem.orderId).update("sessionID", this.sessionID).addOnSuccessListener(documentReference1 -> {
                Log.d("FirestoreData", "sessionId successfully updated in order!");
            }).addOnFailureListener(e -> {
                Log.d("FirestoreData", "Error updating order", e);
            });
            addOrderCallback.onAddOrderReceived(this.totalBill);
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error updating document", e);
        });
    }
    public void setAddOrderCallback(AddOrderCallback callback) {
        this.addOrderCallback = callback;
    }
    public void setStatusChangeCallback(StatusChangeCallback callback) {
        this.statusChangeCallback = callback;
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
