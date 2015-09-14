package com.paularanas.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Paul Aranas on 9/7/2015.
 */
public class MovieProvider extends ContentProvider {
    private MovieDatabaseHelper databaseHelper;
    private static final int FAVORITES = 1;
    private static final UriMatcher sUriMatcher;

    static {

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MovieContract.AUTHORITY,
                MovieContract.FavoriteMovieTable.TABLE_NAME, FAVORITES);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new MovieDatabaseHelper(getContext());

        return null != databaseHelper;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(MovieContract.FavoriteMovieTable.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = builder.query(database, MovieContract.FavoriteMovieTable.PROJECTION_ALL, null,
                null, null, null, null);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                return MovieContract.TYPE_FAVS;

            default:
                return null;

        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                id = sqlDB.insertWithOnConflict(MovieContract.FavoriteMovieTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                break;

            default:
                throw new UnsupportedOperationException("Unrecognized URI : " + uri);
        }
        Uri ur = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(uri, null);
        sqlDB.close();
        return ur;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


}
