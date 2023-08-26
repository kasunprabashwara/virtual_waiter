package com.example.virtualwaiter;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.virtualwaiter.datatypes.FoodItem;
import com.example.virtualwaiter.datatypes.OfferItem;
import com.example.virtualwaiter.datatypes.OrderItem;
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

    private ArrayList<OrderItem> orderedItems = new ArrayList<>();
    private OrderListAdapter orderedListAdapter;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        //set up ordered list
        androidx.recyclerview.widget.RecyclerView orderedList = findViewById(R.id.orderedItems);
        orderedList.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        orderedListAdapter = new OrderListAdapter(orderedItems);
        orderedList.setAdapter(orderedListAdapter);

        //set up offers list
        ArrayList<OfferItem> offerItems = new ArrayList<>();
        androidx.recyclerview.widget.RecyclerView offerList = findViewById(R.id.offersList);
        offerItems.add(new OfferItem("offer 1",R.drawable._546252575451));
        offerItems.add(new OfferItem("offer 2",R.drawable._546252575451));
        offerItems.add(new OfferItem("offer 3",R.drawable._546252575451));
        offerItems.add(new OfferItem("offer 4",R.drawable._546252575451));
        offerItems.add(new OfferItem("offer 5",R.drawable._546252575451));
        offerList.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        OfferSliderAdapter offerSliderAdapter = new OfferSliderAdapter(offerItems);
        offerList.setAdapter(offerSliderAdapter);

        //set up food tab layout
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
        //set up checkout button
        findViewById(R.id.checkOutButton).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View popUpView = getLayoutInflater().inflate(R.layout.pop_up_review, null);
            AlertDialog dialog = builder.create();

            // Initialize pop-up components
            RatingBar ratingBar = popUpView.findViewById(R.id.ratingBar);
            EditText reviewEditText = popUpView.findViewById(R.id.review);
            Button submitButton = popUpView.findViewById(R.id.reveiwSubmit);

            // Handle submission
            submitButton.setOnClickListener(a -> {
                float rating = ratingBar.getRating();
                String review = reviewEditText.getText().toString();
                Log.d("heyyou", "onClick: " + rating + " " + review);
                dialog.dismiss();
            });

            builder.setView(popUpView);
            dialog.show();
        });
    }

    @Override
    public void OnFoodItemClick(FoodItem foodItem) {
        OrderItem orderItem = new OrderItem(foodItem.name, R.drawable._fried_rice, foodItem.price, 1);
        orderedItems.add(orderItem);
        orderedListAdapter.notifyDataSetChanged();
        Log.d("heyyou", "OnFoodItemClick:");
    }
}