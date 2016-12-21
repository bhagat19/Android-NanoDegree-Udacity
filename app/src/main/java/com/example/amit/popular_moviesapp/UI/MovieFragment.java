package com.example.amit.popular_moviesapp.Ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.amit.popular_moviesapp.Model.MovieAdapter;
import com.example.amit.popular_moviesapp.Model.MovieItem;
import com.example.amit.popular_moviesapp.R;
import com.example.amit.popular_moviesapp.Util.FetchMovie;
import com.example.amit.popular_moviesapp.Util.InfiniteScrollListener;
import com.example.amit.popular_moviesapp.Util.Utility;

import java.util.ArrayList;

/**
 * Created by amit on 04-03-2016.
 */
public class MovieFragment extends Fragment {

    public static final String MOVIE = "MOVIE";
    private static final String SI_MOVIE_KEY = "SI_MOVIE_KEY";    //saved instance movie key
    private static final String SI_POS_KEY = "SI_POS_KEY";
    private static final String SI_SORT_KEY = "SI_SORT_KEY";
    String LOG_TAG = MovieFragment.class.getSimpleName();
    GridView mGridView;
    // View mRootView;
    TextView mTextView;
    Context mContext;
    ArrayList<MovieItem> movies = new ArrayList<>();
    MovieAdapter mAdapter;
    Callback mListener;
    String mPrevSort;
    String mCurrentSort;
    int START_PAGE = 1;
    private int mPosition = GridView.INVALID_POSITION;

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        //http://stackoverflow.com/questions/8215308/using-context-in-a-fragment#comment37298365_8215398
        //An instance of context from Activity is captured
        if (context instanceof Activity) {
            this.mListener = (Callback) context;
        }
        mContext = getActivity();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SI_SORT_KEY)) {
                mPrevSort = savedInstanceState.getString(SI_SORT_KEY);
            }

            if (savedInstanceState.containsKey(SI_MOVIE_KEY)) {
                movies = savedInstanceState.getParcelableArrayList(SI_MOVIE_KEY);
            }

            if (savedInstanceState.containsKey(SI_POS_KEY)) {
                mPosition = savedInstanceState.getInt(SI_POS_KEY);
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mAdapter.getCount() != 0) {
            movies = mAdapter.getAll();
        }

        if (movies != null) {
            outState.putParcelableArrayList(SI_MOVIE_KEY,
                    (ArrayList<? extends Parcelable>) movies);
        }

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SI_POS_KEY, mPosition);
        }

        outState.putString(SI_SORT_KEY, mCurrentSort);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {


        return inflater.inflate(R.layout.fragment_view, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {


        mGridView = (GridView) view.findViewById(R.id.gridView);
        mTextView = (TextView) view.findViewById(R.id.NoMoviesText);


        //   movies = savedInstance.getParcelableArrayList(SI_MOVIE_KEY);
        mAdapter = new MovieAdapter(mContext, movies);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MovieItem movieItem = (MovieItem) mAdapter.getItem(position);
                //On why Callback is needed, refer below link
                //http://developer.android.com/guide/components/fragments.html#EventCallbacks
                mListener.onItemSelected(movieItem);
            }
        });

    }

    public void onSortChanged(final String sortOrder) {
        mAdapter.clear();
        mPrevSort = mCurrentSort;
        mCurrentSort = Utility.getSortOrder(mContext);
        Log.v(LOG_TAG, "sort :" + mCurrentSort);

        new FetchMovie(mContext, mAdapter).execute(mCurrentSort, String.valueOf(START_PAGE));
        // movies = mAdapter.getAll();
//        updateMovieFragment();


        mGridView.setOnScrollListener(new InfiniteScrollListener(4) {

            @Override
            public void loadMore(int page, int totalItemsCount) {
                new FetchMovie(mContext, mAdapter).execute(mCurrentSort, String.valueOf(page));
            }
        });
    }

    public void updateMovieFragment() {

        mCurrentSort = Utility.getSortOrder(mContext);

    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

    }

    public interface Callback {
        void onItemSelected(MovieItem movie);
    }

}
