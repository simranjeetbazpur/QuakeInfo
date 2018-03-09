/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<QuakeData>> {

    ListView earthquakeListView;
    QuakeDataAapter mQuakeAdapter = null;
    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String REQUEST_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);




        earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        mQuakeAdapter = new QuakeDataAapter(this,new ArrayList<QuakeData>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mQuakeAdapter);
        earthquakeListView.setEmptyView(findViewById(R.id.emptyView));
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QuakeData clickedData = mQuakeAdapter.getItem(i);
                Uri clickedUri = Uri.parse(clickedData.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW,clickedUri);
                startActivity(intent);
            }
        });
        //Get the earthquakes
        Bundle b = new Bundle();
        b.putString("url",REQUEST_URL);
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ic = cm.getActiveNetworkInfo();
        if (ic!=null&&ic.isConnectedOrConnecting()) {
            getLoaderManager().initLoader(0, b, this);
        }
        else
        {
            TextView empty = (TextView)findViewById(R.id.emptyView);
            empty.setText("No Internet connection");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:startActivity(new Intent(EarthquakeActivity.this,Settings.class));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu_settings,menu);
        return true;
    }

    @Override
    public Loader<List<QuakeData>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String query = sharedPreferences.getString(getString(R.string.settings_min_magnitude_key),getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        Uri requestUri = Uri.parse(bundle.getString("url"));
        Uri.Builder requestBuider = requestUri.buildUpon();
        requestBuider.appendQueryParameter("format","geojson");
        requestBuider.appendQueryParameter("limit","10");
        requestBuider.appendQueryParameter("minmag",query);
        requestBuider.appendQueryParameter("orderby",orderBy);
        return new QuakeDataLoader(this,requestBuider.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<QuakeData>> loader, List<QuakeData> quakeDatas) {
        TextView empty = (TextView)findViewById(R.id.emptyView);
        empty.setText("Nothing to show");
        ProgressBar mBar = (ProgressBar)findViewById(R.id.loading_spinner);
        mBar.setVisibility(View.GONE);
        Log.i(LOG_TAG,"Loader finishing");
        mQuakeAdapter.clear();
        if (quakeDatas!=null) {
            mQuakeAdapter.addAll(quakeDatas);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<QuakeData>> loader) {
        mQuakeAdapter.addAll(new ArrayList<QuakeData>());
        Log.i(LOG_TAG,"Loader Resetting");
    }


}
