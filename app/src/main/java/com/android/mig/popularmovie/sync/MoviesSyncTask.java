package com.android.mig.popularmovie.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;

import com.android.mig.popularmovie.R;
import com.android.mig.popularmovie.data.MoviesContract;
import com.android.mig.popularmovie.data.MoviesDbUtils;
import com.android.mig.popularmovie.utils.NetworkUtils;
import com.android.mig.popularmovie.utils.OpenMoviesJsonUtils;
import java.io.IOException;
import java.net.URL;

public class MoviesSyncTask {
    private static final int MILLISECONDS_SYNC_DELAY = 12000;

    /**
     * Updates every single row on database,
     * then inserts new movies from the cloud onto database if they don't exist
     */
    synchronized public static void syncMovies(Context context){
        try {
            // first you need to query all rows on DB
            Cursor moviesCursor = context.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI, null, null, null, null);
            int c = 1;
            // if query returns 0 rows then skip to the download new data step
            if (moviesCursor.getCount() > 0){
                while (moviesCursor.moveToNext()){
                    // search movie by ID on the cloud
                    String movieID = String.valueOf(moviesCursor.getInt(0));
                    URL movieUrl = NetworkUtils.buildURI(movieID);
                    String urlResponse = NetworkUtils.getResponseFromHttpUrl(movieUrl);

                    // get details of a single movie and update DB
                    ContentValues movieContentValue = OpenMoviesJsonUtils.getSingleMovieContentValueFromJson(urlResponse);
                    Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(movieID).build();
                    context.getContentResolver().update(uri, movieContentValue, "_id=?", new String[]{movieID});

                    // delay if number of requests reached the server's limits
                    if ((c % NetworkUtils.SERVER_REQUESTS_LIMIT) == 0){
                        SystemClock.sleep(MILLISECONDS_SYNC_DELAY);
                    }
                }
            }
            moviesCursor.close();

            SystemClock.sleep(MILLISECONDS_SYNC_DELAY);
            MoviesDbUtils.insertNewDataFromTheCloud(context, context.getString(R.string.pref_order_by_popularity_value));
            MoviesDbUtils.insertNewDataFromTheCloud(context, context.getString(R.string.pref_order_by_rating_value));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
