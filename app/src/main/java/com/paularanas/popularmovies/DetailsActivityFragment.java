package com.paularanas.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        //retrieve intent from MainActivity with Movie object as extra
        // fill views in with relevant data
        Intent movieIntent = getActivity().getIntent();
        if (movieIntent != null && movieIntent.hasExtra("movieData")) {
            Movie movie = (Movie) movieIntent.getParcelableExtra("movieData");

            TextView titleText = (TextView) view.findViewById(R.id.textView_originalTitle);
            titleText.setText(movie.getOriginalTitle());

            TextView releaseDateText = (TextView) view.findViewById(R.id.textView_release_date);
            releaseDateText.setText(getString(R.string.release_date) + " " + movie.getReleaseDate());

            TextView userRatingText = (TextView) view.findViewById(R.id.textView_user_rating);
            userRatingText.setText(getString(R.string.user_rating) + " " + movie.getVoteAverage() + "/10");

            ImageView posterThumbnail = (ImageView) view.findViewById(R.id.poster_thumbnail);
            Picasso.with(getActivity()).load(movie.getPosterPath()).error(R.drawable.movies_thumbnail_placeholder).into(posterThumbnail);

            TextView overview = (TextView) view.findViewById(R.id.textView_overview);
            if ("null".equals(movie.getOverview())) {
                overview.setText("");
            }
            if ("No overview found.".equals(movie.getOverview())) {
                overview.setText("");
            } else {
                overview.setText(movie.getOverview());
            }

        }
        return view;

    }
}