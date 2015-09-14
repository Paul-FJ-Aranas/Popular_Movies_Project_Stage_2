package com.paularanas.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Paul Aranas on 9/1/2015.
 */
public class Review implements Parcelable {

    private static final String TAG = "Error: ";
    private String reviewIdNum;
    private String author;
    private String content;


    public String getIdNum() {
        return reviewIdNum;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public Review() {

    }

    public Review(String reviewId, String reviewAuthor, String reviewContent) {
        Review reviewObj = new Review();
        reviewObj.reviewIdNum = reviewId;
        reviewObj.author = reviewAuthor;
        reviewObj.content = reviewContent;

        reviewIdNum = reviewId;
        author = reviewAuthor;
        content = reviewContent;
    }

    public Review(Parcel source) {
        reviewIdNum = source.readString();
        author = source.readString();
        content = source.readString();
    }

    public static ArrayList<Review> fromJson(JSONObject obj) {
        JSONObject reviewsObject = null;
        JSONArray reviewsArray = null;
        try {
            reviewsObject = obj.getJSONObject("reviews");
            reviewsArray = reviewsObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<Review> reviewList = new ArrayList<>(reviewsArray.length());
        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject singleReview = null;
            try {
                singleReview = reviewsArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Review rv = new Review();
            try {
                rv.reviewIdNum = singleReview.getString("id");
                rv.author = singleReview.getString("author");
                rv.content = singleReview.getString("content");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (rv != null) {
                reviewList.add(rv);
            }
        }
        return reviewList;
    }


    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewIdNum);
        dest.writeString(author);
        dest.writeString(content);
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewIdNum=" + reviewIdNum +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public static ArrayList<Review> retrieveReviewArrayList(Review rv) {
        ArrayList<Review> reviewList = new ArrayList<>();
        reviewList.add(rv);

        return reviewList;
    }
}