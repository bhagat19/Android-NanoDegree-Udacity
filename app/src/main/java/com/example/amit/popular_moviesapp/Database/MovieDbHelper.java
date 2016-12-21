package com.example.amit.popular_moviesapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.amit.popular_moviesapp.Database.MovieContract.FavoriteEntry.TABLE_NAME;

/**
 * Created by amit on 20-05-2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        String tag = this.getClass().getName();
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE, " +
                MovieContract.FavoriteEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +

                MovieContract.FavoriteEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_BACKDROP_IMG + " TEXT NOT NULL, " +

                //             MovieContract.FavoriteEntry.COLUMN_GENRES + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL" +
                ");";

        //Printing the favorite sql statement
        Log.v(tag, SQL_CREATE_FAVORITE_TABLE);

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " (" +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MovieContract.TrailerEntry.COLUMN_FAVORITE_RECORD_ID + " INTEGER NOT NULL, " +

                MovieContract.TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +

                //This will contain the key for the movie trailer on youtube
                MovieContract.TrailerEntry.COLUMN_TRAILER_LINK + " TEXT NOT NULL, " +

                // Set up the movie key column as a foreign key to  favorite table.
                " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_FAVORITE_RECORD_ID + ") REFERENCES " +
                MovieContract.FavoriteEntry.TABLE_NAME + " (" + MovieContract.FavoriteEntry._ID + ")" +
                ");";

        Log.v(tag, SQL_CREATE_TRAILER_TABLE);

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                MovieContract.ReviewEntry.COLUMN_FAVORITE_RECORD_ID + " INTEGER NOT NULL, " +

                MovieContract.ReviewEntry.COLUMN_AUTHOR_NAME + " TEXT NOT NULL, " +

                // The content of the author's review
                MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +

                // Set up the movie key column as a foreign key to  favorite table.
                " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_FAVORITE_RECORD_ID + ") REFERENCES " +
                MovieContract.FavoriteEntry.TABLE_NAME + " (" + MovieContract.FavoriteEntry._ID + ")" +
                " );";

        Log.v(tag, SQL_CREATE_REVIEW_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //Since the data in the database will be entered by the user, we don't want
        //to wipe the data when updating the database
        //We shall alter the table instead
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        //onCreate(sqLiteDatabase);
        sqLiteDatabase.execSQL("ALTER TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + " ADD COLUMN " +
                " COLUMN_NAME");
    }

}

