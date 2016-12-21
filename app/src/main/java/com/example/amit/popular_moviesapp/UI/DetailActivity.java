package com.example.amit.popular_moviesapp.Ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.amit.popular_moviesapp.R;

/**
 * Created by amit on 15-04-2016.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieFragment.MOVIE,
                    getIntent().getExtras().getParcelable(MovieFragment.MOVIE));

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }

    }
}
