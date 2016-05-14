package com.example.amit.popular_moviesapp;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import java.util.logging.Logger;

/**
 * Created by amit on 15-04-2016.
 */
public class MovieDetailFragment extends Fragment {

    ImageView backgroundImage;
    ImageView thumbnail;
    TextView genre;
    TextView releaseDate;
    RatingBar ratings;
    TextView overView;
    MovieItem mCurrentMovie;
    TextView ratingsInput;
    TextView noReviews;
   // Context mContext;
    ArrayList<TrailerItem> trailerItemArrayList = new ArrayList<>();
    ArrayList<ReviewItem> reviewItemArrayList = new ArrayList<>();
   // Context mContext;
    View mRootView;
    Context mContext;
    ReviewAdapter mReviewAdapter;
    TrailerAdapter mTrailerAdapter;
    LayoutInflater mInflater;
    final String LOG_TAG = MovieDetailFragment.class.getSimpleName();


    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){

       mRootView = inflater.inflate(R.layout.detail_fragment,parent,false);
        return mRootView;

    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

       mContext = getActivity();
        mInflater = LayoutInflater.from(mContext);

        Bundle bundle = getArguments();

        final String TRAILERS = mContext.getString(R.string.TRAILER);
        final String REVIEWS = mContext.getString(R.string.REVIEW);



        if (bundle != null) {

            mCurrentMovie = bundle.getParcelable(MovieFragment.MOVIE);
            String movieId = mCurrentMovie.getMovie().get("id");
            // bundle.get("movie");


            // new FetchTrailersAndReviews(context).execute(movieId,TRAILERS);
            // new FetchTrailersAndReviews(context).execute(movieId,REVIEWS);
            initTrailerAndReviewViews();
            new FetchReviewsTask(mContext).execute(movieId, REVIEWS);
            new FetchTrailersTask(mContext).execute(movieId, TRAILERS);

            //Initiating Detail Fragment views
            String posterUrl = mCurrentMovie.getMovie().get(mContext.getString(R.string.movie_poster_url));
            String backgroundImg = mCurrentMovie.getMovie().get(mContext.getString(R.string.movie_background_url));


            backgroundImage = (ImageView) view.findViewById(R.id.bckground_Img);
            thumbnail = (ImageView) view.findViewById(R.id.poster);
            ratingsInput = (TextView) view.findViewById(R.id.movie_detail_ratings);
            releaseDate = (TextView) view.findViewById(R.id.movie_releaseDate);
            ratings = (RatingBar) view.findViewById(R.id.movie_ratings);
            overView = (TextView) view.findViewById(R.id.movie_overview);
            noReviews = (TextView) (mInflater.inflate(R.layout.review,null,false)).
                    findViewById(R.id.noReviews);
  //          noReviews = (TextView) view.findViewById(R.id.noReviews);
         //   overView.setMovementMethod(new ScrollingMovementMethod());


            Picasso.with(mContext).load(posterUrl).into(thumbnail);
            Picasso.with(mContext).load(backgroundImg).into(backgroundImage);

            ratings.setRating(Float.valueOf(mCurrentMovie.getMovie().
                    get(mContext.getString(R.string.movie_vote_average))) / 2);

            overView.setText(mCurrentMovie.getMovie().
                    get(mContext.getString(R.string.movie_overview)));

            ratingsInput.setText(mCurrentMovie.getMovie().
                    get(mContext.getString(R.string.movie_vote_average)));

            ((TextView) view.findViewById(R.id.title)).
                    setText(mCurrentMovie.getMovie().get(mContext.getString(R.string.movie_title)));

            ((TextView) view.findViewById(R.id.movie_releaseDate)).
                    setText(mCurrentMovie.getMovie().get(mContext.getString(R.string.movie_release_date)));


        }
    }

        public void initTrailerAndReviewViews() {

        ListView reviewList = (ListView) mRootView.findViewById(R.id.reviews_listView);
        ListView trailerList = (ListView) mRootView.findViewById(R.id.trailers_listview);

            mReviewAdapter = new ReviewAdapter(mContext, R.layout.review, new ArrayList<ReviewItem>());
            mTrailerAdapter = new TrailerAdapter(mContext,R.layout.trailer, new ArrayList<TrailerItem>());



        reviewList.setAdapter(mReviewAdapter);
        trailerList.setAdapter(mTrailerAdapter);
            //
   /*         reviewList.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });
            */
/*
        reviewList.setOnTouchListener(new View.OnTouchListener(){
            @Override
            //http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
        }});
        */

        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link = trailerItemArrayList.get(position).getLink();
                Intent startTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(startTrailer);

            }
        });
    }



    public class FetchTrailersTask extends AsyncTask<String,Void,ArrayList> {

        Context context;
        StringBuilder builder;
        String line;
        BufferedReader reader;
        String movieTrailJson;
        String identifier;
        String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    //    TrailerAdapter mTrailerAdapter;
        //     ReviewAdapter mReviewAdapter;
        //    ArrayList<TrailerItem> mCurrentMovieTrailerData;
        //   ArrayList<ReviewItem> mCurrentMovieReviewData;


        public FetchTrailersTask(Context context) {
            this.context = context;
        }


        @Override
        protected ArrayList doInBackground(String... params) {


            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEY_PARAM = "api_key";
            //  final String REQUIRED_TYPE = "videos";
            HttpURLConnection connection = null;

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(params[0] + "/")
                    .appendEncodedPath(params[1])
                    .appendQueryParameter(KEY_PARAM, context.getString(R.string.APIKEY))
                    .build();
            try {
                identifier = params[1];
                URL url = new URL(builtUri.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream in = connection.getInputStream();
                builder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(in));

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                movieTrailJson = builder.toString();
                Log.v(LOG_TAG, "TrailJson :" + movieTrailJson);
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
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
                return getTrailersAndReviewsFromJson(context, movieTrailJson, identifier);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList trailerList) {
            super.onPostExecute(trailerList);

            if (trailerList.size() != 0 && mTrailerAdapter != null) {
                trailerItemArrayList = trailerList;
                Log.v(LOG_TAG, "TrailerList :" + trailerItemArrayList);
                mTrailerAdapter.clear();
                mTrailerAdapter.addAll(trailerItemArrayList);
            }


        }
    }

    public class FetchReviewsTask extends AsyncTask<String,Void,ArrayList> {

        Context context;
        StringBuilder builder;
        String line;
        BufferedReader reader;
        String movieReviewJson;
        String identifier;
        String LOG_TAG = FetchReviewsTask.class.getSimpleName();
        //     TrailerAdapter mTrailerAdapter;
        //    ReviewAdapter mReviewAdapter;
        //      ArrayList<TrailerItem> mCurrentMovieTrailerData;
        ArrayList<ReviewItem> mCurrentMovieReviewData;


        public FetchReviewsTask(Context context) {
            this.context = context;
        }


        @Override
        protected ArrayList doInBackground(String... params) {


            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEY_PARAM = "api_key";
            //  final String REQUIRED_TYPE = "videos";
            HttpURLConnection connection = null;

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(params[0] + "/")
                    .appendEncodedPath(params[1])
                    .appendQueryParameter(KEY_PARAM, context.getString(R.string.APIKEY))
                    .build();
            try {
                identifier = params[1];
                URL url = new URL(builtUri.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream in = connection.getInputStream();
                builder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(in));

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                movieReviewJson = builder.toString();
                Log.v(LOG_TAG, "ReviewJson :" + movieReviewJson);

            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
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
                return getTrailersAndReviewsFromJson(context, movieReviewJson, identifier);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;


        }

        @Override
        protected void onPostExecute(ArrayList reviewList) {
            super.onPostExecute(reviewList);
            Log.v(LOG_TAG, "reviewList :" + reviewList);
            reviewItemArrayList = reviewList;
            if (mReviewAdapter != null && reviewItemArrayList != null) {
                mReviewAdapter.clear();
                mReviewAdapter.addAll(reviewItemArrayList);
                mReviewAdapter.notifyDataSetChanged();
                //    }
                //        }
   /*     if (mReviewAdapter != null) {
                mReviewAdapter.clear();
                mReviewAdapter.addAll(reviewList);
                mReviewAdapter.notifyDataSetChanged();
            }
            */

            }
            else
                noReviews.setVisibility(View.VISIBLE);

        }
    }




        public ArrayList getTrailersAndReviewsFromJson(Context context,String movieTrailerAndRevJson, String identifier)
                throws JSONException{

            final String PMA_RESULTS = context.getString(R.string.movie_results);
            final String PMA_MOVIEID = context.getString(R.string.movie_id);

            final String MDB_NAME = context.getString(R.string.trailer_name);
            final String MDB_KEY = context.getString(R.string.trailer_key);
            final String TRAILER_LINK = context.getString(R.string.trailer_link);

            final String MDB_RESULTS = context.getString(R.string.review_results);
            final String MDB_AUTHOR = context.getString(R.string.review_author);
            final String MDB_CONTENT = context.getString(R.string.review_content);

            if (identifier.equals(mContext.getString(R.string.TRAILER))){
                JSONObject movieTrailers = new JSONObject(movieTrailerAndRevJson);
                JSONArray results = movieTrailers.getJSONArray(PMA_RESULTS);
                int numTrailers = results.length();
                ArrayList<TrailerItem> trailerList = new ArrayList<>();

                String YOUTUBE_BASE_URL = "https://www.youtube.com/";

                String TRAILER_ACTION = "watch";
                String TRAILER_PARAM = "v";

                for (int i=0; i<numTrailers; i++) {
                    //  HashMap<String,String> trailerMap = new HashMap<>();
                    JSONObject trailerData = results.getJSONObject(i);

                    String key = trailerData.getString(MDB_KEY);
                    String name = trailerData.getString(MDB_NAME);

                    Uri trailerUri;
                    trailerUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                            .appendEncodedPath(TRAILER_ACTION)
                            .appendQueryParameter(TRAILER_PARAM, key)
                            .build();
                    trailerList.add(new TrailerItem(name, key, trailerUri.toString()));
                }
                Log.v(LOG_TAG,"TrailerList From Method :" +trailerList);
                    return trailerList;
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
                    Log.v(LOG_TAG,"content :" +content);

                    reviewList.add(new ReviewItem(author,content));


                    return reviewList;


                }


            }
            return null;
        }

    }





