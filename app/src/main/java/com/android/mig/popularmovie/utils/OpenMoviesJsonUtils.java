package com.android.mig.popularmovie.utils;

import android.content.ContentValues;

import com.android.mig.popularmovie.Movie;
import com.android.mig.popularmovie.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OpenMoviesJsonUtils {
    /**
     * Breaks down a Json string into Json objects to save the appropriate data
     * to an ArrayList of Movie objects.
     *
     * @param movieJsonResponse the Json string retrieved from the Internet
     * @return an ArrayList of Movie objects
     */
    public static ArrayList<Movie> getMovieArrayFromJson(String movieJsonResponse){
        final String JSON_ARRAY_RESULTS = "results";
        final String M_MOVIE_ID = "id";
        final String M_TITLE = "original_title";
        final String M_POSTER_PATH = "poster_path";
        final String M_PLOT_SYNOPSIS = "overview";
        final String M_USER_RATING = "vote_average";
        final String M_POPULARITY = "popularity";
        final String M_RELEASE_DATE = "release_date";
        ArrayList<Movie> mMovieArray = new ArrayList<>();

        try {
            JSONObject moviesJson = new JSONObject(movieJsonResponse);
            JSONArray moviesJsonArray = moviesJson.getJSONArray(JSON_ARRAY_RESULTS);

            for (int i = 0; i < moviesJsonArray.length(); i++) {
                JSONObject resultJsonObject = moviesJsonArray.getJSONObject(i);
                int id = resultJsonObject.getInt(M_MOVIE_ID);
                String title = resultJsonObject.getString(M_TITLE);
                String path = resultJsonObject.getString(M_POSTER_PATH);
                String plot = resultJsonObject.getString(M_PLOT_SYNOPSIS);
                double rating = resultJsonObject.getDouble(M_USER_RATING);
                double popularity = resultJsonObject.getDouble(M_POPULARITY);
                String releaseDate = resultJsonObject.getString(M_RELEASE_DATE);
                // last argument 0 means false in is_favorite column
                Movie movie = new Movie(id, title, path, plot, rating, popularity, releaseDate , 0);
                mMovieArray.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mMovieArray;
    }

    /**
     * Gets a single row of data from a Json object
     *
     * @param movieJsonResponse the Json string retrieved from internet
     * @return a ContentValues containing the columns of the row
     */
    public static ContentValues getSingleMovieContentValueFromJson(String movieJsonResponse) {
        final String M_TITLE = "original_title";
        final String M_POSTER_PATH = "poster_path";
        final String M_PLOT_SYNOPSIS = "overview";
        final String M_USER_RATING = "vote_average";
        final String M_POPULARITY = "popularity";
        final String M_RELEASE_DATE = "release_date";

        ContentValues contentValue = new ContentValues();
        try {
            JSONObject movieJson = new JSONObject(movieJsonResponse);

            String title = movieJson.getString(M_TITLE);
            String path = movieJson.getString(M_POSTER_PATH);
            String plot = movieJson.getString(M_PLOT_SYNOPSIS);
            double rating = movieJson.getDouble(M_USER_RATING);
            double popularity = movieJson.getDouble(M_POPULARITY);
            String releaseDate = movieJson.getString(M_RELEASE_DATE);

            contentValue.put(MoviesContract.MoviesEntry.COLUMN_TITLE, title);
            contentValue.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, path);
            contentValue.put(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS, plot);
            contentValue.put(MoviesContract.MoviesEntry.COLUMN_RATING, rating);
            contentValue.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, popularity);
            contentValue.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, releaseDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentValue;
    }

    public static ContentValues[] getMovieContentValuesFromJson(String movieJsonResponse) {
        final String JSON_ARRAY_RESULTS = "results";
        final String M_MOVIE_ID = "id";
        final String M_TITLE = "original_title";
        final String M_POSTER_PATH = "poster_path";
        final String M_PLOT_SYNOPSIS = "overview";
        final String M_USER_RATING = "vote_average";
        final String M_POPULARITY = "popularity";
        final String M_RELEASE_DATE = "release_date";

        ContentValues[] contentValuesArray = null;
        try {
            JSONObject moviesJson = new JSONObject(movieJsonResponse);
            JSONArray moviesJsonArray = moviesJson.getJSONArray(JSON_ARRAY_RESULTS);
            int jsonArraySize = moviesJsonArray.length();

            contentValuesArray = new ContentValues[jsonArraySize];

            for (int i = 0; i < jsonArraySize; i++){
                JSONObject resultJsonObject = moviesJsonArray.getJSONObject(i);
                int id = resultJsonObject.getInt(M_MOVIE_ID);
                String title = resultJsonObject.getString(M_TITLE);
                String path = resultJsonObject.getString(M_POSTER_PATH);
                String plot = resultJsonObject.getString(M_PLOT_SYNOPSIS);
                double rating = resultJsonObject.getDouble(M_USER_RATING);
                double popularity = resultJsonObject.getDouble(M_POPULARITY);
                String releaseDate = resultJsonObject.getString(M_RELEASE_DATE);

                ContentValues contentValue = new ContentValues();
                contentValue.put(MoviesContract.MoviesEntry._ID, id);
                contentValue.put(MoviesContract.MoviesEntry.COLUMN_TITLE, title);
                contentValue.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, path);
                contentValue.put(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS, plot);
                contentValue.put(MoviesContract.MoviesEntry.COLUMN_RATING, rating);
                contentValue.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, popularity);
                contentValue.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, releaseDate);
                contentValuesArray[i] = contentValue;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentValuesArray;
    }

}
