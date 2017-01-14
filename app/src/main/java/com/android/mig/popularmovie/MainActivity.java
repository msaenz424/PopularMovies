package com.android.mig.popularmovie;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.mig.popularmovie.data.MoviesContract.MoviesEntry;
import com.android.mig.popularmovie.utils.NetworkUtils;
import com.android.mig.popularmovie.utils.OpenMoviesJsonUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.GridLayout.VERTICAL;
import static com.android.mig.popularmovie.SettingsActivity.SettingsFragment.getSortByPreference;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MovieAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 900;
    private static final int NUMBER_OF_COLUMNS = 2;         // number of columns in RecyclerView
    private static final int NUMBER_OF_ROWS = 20;           // number of rows in RecyclerView
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);   // registers preference changes for later notifications when updated
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true); // ensures that the default preferences values are set

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);    // this will jump to its onLoadFinished method if Loader already exists
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
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
            getSupportLoaderManager().restartLoader(LOADER_ID,null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this){
            Cursor mMoviesCursor;

            @Override
            protected void onStartLoading() {
                mPbLoading.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                URL movieUrl = NetworkUtils.buildURI(MainActivity.this);
                String strResponse = null;
                if (isOnline()){
                    try {
                        // if there isn't any problem getting a url response then write the DB
                        strResponse = NetworkUtils.getResponseFromHttpUrl(movieUrl);
                        writeDB(strResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Cursor data = readDB();
                if (data.getCount() == 0){
                    return null;
                }
                return data;
            }

            @Override
            public void deliverResult(Cursor data) {
                mMoviesCursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPbLoading.setVisibility(View.INVISIBLE);
        mTvNoConnection.setVisibility(View.VISIBLE);
        /** TODO find a way to make use of isOnline() function only once (it's being used in loadInBackground as well) */
        if (isOnline()){
            mTvNoConnection.setVisibility(View.INVISIBLE);
        }
        if (data != null){
            mMoviesAdapter.setMoviesData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Updates Movies' info on database.
     * If there are no rows to update then it inserts new rows
     *
     * @param urlResponse a string response the internet
     */
    public void writeDB(String urlResponse){
        ArrayList<Movie> movieArrayFromJson = OpenMoviesJsonUtils.getMovieArrayFromJson(urlResponse);

        for (int i = 0; i < movieArrayFromJson.size(); i++){
            ContentValues contentValue = new ContentValues();
            int colID = movieArrayFromJson.get(i).getMovieID();
            contentValue.put(MoviesEntry._ID, colID );
            contentValue.put(MoviesEntry.COLUMN_TITLE, movieArrayFromJson.get(i).getTitle());
            contentValue.put(MoviesEntry.COLUMN_POSTER_PATH, movieArrayFromJson.get(i).getPosterPath());
            contentValue.put(MoviesEntry.COLUMN_PLOT_SYNOPSIS, movieArrayFromJson.get(i).getPlotSynopsis());
            contentValue.put(MoviesEntry.COLUMN_RATING, movieArrayFromJson.get(i).getRating());
            contentValue.put(MoviesEntry.COLUMN_POPULARITY, movieArrayFromJson.get(i).getPopularity());
            contentValue.put(MoviesEntry.COLUMN_RELEASE_DATE, movieArrayFromJson.get(i).getReleaseDate());

            int rowUpdated = getContentResolver().update(MoviesEntry.CONTENT_URI, contentValue, "_id=?", new String[]{String.valueOf(colID)});

            if (rowUpdated == 0) getContentResolver().insert(MoviesEntry.CONTENT_URI, contentValue);
        }
    }

    /**
     * Reads data from the local database that was earlier stored from internet
     *
     * @return a sorted Cursor
     */
    public Cursor readDB(){
        String sortBy = getSortByPreference(MainActivity.this);
        String sortOrder = "";

        if (sortBy.equals(getString(R.string.pref_order_by_popularity_value))){
            sortOrder = MoviesEntry.COLUMN_POPULARITY + " DESC LIMIT " + NUMBER_OF_ROWS;
        }else if (sortBy.equals(getString(R.string.pref_order_by_rating_value))){
            sortOrder = MoviesEntry.COLUMN_RATING + " DESC LIMIT " + NUMBER_OF_ROWS;
        }

        // 2nd argument is null so all columns can be retrieved for later use on DetailsActivity
        Cursor postersCursor = getContentResolver().query(MoviesEntry.CONTENT_URI, null, null, null, sortOrder);
        return postersCursor;
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
