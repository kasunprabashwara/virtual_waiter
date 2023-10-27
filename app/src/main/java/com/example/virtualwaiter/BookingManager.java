package com.example.virtualwaiter;

import android.util.Log;

import com.example.virtualwaiter.datatypes.Booking;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class BookingManager {
    private ArrayList<Booking> bookings;
    private Integer tableID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BookingCallback callback;
    public interface BookingCallback {
        void onBookingReceived();
    }

    public BookingManager(Integer tableID){
        this.tableID = tableID;
        this.bookings = new ArrayList<>();
        // Define the time constraint
        Date now = new Date(); // Current date and time
        Query query = db.collection("tableBookings")
                .whereEqualTo("tableID", tableID)
                .whereGreaterThanOrEqualTo("start", now);
        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("FirestoreData", "tableBookings listen failed.", error);
                return;
            }
            for (DocumentChange change : value.getDocumentChanges()) {
                switch (change.getType()) {
                    case ADDED:
                        // Handle added documents here
                        Log.d("FirestoreData", "Booking " + change.getDocument().getData());
                        Booking booking = new Booking(
                                change.getDocument().getString("name"),
                                change.getDocument().getLong("tableID").intValue(),
                                change.getDocument().getString("key"),
                                change.getDocument().getTimestamp("start"),
                                change.getDocument().getTimestamp("end"),
                                change.getDocument().getId()
                        );
                        bookings.add(booking);
                        bookings.sort(Comparator.comparing(Booking::getStartTime));
                        callback.onBookingReceived();
                        break;

                    case MODIFIED:
                        // Handle modified documents here
                        Log.d("FirestoreData", "Modified booking: ");
                        break;

                    case REMOVED:
                        // Handle removed documents here
                        Log.d("FirestoreData", "Removed booking: ");
                        break;
                }
            }
        });

    }
    public Booking getNextBooking(){
        if(bookings.size() == 0){
            return null;
        }
        return bookings.get(0);
    }
    public void setBookingCallback(BookingCallback callback) {
        this.callback = callback;
    }
}
