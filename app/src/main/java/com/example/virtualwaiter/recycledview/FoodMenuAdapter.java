package com.example.virtualwaiter.recycledview;

import com.example.virtualwaiter.datatypes.FoodItem;
import com.example.virtualwaiter.R;

public class FoodMenuAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<FoodMenuAdapter.FoodItemViewHolder>{
    public java.util.ArrayList<FoodItem> foodItems;
    public FoodMenuAdapter(java.util.ArrayList<FoodItem> foodItems){
        this.foodItems = foodItems;
    }
    @Override
    public FoodItemViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        FoodItemViewHolder foodItemViewHolder = new FoodItemViewHolder(view);
        return foodItemViewHolder;
    }
    @Override
    public void onBindViewHolder(FoodItemViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.foodName.setText(foodItem.name);
        holder.foodPrice.setText(foodItem.price);
        holder.foodImage.setImageResource(R.drawable.baseline_emoji_food_beverage_24);
    }
    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public static class FoodItemViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        public android.widget.TextView foodName;
        public android.widget.TextView foodPrice;
        public android.widget.ImageView foodImage;
        public FoodItemViewHolder(android.view.View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodImage = itemView.findViewById(R.id.foodImage);
        }
    }
}
