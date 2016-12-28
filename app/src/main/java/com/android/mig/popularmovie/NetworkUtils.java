package com.android.mig.popularmovie;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    public static final String BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w185/"; // valid values: "w92/", "w154/", "w185/", "w342/", "w500/", "w780/", "original/"

    private static final String MOVIES_AUTHORITY = "http://api.themoviedb.org/3";
    private static final String API_KEY = "[YOUR API KEY GOES HERE]"; // update your API KEY for app to work
    private static final String API_KEY_PARAM = "api_key";
    private static final String MOVIES_PATH_SEGMENT = "/movie/";

    /**
     * Builds the URL to query the data
     *
     * @return a built URL
     */
    public static URL buildURI(Context context){
        Uri buildUri = Uri.parse(MOVIES_AUTHORITY + MOVIES_PATH_SEGMENT + getQueryEndPoint(context)).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Retrieves the default value of order by in Preferences.
     *
     * @param context The Context of Activity
     * @returns either "popular" or "top_rated"
     */
    public static String getQueryEndPoint(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String orderBy = sharedPrefs.getString(
                context.getString(R.string.pref_order_by_key),
                context.getString(R.string.pref_order_by_default)
        );
        return orderBy;
    }
}
