package com.android.mig.popularmovie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Miguel on 12/21/2016.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>{

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185/"; // valid values: "w92/", "w154/", "w185/", "w342/", "w500/", "w780/", "original/"
    private ArrayList<Movie> mMoviesData;

    private Context mContext;

    public MoviesAdapter(Context context){
        mContext = context;
    }

    /**
     * Sets the Adapter with an ArrayList containing the data
     *
     * @param moviesData ArrayList that contains the data
     */
    public void setMoviesData(ArrayList<Movie> moviesData){
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_movie_poster, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        String path = mMoviesData.get(position).getPosterPath();
        Picasso.with(mContext).load(BASE_URL + IMAGE_SIZE + path).into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData != null){
            return mMoviesData.size();
        }
        return 0;
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder{
        public final ImageView mMoviePoster;

        public MoviesAdapterViewHolder(View view){
            super(view);
            mMoviePoster = (ImageView)view.findViewById(R.id.iv_item_movie_poster);
        }
    }
}
