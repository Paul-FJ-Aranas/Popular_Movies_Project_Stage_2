package com.paularanas.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Paul Aranas on 9/12/2015.
 */
public class FavoritesEmptyFragment extends android.app.DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_empty_fragment, container, false);
    }
}
