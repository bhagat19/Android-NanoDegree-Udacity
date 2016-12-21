package com.example.amit.popular_moviesapp.Model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by amit on 05-04-2016.
 */
public class MovieItem implements Parcelable {

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
    HashMap<String, String> movieMap;
    Context context;
    private int ImageId;
    private String title;

    public MovieItem(HashMap<String, String> movieMap) {
        this.movieMap = movieMap;

    }


    protected MovieItem(Parcel in) {
        movieMap = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    public HashMap<String, String> getMovie() {
        return movieMap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(movieMap);
    }
}

