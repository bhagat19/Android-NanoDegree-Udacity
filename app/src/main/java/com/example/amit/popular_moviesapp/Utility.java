package com.example.amit.popular_moviesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.example.amit.popular_moviesapp.Database.MovieContract;

/**
 * Created by amit on 27-04-2016.
 */
public class Utility {

    public static String getSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_popular));

    }

    public static boolean isMovieFavorite(Context context, String movieId) {
        String selection = MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = {movieId};

        Cursor cursor = context.getContentResolver().query(
                MovieContract.FavoriteEntry.CONTENT_URI,
                new String[]{MovieContract.FavoriteEntry._ID},
                selection,
                selectionArgs,
                null);

        if (cursor.moveToFirst()) {
            return true;
        } else
            return false;
    }
}

