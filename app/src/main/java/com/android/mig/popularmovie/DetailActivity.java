package com.android.mig.popularmovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final float STEP_SIZE = 0.25f;
    private static int MAX_ORIGINAL_STARS = 10;    // number of starts used on API
    private static int NUMBER_STARS = 5;           // number of starts to be used on app
    private TextView tvTitle, tvYearRelease, tvSynopsis;
    private RatingBar rbRating;
    private ImageView ivPoster;
    private Movie mMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivPoster = (ImageView) findViewById(R.id.iv_poster);
        tvYearRelease = (TextView) findViewById(R.id.tv_release_date);
        rbRating = (RatingBar) findViewById(R.id.rb_movies);
        tvSynopsis = (TextView) findViewById(R.id.tv_synopsis);

        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra(Intent.EXTRA_TEXT)){
                mMovie = intent.getParcelableExtra(Intent.EXTRA_TEXT);
                tvTitle.setText(mMovie.getTitle());
                Picasso.with(this).load(NetworkUtils.BASE_URL + NetworkUtils.IMAGE_SIZE + mMovie.getPosterPath()).into(ivPoster);
                tvYearRelease.setText(mMovie.getReleaseDate().substring(0, 4));             // extracts only the year
                double newRating = mMovie.getRating() / MAX_ORIGINAL_STARS * NUMBER_STARS;  // calculates a proportional value of rating
                rbRating.setRating(Float.parseFloat(String.valueOf(newRating)));
                rbRating.setNumStars(NUMBER_STARS);
                rbRating.setStepSize(STEP_SIZE);
                tvSynopsis.setText(mMovie.getPlotSynopsis());
            }
        }
    }
}
