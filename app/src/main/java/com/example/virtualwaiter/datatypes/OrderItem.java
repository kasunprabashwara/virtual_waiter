package com.example.virtualwaiter.datatypes;

public class OrderItem {
    public String name;
    public int image;
    public Integer price;
    public Integer quantity;
    public Integer totalPrice;
    public String status;

    public OrderItem(String name, int image, Integer price, Integer quantity) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = price * quantity;
        this.status = "Ordered";
    }
}
