package com.example.amit.popular_moviesapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    boolean mTwoPane;
    String mSort;
    Context mContext;
    String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        if(findViewById(R.id.detail_container) != null){
            mTwoPane = true;
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.detail_container, new MovieDetailFragment()).commit();
            }
        }
        else{
            mTwoPane = false;
        }

    }

    @Override
    protected void onStart() {
        Log.v(LOG_TAG, "in onStart");
        super.onStart();
        // The activity is about to become visible.
    }

    @Override
    protected void onResume(){
        super.onResume();

        String sortOrder = Utility.getSortOrder(mContext);
        Log.v(LOG_TAG,"In onResume :" +sortOrder+mSort);


        if (!sortOrder.equals(mSort)){
        MovieFragment ff = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        ff.onSortChanged(sortOrder);
            mSort = sortOrder;
    }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(MovieItem movieItem){
        //if mTwoPane = true
        //FrameLayout in activity.main.xml got to be replaced with Detail Fragment
        //else
        //an Intent to Call DetailActivity for single pane Layout and passing relevant info

      if (mTwoPane) {

          Bundle bundle = new Bundle();
          bundle.putParcelable(MovieFragment.MOVIE, movieItem);

          MovieDetailFragment df = new MovieDetailFragment();
          df.setArguments(bundle);

          getSupportFragmentManager().beginTransaction().
                  replace(R.id.detail_container, df).commit();
      }

      else{

       Intent intent = new Intent(this,DetailActivity.class);
       intent.putExtra(MovieFragment.MOVIE,movieItem);
       startActivity(intent);
      }
    }
}
