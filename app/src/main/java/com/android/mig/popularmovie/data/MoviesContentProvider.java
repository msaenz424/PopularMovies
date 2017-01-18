package com.android.mig.popularmovie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import static com.android.mig.popularmovie.data.MoviesContract.MoviesEntry.TABLE_MOVIES;

public class MoviesContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mMoviesDbHelper;

    /**
     Initialize a new matcher object without any matches,
     then use .addURI(String authority, String path, int match) to add matches
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mMoviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case MOVIES:
                retCursor =  db.query(TABLE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        Uri returnUri = null;

        switch (sUriMatcher.match(uri)){
            case MOVIES:
                long id = db.insert(TABLE_MOVIES, null, contentValues);
                if (id != -1 ){
                    returnUri = ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int rowInserted = 0;
        switch (sUriMatcher.match(uri)){
            case MOVIES:
                rowInserted = db.updateWithOnConflict(TABLE_MOVIES, contentValues, whereClause, whereArgs, SQLiteDatabase.CONFLICT_IGNORE);
                break;
            case MOVIES_WITH_ID:
                rowInserted = db.update(TABLE_MOVIES, contentValues, whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowInserted > 0) getContext().getContentResolver().notifyChange(uri, null);

        return rowInserted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] contentValues) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case MOVIES:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : contentValues) {
                        long _id = db.insertWithOnConflict(TABLE_MOVIES, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, contentValues);
        }

    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

}
