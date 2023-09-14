package com.example.virtualwaiter.datatypes;

public class FoodItem {
    public String name;
    public Integer price;
    public String description;
    public String image;

    public FoodItem(String name, Integer price, String description, String image){
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
    }
}
