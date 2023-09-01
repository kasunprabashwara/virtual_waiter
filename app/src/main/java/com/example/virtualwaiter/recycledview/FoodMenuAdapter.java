package com.example.virtualwaiter.recycledview;

import com.example.virtualwaiter.datatypes.FoodItem;
import com.example.virtualwaiter.R;

import java.util.ArrayList;


public class FoodMenuAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<FoodMenuAdapter.FoodItemViewHolder>{
    public ArrayList<FoodItem> foodItems;

    public int [] foodImages = {R.drawable._fries,R.drawable._pizza,R.drawable._fried_rice,R.drawable._biriyani,R.drawable._chinese_noodles};
    public Integer ImageIndex = 0;
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
        holder.foodDescription.setText(foodItem.description);
        holder.foodImage.setImageResource(foodImages[ImageIndex]);
        ImageIndex = (ImageIndex + 1) % foodImages.length;
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
        public android.widget.TextView foodDescription;
        public FoodItemViewHolder(android.view.View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.unitPrice);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodDescription = itemView.findViewById(R.id.description);
        }
    }
}
