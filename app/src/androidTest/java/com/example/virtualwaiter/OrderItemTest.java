package com.example.virtualwaiter;

import static org.junit.Assert.*;

import com.example.virtualwaiter.datatypes.OrderItem;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Test;

public class OrderItemTest {
    @Test
    public void firebaseUpload() {
        OrderItem orderItem = new OrderItem("Rice", 1500, 1,1,"","https://i.ibb.co/Ks3JfQW/Square-Image-Chicken-Fried-Rice.jpg");
        orderItem.setCallback((orderId) -> {
        });
        orderItem.firebaseUpload();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").document(orderItem.getOrderId()).get().addOnSuccessListener(documentSnapshot -> {
            assertEquals("Rice", documentSnapshot.get("name"));
            assertEquals(1500, documentSnapshot.get("price"));
            assertEquals(1, documentSnapshot.get("quantity"));
            assertEquals(1, documentSnapshot.get("tableID"));
            assertEquals("", documentSnapshot.get("notes"));
            assertEquals("https://i.ibb.co/Ks3JfQW/Square-Image-Chicken-Fried-Rice.jpg", documentSnapshot.get("image"));
        }).addOnSuccessListener(documentSnapshot -> {
            db.collection("orders").document(orderItem.getOrderId()).delete();
        });
    }
}