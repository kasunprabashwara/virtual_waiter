package com.example.virtualwaiter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.virtualwaiter.datatypes.FoodItem;
import com.example.virtualwaiter.datatypes.OfferItem;
import com.example.virtualwaiter.recycledview.FoodMenuAdapter;
import com.example.virtualwaiter.foodtypes.FoodTypeAdapter;
import com.example.virtualwaiter.recycledview.OfferSliderAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FoodTypeAdapter foodTypeAdapter;
    private TabLayout foodtypeTabLayout;
    private ViewPager2 foodtypeViewPager;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        ArrayList<FoodItem> orderedItems = new ArrayList<>();
        androidx.recyclerview.widget.RecyclerView orderedList = findViewById(R.id.orderedItems);
        orderedList.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        FoodMenuAdapter foodMenuAdapter = new FoodMenuAdapter(orderedItems);
        orderedList.setAdapter(foodMenuAdapter);

        db.collection("foods")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String name = document.getString("name");
                            String description = document.getString("description"); // Replace with actual field name
                            Integer price = document.getLong("price").intValue(); // Replace with actual field name
                            Log.d("FirestoreData", "Name: " + name + ", Description: " + description);

                            orderedItems.add(new FoodItem(name, description));
                        }
                        foodMenuAdapter.notifyDataSetChanged();
                    }
                });

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
        Log.d("hey", "hihi:");

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
    }
    public void openSettings(android.view.View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        Log.d("MainActivity", "openSettings: ");
        startActivity(intent);
    }
}