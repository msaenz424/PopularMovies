package com.android.mig.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.mig.popularmovie.data.MoviesContract;
import com.android.mig.popularmovie.utils.NetworkUtils;
import com.android.mig.popularmovie.utils.OpenMoviesJsonUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    private static final float STEP_SIZE = 0.25f;
    private static final int TRAILER_LOADER_ID = 77;
    private static int MAX_ORIGINAL_STARS = 10;    // number of starts used on API
    private static int NUMBER_STARS = 5;           // number of starts to be used on app
    private TextView tvTitle, tvYearRelease, tvSynopsis, tvTrailerLabel;
    private RatingBar rbRating;
    private ImageView ivPoster;
    private ImageButton ibFavorite;
    private RecyclerView rvTrailers;
    private ProgressBar pbLoadingTrailers;
    private LinearLayoutManager mLinearLayoutManager;
    private TrailersAdapter mTrailerAdapter;
    private boolean mIsFavorite;
    private int mMovieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivPoster = (ImageView) findViewById(R.id.iv_poster);
        tvYearRelease = (TextView) findViewById(R.id.tv_release_date);
        rbRating = (RatingBar) findViewById(R.id.rb_movies);
        tvSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        ibFavorite = (ImageButton) findViewById(R.id.ib_favorite);
        rvTrailers = (RecyclerView) findViewById(R.id.rv_trailers);
        tvTrailerLabel = (TextView)findViewById(R.id.tv_trailer_label);
        pbLoadingTrailers = (ProgressBar) findViewById(R.id.pb_loading_trailers);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                Movie mMovie = intent.getParcelableExtra(Intent.EXTRA_TEXT);
                mMovieID = mMovie.getMovieID();
                tvTitle.setText(mMovie.getTitle());
                Picasso.with(this).load(NetworkUtils.BASE_URL + NetworkUtils.IMAGE_SIZE + mMovie.getPosterPath()).into(ivPoster);
                tvYearRelease.setText(mMovie.getReleaseDate().substring(0, 4));             // extracts only the year
                double newRating = mMovie.getRating() / MAX_ORIGINAL_STARS * NUMBER_STARS;  // calculates a proportional value of rating
                rbRating.setRating(Float.parseFloat(String.valueOf(newRating)));
                rbRating.setNumStars(NUMBER_STARS);
                rbRating.setStepSize(STEP_SIZE);
                tvSynopsis.setText(mMovie.getPlotSynopsis());
                mIsFavorite = (mMovie.getIsFavorite() == 1);    // 1 = true, 0 = false
            }
        }
        setFavoriteStarOnOff();
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTrailers.setLayoutManager(mLinearLayoutManager);
        mTrailerAdapter = new TrailersAdapter(this);
        rvTrailers.setAdapter(mTrailerAdapter);
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Updates the "is_favorite" column in the movies's table
     *
     * @param view
     */
    public void setMovieAsFavorite(View view){
        mIsFavorite = !mIsFavorite;
        Uri uriWithID = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieID)).build();
        ContentValues contentValue = new ContentValues();
        contentValue.put(MoviesContract.MoviesEntry.COLUMN_IS_FAVORITE, mIsFavorite);
        getContentResolver().update(uriWithID, contentValue, "_id=?", new String[]{String.valueOf(mMovieID)});
        setFavoriteStarOnOff();
    }

    /**
     * Switches the star icon to on and off
     */
    public void setFavoriteStarOnOff(){
        if (mIsFavorite){
            ibFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        }else {
            ibFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    /**
     * Loader for displaying trailer thumbnails
     */
    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<String>>(this) {
            ArrayList<String> mTrailersKeys;

            @Override
            protected void onStartLoading() {
                pbLoadingTrailers.setVisibility(View.VISIBLE);
                if (mTrailersKeys != null){
                    deliverResult(mTrailersKeys);
                }
                forceLoad();
            }

            @Override
            public ArrayList<String> loadInBackground() {
                ArrayList<String> arrayFromJson = new ArrayList<>();
                URL movieURL = NetworkUtils.buildURI(String.valueOf(mMovieID) + NetworkUtils.VIDEOS_PATH);
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieURL);
                    arrayFromJson = OpenMoviesJsonUtils.getTrailerArrayFromJson(jsonResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return arrayFromJson;
            }

            @Override
            public void deliverResult(ArrayList<String> data) {
                super.deliverResult(data);
                mTrailersKeys = data;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        pbLoadingTrailers.setVisibility(View.INVISIBLE);
        if (data.size() == 0){
            tvTrailerLabel.setText(getString(R.string.no_trailers_label));
        }else {
            tvTrailerLabel.setText(getString(R.string.trailer_label));
            mTrailerAdapter.setTrailersData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) { }
}
