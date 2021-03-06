package com.example.amit.popular_moviesapp.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.GridView;

import com.example.amit.popular_moviesapp.Database.MovieContract;
import com.example.amit.popular_moviesapp.Model.MovieAdapter;
import com.example.amit.popular_moviesapp.Model.MovieItem;
import com.example.amit.popular_moviesapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by amit on 07-04-2016.
 */

public class FetchMovie extends AsyncTask<String, Void, ArrayList<MovieItem>> {

    final String LOG_TAG = FetchMovie.class.getSimpleName();
    Context mContext;
    MovieAdapter movieAdapter;
    GridView mGridView;
    LayoutInflater inflater;


    public FetchMovie(Context context, MovieAdapter movieAdapter) {
        this.mContext = context;
        this.movieAdapter = movieAdapter;
        inflater = LayoutInflater.from(context);

    }

    @Override
    protected ArrayList<MovieItem> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        String line;
        BufferedReader reader = null;
        StringBuilder builder;
        String movieJsonStr = "";
        String sortOrder = params[0];
        Log.v(LOG_TAG, "sortOrder " + sortOrder);


        if (sortOrder.equals(mContext.getString(R.string.pref_sort_favourite))) {


            Vector<ContentValues> vector = getFavMoviesFromDB();
            Log.v(LOG_TAG, "Favourite VEctor" + vector);
            return getMovieListFromContentValues(vector);
        }

        try {
            Uri.Builder uri = new Uri.Builder();
            uri.scheme("http").
                    authority("api.themoviedb.org").
                    appendPath("3").
                    appendPath("movie").
                    appendPath(sortOrder).
                    appendQueryParameter("page", params[1]).
                    appendQueryParameter("api_key", mContext.getString(R.string.APIKEY)).
                    build();
            //Check Network Connection


            URL url = new URL(uri.toString());
            Log.v(LOG_TAG, uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream in = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            // String line;
            reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            movieJsonStr += buffer.toString();
            Log.v(LOG_TAG, "Movie String: " + movieJsonStr);

        } catch (IOException exception) {
            movieJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }

            }

        }
        try {
            return getMoviesFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
        //return getMoviesFromJson(movieJsonStr);
    }


    @Override
    protected void onPostExecute(final ArrayList<MovieItem> movieList) {
        super.onPostExecute(movieList);
        //      movieAdapter.notifyDataSetChanged();

        if (movieList != null) {
            if ((Utility.getSortOrder(mContext)).equals(mContext.getString(R.string.pref_sort_favourite))) {
                movieAdapter.clear();
            }
            movieAdapter.addAll(movieList);
            movieAdapter.notifyDataSetChanged();
        }

    }

    public Vector<ContentValues> getFavMoviesFromDB() {

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);


        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
                contentValuesVector.add(contentValues);
            } while (cursor.moveToNext());

            return contentValuesVector;
        }
        return null;

    }

    public ArrayList<MovieItem> getMovieListFromContentValues(Vector<ContentValues> vector) {
        if (vector == null) {
            return null;
        }

        ArrayList<MovieItem> favoriteList = new ArrayList<>();
        for (int i = 0; i < vector.size(); i++) {

            HashMap<String, String> map = new HashMap<>();

            ContentValues cv = vector.elementAt(i);

            map.put(mContext.getString(R.string.movie_id),
                    (String) cv.get(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID));
            map.put(mContext.getString(R.string.movie_title),
                    (String) cv.get(MovieContract.FavoriteEntry.COLUMN_ORIGINAL_TITLE));
            map.put(mContext.getString(R.string.movie_poster_url),
                    (String) cv.get(MovieContract.FavoriteEntry.COLUMN_MOVIE_POSTER));
            map.put(mContext.getString(R.string.movie_release_date),
                    (String) cv.get(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE));
            map.put(mContext.getString(R.string.movie_background_url),
                    (String) cv.get(MovieContract.FavoriteEntry.COLUMN_BACKDROP_IMG));
            map.put(mContext.getString(R.string.movie_overview),
                    (String) cv.get(MovieContract.FavoriteEntry.COLUMN_OVERVIEW));
            map.put(mContext.getString(R.string.movie_vote_average),
                    (String) cv.get(MovieContract.FavoriteEntry.COLUMN_VOTE_AVG));

            MovieItem favMovie = new MovieItem(map);
            favoriteList.add(favMovie);
        }
        return favoriteList;
    }


    public ArrayList<MovieItem> getMoviesFromJson(String movieJsonStr) throws JSONException {

        final String PMA_RESULTS = mContext.getString(R.string.movie_results);
        final String PMA_MOVIEID = mContext.getString(R.string.movie_id);
        final String PMA_TITLE = mContext.getString(R.string.movie_title);

        final String PMA_RELEASEDATE = mContext.getString(R.string.movie_release_date);
        final String PMA_VOTEAVG = mContext.getString(R.string.movie_vote_average);
        final String PMA_OVERVIEW = mContext.getString(R.string.movie_overview);
        final String PMA_BG = mContext.getString(R.string.movie_background_url);
        final String PMA_POSTER = mContext.getString(R.string.movie_poster_url);
        final String POSTER_SIZE = mContext.getString(R.string.poster_size);
        final String BG_SIZE = mContext.getString(R.string.background_size);

        if (movieJsonStr == null) {
            return null;
        }

        JSONObject movieDetails = new JSONObject(movieJsonStr);
        JSONArray results = movieDetails.getJSONArray(PMA_RESULTS);

        int totalNumMovies = results.length();
        ArrayList<MovieItem> movieList = new ArrayList<>();
        String baseUrl = "http://image.tmdb.org/t/p/";


        for (int i = 0; i < totalNumMovies; i++) {


            HashMap<String, String> movieMap = new HashMap<>();
            JSONObject movieData = results.getJSONObject(i);

            movieMap.put(PMA_MOVIEID, movieData.getString(PMA_MOVIEID));
            //    movieMap.put(PMA_BG, movieData.getString(PMA_BG));
            movieMap.put(PMA_OVERVIEW, movieData.getString(PMA_OVERVIEW));
            //    movieMap.put(PMA_POSTER,movieData.getString(PMA_POSTER));
            movieMap.put(PMA_RELEASEDATE, movieData.getString(PMA_RELEASEDATE));
            movieMap.put(PMA_VOTEAVG, movieData.getString(PMA_VOTEAVG));
            movieMap.put(PMA_TITLE, movieData.getString(PMA_TITLE));

            //  Log.v(LOG_TAG, "MovieMap :" +movieMap);


            String posterUrl = movieData.getString(PMA_POSTER);
            String backgroundImgUrl = movieData.getString(PMA_BG);
            Uri moviePosterUri;
            Uri movieBackgroundUri;

            if (posterUrl != null) {

                moviePosterUri = Uri.parse(baseUrl).buildUpon().
                        appendEncodedPath(POSTER_SIZE).
                        appendEncodedPath(posterUrl).
                        build();
                movieMap.put(PMA_POSTER, moviePosterUri.toString());
            }

            if (backgroundImgUrl != null) {

                movieBackgroundUri = Uri.parse(baseUrl).buildUpon().
                        appendEncodedPath(BG_SIZE).
                        appendEncodedPath(backgroundImgUrl).
                        build();
                movieMap.put(PMA_BG, movieBackgroundUri.toString());
            }

            movieList.add(new MovieItem(movieMap));


        }
        //  Log.v(LOG_TAG,"MovieList :" +movieList);

        return movieList;
    }
}