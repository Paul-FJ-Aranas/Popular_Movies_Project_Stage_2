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
public class Trailer implements Parcelable {

    private static final String TAG = "Error: ";
    private String trailerName;
    private String trailerSize;
    private String trailerSource;

    public String getTrailerName() {
        return trailerName;
    }

    public String getSize() {
        return trailerSize;
    }

    public String getSource() {
        return trailerSource;
    }


    public Trailer() {

    }

    public Trailer(String name, String size, String source) {
        Trailer trailerObj = new Trailer();
        trailerObj.trailerName = name;
        trailerObj.trailerSize = size;
        trailerObj.trailerSource = source;

        trailerName = name;
        trailerSize = size;
        trailerSource = source;
    }

    public Trailer(Parcel source) {
        trailerName = source.readString();
        trailerSize = source.readString();
        trailerSource = source.readString();
    }

    public static ArrayList<Trailer> fromJson(JSONObject obj) {
        JSONObject trailers = null;
        JSONArray youtubeTrailers = null;
        JSONObject reviewsObject = null;
        JSONArray reviewArray = null;
        try {
            trailers = obj.getJSONObject("trailers");
            youtubeTrailers = trailers.getJSONArray("youtube");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Trailer> trailerList = new ArrayList<>(youtubeTrailers.length());
        for (int i = 0; i < youtubeTrailers.length(); i++) {
            JSONObject trailerObjects;
            try {
                trailerObjects = youtubeTrailers.getJSONObject(i);
            } catch (Exception e) {
                Log.e(TAG, "Json parsing error");
                continue;
            }
            Trailer tr = new Trailer();
            try {
                tr.trailerName = trailerObjects.getString("name");
                tr.trailerSize = trailerObjects.getString("size");
                tr.trailerSource = trailerObjects.getString("source");
            } catch (JSONException exc) {
                Log.e(TAG, "Json parsing error");
            }
            if (tr != null) {
                trailerList.add(tr);
            }
        }
            return trailerList;

    }
        public static final Parcelable.Creator<Trailer> CREATOR = new Creator<Trailer>() {
            @Override
            public Trailer createFromParcel(Parcel source) {
                return new Trailer(source);
            }

            @Override
            public Trailer[] newArray(int size) {
                return new Trailer[size];
            }
        };


        @Override
        public int describeContents () {
            return 0;
        }

        @Override
        public void writeToParcel (Parcel dest,int flags){
            dest.writeString(trailerName);
            dest.writeString(trailerSize);
            dest.writeString(trailerSource);
        }



    @Override
    public String toString() {
        return "Trailer{" +
                "trailerName='" + trailerName + '\'' +
                ", size='" + trailerSize + '\'' +
                ", trailerSource='" + trailerSource + '\'' +
                '}';
    }
}