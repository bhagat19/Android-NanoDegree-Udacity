package com.example.amit.popular_moviesapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by amit on 23-04-2016.
 */
public class FetchTrailersAndReviews extends AsyncTask<String,Void,ArrayList> {

    Context context;
    StringBuilder builder;
    String line;
    BufferedReader reader;
    String movieTrailAndRevJson;
    String identifier;
    String LOG_TAG = FetchTrailersAndReviews.class.getSimpleName();
    TrailerAdapter mTrailerAdapter;
    ReviewAdapter mReviewAdapter;
     ArrayList<TrailerItem> mCurrentMovieTrailerData;
    ArrayList<ReviewItem> mCurrentMovieReviewData;


    public FetchTrailersAndReviews(Context context){
        this.context = context;
    }


    @Override
    protected ArrayList doInBackground(String...params){


            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEY_PARAM = "api_key";
          //  final String REQUIRED_TYPE = "videos";
             HttpURLConnection connection = null;

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(params[0] + "/")
                    .appendEncodedPath(params[1])
                    .appendQueryParameter(KEY_PARAM, context.getString(R.string.APIKEY))
                    .build();
            try{
                identifier = params[1];
                URL url = new URL(builtUri.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream in = connection.getInputStream();
                builder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(in));

                while ((line = reader.readLine()) != null){
                    builder.append(line);
                }

                movieTrailAndRevJson = builder.toString();
            }
            catch (IOException exception) {
                exception.printStackTrace();
    }finally {
                if (connection != null) {
                    connection.disconnect();
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
            return getTrailersAndReviewsFromJson(movieTrailAndRevJson, identifier);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
            }

    @Override
    protected void onPostExecute(ArrayList trailOrReviews) {
        super.onPostExecute(trailOrReviews);
        if (trailOrReviews != null) {

            if (identifier.equals("videos")) {

                mTrailerAdapter.clear();
                mCurrentMovieTrailerData = trailOrReviews;
                for (TrailerItem trailerItem : mCurrentMovieTrailerData) {
                    mTrailerAdapter.add(trailerItem);

                }
            } else {
                mReviewAdapter.clear();
                mCurrentMovieReviewData = trailOrReviews;
                for (ReviewItem reviewItem : mCurrentMovieReviewData) {
                    mReviewAdapter.add(reviewItem);
                }

            }
        }
    }

    public ArrayList getTrailersAndReviewsFromJson(String movieTrailerAndRevJson, String identifier) throws JSONException{

        final String PMA_RESULTS = context.getString(R.string.movie_results);
        final String PMA_MOVIEID = context.getString(R.string.movie_id);

        final String MDB_NAME = context.getString(R.string.trailer_name);
        final String MDB_KEY = context.getString(R.string.trailer_key);
        final String TRAILER_LINK = context.getString(R.string.trailer_link);

        final String MDB_RESULTS = context.getString(R.string.review_results);
        final String MDB_AUTHOR = context.getString(R.string.review_author);
        final String MDB_CONTENT = context.getString(R.string.review_content);

        if (identifier.equals("videos")){
            JSONObject movieTrailers = new JSONObject(movieTrailerAndRevJson);
            JSONArray results = movieTrailers.getJSONArray(PMA_RESULTS);
            int numTrailers = results.length();
            ArrayList<TrailerItem> trailerList = new ArrayList<>();

            String YOUTUBE_BASE_URL = "https://www.youtube.com/";

            String TRAILER_ACTION = "watch";
            String TRAILER_PARAM = "v";

            for (int i=0; i<numTrailers; i++){
              //  HashMap<String,String> trailerMap = new HashMap<>();
                JSONObject trailerData = results.getJSONObject(i);

                String key = trailerData.getString(MDB_KEY);
                String name = trailerData.getString(MDB_NAME);

                Uri trailerUri;
                trailerUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                        .appendEncodedPath(TRAILER_ACTION)
                        .appendQueryParameter(TRAILER_PARAM, key)
                        .build();
                trailerList.add(new TrailerItem(name,key,trailerUri.toString()));

                return trailerList;
            }
        }
        else{
            JSONObject movieReviews = new JSONObject(movieTrailerAndRevJson);
            JSONArray results = movieReviews.getJSONArray(PMA_RESULTS);
            int numReviews = results.length();
            ArrayList reviewList = new ArrayList();


            for (int i=0; i<numReviews; i++){
              //  HashMap<String,String> reviewMap = new HashMap<>();
                JSONObject reviewData = results.getJSONObject(i);

                String author = reviewData.getString(MDB_AUTHOR);
                String content = reviewData.getString(MDB_CONTENT);

                reviewList.add(new ReviewItem(author,content));

                return reviewList;
            }

        }
        return null;
    }

}
