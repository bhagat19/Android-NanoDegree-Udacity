package com.example.amit.popular_moviesapp;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
   // Context mContext;
    ArrayList<TrailerItem> trailerItemArrayList;
    ArrayList<ReviewItem> reviewItemArrayList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){

        return inflater.inflate(R.layout.detail_fragment,parent,false);

    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        Context context = getActivity();

        Bundle bundle = getArguments();

        final String TRAILERS = context.getString(R.string.TRAILER);
        final String REVIEWS = context.getString(R.string.REVIEW);



        if (bundle != null){

            mCurrentMovie = bundle.getParcelable(MovieFragment.MOVIE);
            String movieId = mCurrentMovie.getMovie().get("id");
           // bundle.get("movie");


            // new FetchTrailersAndReviews(context).execute(movieId,TRAILERS);
            // new FetchTrailersAndReviews(context).execute(movieId,REVIEWS);

            FetchTrailersAndReviews ft = new FetchTrailersAndReviews(context);


            ft.execute(movieId,TRAILERS);
            trailerItemArrayList = ft.mCurrentMovieTrailerData;
            ft.execute(movieId,REVIEWS);
            reviewItemArrayList = ft.mCurrentMovieReviewData;


        }

       ListView reviewList = (ListView) view.findViewById(R.id.reviews_listView);
        ListView trailerList = (ListView) view.findViewById(R.id.trailers_listview);



        reviewList.setAdapter(new ReviewAdapter(context,R.layout.review,new ArrayList<ReviewItem>()));
        trailerList.setAdapter(new TrailerAdapter(context,R.layout.trailer,new ArrayList<TrailerItem>()));
        trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String link = trailerItemArrayList.get(position).getLink();
                Intent startTrailer = new Intent(Intent.ACTION_SEND, Uri.parse(link));
                startActivity(startTrailer);

            }
        });

        String posterUrl = mCurrentMovie.getMovie().get(context.getString(R.string.movie_poster_url));
        String backgroundImg = mCurrentMovie.getMovie().get(context.getString(R.string.movie_background_url));


        backgroundImage = (ImageView) view.findViewById(R.id.bckground_Img);
        thumbnail = (ImageView) view.findViewById(R.id.poster);
        genre = (TextView) view.findViewById(R.id.movie_genre);
        releaseDate = (TextView) view.findViewById(R.id.movie_releaseDate);
        ratings = (RatingBar) view.findViewById(R.id.movie_ratings);
        overView = (TextView) view.findViewById(R.id.movie_overview);


        Picasso.with(context).load(posterUrl).into(thumbnail);
        Picasso.with(context).load(backgroundImg).into(backgroundImage);

        ratings.setRating(Float.valueOf(mCurrentMovie.getMovie().
                get(context.getString(R.string.movie_vote_average))) / 2);

        overView.setText(mCurrentMovie.getMovie().
                get(context.getString(R.string.movie_overview)));

        genre.setText(mCurrentMovie.getMovie().
                get(context.getString(R.string.movie_genres)));

        ((TextView) view.findViewById(R.id.title)).
                setText(mCurrentMovie.getMovie().get(context.getString(R.string.movie_title)));

        ((TextView) view.findViewById(R.id.movie_releaseDate)).
                setText(mCurrentMovie.getMovie().get(context.getString(R.string.movie_release_date)));








    }
}
