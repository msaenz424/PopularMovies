package com.android.mig.popularmovie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

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
                tvYearRelease.setText(mMovie.getReleaseDate());
                rbRating.setRating(Float.parseFloat(mMovie.getRating().toString()));
                tvSynopsis.setText(mMovie.getPlotSynopsis());
            }
        }
    }
}
