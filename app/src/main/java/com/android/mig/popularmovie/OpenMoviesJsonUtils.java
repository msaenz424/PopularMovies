package com.android.mig.popularmovie;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Miguel on 12/22/2016.
 */

public class OpenMoviesJsonUtils {

    public static ArrayList<Movie> getMovieArrayFromJson(Context context, String movieJsonResponse){
        final String M_MOVIE_ID = "id";
        final String M_TITLE = "original_title";
        final String M_POSTER_PATH = "poster_path";
        final String M_PLOT_SYNOPSIS = "overview";
        final String M_USER_RATING = "vote_average";
        final String M_RELEASE_DATE = "release_date";
        ArrayList<Movie> mMovieArray = new ArrayList<>();

        try {
            JSONObject moviesJson = new JSONObject(movieJsonResponse);
            JSONArray moviesJsonArray = moviesJson.getJSONArray("results");

            for (int i = 0; i < moviesJsonArray.length(); i++) {
                JSONObject resultJsonObject = moviesJsonArray.getJSONObject(i);
                int id = resultJsonObject.getInt(M_MOVIE_ID);
                String title = resultJsonObject.getString(M_TITLE);
                String path = resultJsonObject.getString(M_POSTER_PATH);
                String plot = resultJsonObject.getString(M_PLOT_SYNOPSIS);
                double rating = resultJsonObject.getDouble(M_USER_RATING);
                String releaseDate = resultJsonObject.getString(M_RELEASE_DATE);
                Movie movie = new Movie(id, title, path, plot, rating, releaseDate);
                mMovieArray.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return mMovieArray;
    }
}
