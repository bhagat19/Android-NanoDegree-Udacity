package com.example.amit.popular_moviesapp.Model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amit.popular_moviesapp.R;

import java.util.ArrayList;

/**
 * Created by amit on 22-04-2016.
 */
public class TrailerAdapter extends ArrayAdapter<TrailerItem> {
    Context context;
    int LayoutResourceId;
    ArrayList<TrailerItem> trailers;
    String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Context context, int LayoutResource, ArrayList<TrailerItem> trailers) {
        super(context, LayoutResource, trailers);
        this.context = context;
        this.LayoutResourceId = LayoutResource;
        this.trailers = trailers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder;

        Log.v(LOG_TAG, "Inside Trailer :");

        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.trailer, parent, false);
            holder = new viewHolder();
            holder.logo = (ImageView) convertView.findViewById(R.id.youtube_logo);
            holder.link = (TextView) convertView.findViewById(R.id.trailer_link);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }
        holder.logo.setImageResource(R.drawable.youtube_icon);
        holder.link.setText(trailers.get(position).getName());

        return convertView;
    }


    private static class viewHolder {
        ImageView logo;
        TextView link;
    }
}
