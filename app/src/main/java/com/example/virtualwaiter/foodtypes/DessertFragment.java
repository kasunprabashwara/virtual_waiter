package com.example.virtualwaiter.foodtypes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.virtualwaiter.datatypes.FoodItem;
import com.example.virtualwaiter.R;
import com.example.virtualwaiter.recycledview.FoodMenuAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DessertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DessertFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DessertFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DessertFragment newInstance() {
        DessertFragment fragment = new DessertFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_dessert, container, false);
        ArrayList<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(new FoodItem("Pizza", "400"));
        foodItems.add(new FoodItem("Burger", "500"));
        foodItems.add(new FoodItem("Pasta", "800"));
        foodItems.add(new FoodItem("Fries", "300"));
        androidx.recyclerview.widget.RecyclerView recyclerView = view.findViewById(R.id.dessertMenu);
        recyclerView.setAdapter(new FoodMenuAdapter(foodItems));
        Log.d("hey",recyclerView.getAdapter().toString());
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), 2));
        return view;
    }
}