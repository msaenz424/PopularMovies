package com.android.mig.popularmovie.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.mig.popularmovie.data.MoviesContract.MoviesEntry;

public class MoviesDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "MoviesDB.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + MoviesEntry.TABLE_MOVIES + " (" +
                MoviesEntry._ID                     + " INTEGER PRIMARY KEY, " +
                MoviesEntry.COLUMN_TITLE            + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_POSTER_PATH      + " TEXT NOT NULL," +
                MoviesEntry.COLUMN_PLOT_SYNOPSIS    + " TEXT NOT NULL, " +
                MoviesEntry.COLUMN_RATING           + " REAL, " +
                MoviesEntry.COLUMN_POPULARITY       + " REAL, " +
                MoviesEntry.COLUMN_RELEASE_DATE     + " TEXT, " +
                MoviesEntry.COLUMN_IS_FAVORITE      + " INTEGER);";    // 1 for true, 0 for false

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_MOVIES);
        onCreate(sqLiteDatabase);
    }
}
