package com.example.virtualwaiter.foodtypes;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.virtualwaiter.R;
import com.example.virtualwaiter.datatypes.FoodItem;
import com.example.virtualwaiter.recycledview.FoodMenuAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class MainCourseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_main_course, container, false);
        ArrayList<FoodItem> mainCourseItems = new ArrayList<>();
        RecyclerView mainCourseMenu = view.findViewById(R.id.mainCourseMenu);
        mainCourseMenu.setLayoutManager(new GridLayoutManager(getContext(), 4));
        FoodMenuAdapter foodMenuAdapter = new FoodMenuAdapter(mainCourseItems, (FoodMenuAdapter.OnFoodItemListener) getActivity());
        mainCourseMenu.setAdapter(foodMenuAdapter);

        CollectionReference foods= FirebaseFirestore.getInstance().collection("foods");
        foods.whereEqualTo("type", "Main Course")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            Integer price = document.getLong("price").intValue();
                            String image = document.getString("url");
                            mainCourseItems.add(new FoodItem(name, price, description, image));
                        }
                        foodMenuAdapter.notifyDataSetChanged();
                    }
                });

        return view;
    }
}