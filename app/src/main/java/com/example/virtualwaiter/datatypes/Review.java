package com.example.virtualwaiter.datatypes;

public class Review {
    private String review;
    private Integer rating;
    public Review(String review, Integer rating){
        this.review = review;
        this.rating = rating;
    }
    public Review(Integer rating){
        this.rating = rating;
    }
}
