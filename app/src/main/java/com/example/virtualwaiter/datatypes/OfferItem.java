package com.example.virtualwaiter.datatypes;

import android.media.Image;

public class OfferItem {
    private String name;
    private int image;

    public OfferItem(String name, int image){
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}
