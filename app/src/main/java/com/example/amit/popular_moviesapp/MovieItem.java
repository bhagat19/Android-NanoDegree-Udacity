package com.example.amit.popular_moviesapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by amit on 05-04-2016.
 */
public class MovieItem implements Parcelable {

    private int ImageId;
    private String title;
    HashMap<String, String> movieMap;
    Context context;


    public MovieItem(HashMap<String, String> movieMap) {
        this.movieMap = movieMap;

    }

    public HashMap<String, String> getMovie() {
        return movieMap;
    }


    protected MovieItem(Parcel in) {
        movieMap = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(movieMap);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}

