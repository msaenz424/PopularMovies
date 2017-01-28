package com.android.mig.popularmovie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.android.mig.popularmovie.R;
import com.android.mig.popularmovie.utils.NetworkUtils;
import com.android.mig.popularmovie.utils.OpenMoviesJsonUtils;

import java.io.IOException;
import java.net.URL;

public class MoviesDbUtils {

    /**
     * Performs a bulk insert to the Content Provider
     *
     * @param context
     * @param sortBy the current sorting preference in app
     */
    public static void insertNewDataFromTheCloud(Context context, String sortBy){

        URL ratingMoviesUrl = NetworkUtils.buildURI(sortBy);
        String urlJsonResponse = null;
        try {
            urlJsonResponse = NetworkUtils.getResponseFromHttpUrl(ratingMoviesUrl);
            ContentValues[] ratedContentValuesArray = OpenMoviesJsonUtils.getMovieContentValuesFromJson(urlJsonResponse);
            context.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, ratedContentValuesArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads data from the local database based on the sorting preference
     *
     * @return a sorted Cursor
     */
    public static Cursor readDbSortedByPreference(Context context, String sortBy, int rows){
        String sortOrder = null;
        String where = null;
        String whereArgs[] = null ;

        if (sortBy.equals(context.getString(R.string.pref_order_by_popularity_value))){
            sortOrder = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC, " + MoviesContract.MoviesEntry.COLUMN_TITLE + " ASC LIMIT " + rows;
        }else if (sortBy.equals(context.getString(R.string.pref_order_by_rating_value))){
            sortOrder = MoviesContract.MoviesEntry.COLUMN_RATING + " DESC, " + MoviesContract.MoviesEntry.COLUMN_TITLE + " ASC LIMIT " + rows;
        }else if (sortBy.equals(context.getString(R.string.pref_order_by_favorite_value))){
            where = MoviesContract.MoviesEntry.COLUMN_IS_FAVORITE + "=?";
            whereArgs = new String[]{String.valueOf(1)};    // 1 = true, 0 = false
        }

        // 2nd argument is null so all columns can be retrieved for later use on DetailsActivity
        Cursor postersCursor = context.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI, null, where, whereArgs, sortOrder);
        return postersCursor;
    }

}
