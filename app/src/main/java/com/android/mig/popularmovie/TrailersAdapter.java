package com.android.mig.popularmovie;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.mig.popularmovie.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder>{

    private Context mContext;
    private ArrayList<String> mTrailersArray;

    public TrailersAdapter(Context context){
        mContext = context;
        mTrailersArray = new ArrayList<>();
    }

    public void setTrailersData(ArrayList<String> trailersArray){
        mTrailersArray = trailersArray;
        notifyDataSetChanged();
    }

    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_trailer_thumbnail, parent, false);
        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder holder, int position) {
        // getS thumbnail from array and display it on viewVideo
        String trailerKey = mTrailersArray.get(position);
        String thumbnailPath = NetworkUtils.YOUTUBE_THUMBNAIL_AUTHORITY + trailerKey + NetworkUtils.YOUTUBE_THUMBNAIL_ENDPOINT;
        Picasso.with(mContext).load(thumbnailPath).into(holder.mTrailerThumbnail);
    }

    @Override
    public int getItemCount() {
        return mTrailersArray.size();
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder {
        public ImageView mTrailerThumbnail;

        public TrailersAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerThumbnail = (ImageView) itemView.findViewById(R.id.iv_trailer_thumbnail);
        }
    }
}
