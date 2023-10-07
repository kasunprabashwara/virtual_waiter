package com.example.virtualwaiter.datatypes;

public class FoodItem {
    private String name;
    private Integer price;
    private String description;
    private String image;

    public FoodItem(String name, Integer price, String description, String image){
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }
}
