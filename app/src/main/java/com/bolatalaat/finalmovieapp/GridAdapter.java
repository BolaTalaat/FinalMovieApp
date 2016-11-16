package com.bolatalaat.finalmovieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Boal on 9/22/2016.
 */
public class GridAdapter extends ArrayAdapter<ModelData> {

    public GridAdapter(Context context) {
        super(context, 0);
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_grid, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        ModelData modelData = getItem(position);
        String image_url = "http://image.tmdb.org/t/p/w185" + modelData.getImage(); // setup image url

        viewHolder = (ViewHolder) view.getTag();

        Picasso.with(getContext())
                .load(image_url)
                .into(viewHolder.movieImgV); // load image from url into ImageView
        viewHolder.movieTitleTv.setText(modelData.getTitle()); // set modelData title to TextView
        return view;
    }

    public static class ViewHolder {
        public final ImageView movieImgV;
        public final TextView movieTitleTv;

        public ViewHolder(View view) {
            movieImgV = (ImageView) view.findViewById(R.id.movieImgV);
            movieTitleTv = (TextView) view.findViewById(R.id.movieTitleTv);
        }
    }
}