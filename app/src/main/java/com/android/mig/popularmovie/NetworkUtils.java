package com.android.mig.popularmovie;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Miguel on 12/21/2016.
 */

public final class NetworkUtils {

    public static final String BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE = "w185/"; // valid values: "w92/", "w154/", "w185/", "w342/", "w500/", "w780/", "original/"

    private static final String MOVIES_AUTHORITY = "http://api.themoviedb.org/3";
    private static final String MOST_POPULAR = "/movie/popular";
    private static final String TOP_RATED = "/movie/top_rated";
    private static final String API_KEY = "[YOUR API KEY GOES HERE]"; // update your API KEY for app to work

    private static final String API_KEY_PARAM = "api_key";
    private static final String QUERY_PARAM = "q";

    private static String queryPreference = MOST_POPULAR; // default value


    /**
     * Builds the URL to query the data
     *
     * @return a built URL
     */
    public static URL buildURI(){
        Uri buildUri = Uri.parse(MOVIES_AUTHORITY + queryPreference).buildUpon()
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
     * Sets the preferred query method to show movies: popular or top rated
     *
     * @param query The segment that will be appended to the authority path
     */
    public static void setQueryEndPoint(String query){
        queryPreference = query;
    }
}
