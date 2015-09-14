package com.paularanas.popularmovies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Paul Aranas on 9/4/2015.
 */
public class ReviewDialogFragment extends android.support.v4.app.DialogFragment {
    TextView content = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.Base_Theme_AppCompat_Dialog_FixedSize);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_dialog_review, container, false);
        Bundle bundle = getArguments();
        ArrayList<Review> allReviewsList = bundle.getParcelableArrayList("reviewData");
        StringBuilder buffer = new StringBuilder();
        if (allReviewsList != null) {
            for (int i = 0; i < allReviewsList.size(); i++) {
                    String reviewListItem = "Reviewer: " + allReviewsList.get(i).getAuthor() + "\n \n" + allReviewsList.get(i).getContent() + "\n \n \n";
                    buffer.append(reviewListItem);
                }
            }
            String allReviews = buffer.toString();
            content = (TextView) view.findViewById(R.id.textDialogContent);
            if (!allReviews.isEmpty()) {
                content.setText(allReviews);
            } else {
                content.setText("No reviews");

            }
            return view;
        }
    }
