package com.example.virtualwaiter.recycledview;


import com.example.virtualwaiter.R;
import com.example.virtualwaiter.datatypes.OrderItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class OrderListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<OrderListAdapter.OrderItemViewHolder>{
    public ArrayList<OrderItem> orderItems;
    public Picasso picasso;

    public OrderListAdapter(ArrayList<OrderItem> orderItems){
        this.orderItems = orderItems;
    }
    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        picasso = Picasso.get();
        return new OrderItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.foodName.setText(orderItem.getName());
        holder.foodPrice.setText(orderItem.getPrice().toString());
        holder.quantity.setText(orderItem.getQuantity().toString());
        holder.totalPrice.setText(orderItem.getTotalPrice().toString());
        picasso.load(orderItem.getImage()).error(R.drawable.baseline_emoji_food_beverage_24).into(holder.foodImage);
        holder.foodImage.setClipToOutline(true);
        holder.status.setText(orderItem.getStatus());
        holder.status.setTextColor(ContextCompat.getColor(holder.status.getContext(), getColor(orderItem.getStatus())));
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
        public TextView status;
        public OrderItemViewHolder(android.view.View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodImage = itemView.findViewById(R.id.foodImage);
            quantity = itemView.findViewById(R.id.orderQuantity);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            status = itemView.findViewById(R.id.orderStatus);
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
