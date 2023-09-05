package com.example.virtualwaiter.recycledview;


import android.util.Log;

import com.example.virtualwaiter.R;
import com.example.virtualwaiter.datatypes.OrderItem;
import java.util.ArrayList;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

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
        holder.foodImage.setImageResource(orderItem.image);
        holder.status.setText(orderItem.status);
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
}
