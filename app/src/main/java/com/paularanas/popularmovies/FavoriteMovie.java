package com.paularanas.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Paul Aranas on 9/10/2015.
 */
public class FavoriteMovie<T extends Parcelable> implements Parcelable, MovieAdapterInterface {
    private static final String TAG = "Error: ";
    private int favoriteId;
    private String favoriteTitle;
    private String favoriteOverview;
    private String favoriteReleaseDate;
    private String favoritePosterPath;
    private String favoriteBackdropPath;
    private double favoriteVoteAverage;
    private String favoritePosterLastPathSegment;
    private String favoriteTrailersName;
    private String favoriteTrailersSize;
    private String favoriteTrailersSource;
    private String favoriteReviewsId;
    private String favoriteReviewsAuthor;
    private String favoriteReviewsContent;


    public int getFavoriteId() {
        return favoriteId;
    }

    public String getFavoriteTitle() {
        return favoriteTitle;
    }

    public String getFavoriteOverview() {
        return favoriteOverview;
    }

    public String getFavoriteReleaseDate() {
        return favoriteReleaseDate;
    }

    public String getFavoritePosterPath() {
        return favoritePosterPath;
    }

    public String getFavoriteBackdropPath() {
        return favoriteBackdropPath;
    }

    public double getFavoriteVoteAverage() {
        return favoriteVoteAverage;
    }

    public String getFavoritePosterLastPathSegment() {
        return favoritePosterLastPathSegment;
    }

    public String getFavoriteTrailersName() {
        return favoriteTrailersName;
    }

    public String getFavoriteTrailersSize() {
        return favoriteTrailersSize;
    }

    public String getFavoriteTrailersSource() {
        return favoriteTrailersSource;
    }

    public String getFavoriteReviewsId() {
        return favoriteReviewsId;
    }

    public String getFavoriteReviewsAuthor() {return favoriteReviewsAuthor;}

    public String getFavoriteReviewsContent() {return favoriteReviewsContent;}


    public FavoriteMovie() {

    }

    public FavoriteMovie(int id, String title, String overview, String releaseDate,
                         String posterPath, String backdropPath, double voteAverage, String posterLastPathSegment,
                         String trailersName, String trailersSize, String trailersSource,
                         String reviewsId, String reviewsAuthor, String reviewsContent) {

        FavoriteMovie fm = new FavoriteMovie();
        fm.favoriteId = id;
        fm.favoriteTitle = title;
        fm.favoriteOverview = overview;
        fm.favoriteReleaseDate = releaseDate;
        fm.favoritePosterPath = posterPath;
        fm.favoriteBackdropPath = backdropPath;
        fm.favoriteVoteAverage = voteAverage;
        fm.favoritePosterLastPathSegment = posterLastPathSegment;
        fm.favoriteTrailersName = trailersName;
        fm.favoriteTrailersSize = trailersSize;
        fm.favoriteTrailersSource = trailersSource;
        fm.favoriteReviewsId = reviewsId;
        fm.favoriteReviewsAuthor = reviewsAuthor;
        fm.favoriteReviewsContent = reviewsContent;


        favoriteId = id;
        favoriteTitle = title;
        favoriteOverview = overview;
        favoriteReleaseDate = releaseDate;
        favoritePosterPath = posterPath;
        favoriteBackdropPath = backdropPath;
        favoriteVoteAverage = voteAverage;
        favoritePosterLastPathSegment = posterLastPathSegment;
        favoriteTrailersName = trailersName;
        favoriteTrailersSize = trailersSize;
        favoriteTrailersSource = trailersSource;
        favoriteReviewsId = reviewsId;
        favoriteReviewsAuthor = reviewsAuthor;
        favoriteReviewsContent = reviewsContent;


    }

    public FavoriteMovie(Parcel source) {
        favoriteId = source.readInt();
        favoriteTitle = source.readString();
        favoriteOverview = source.readString();
        favoriteReleaseDate = source.readString();
        favoritePosterPath = source.readString();
        favoriteBackdropPath = source.readString();
        favoriteVoteAverage = source.readDouble();
        favoritePosterLastPathSegment = source.readString();
        favoriteTrailersName = source.readString();
        favoriteTrailersSize = source.readString();
        favoriteTrailersSource = source.readString();
        favoriteReviewsId = source.readString();
        favoriteReviewsAuthor = source.readString();
        favoriteReviewsContent = source.readString();
    }


    public static final Parcelable.Creator<FavoriteMovie> CREATOR = new Creator<FavoriteMovie>() {
        @Override
        public FavoriteMovie createFromParcel(Parcel source) {
            return new FavoriteMovie(source);
        }

        @Override
        public FavoriteMovie[] newArray(int size) {
            return new FavoriteMovie[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(favoriteId);
        dest.writeString(favoriteTitle);
        dest.writeString(favoriteOverview);
        dest.writeString(favoriteReleaseDate);
        dest.writeString(favoritePosterPath);
        dest.writeString(favoriteBackdropPath);
        dest.writeDouble(favoriteVoteAverage);
        dest.writeString(favoritePosterLastPathSegment);
        dest.writeString(favoriteTrailersName);
        dest.writeString(favoriteTrailersSize);
        dest.writeString(favoriteTrailersSource);
        dest.writeString(favoriteReviewsId);
        dest.writeString(favoriteReviewsAuthor);
        dest.writeString(favoriteReviewsContent);
    }

    @Override
    public String toString() {
        return "FavoriteMovie{" +
                "favoriteId=" + favoriteId +
                ", favoriteTitle='" + favoriteTitle + '\'' +
                ", favoriteOverview='" + favoriteOverview + '\'' +
                ", favoriteReleaseDate='" + favoriteReleaseDate + '\'' +
                ", favoritePosterPath='" + favoritePosterPath + '\'' +
                ", favoriteBackdropPath='" + favoriteBackdropPath + '\'' +
                ", favoriteVoteAverage=" + favoriteVoteAverage +
                ", favoritePosterLastPathSegment='" + favoritePosterLastPathSegment + '\'' +
                ", favoriteTrailersName='" + favoriteTrailersName + '\'' +
                ", favoriteTrailersSize='" + favoriteTrailersSize + '\'' +
                ", favoriteTrailersSource='" + favoriteTrailersSource + '\'' +
                ", favoriteReviewsId='" + favoriteReviewsId + '\'' +
                ", favoriteReviewsAuthor='" + favoriteReviewsAuthor + '\'' +
                ", favoriteReviewsContent='" + favoriteReviewsContent + '\'' +
                '}';
    }


    @Override
    public String getDescription() {
        return "All your favorite movies!";
    }
}
    
    
    

