package com.example.virtualwaiter.foodtypes;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class FoodTypeAdapter extends FragmentStateAdapter {

    public FoodTypeAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new MainCourseFragment();
            case 1:
                return new DessertFragment();
            case 2:
                return new DrinksFragment();
            default:
                return new MainCourseFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
