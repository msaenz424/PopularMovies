package com.android.mig.popularmovie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Miguel on 12/21/2016.
 */

public class Movie implements Parcelable {

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

    protected Movie(Parcel in) {
        mMovieID = in.readInt();
        mTitle = in.readString();
        mPosterPath = in.readString();
        mPlotSynopsis = in.readString();
        mRating = in.readDouble();
        mReleaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getMovieID(){return mMovieID;}
    public String getTitle(){return mTitle;}
    public String getPosterPath(){return mPosterPath;}
    public String getPlotSynopsis(){return mPlotSynopsis;}
    public Double getRating(){return mRating;}
    public String getReleaseDate(){return mReleaseDate;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mMovieID);
        parcel.writeString(mTitle);
        parcel.writeString(mPosterPath);
        parcel.writeString(mPlotSynopsis);
        parcel.writeDouble(mRating);
        parcel.writeString(mReleaseDate);
    }
}
