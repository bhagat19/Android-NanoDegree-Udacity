package com.example.amit.popular_moviesapp;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by amit on 15-04-2016.
 */
public class ReviewAdapter extends ArrayAdapter<ReviewItem> {
    Context context;
    int layoutResourceId;
    ArrayList<ReviewItem> reviewItemArrayList = new ArrayList<>();

    public ReviewAdapter(Context context, int layoutResource, ArrayList<ReviewItem> reviewItems){

        super(context,layoutResource,reviewItems);
        this.context = context;
        this.layoutResourceId = layoutResource;
        this.reviewItemArrayList = reviewItems;
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent){

        viewHolder holder;

        LayoutInflater inflater = LayoutInflater.from(context);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.review,parent,false);
            holder = new viewHolder();
            holder.author = (TextView) convertView.findViewById(R.id.author_name);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        }
        else{
            holder = (viewHolder)convertView.getTag();
        }

        ReviewItem review = reviewItemArrayList.get(position);
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());

        return convertView;
    }

    private static class viewHolder{

        private TextView author;
        private TextView content;
    }

}
