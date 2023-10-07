package com.example.virtualwaiter.recycledview;

import com.example.virtualwaiter.datatypes.FoodItem;
import com.example.virtualwaiter.R;
import com.squareup.picasso.Picasso;

import android.widget.TextView;
import android.widget.ImageView;

import java.util.ArrayList;


public class FoodMenuAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<FoodMenuAdapter.FoodItemViewHolder>{
    public ArrayList<FoodItem> foodItems;
    public Picasso picasso;
    public OnFoodItemListener OnFoodItemListener;
    public FoodMenuAdapter(ArrayList<FoodItem> foodItems, OnFoodItemListener activity){
        this.foodItems = foodItems;
        this.OnFoodItemListener = activity;
    }
    @Override
    public FoodItemViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        picasso = Picasso.get();
        return new FoodItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.foodName.setText(foodItem.getName());
        holder.foodPrice.setText(foodItem.getPrice().toString());
        holder.foodDescription.setText(foodItem.getDescription());
        picasso.load(foodItem.getImage()).error(R.drawable.baseline_emoji_food_beverage_24).into(holder.foodImage);
        holder.foodImage.setClipToOutline(true);
        holder.itemView.setOnClickListener(v -> OnFoodItemListener.OnFoodItemClick(foodItem));
    }
    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public interface OnFoodItemListener {
        void OnFoodItemClick(FoodItem foodItem);
    }

    public static class FoodItemViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        public TextView foodName;
        public TextView foodPrice;
        public ImageView foodImage;
        public TextView foodDescription;
        public FoodItemViewHolder(android.view.View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.unitPrice);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodDescription = itemView.findViewById(R.id.orderDescription);
        }
    }
}
