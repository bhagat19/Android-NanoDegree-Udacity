package com.example.amit.popular_moviesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

/**
 * Created by amit on 30-03-2016.
 */
public class MovieAdapter extends ArrayAdapter {
    Context context;
    ArrayList<MovieItem> movieList;
    private LayoutInflater mInflater;
    String LOG_TAG = MovieAdapter.class.getSimpleName();


    public MovieAdapter(Context context,ArrayList movies){
        super(context,R.layout.grid_view, movies);
        this.context = context;
        this.movieList = movies;
       // movieList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        Log.v(LOG_TAG,"MovieList :" +movieList);

    }

    public ArrayList<MovieItem> getAll(){return movieList;}

/*
    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
*/

    public void addAll(ArrayList<MovieItem> data) {
        movieList.addAll(data);
        this.notifyDataSetChanged();
    }



   // public void addAll( ArrayList<MovieItem> movieList){};

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder;
       // MovieItem movieItem = getItem(position);
      //  LayoutInflater Inflater = LayoutInflater.from(getContext());
        HashMap<String,String> movieDetails = movieList.get(position).getMovie();

        Log.v(LOG_TAG,"Inside getView");

        if (convertView == null) {
            Log.v(LOG_TAG,+position +" "+convertView);
            convertView = mInflater.inflate(R.layout.grid_view, parent, false);
            holder = new viewHolder();


            holder.thumbnail = (ImageView) convertView.findViewById(R.id.grid_thumbnail);
            holder.ratings = (RatingBar) convertView.findViewById(R.id.grid_item_ratingBar);
            holder.title = (TextView) convertView.findViewById(R.id.grid_item_text_title);

            convertView.setTag(holder);
        }
        else{
            holder = (viewHolder) convertView.getTag();
        }

        String posterString = movieDetails.get(context.getString(R.string.movie_poster_url));

  //      Log.v(LOG_TAG,"Movie Title :" +movieDetails.get(R.string.movie_title));

        Log.v(LOG_TAG,"Poster String :" +posterString);

        if (posterString != null){

            try {
                Picasso.with(context).load(posterString).into(holder.thumbnail);
                holder.title.setText(movieDetails.get(context.getString(R.string.movie_title)));
                holder.ratings.setRating(Float.valueOf(movieDetails.get(context.getString(R.string.movie_vote_average)))/2);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

      //  holder.thumbnail.setImageResource(movieItem.getImageId());
       // holder.title.setText(movieItem.getTitle());
        //holder.ratings.toString().valueOf(movieItem.getRatings());


        return convertView;


    }
    private static class viewHolder{

        private ImageView thumbnail;
        private TextView title;
        private RatingBar ratings;

    }
}
