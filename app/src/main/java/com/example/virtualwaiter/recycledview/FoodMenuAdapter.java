package com.example.virtualwaiter.recycledview;

import com.example.virtualwaiter.datatypes.FoodItem;
import com.example.virtualwaiter.R;

import java.util.ArrayList;


public class FoodMenuAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<FoodMenuAdapter.FoodItemViewHolder>{
    public ArrayList<FoodItem> foodItems;
    public OnFoodItemListener OnFoodItemListener;
    public FoodMenuAdapter(ArrayList<FoodItem> foodItems, OnFoodItemListener activity){
        this.foodItems = foodItems;
        this.OnFoodItemListener = activity;
    }
    @Override
    public FoodItemViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new FoodItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.foodName.setText(foodItem.name);
        holder.foodPrice.setText(foodItem.price.toString());
        holder.foodImage.setImageResource(R.drawable.baseline_emoji_food_beverage_24);
holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                OnFoodItemListener.OnFoodItemClick(foodItem);
            }
        });
    }
    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public interface OnFoodItemListener {
        void OnFoodItemClick(FoodItem foodItem);
    }

    public static class FoodItemViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        public android.widget.TextView foodName;
        public android.widget.TextView foodPrice;
        public android.widget.ImageView foodImage;
        public FoodItemViewHolder(android.view.View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.unitPrice);
            foodImage = itemView.findViewById(R.id.foodImage);
        }
    }
}
