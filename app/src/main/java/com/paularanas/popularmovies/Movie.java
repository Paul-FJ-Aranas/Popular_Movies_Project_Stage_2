package com.paularanas.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Paul Aranas on 8/2/2015.
 */
public class Movie implements Parcelable {
    private static final String TAG = "Error: ";
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private String posterPath;
    private double voteAverage;
    private String posterLastPathSegment;


    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getPosterLastPathSegment() {
        return posterLastPathSegment;
    }

    public Movie() {

    }

    public Movie(Parcel source) {
        originalTitle = source.readString();
        overview = source.readString();
        releaseDate = source.readString();
        posterPath = source.readString();
        voteAverage = source.readDouble();
        posterLastPathSegment = source.readString();
    }

    public static Movie fromJson(JSONObject jsonObject) {
        Movie m = new Movie();
        try {
            m.originalTitle = jsonObject.getString("original_title");
            m.overview = jsonObject.getString("overview");
            m.releaseDate = jsonObject.getString("release_date");
            m.posterPath = "http://image.tmdb.org/t/p/w185" + jsonObject.getString("poster_path");
            m.posterLastPathSegment = jsonObject.getString("poster_path");
            m.voteAverage = jsonObject.getDouble("vote_average");
        } catch (JSONException e) {
            Log.e(TAG, "Json parsing error");
        }
        return m;
    }


    public static ArrayList<Movie> fromJson(JSONArray jsonArray) {


        ArrayList<Movie> movies = new ArrayList<>(jsonArray.length());

        //loop through results in json array, decode and convert to movie object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movieJson;
            try {
                movieJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                Log.e(TAG, "Json parsing error");
                continue;
            }
            Movie movie = fromJson(movieJson);
            if (movie != null) {
                movies.add(movie);
            }
        }
        return movies;


    }


    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeDouble(voteAverage);
        dest.writeString(posterLastPathSegment);

    }

    @Override
    public String toString() {
        return "Movie{" +
                "originalTitle='" + originalTitle + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", voteAverage=" + voteAverage +
                '}';
    }

}
