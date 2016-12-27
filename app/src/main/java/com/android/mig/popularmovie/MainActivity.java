package com.android.mig.popularmovie;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.GridLayout.VERTICAL;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieAdapterOnClickHandler {

    private static final int NUMBER_OF_COLUMNS = 2;
    private static final String RECYCLER_POSITION_KEY = "index";
    private ArrayList<Movie>  movieArrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        gridLayoutManager = new GridLayoutManager(
                this,
                NUMBER_OF_COLUMNS,
                VERTICAL,
                false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        if (savedInstanceState == null) {
            FetchMovieTask fetchMovieTask = new FetchMovieTask();
            fetchMovieTask.execute();
        }else{
            movieArrayList = savedInstanceState.getParcelableArrayList(RECYCLER_POSITION_KEY);
            mMoviesAdapter.setMoviesData(movieArrayList);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECYCLER_POSITION_KEY, movieArrayList);
    }

    /**
     * Opens a Detail Activity to show detail information about the movie
     *
     * @param movie a Movie object that contains info of one movie
     */
    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movie);
        startActivity(intent);
    }

    public class FetchMovieTask extends AsyncTask<Void, Void, ArrayList<Movie>>{

        @Override
        protected ArrayList<Movie> doInBackground(Void... voids) {
            URL movieUrl = NetworkUtils.buildURI();
            try {
                String strResponse = NetworkUtils.getResponseFromHttpUrl(movieUrl);
                ArrayList<Movie> movieArrayFromJson = OpenMoviesJsonUtils.getMovieArrayFromJson(MainActivity.this, strResponse);
                return movieArrayFromJson;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> moviesData) {
            if (moviesData != null){
                mMoviesAdapter.setMoviesData(moviesData);
                // A copy of the data is used to save the state of RecyclerView and to avoid re-fetch of data
                movieArrayList = moviesData;
            }
        }
    }

}
