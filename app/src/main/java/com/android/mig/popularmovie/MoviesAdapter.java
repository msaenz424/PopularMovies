package com.android.mig.popularmovie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

    private ArrayList<Movie> mMoviesData;

    private Context mContext;
    final private MovieAdapterOnClickHandler movieAdapterOnClickHandle;

    public MoviesAdapter(Context context, MovieAdapterOnClickHandler clickHandler){
        mContext = context;
        movieAdapterOnClickHandle = clickHandler;
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
        Movie movie = mMoviesData.get(position);
        Picasso.with(mContext).load(NetworkUtils.BASE_URL + NetworkUtils.IMAGE_SIZE + movie.getPosterPath()).into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData != null){
            return mMoviesData.size();
        }
        return 0;
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mMoviePoster;

        public MoviesAdapterViewHolder(View view){
            super(view);
            mMoviePoster = (ImageView)view.findViewById(R.id.iv_item_movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            movieAdapterOnClickHandle.onClick(mMoviesData.get(getAdapterPosition()));
        }
    }

    public interface MovieAdapterOnClickHandler{
        void onClick(Movie movie);
    }
}
