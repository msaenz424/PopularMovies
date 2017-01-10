package com.android.mig.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.mig.popularmovie.utils.NetworkUtils;
import com.android.mig.popularmovie.utils.OpenMoviesJsonUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.GridLayout.VERTICAL;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MovieAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int NUMBER_OF_COLUMNS = 2;
    private static final String RECYCLER_POSITION_KEY = "index";
    private ArrayList<Movie>  movieArrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TextView mTvNoConnection;
    private ProgressBar mPbLoading;
    private MoviesAdapter mMoviesAdapter;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mTvNoConnection = (TextView) findViewById(R.id.tv_no_connection);
        mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        gridLayoutManager = new GridLayoutManager(
                this,
                NUMBER_OF_COLUMNS,
                VERTICAL,
                false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mMoviesAdapter);

        // if there is a savedInstanceState, display it even if there is no network connection
        if (savedInstanceState != null) {
            movieArrayList = savedInstanceState.getParcelableArrayList(RECYCLER_POSITION_KEY);
            mMoviesAdapter.setMoviesData(movieArrayList);
        }

        mTvNoConnection.setVisibility(View.VISIBLE);
        if (isOnline()){
            mTvNoConnection.setVisibility(View.INVISIBLE);
            if (savedInstanceState == null){
                // fetches data from internet only when there are both network connection and the savedInstanceState is null
                fetchData();
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);   // registers preference changes for later notifications when updated
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true); // ensures that the default preferences values are set
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Fetches data from Internet in a background task
     */
    private void fetchData() {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_order_by_key))){
            fetchData();
        }
    }

    public class FetchMovieTask extends AsyncTask<Void, Void, ArrayList<Movie>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... voids) {
            URL movieUrl = NetworkUtils.buildURI(MainActivity.this);
            try {
                String strResponse = NetworkUtils.getResponseFromHttpUrl(movieUrl);
                ArrayList<Movie> movieArrayFromJson = OpenMoviesJsonUtils.getMovieArrayFromJson(strResponse);
                return movieArrayFromJson;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> moviesData) {
            mPbLoading.setVisibility(View.INVISIBLE);
            if (moviesData != null){
                mMoviesAdapter.setMoviesData(moviesData);
                // A copy of the data is used to save the state of RecyclerView and to avoid re-fetching of data
                movieArrayList = moviesData;
            }
        }
    }

    /**
     * Checks if there is Internet connection
     *
     * @return true if there is connection; false if there isn't
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
