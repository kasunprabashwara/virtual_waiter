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
import com.google.android.material.tabs.TabLayout;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        //get table id from shared preferences
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                "virtualWaiterTableConfig", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        tableID = sharedPref.getInt("tableID", 1);
        Log.d("heyyou", "tableID: "+tableID);
        setUpOrderedList();

        setUpOffersList();

        setUpTabLayout();

        setUpCheckoutArea();

        setUpTableInfoButton(editor);
        
        bookingManager = new BookingManager(tableID);
        bookingManager.setBookingCallback(
                () ->{
                    Booking booking = bookingManager.getNextBooking();
                    if(booking == null){
                        return;
                    }
                    waitForBooking(booking);
                }
        );
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
            sessionManager.checkOut();
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
                    orderedItems.clear();
                    orderedListAdapter.notifyDataSetChanged();
                    TextView beforeDiscount = findViewById(R.id.beforeDiscount);
                    TextView afterDiscount = findViewById(R.id.afterDiscount);
                    beforeDiscount.setText("0");
                    afterDiscount.setText("0");
                    waitForBooking(bookingManager.getNextBooking());
                }
            }.start();
        });

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
        foodName.setText(foodItem.name);
        description.setText(foodItem.description);
        foodPrice.setText(foodItem.price.toString());
        totalPrice.setText(foodItem.price.toString());
        picasso.load(foodItem.image).error(R.drawable.baseline_emoji_food_beverage_24).into(foodImage);
        builder.setView(popUpView);
        AlertDialog dialog = builder.create();
        confirmButton.setOnClickListener(v -> {
            Integer quantityValue = Integer.parseInt(quantity.getText().toString());
            String note = notes.getText().toString();
            OrderItem orderItem = new OrderItem(foodItem.name, foodItem.price, quantityValue, tableID ,note ,foodItem.image);
            orderItem.setCallback(orderId -> {
                if(!onGoingSession){
                    onGoingSession = true;
                    sessionManager = new SessionManager(orderItem, orderedItems);
                    sessionManager.setAddOrderCallback(totalBill -> {
                        TextView beforeDiscount = findViewById(R.id.beforeDiscount);
                        TextView afterDiscount = findViewById(R.id.afterDiscount);
                        beforeDiscount.setText(totalBill.toString());
                        afterDiscount.setText(totalBill.toString());
                    });
                    sessionManager.setStatusChangeCallback((position) -> {
                        orderedListAdapter.notifyItemChanged(position);
                    });
                }
                else{
                    sessionManager.addOrder(orderItem);
                }
            });
            orderedListAdapter.notifyItemInserted(orderedItems.size());
            dialog.dismiss();
        });
        quantity.setOnEditorActionListener((v, actionId, event) -> {
            Integer quantityValue = Integer.parseInt(quantity.getText().toString());
            Integer totalPriceValue = foodItem.price * quantityValue;
            totalPrice.setText(totalPriceValue.toString());
            return false;
        });
        dialog.show();
        Log.d("heyyou", "OnFoodItemClick:");
    }

    public void setUpBooking(Booking booking){
        Intent intent = new Intent(this, BookedActivity.class);
        intent.putExtra("tableID", booking.tableID);
        intent.putExtra("dateTime", booking.dateTime.toString());
        intent.putExtra("key",booking.key);
        intent.putExtra("name", booking.name);
        startActivity(intent);
    }
    public void waitForBooking(Booking booking){
        if(booking == null){
            return;
        }
        countDownTimer= new CountDownTimer(booking.dateTime.toDate().getTime() - new Date().getTime(), 1000) {
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