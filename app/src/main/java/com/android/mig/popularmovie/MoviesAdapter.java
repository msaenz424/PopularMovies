package com.android.mig.popularmovie;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.mig.popularmovie.utils.NetworkUtils;
import com.squareup.picasso.Picasso;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>{

    private Cursor mMoviesCursor = null;
    private Context mContext;
    final private MovieAdapterOnClickHandler movieAdapterOnClickHandle;

    public MoviesAdapter(Context context){
        mContext = context;
        movieAdapterOnClickHandle = (MovieAdapterOnClickHandler) context;
    }

    /**
     * Sets the Adapter with a Cursor that contains the data
     *
     * @param cursor the Cursor that cointas the data
     */
    public void setMoviesData(Cursor cursor){
        mMoviesCursor = cursor;
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
        mMoviesCursor.moveToPosition(position);
        /** TODO use constant variable. 0 is the index of column "poster_path" in cursor. Only this column was retrieved on query */
        String posterPath = mMoviesCursor.getString(0);
        Picasso.with(mContext).load(NetworkUtils.BASE_URL + NetworkUtils.IMAGE_SIZE + posterPath).into(holder.mMoviePoster);

    }

    @Override
    public int getItemCount() {
        if (mMoviesCursor != null) return mMoviesCursor.getCount();
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
            /** TODO get data from cursor selected*/
            //movieAdapterOnClickHandle.onClick(mMoviesData.get(getAdapterPosition()));
        }
    }

    public interface MovieAdapterOnClickHandler{
        void onClick(Movie movie);
    }
}
