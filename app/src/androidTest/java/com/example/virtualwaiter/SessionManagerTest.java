package com.example.virtualwaiter;

import static org.junit.Assert.*;

import android.util.Log;
import android.widget.TextView;

import com.example.virtualwaiter.datatypes.OrderItem;
import com.example.virtualwaiter.recycledview.OrderListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class SessionManagerTest {
    private SessionManager sessionManager;
    private FirebaseFirestore db;

    @Before
    public void setUp() throws Exception {
        OrderListAdapter orderListAdapter = new OrderListAdapter(new ArrayList<OrderItem>());
        sessionManager = new SessionManager(1, (totalBill,orderItem) -> {},orderListAdapter);
        db = FirebaseFirestore.getInstance();
    }

    @Test
    public void testStatusChangeUpdate(){
        OrderItem orderItem = new OrderItem("Rice", 1500, 1,1,"","https://i.ibb.co/Ks3JfQW/Square-Image-Chicken-Fried-Rice.jpg");
        orderItem.setCallback((orderId) -> {
            sessionManager.addOrder(orderItem);
        });
        orderItem.firebaseUpload();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        db.collection("orders").document(orderItem.getOrderId()).update("status", "Delivered");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("Delivered", orderItem.getStatus());
    }

    @Test
    public void firebaseDownload() {
        sessionManager.firebaseDownload("beTJSEi3oMU9qmZMyVhY");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(Integer.valueOf(1500), sessionManager.getTotalBill());

    }
    @Test
    public void testAddOrder() {
        OrderItem orderItem = new OrderItem("Rice", 1500, 1,1,"","https://i.ibb.co/Ks3JfQW/Square-Image-Chicken-Fried-Rice.jpg");
        orderItem.setCallback((orderId) -> {
            sessionManager.addOrder(orderItem);
        });
        orderItem.firebaseUpload();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(Integer.valueOf(1500), sessionManager.getTotalBill());
    }

    @Test
    public void testAddReview() {
        sessionManager.firebaseDownload("beTJSEi3oMU9qmZMyVhY");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sessionManager.addReview(5, "Good");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //fetch the review from the database
        db.collection("sessions").document("beTJSEi3oMU9qmZMyVhY").get().addOnSuccessListener(sessionSnapshot -> {
            String reviewId = sessionSnapshot.getString("review");
            db.collection("reviews").document(reviewId).get().addOnSuccessListener(reviewSnapshot -> {
                int rating = reviewSnapshot.getLong("rating").intValue();
                String review = reviewSnapshot.getString("review");
                assertEquals(5, rating);
                assertEquals("Good", review);
            });
        });
    }
    @Test
    public void testCheckOut() {
        OrderItem orderItem = new OrderItem("Rice", 1500, 1,1,"","https://i.ibb.co/Ks3JfQW/Square-Image-Chicken-Fried-Rice.jpg");
        orderItem.setCallback((orderId) -> {
            sessionManager.addOrder(orderItem);
        });
        orderItem.firebaseUpload();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sessionManager.checkOut();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        db.collection("sessions").document(sessionManager.getSessionID()).get().addOnSuccessListener(sessionSnapshot -> {
            boolean checkedOut = sessionSnapshot.getBoolean("checkedOut");
            assertTrue(checkedOut);
        });
    }


}