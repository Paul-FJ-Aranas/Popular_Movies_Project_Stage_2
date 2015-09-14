package com.paularanas.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;


public class DetailsActivity extends AppCompatActivity {
    Parcelable object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        Intent movieIntent = getIntent();

        if (movieIntent != null) {

            if (movieIntent.getParcelableExtra("movieData") instanceof Movie) {
                object = getIntent().getParcelableExtra("movieData");

            } else if (movieIntent.getParcelableExtra("movieFavData") instanceof FavoriteMovie) {

                object = getIntent().getParcelableExtra("movieFavData");
            }
        }
        if (savedInstanceState == null) {


            DetailsActivityFragment detailsFragment = DetailsActivityFragment.newInstance(object);
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.movie_detail_container, detailsFragment);
            transaction.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
     android.support.v7.widget.ShareActionProvider mShareActionProvider =
                (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }

        return true;
    }

}