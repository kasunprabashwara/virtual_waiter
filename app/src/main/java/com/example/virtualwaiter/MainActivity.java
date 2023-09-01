package com.example.virtualwaiter;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.virtualwaiter.datatypes.FoodItem;
import com.example.virtualwaiter.datatypes.OfferItem;
import com.example.virtualwaiter.datatypes.OrderItem;
import com.example.virtualwaiter.datatypes.Session;
import com.example.virtualwaiter.recycledview.FoodMenuAdapter;
import com.example.virtualwaiter.foodtypes.FoodTypeAdapter;
import com.example.virtualwaiter.recycledview.OfferSliderAdapter;
import com.example.virtualwaiter.recycledview.OrderListAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FoodMenuAdapter.OnFoodItemListener {
    private FoodTypeAdapter foodTypeAdapter;
    private TabLayout foodtypeTabLayout;
    private ViewPager2 foodtypeViewPager;
    private Session session;
    private Integer tableID;

    private ArrayList<OrderItem> orderedItems = new ArrayList<>();
    private OrderListAdapter orderedListAdapter;
    FirebaseFirestore db;
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
        session = new Session(tableID);

        setUpOrderedList();

        setUpOffersList();

        setUpTabLayout();

        setUpCheckoutButton();

        findViewById(R.id.tableInfoButton).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View popUpView = getLayoutInflater().inflate(R.layout.pop_up_table_info, null);
            TextView tableNumber = popUpView.findViewById(R.id.tableNumber);
            Button closeButton = popUpView.findViewById(R.id.closeButton);
            Button editButton = popUpView.findViewById(R.id.editButton);
            EditText email = popUpView.findViewById(R.id.email);
            EditText password = popUpView.findViewById(R.id.password);
            EditText newTableNumber = popUpView.findViewById(R.id.newTableNumber);
            Button confirmButton = popUpView.findViewById(R.id.confirmButton);
            tableNumber.setText(tableID.toString());
            newTableNumber.setText(tableID.toString());
            builder.setView(popUpView);
            AlertDialog dialog = builder.create();
            closeButton.setOnClickListener(v1 -> {
                dialog.dismiss();
            });
            editButton.setOnClickListener(v1 -> {
                tableNumber.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                closeButton.setVisibility(View.GONE);
                email.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                newTableNumber.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.VISIBLE);
            });
            confirmButton.setOnClickListener(v1 -> {
                tableID  = Integer.parseInt(newTableNumber.getText().toString());
                editor.putInt("tableID", tableID);
                editor.apply();
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    private void setUpCheckoutButton() {
        findViewById(R.id.checkOutButton).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View popUpView = getLayoutInflater().inflate(R.layout.pop_up_review, null);
            RatingBar ratingBar = popUpView.findViewById(R.id.ratingBar);
            EditText reviewEditText = popUpView.findViewById(R.id.review);
            Button submitButton = popUpView.findViewById(R.id.editButton);

            builder.setView(popUpView);
            AlertDialog dialog = builder.create();
            submitButton.setOnClickListener(v1 -> {
                String review = reviewEditText.getText().toString();
                Integer rating = (int) ratingBar.getRating();
                Log.d("heyyou", "onCreate: " + review + " " + rating);
                dialog.dismiss();
            });
            dialog.show();
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
        foodtypeViewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
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

    @Override
    public void OnFoodItemClick(FoodItem foodItem) {
        OrderItem orderItem = new OrderItem(foodItem.name, R.drawable._fried_rice, foodItem.price, 1);
        orderedItems.add(orderItem);
        orderedListAdapter.notifyDataSetChanged();
        Log.d("heyyou", "OnFoodItemClick:");
    }
}