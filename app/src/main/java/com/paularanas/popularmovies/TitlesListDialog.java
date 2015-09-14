package com.paularanas.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Paul Aranas on 9/12/2015.
 */
public class TitlesListDialog extends android.app.DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.titles_fav_dialog, container,false);
        Bundle bundle = getArguments();
       String[] titles = bundle.getStringArray("TitlesFavData");
        TextView textFav = (TextView) view.findViewById(R.id.titles_dialog);
        for (int i = 0; i< titles.length; i++){
            textFav.setText(titles[i] + "\n");
            textFav.setTextColor(R.color.teal);

        }

        return view;
    }
}
