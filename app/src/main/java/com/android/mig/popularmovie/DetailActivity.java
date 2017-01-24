package com.android.mig.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final int REVIEW_LOADER_ID = 88;
    private static final String FAVORITE_VALUE = "favorite";
    private static int MAX_ORIGINAL_STARS = 10;    // number of starts used on API
    private static int NUMBER_STARS = 5;           // number of starts to be used on app
    private TextView tvYearRelease, tvSynopsis, tvTrailerLabel, tvCardviewReviewLabel;
    private RatingBar rbRating;
    private ImageView ivPoster;
    private ImageButton ibFavorite;
    private RecyclerView rvTrailers, rvReviews;
    private ProgressBar pbLoadingTrailers, pbLoadingReviews;
    private LinearLayoutManager mLinearLayoutManager, mReviewLinearLayoutManager;
    private TrailersAdapter mTrailerAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private boolean mIsFavorite;
    private int mMovieID;
    private ShareActionProvider mShareActionProvider;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ivPoster = (ImageView) findViewById(R.id.iv_poster);
        tvYearRelease = (TextView) findViewById(R.id.tv_release_date);
        rbRating = (RatingBar) findViewById(R.id.rb_movies);
        tvSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        ibFavorite = (ImageButton) findViewById(R.id.ib_favorite);
        rvTrailers = (RecyclerView) findViewById(R.id.rv_trailers);
        tvTrailerLabel = (TextView)findViewById(R.id.tv_trailer_label);
        pbLoadingTrailers = (ProgressBar) findViewById(R.id.pb_loading_trailers);
        rvReviews = (RecyclerView) findViewById(R.id.rv_reviews);
        tvCardviewReviewLabel = (TextView)findViewById(R.id.tv_cardview_review_label);
        pbLoadingReviews = (ProgressBar) findViewById(R.id.pb_loading_reviews);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                Movie mMovie = intent.getParcelableExtra(Intent.EXTRA_TEXT);
                mMovieID = mMovie.getMovieID();
                collapsingToolbarLayout.setTitle(mMovie.getTitle());
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
        if (savedInstanceState != null){
            mIsFavorite = savedInstanceState.getBoolean(FAVORITE_VALUE);
        }
        setFavoriteStarOnOff();
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTrailers.setLayoutManager(mLinearLayoutManager);
        mTrailerAdapter = new TrailersAdapter(this);
        rvTrailers.setAdapter(mTrailerAdapter);
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);

        mReviewLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvReviews.setLayoutManager(mReviewLinearLayoutManager);
        mReviewsAdapter = new ReviewsAdapter();
        rvReviews.setAdapter(mReviewsAdapter);
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);

        // if there no internet connection tell user to connect in order to watch trailers and reviews
        if (!NetworkUtils.isOnline(this)){
            Toast.makeText(this, getString(R.string.toast_connect_to_internet_message), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FAVORITE_VALUE, mIsFavorite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_details_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        MenuItem item = menu.findItem(R.id.action_details_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    /**
     * Sets the share intent
     * @param shareIntent a created share intent
     */
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    /**
     * Creates an intent with the url of the first trailer
     * @return a share intent
     */
    private Intent createShareIntent(){
        String url = NetworkUtils.YOUTUBE_WEB_AUTHORITY + mTrailerAdapter.getFirstTrailer();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        return sendIntent;
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
     * Loader for displaying trailer thumbnails and reviews
     */
    @Override
    public Loader<ArrayList<String>> onCreateLoader(final int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<String>>(this) {
            ArrayList<String> mTrailersKeys;
            ArrayList<String> mReviews;

            @Override
            protected void onStartLoading() {
                switch (id){
                    case TRAILER_LOADER_ID:
                        pbLoadingTrailers.setVisibility(View.VISIBLE);
                        if (mTrailersKeys != null){
                            deliverResult(mTrailersKeys);
                        }
                        forceLoad();
                        break;
                    case REVIEW_LOADER_ID:
                        pbLoadingReviews.setVisibility(View.VISIBLE);
                        if(mReviews != null){
                            deliverResult(mReviews);
                        }
                        forceLoad();
                        break;
                }
            }

            @Override
            public ArrayList<String> loadInBackground() {
                ArrayList<String> arrayFromJson = new ArrayList<>();
                switch (id){
                    case TRAILER_LOADER_ID:
                        URL videosURL = NetworkUtils.buildURI(String.valueOf(mMovieID) + NetworkUtils.VIDEOS_PATH);
                        try {
                            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(videosURL);
                            arrayFromJson = OpenMoviesJsonUtils.getTrailerArrayFromJson(jsonResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    break;
                    case REVIEW_LOADER_ID:
                        URL reviewsURL = NetworkUtils.buildURI(String.valueOf(mMovieID) + NetworkUtils.REVIEWS_PATH);
                        try {
                            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
                            arrayFromJson = OpenMoviesJsonUtils.getReviewsArrayFromJson(jsonResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return arrayFromJson;
            }

            @Override
            public void deliverResult(ArrayList<String> data) {
                super.deliverResult(data);
                switch (id){
                    case TRAILER_LOADER_ID:
                        mTrailersKeys = data;
                        break;
                    case REVIEW_LOADER_ID:
                        mReviews = data;
                        break;
                }

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        switch (loader.getId()){
            case TRAILER_LOADER_ID:
                pbLoadingTrailers.setVisibility(View.INVISIBLE);
                if (data.size() == 0){
                    tvTrailerLabel.setText(getString(R.string.no_trailers_label));
                }else {
                    tvTrailerLabel.setText(getString(R.string.trailer_label));
                    mTrailerAdapter.setTrailersData(data);
                    // Share Intent needs to be created after the adapter for trailer exist
                    // because the url key is obtained from the adapter
                    setShareIntent(createShareIntent());
                }
                break;
            case REVIEW_LOADER_ID:
                pbLoadingReviews.setVisibility(View.INVISIBLE);
                if (data.size() == 0){
                    tvCardviewReviewLabel.setVisibility(View.VISIBLE);
                    tvCardviewReviewLabel.setText(getString(R.string.no_reviews_label));
                }else {
                    tvCardviewReviewLabel.setVisibility(View.INVISIBLE);
                    mReviewsAdapter.setReviewsData(data);
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) { }
}
