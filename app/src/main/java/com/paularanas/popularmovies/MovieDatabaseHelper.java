package com.paularanas.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.paularanas.popularmovies.MovieContract.FavoriteMovieTable;

/**
 * Created by Paul Aranas on 9/7/2015.
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper {

    public MovieDatabaseHelper(Context context) {
        super(context, MovieContract.DATABASE_NAME, null, MovieContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FavoriteMovieTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FavoriteMovieTable.DELETE_TABLE);
        onCreate(db);
    }
}