package com.example.virtualwaiter;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtualwaiter.datatypes.Booking;
import com.example.virtualwaiter.datatypes.FoodItem;
import com.example.virtualwaiter.datatypes.OfferItem;
import com.example.virtualwaiter.datatypes.OrderItem;
import com.example.virtualwaiter.recycledview.FoodMenuAdapter;
import com.example.virtualwaiter.foodtypes.FoodTypeAdapter;
import com.example.virtualwaiter.recycledview.OfferSliderAdapter;
import com.example.virtualwaiter.recycledview.OrderListAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements FoodMenuAdapter.OnFoodItemListener {
    private FoodTypeAdapter foodTypeAdapter;
    private TabLayout foodtypeTabLayout;
    private ViewPager2 foodtypeViewPager;
    private SessionManager sessionManager;
    private ArrayList<OrderItem> orderedItems = new ArrayList<>();
    private  Boolean onGoingSession = false;
    private Integer tableID;

    private BookingManager bookingManager;
    private OrderListAdapter orderedListAdapter;
    private FirebaseFirestore db;
    private Picasso picasso = Picasso.get();
    private CountDownTimer countDownTimer;
    private SessionManager.AddOrderCallback addOrderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        //get table id from shared preferences
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("virtualWaiterTableConfig", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        tableID = sharedPref.getInt("tableID", 1);
        Log.d("heyyou", "tableID: "+tableID);

        setUpOrderedList();

        //initiate the session manager
        sessionManager = new SessionManager(tableID,addOrderCallback,orderedListAdapter);


        setUpOffersList();

        setUpTabLayout();

        setUpCheckoutArea();

        setUpTableInfoButton(editor);

        bookingManager = new BookingManager(tableID);

        // this callback is called when a booking is found for the table
        bookingManager.setBookingCallback(() ->{
            if(onGoingSession){
                return;
            }
            Booking booking = bookingManager.getNextBooking();
            if(booking == null){
                return;
            }
            waitForBooking(booking);
        });
        //listen to sessions updates
        db.collection("sessions").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("FirestoreData", "session change listen failed.", error);
                return;
            }
            if(value == null){
                return;
            }
            for (DocumentChange change : value.getDocumentChanges()) {
                Log.d("FirestoreData", "session change: " + change.getDocument().getData());
                if(!onGoingSession && change.getDocument().getLong("tableID").intValue()==tableID && change.getDocument().getBoolean("checkedOut").equals(false)){
                    if(change.getType() == DocumentChange.Type.ADDED){
                        Log.d("FirestoreData", "New session: " + change.getDocument().getData());
                        sessionManager.firebaseDownload(change.getDocument().getId());
                        onGoingSession = true;
                    }
                }
            }
        });
    }

    private void setUpTableInfoButton(SharedPreferences.Editor editor) {
        findViewById(R.id.tableInfoButton).setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            View popUpView = getLayoutInflater().inflate(R.layout.pop_up_table_info, null);
            TextView tableNumber = popUpView.findViewById(R.id.tableNumber);
            TextView tableNumberLabel = popUpView.findViewById(R.id.tableNumberLabel);
            TextView bookingInfo = popUpView.findViewById(R.id.bookingInfo);
            TextView bookingInfoLabel = popUpView.findViewById(R.id.bookingLabel);
            TextView tableInfoTopic = popUpView.findViewById(R.id.tableInfoTopic);
            Button closeButton = popUpView.findViewById(R.id.closeButton);
            Button editButton = popUpView.findViewById(R.id.editButton);
            EditText email = popUpView.findViewById(R.id.email);
            EditText password = popUpView.findViewById(R.id.password);
            EditText newTableNumber = popUpView.findViewById(R.id.newTableNumber);
            Button confirmButton = popUpView.findViewById(R.id.orderConfirmButton);
            tableNumber.setText(tableID.toString());
            builder.setView(popUpView);
            AlertDialog dialog = builder.create();
            closeButton.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            editButton.setOnClickListener(v1 -> {
                tableInfoTopic.setText("Edit Table Number");
                tableNumber.setVisibility(View.GONE);
                tableNumberLabel.setVisibility(View.GONE);
                bookingInfo.setVisibility(View.GONE);
                bookingInfoLabel.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
                email.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                newTableNumber.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.VISIBLE);
            });
            confirmButton.setOnClickListener(v1 -> {
                if(email.getText().toString().equals("") || password.getText().toString().equals("") || newTableNumber.getText().toString().equals("")){
                    Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    tableID = Integer.parseInt(newTableNumber.getText().toString());
                    editor.putInt("tableID", tableID);
                    editor.apply();
                    dialog.dismiss();
                }
            });
            dialog.show();
        });
    }

    private void setUpCheckoutArea() {
        findViewById(R.id.checkOutButton).setOnClickListener(v -> {
            if(!onGoingSession){
                Toast.makeText(this, "No orders to checkout", Toast.LENGTH_SHORT).show();
                return;
            }
            MaterialAlertDialogBuilder confirmBuilder = new MaterialAlertDialogBuilder(this);
            confirmBuilder.setTitle("Confirm Checkout");
            confirmBuilder.setBackground(this.getDrawable(R.drawable.rounded_corners));
            confirmBuilder.setMessage("Are you sure you want to checkout?");
            confirmBuilder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                orderedItems.clear();
                orderedListAdapter.notifyDataSetChanged();
                TextView beforeDiscount = findViewById(R.id.beforeDiscount);
                TextView afterDiscount = findViewById(R.id.afterDiscount);
                beforeDiscount.setText("0");
                afterDiscount.setText("0");
                showReviewDialog();
            });
            confirmBuilder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });
            confirmBuilder.show();
        });

    }

    private void showReviewDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        View popUpView = getLayoutInflater().inflate(R.layout.pop_up_review, null);
        RatingBar ratingBar = popUpView.findViewById(R.id.ratingBar);
        EditText reviewEditText = popUpView.findViewById(R.id.review);
        Button submitButton = popUpView.findViewById(R.id.editButton);
        builder.setView(popUpView);
        AlertDialog dialog = builder.create();
        submitButton.setOnClickListener(v1 -> {
            String review = reviewEditText.getText().toString();
            Integer rating = (int) ratingBar.getRating();
            if(rating == 0){
                Toast.makeText(this, "Please give a rating", Toast.LENGTH_SHORT).show();
                return;
            }
            //add these data to firebase session document
            sessionManager.addReview(rating, review);
            dialog.dismiss();

        });
        dialog.show();

        //this timer is to close the add review pop up menu after a certain time of inactivity
        setUpClosingTimer(dialog);
    }

    private void setUpClosingTimer(AlertDialog dialog) {
        CountDownTimer reviewTimer = new CountDownTimer(60000, 500) {
            public void onTick(long millisUntilFinished) {
                if(!(dialog.isShowing())){
                    this.onFinish();
                    this.cancel();
                }
            }
            public void onFinish() {
                dialog.dismiss();
                onGoingSession = false;
                sessionManager.checkOut();
                //this will create a new session manager for the next session
                sessionManager = new SessionManager(tableID,addOrderCallback,orderedListAdapter);
                waitForBooking(bookingManager.getNextBooking());
            }
        }.start();
    }


    private void setUpTabLayout() {
        foodtypeTabLayout = findViewById(R.id.tabLayout);
        foodtypeViewPager = findViewById(R.id.viewPager);
        foodTypeAdapter = new FoodTypeAdapter(this);
        foodtypeViewPager.setAdapter(foodTypeAdapter);
        foodtypeTabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        foodtypeViewPager.setCurrentItem(tab.getPosition());
                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                    }
                }
        );
        foodtypeViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        foodtypeTabLayout.selectTab(foodtypeTabLayout.getTabAt(position));
                    }
                }
        );
    }

    private void setUpOffersList() {
        ArrayList<OfferItem> offerItems = new ArrayList<>();
        RecyclerView offerList = findViewById(R.id.offersList);
        offerItems.add(new OfferItem("offer 1",R.drawable._546252575451));
        offerItems.add(new OfferItem("offer 2",R.drawable._offer2));
        offerItems.add(new OfferItem("offer 3",R.drawable._offer3));
        offerItems.add(new OfferItem("offer 4",R.drawable._offer4));
        offerItems.add(new OfferItem("offer 5",R.drawable._546252575451));
        offerList.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        OfferSliderAdapter offerSliderAdapter = new OfferSliderAdapter(offerItems);
        offerList.setAdapter(offerSliderAdapter);
    }

    private void setUpOrderedList() {
        RecyclerView orderedList = findViewById(R.id.orderedItems);
        orderedList.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        orderedListAdapter = new OrderListAdapter(orderedItems);
        orderedList.setAdapter(orderedListAdapter);
        //this callback is used to update the total bill in the checkout area and ordered list when a new order is added
        addOrderCallback = (totalBill,orderItem) -> {
            TextView beforeDiscount = findViewById(R.id.beforeDiscount);
            TextView afterDiscount = findViewById(R.id.afterDiscount);
            beforeDiscount.setText(totalBill.toString());
            afterDiscount.setText(totalBill.toString());
            orderedItems.add(orderItem);
            orderedListAdapter.notifyItemInserted(orderedItems.size()-1);
            Log.d("heyyou", "addOrderCallback: "+orderedItems.size());
        };
    }



    //this is called when a food item is clicked. this will open a pop up to confirm the order and add the order to the ordered list.
    //this will also create a new session if there is no ongoing session or add the order to the existing session
    //these data will be added to the firestore database through the session and orderitem classes
    @Override
    public void OnFoodItemClick(FoodItem foodItem) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        View popUpView = getLayoutInflater().inflate(R.layout.pop_up_order_confirm, null);
        TextView foodName = popUpView.findViewById(R.id.orderItemName);
        TextView foodPrice = popUpView.findViewById(R.id.orderItemPrice);
        TextView description = popUpView.findViewById(R.id.orderDescription);
        Button confirmButton = popUpView.findViewById(R.id.orderConfirmButton);
        EditText quantity = popUpView.findViewById(R.id.orderQuantity);
        TextView totalPrice = popUpView.findViewById(R.id.orderTotalPrice);
        EditText notes = popUpView.findViewById(R.id.orderNotes);
        ImageView foodImage = popUpView.findViewById(R.id.popupFoodImage);
        foodImage.setClipToOutline(true);
        foodName.setText(foodItem.getName());
        description.setText(foodItem.getDescription());
        foodPrice.setText(foodItem.getPrice().toString());
        totalPrice.setText(foodItem.getPrice().toString());
        picasso.load(foodItem.getImage()).error(R.drawable.baseline_emoji_food_beverage_24).into(foodImage);
        builder.setView(popUpView);
        AlertDialog dialog = builder.create();
        confirmButton.setOnClickListener(v -> {
            Integer quantityValue = Integer.parseInt(quantity.getText().toString());
            String note = notes.getText().toString();
            OrderItem orderItem = new OrderItem(foodItem.getName(), foodItem.getPrice(), quantityValue, tableID ,note , foodItem.getImage());

            //this callback is used to get the order id from the orderitem class and add it to the session.
            orderItem.setCallback(orderId -> {
                onGoingSession = true;
                TextView beforeDiscount = findViewById(R.id.beforeDiscount);
                TextView afterDiscount = findViewById(R.id.afterDiscount);
                beforeDiscount.setText(sessionManager.getTotalBill().toString());
                afterDiscount.setText(sessionManager.getTotalBill().toString());
            });
            sessionManager.addOrder(orderItem);
            dialog.dismiss();
        });
        quantity.setOnEditorActionListener((v, actionId, event) -> {
            Integer quantityValue = Integer.parseInt(quantity.getText().toString());
            Integer totalPriceValue = foodItem.getPrice() * quantityValue;
            totalPrice.setText(totalPriceValue.toString());
            return false;
        });
        dialog.show();
        dialog.getWindow().setLayout(1200, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    public void setUpBooking(Booking booking){
        Intent intent = new Intent(this, BookedActivity.class);
        intent.putExtra("tableID", booking.getTableID());
        intent.putExtra("dateTime", booking.getStartTime().toString());
        intent.putExtra("key", booking.getKey());
        intent.putExtra("name", booking.getName());
        startActivity(intent);
    }
    public void waitForBooking(Booking booking){
        if(booking == null){
            return;
        }
        Long timeToBooking = booking.getStartTime().toDate().getTime()-new Date().getTime();
        Log.d("heyyou", "waitForBooking: "+ booking.getStartTime().toDate().getTime());
        Log.d("heyyou", "waitForBooking: "+ new Date().getTime());
        countDownTimer= new CountDownTimer(timeToBooking, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.d("heyyou", "onTick: "+millisUntilFinished);
                if(onGoingSession){
                    countDownTimer.cancel();
                }
            }
            public void onFinish() {
                setUpBooking(booking);
            }
        }.start();
    }
}