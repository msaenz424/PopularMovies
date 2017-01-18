package com.android.mig.popularmovie.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.android.mig.popularmovie.R;
import com.android.mig.popularmovie.data.MoviesContract;
import com.android.mig.popularmovie.utils.NetworkUtils;
import com.android.mig.popularmovie.utils.OpenMoviesJsonUtils;
import java.io.IOException;
import java.net.URL;

public class MoviesSyncTask {

    /**
     * Updates every single row on database,
     * then inserts new movies from the cloud onto database if they don't exist
     */
    synchronized public static void syncMovies(Context context){
        try {
            // first you need to query all rows on DB
            Cursor moviesCursor = context.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI, null, null, null, null);

            // if query returns 0 rows then skip to the download new data step
            if (moviesCursor.getCount() > 0){
                moviesCursor.moveToFirst();
                while (!moviesCursor.isAfterLast()){
                    // search movie by ID on the cloud
                    String movieID = String.valueOf(moviesCursor.getInt(0));
                    URL movieUrl = NetworkUtils.buildURI(movieID);
                    String urlResponse = NetworkUtils.getResponseFromHttpUrl(movieUrl);

                    // get details of a single movie and update DB
                    ContentValues movieContentValue = OpenMoviesJsonUtils.getSingleMovieContentValueFromJson(urlResponse);
                    Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(movieID).build();
                    context.getContentResolver().update(uri, movieContentValue, "_id=?", new String[]{movieID});
                }
            }

            /** TODO find a way to run the lines below inside a loop */
            // download new data and insert to DB, ignore existing rows (for popular)
            String sortByPopularity = context.getString(R.string.pref_order_by_popularity_value);
            URL popularMoviesUrl = NetworkUtils.buildURI(sortByPopularity);
            String urlResponse1 = NetworkUtils.getResponseFromHttpUrl(popularMoviesUrl);

            ContentValues[] popularContentValues = OpenMoviesJsonUtils.getMovieContentValuesFromJson(urlResponse1);
            context.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, popularContentValues);

            // download new data and insert to DB, ignore existing rows (for top_rated)
            String sortByRating = context.getString(R.string.pref_order_by_rating_value);
            URL ratingMoviesUrl = NetworkUtils.buildURI(sortByRating);
            String urlResponse2 = NetworkUtils.getResponseFromHttpUrl(ratingMoviesUrl);

            ContentValues[] ratedContentValuesArray = OpenMoviesJsonUtils.getMovieContentValuesFromJson(urlResponse2);
            context.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, ratedContentValuesArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
