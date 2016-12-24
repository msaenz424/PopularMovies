package com.android.mig.popularmovie;

/**
 * Created by Miguel on 12/21/2016.
 */

public class Movie {

    private int mMovieID;
    private String mTitle;
    private String mPosterPath;
    private String mPlotSynopsis;
    private Double mRating;
    private String mReleaseDate;

    /**
     * Constructor class
     *
     * @param id ID of movie
     * @param title title of movie
     * @param path the poster path where the image is stored
     * @param plot the plot sypnosis of the movie
     * @param rating the user rating for the movie
     * @param date the release date of the movie
     */
    public Movie(int id, String title, String path, String plot, Double rating, String date){
        mMovieID = id;
        mTitle = title;
        mPosterPath = path;
        mPlotSynopsis = plot;
        mRating = rating;
        mReleaseDate = date;
    }

    public int getMovieID(){return mMovieID;}
    public String getTitle(){return mTitle;}
    public String getPosterPath(){return mPosterPath;}
    public String getPlotSynopsis(){return mPlotSynopsis;}
    public Double getRating(){return mRating;}
    public String getReleaseDate(){return mReleaseDate;}
}
