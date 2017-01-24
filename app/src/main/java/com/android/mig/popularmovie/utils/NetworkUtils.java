package com.android.mig.popularmovie.utils;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    public static final String BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w342/"; // valid values: "w92/", "w154/", "w185/", "w342/", "w500/", "w780/", "original/"

    private static final String MOVIES_AUTHORITY = "http://api.themoviedb.org/3";
    private static final String API_KEY = "[YOUR API KEY GOES HERE]"; // update your API KEY for app to work
    private static final String API_KEY_PARAM = "api_key";
    private static final String MOVIES_PATH_SEGMENT = "/movie/";

    public static final String YOUTUBE_WEB_AUTHORITY = "https://www.youtube.com/watch?v=";
    public static final String YOUTUBE_APP_AUTHORITY = "vnd.youtube:";
    public static final String YOUTUBE_THUMBNAIL_AUTHORITY = "http://img.youtube.com/vi/";
    public static final String YOUTUBE_THUMBNAIL_ENDPOINT = "/0.jpg";
    public static final String VIDEOS_PATH = "/videos";
    public static final String REVIEWS_PATH = "/reviews";

    /**
     * Builds the URL to query details of a movie
     *
     * @param movieSegment either a movie ID or a sort-by preference
     * @return a built URL
     */
    public static URL buildURI(String movieSegment){
        Uri buildUri = Uri.parse(MOVIES_AUTHORITY + MOVIES_PATH_SEGMENT + movieSegment).buildUpon()
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
     * Checks if there is Internet connection
     *
     * @return true if there is connection; false if there isn't
     */
    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager)  activity.getSystemService(activity.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}