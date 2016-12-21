package com.example.amit.popular_moviesapp.Ui;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amit.popular_moviesapp.Database.MovieContract;
import com.example.amit.popular_moviesapp.Model.MovieItem;
import com.example.amit.popular_moviesapp.Model.ReviewAdapter;
import com.example.amit.popular_moviesapp.Model.ReviewItem;
import com.example.amit.popular_moviesapp.Model.TrailerAdapter;
import com.example.amit.popular_moviesapp.Model.TrailerItem;
import com.example.amit.popular_moviesapp.R;
import com.example.amit.popular_moviesapp.Util.Utility;
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
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by amit on 15-04-2016.
 */
public class MovieDetailFragment extends Fragment {

    final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    ImageView backgroundImage;
    ImageView thumbnail;
    TextView genre;
    TextView releaseDate;
    RatingBar ratings;
    TextView overView;
    MovieItem mCurrentMovie;
    ImageButton favButton;
    TextView ratingsInput;
    TextView noReviews;
    // Context mContext;
    ArrayList<TrailerItem> trailerItemArrayList = new ArrayList<>();
    ArrayList<ReviewItem> reviewItemArrayList = new ArrayList<>();
    // Context mContext;
    boolean mFavoriteButtonStatus;
    View mRootView;
    Context mContext;
    ReviewAdapter mReviewAdapter;
    TrailerAdapter mTrailerAdapter;
    LayoutInflater mInflater;
    String mFavMsg;


    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.detail_fragment, parent, false);
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


            initTrailerAndReviewViews();

            new FetchReviewsTask(mContext).execute(movieId, REVIEWS);
            new FetchTrailersTask(mContext).execute(movieId, TRAILERS);

            mFavoriteButtonStatus = Utility.isMovieFavorite(mContext, movieId);
            updateFavoriteButton(mRootView, mFavoriteButtonStatus);

            //Initiating Detail Fragment views
            String posterUrl = mCurrentMovie.getMovie().get(mContext.getString(R.string.movie_poster_url));
            String backgroundImg = mCurrentMovie.getMovie().get(mContext.getString(R.string.movie_background_url));


            favButton = (ImageButton) view.findViewById(R.id.favorite_button);
            backgroundImage = (ImageView) view.findViewById(R.id.bckground_Img);
            thumbnail = (ImageView) view.findViewById(R.id.poster);
            ratingsInput = (TextView) view.findViewById(R.id.movie_detail_ratings);
            releaseDate = (TextView) view.findViewById(R.id.movie_releaseDate);
            ratings = (RatingBar) view.findViewById(R.id.movie_ratings);
            overView = (TextView) view.findViewById(R.id.movie_overview);
            noReviews = (TextView) (mInflater.inflate(R.layout.review, null, false)).
                    findViewById(R.id.noReviews);


            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //     Log.v(LOG_TAG, "Favourite Button start "+mFavoriteButtonStatus);
                    mFavoriteButtonStatus = !mFavoriteButtonStatus;
                    Log.v(LOG_TAG, "Favourite Button now " + mFavoriteButtonStatus);
                    updateFavoriteButton(mRootView, mFavoriteButtonStatus);
                    Log.v(LOG_TAG, "fav msg " + mFavMsg);

                    Toast toast = Toast.makeText(getActivity(), mFavMsg, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 100);
                    toast.show();

                    new FetchMovieDB(mContext).execute(
                            mCurrentMovie.getMovie(),
                            reviewItemArrayList,
                            trailerItemArrayList);


                }
            });

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

    private long updateFavTable(HashMap<String, String> MovieMap) {
        long inserted_id = -1;

        String movieId = MovieMap.get(mContext.getString(R.string.movie_id));

        Uri uri = MovieContract.FavoriteEntry.CONTENT_URI;
        String[] projection = {MovieContract.FavoriteEntry._ID};
        String selection = MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = {movieId};

        Cursor cursor = mContext.getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                null);
        if (cursor.moveToFirst()) {
            mContext.getContentResolver().delete(
                    uri, selection, selectionArgs);
            return inserted_id;

        } else {
            ContentValues contentValues = new ContentValues();

            //         String title = MovieMap.get(mContext.getString(R.string.movie_title));
            contentValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, MovieMap.get(
                    mContext.getString(R.string.movie_id)));
            contentValues.put(MovieContract.FavoriteEntry.COLUMN_ORIGINAL_TITLE, MovieMap.get(
                    mContext.getString(R.string.movie_title)));
            contentValues.put(MovieContract.FavoriteEntry.COLUMN_BACKDROP_IMG, MovieMap.get(
                    mContext.getString(R.string.movie_background_url)));
            contentValues.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_POSTER, MovieMap.get(
                    mContext.getString(R.string.movie_poster_url)));
            contentValues.put(MovieContract.FavoriteEntry.COLUMN_VOTE_AVG, MovieMap.get(
                    mContext.getString(R.string.movie_vote_average)));
            contentValues.put(MovieContract.FavoriteEntry.COLUMN_OVERVIEW, MovieMap.get(
                    mContext.getString(R.string.movie_overview)));
            contentValues.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE, MovieMap.get(
                    mContext.getString(R.string.movie_release_date)));


            Uri inserted_uri = mContext.getContentResolver().insert(
                    uri, contentValues);

            return ContentUris.parseId(inserted_uri);
        }
        //     cursor.close();
    }

    private void updateTrailerTable(ArrayList<TrailerItem> TrailerList, long FavId) {
        if (TrailerList == null || TrailerList.size() == 0) {
            return;
        }

        Uri uri = MovieContract.TrailerEntry.CONTENT_URI;
        //      String[] projection = {MovieContract.TrailerEntry.COLUMN_FAVORITE_RECORD_ID};
        String selection = MovieContract.TrailerEntry.COLUMN_FAVORITE_RECORD_ID + " = ?";
        String[] selectionArgs = {Long.toString(FavId)};

        Cursor cursor = mContext.getContentResolver().query(
                uri,
                new String[]{MovieContract.TrailerEntry._ID},
                selection,
                selectionArgs,
                null);

        if (cursor.moveToFirst()) {
            mContext.getContentResolver().delete(
                    uri,
                    selection,
                    selectionArgs);
        } else {
            Vector<ContentValues> trailerVector = new Vector<>(TrailerList.size());
            for (int i = 0; i < TrailerList.size(); i++) {
                ContentValues trailerValues = new ContentValues();
                //      ArrayList<TrailerItem> trailerItem = TrailerList.get(i);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_LINK,
                        TrailerList.get(i).getLink());
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY,
                        TrailerList.get(i).getKey());
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
                        TrailerList.get(i).getName());
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_FAVORITE_RECORD_ID, FavId);

                trailerVector.add(trailerValues);
            }

            if (trailerVector.size() > 0) {
                ContentValues[] cVArray = new ContentValues[trailerVector.size()];
                trailerVector.toArray(cVArray);
                mContext.getContentResolver().bulkInsert(
                        uri, cVArray);
            }
        }
        cursor.close();
    }

    private void updateReviewTable(ArrayList<ReviewItem> ReviewList, long favId) {

        if (ReviewList.size() == 0) {
            return;
        }

        Uri uri = MovieContract.ReviewEntry.CONTENT_URI;
        String[] projection = {MovieContract.ReviewEntry._ID};
        String selection = MovieContract.ReviewEntry.COLUMN_FAVORITE_RECORD_ID + " = ?";
        String[] selectionArgs = {Long.toString(favId)};

        Cursor cursor = mContext.getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                null);
        if (cursor.moveToFirst()) {
            mContext.getContentResolver().delete(
                    uri, selection, selectionArgs);
        } else {

            Vector<ContentValues> reviewVector = new Vector<ContentValues>(ReviewList.size());

            for (int i = 0; i < ReviewList.size(); i++) {
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR_NAME,
                        ReviewList.get(i).getAuthor());
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
                        ReviewList.get(i).getContent());
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_FAVORITE_RECORD_ID, favId);

                reviewVector.add(reviewValues);
            }
            if (reviewVector.size() != 0) {
                ContentValues[] cVReview = new ContentValues[reviewVector.size()];
                reviewVector.toArray(cVReview);
                mContext.getContentResolver().bulkInsert(
                        uri,
                        cVReview);
            }
        }
        cursor.close();


    }

    public void updateFavoriteButton(View view, boolean mFavoriteButtonStatus) {
        favButton = (ImageButton) view.findViewById(R.id.favorite_button);
        if (mFavoriteButtonStatus == true) {
            favButton.setImageResource(R.mipmap.ic_favorite_black_36dp);
            mFavMsg = "Added to Favorites";

        } else {
            favButton.setImageResource(R.mipmap.ic_favorite_border_black_36dp);
            mFavMsg = "Removed from Favorites";
        }
    }

    public void initTrailerAndReviewViews() {

        ListView reviewList = (ListView) mRootView.findViewById(R.id.reviews_listView);
        ListView trailerList = (ListView) mRootView.findViewById(R.id.trailers_listview);

        mReviewAdapter = new ReviewAdapter(mContext, R.layout.review, new ArrayList<ReviewItem>());
        mTrailerAdapter = new TrailerAdapter(mContext, R.layout.trailer, new ArrayList<TrailerItem>());


        reviewList.setAdapter(mReviewAdapter);
        trailerList.setAdapter(mTrailerAdapter);


        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link = trailerItemArrayList.get(position).getLink();
                Intent startTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(startTrailer);

            }
        });
    }

    public ArrayList getTrailersAndReviewsFromJson(Context context, String movieTrailerAndRevJson, String identifier)
            throws JSONException {

        final String PMA_RESULTS = context.getString(R.string.movie_results);
        final String PMA_MOVIEID = context.getString(R.string.movie_id);

        final String MDB_NAME = context.getString(R.string.trailer_name);
        final String MDB_KEY = context.getString(R.string.trailer_key);
        final String TRAILER_LINK = context.getString(R.string.trailer_link);

        final String MDB_RESULTS = context.getString(R.string.review_results);
        final String MDB_AUTHOR = context.getString(R.string.review_author);
        final String MDB_CONTENT = context.getString(R.string.review_content);

        if (identifier.equals(mContext.getString(R.string.TRAILER))) {
            JSONObject movieTrailers = new JSONObject(movieTrailerAndRevJson);
            JSONArray results = movieTrailers.getJSONArray(PMA_RESULTS);
            int numTrailers = results.length();
            ArrayList<TrailerItem> trailerList = new ArrayList<>();

            String YOUTUBE_BASE_URL = "https://www.youtube.com/";

            String TRAILER_ACTION = "watch";
            String TRAILER_PARAM = "v";

            for (int i = 0; i < numTrailers; i++) {
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
            Log.v(LOG_TAG, "TrailerList From Method :" + trailerList);
            return trailerList;
        } else {
            Log.v(LOG_TAG, "Inside getReviewsMethod" + movieTrailerAndRevJson);

            JSONObject movieReviews = new JSONObject(movieTrailerAndRevJson);
            JSONArray results = movieReviews.getJSONArray(PMA_RESULTS);
            int numReviews = results.length();
            ArrayList reviewList = new ArrayList();


            for (int i = 0; i < numReviews; i++) {
                //  HashMap<String,String> reviewMap = new HashMap<>();
                JSONObject reviewData = results.getJSONObject(i);

                String author = reviewData.getString(MDB_AUTHOR);
                String content = reviewData.getString(MDB_CONTENT);
                Log.v(LOG_TAG, "content :" + content);

                reviewList.add(new ReviewItem(author, content));


                return reviewList;


            }
        }


        return null;
    }

    public class FetchMovieDB extends AsyncTask<Object, Void, Void> {

        public FetchMovieDB(Context context) {
        }

        @Override
        protected Void doInBackground(Object... params) {

            long id = updateFavTable((HashMap<String, String>) params[0]);
            updateTrailerTable((ArrayList<TrailerItem>) params[2], id);
            updateReviewTable((ArrayList<ReviewItem>) params[1], id);

            return null;

        }


    }

    public class FetchTrailersTask extends AsyncTask<String, Void, ArrayList> {

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

            String sortOrder = Utility.getSortOrder(context);
            String movieId = params[0];
            if (sortOrder.equals(context.getString(R.string.pref_sort_favourite))) {
                Cursor cursor = mContext.getContentResolver().query(
                        MovieContract.TrailerEntry.CONTENT_URI,
                        null,
                        MovieContract.TrailerEntry.COLUMN_FAVORITE_RECORD_ID + " = ?",
                        new String[]{movieId},
                        null);
                if (cursor.moveToFirst()) {

                    Vector<ContentValues> trailerVector = new Vector<>(cursor.getCount());
                    for (int i = 0; i < cursor.getCount(); i++) {
                        ContentValues cv = new ContentValues();
                        DatabaseUtils.cursorRowToContentValues(cursor, cv);
                        trailerVector.add(cv);
                    }
                    //         return reviewVector;
                    ArrayList<TrailerItem> trailerList = new ArrayList<>();
                    for (int i = 0; i < trailerVector.size(); i++) {
                        ContentValues cv = trailerVector.elementAt(i);
                        TrailerItem item = new TrailerItem((String) cv.get(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME),
                                (String) cv.get(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY),
                                (String) cv.get(MovieContract.TrailerEntry.COLUMN_TRAILER_LINK));
                        trailerList.add(item);
                    }
                    return trailerList;


                }
            }


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

    public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList> {

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

            String sortOrder = Utility.getSortOrder(context);
            String movieId = params[0];
            if (sortOrder.equals(context.getString(R.string.pref_sort_favourite))) {
                Cursor cursor = mContext.getContentResolver().query(
                        MovieContract.ReviewEntry.CONTENT_URI,
                        null,
                        MovieContract.ReviewEntry.COLUMN_FAVORITE_RECORD_ID + " = ?",
                        new String[]{movieId},
                        null);
                if (cursor.moveToFirst()) {

                    Vector<ContentValues> reviewVector = new Vector<>(cursor.getCount());
                    for (int i = 0; i < cursor.getCount(); i++) {
                        ContentValues cv = new ContentValues();
                        DatabaseUtils.cursorRowToContentValues(cursor, cv);
                        reviewVector.add(cv);
                    }
                    //         return reviewVector;
                    ArrayList<ReviewItem> reviewList = new ArrayList<>();
                    for (int i = 0; i < reviewVector.size(); i++) {
                        ContentValues cv = reviewVector.elementAt(i);
                        ReviewItem item = new ReviewItem((String) cv.get(MovieContract.ReviewEntry.COLUMN_AUTHOR_NAME),
                                (String) cv.get(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT));
                        reviewList.add(item);
                    }
                    return reviewList;


                }
            } else {

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
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList reviewList) {
            super.onPostExecute(reviewList);
            Log.v(LOG_TAG, "reviewList :" + reviewList);

            if (mReviewAdapter != null && reviewList != null) {
                reviewItemArrayList = reviewList;
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

            } else
                noReviews.setVisibility(View.VISIBLE);

        }
    }

}





