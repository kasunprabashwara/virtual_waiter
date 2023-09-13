package com.example.virtualwaiter.recycledview;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;

import com.example.virtualwaiter.MainActivity;
import com.example.virtualwaiter.R;
import com.example.virtualwaiter.datatypes.OrderItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class OrderListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<OrderListAdapter.OrderItemViewHolder>{
    public ArrayList<OrderItem> orderItems;

    public OrderListAdapter(ArrayList<OrderItem> orderItems){
        this.orderItems = orderItems;
    }
    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.foodName.setText(orderItem.name);
        holder.foodPrice.setText(orderItem.price.toString());
        holder.quantity.setText(orderItem.quantity.toString());
        holder.totalPrice.setText(orderItem.totalPrice.toString());
        String path ="https://i.ibb.co/m48NQyc/image-5.png";
        Picasso.get().load(path).error(R.drawable.baseline_emoji_food_beverage_24).into(holder.foodImage);
        holder.status.setText(orderItem.status);
        holder.status.setBackgroundColor(ContextCompat.getColor(holder.status.getContext(), getColor(orderItem.status)));
}

    @Override
    public int getItemCount() {
        return orderItems.size();
    }
    public static class OrderItemViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        public TextView foodName;
        public TextView foodPrice;
        public ImageView foodImage;
        public TextView quantity;
        public TextView totalPrice;
        public Button status;
        public OrderItemViewHolder(android.view.View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodImage = itemView.findViewById(R.id.foodImage);
            quantity = itemView.findViewById(R.id.orderQuantity);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            status = itemView.findViewById(R.id.orderStatus);
            Log.d("hey", "OrderItemViewHolder: " + foodName.getText());
        }
    }
    private int getColor(String status){
        switch (status){
            case "Ordered":
                return R.color.Ordered;
            case "Preparing":
                return R.color.Preparing;
            case "Prepared":
                return R.color.Prepared;
            case "Delivering":
                return R.color.Delivering;
            case "Delivered":
                return R.color.Success;
            default:
                return R.color.Ordered;
        }
    }
}
