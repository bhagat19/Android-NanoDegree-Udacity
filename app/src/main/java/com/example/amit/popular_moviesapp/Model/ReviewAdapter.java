package com.example.amit.popular_moviesapp.Model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.amit.popular_moviesapp.R;

import java.util.ArrayList;

/**
 * Created by amit on 15-04-2016.
 */
public class ReviewAdapter extends ArrayAdapter<ReviewItem> {
    Context context;
    int layoutResourceId;
    ArrayList<ReviewItem> reviewItemArrayList = new ArrayList<>();
    String LOG_TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Context context, int layoutResource, ArrayList<ReviewItem> reviewItems) {

        super(context, layoutResource, reviewItems);
        this.context = context;
        this.layoutResourceId = layoutResource;
        this.reviewItemArrayList = reviewItems;
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder holder;

        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new viewHolder();
            holder.author = (TextView) convertView.findViewById(R.id.author_name);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.noReviews = (TextView) convertView.findViewById(R.id.noReviews);
            //        holder.content.setMovementMethod(new ScrollingMovementMethod());
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

        ReviewItem review = reviewItemArrayList.get(position);

        Log.v(LOG_TAG, "reviewList :" + reviewItemArrayList);
        if (reviewItemArrayList != null) {
            holder.author.setText(review.getAuthor());
            holder.content.setText(review.getContent());
        } else
            holder.noReviews.setVisibility(View.VISIBLE);

        return convertView;
    }

    private static class viewHolder {

        private TextView author;
        private TextView content;
        private TextView noReviews;
    }

}
