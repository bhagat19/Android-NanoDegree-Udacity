package com.example.amit.popular_moviesapp.Model;

/**
 * Created by amit on 22-04-2016.
 */
public class TrailerItem {
    String link;
    String key;
    String name;

    public TrailerItem(String name, String key, String link) {
        this.link = link;
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getKey() {
        return key;
    }
}
