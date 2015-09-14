package com.paularanas.popularmovies;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Paul Aranas on 8/30/2015.
 */
public class MovieContract {

    private MovieContract() {
    }

    public static final String DATABASE_NAME = "pop_movies_database";
    public static final int DATABASE_VERSION = 2;
    public static final String AUTHORITY = "com.paularanas.popularmovies.movieprovider";
    public static final Uri URI_BASE = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY).build();
    public static final Uri URI_FAVORITES = URI_BASE.buildUpon()
            .appendPath(FavoriteMovieTable.TABLE_NAME).build();
    public static final String TYPE_FAVS = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd." + AUTHORITY + FavoriteMovieTable.TABLE_NAME;
    public static final String MOVIE_TRAILERS = "movie_trailers";
    public static final String MOVIE_REVIEWS = "movie_reviews";
    private static final String COMMA = ",";
    private static final String TEXT = " TEXT";
    private static final String REAL = " REAL";
    private static final String INTEGER = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";


    public static abstract class FavoriteMovieTable implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String _ID = "_id";
        public static final String MOVIE_ID = "movieId";
        public static final String BACKDROP = "backdropPath";
        public static final String TITLE = "originalTitle";
        public static final String RELEASE_DATE = "releaseDate";
        public static final String VOTE_AVERAGE = "voteAverage";
        public static final String POSTER_PATH = "posterPath";
        public static final String POSTER_LAST_PATH = "posterLastPath";
        public static final String OVERVIEW = "movieOverview";
        public static final String TRAILERS_NAMES = "trailerNames";
        public static final String TRAILERS_SIZES = "trailerSizes";
        public static final String TRAILERS_SOURCES = "trailerSources";
        public static final String REVIEWS_ID = "reviewsId";
        public static final String REVIEWS_AUTHOR = "reviewsAuthor";
        public static final String REVIEWS_CONTENT = "reviewsContent";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + "(" + _ID + INTEGER + " PRIMARY KEY AUTOINCREMENT " + COMMA +
                MOVIE_ID + TEXT + " UNIQUE " + COMMA +
                BACKDROP + TEXT  + COMMA +
                TITLE + TEXT + COMMA +
                RELEASE_DATE + TEXT  + COMMA +
                VOTE_AVERAGE + REAL + COMMA +
                POSTER_PATH + TEXT + COMMA +
                POSTER_LAST_PATH + TEXT + COMMA +
                OVERVIEW + TEXT + COMMA +
                TRAILERS_NAMES + TEXT + COMMA +
                TRAILERS_SIZES + TEXT + COMMA +
                TRAILERS_SOURCES + TEXT + COMMA +
                REVIEWS_ID + TEXT  + NOT_NULL + COMMA +
                REVIEWS_AUTHOR + TEXT + NOT_NULL +COMMA +
                REVIEWS_CONTENT  + TEXT + NOT_NULL + ")";

        public static final String[] PROJECTION_ALL =
                { MOVIE_ID, BACKDROP, TITLE,
                        RELEASE_DATE, VOTE_AVERAGE,
                        POSTER_PATH, POSTER_LAST_PATH,
                        OVERVIEW, TRAILERS_NAMES, TRAILERS_SIZES,
                        TRAILERS_SOURCES, REVIEWS_ID, REVIEWS_AUTHOR,
                        REVIEWS_CONTENT
                };

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    }
}
