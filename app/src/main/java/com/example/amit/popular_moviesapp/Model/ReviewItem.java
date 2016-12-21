package com.example.amit.popular_moviesapp.Model;

/**
 * Created by amit on 15-04-2016.
 */
public class ReviewItem {
    String author;
    String content;

    public ReviewItem(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
