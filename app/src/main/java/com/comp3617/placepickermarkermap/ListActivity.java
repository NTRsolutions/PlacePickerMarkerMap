package com.comp3617.placepickermarkermap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * FinalProject : com.comp3617.placepickermarkermap
 * File: ListActivity.java
 * Modified by G.E. Eidsness on 10/15/2017
 */

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = ListActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 100;
    private ListView lvLocations;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(ListActivity.this, MapMarkerActivity.class);
                    startActivity(intent);
                    ListActivity.this.finish();
                    break;
                case R.id.navigation_location_list:
                    Toast.makeText(ListActivity.this, String.format("Clicked %s", R.string.action_list), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.navigation_location_search:
                    intent = new Intent(ListActivity.this, GooglePlacesActivity.class);
                    startActivity(intent);
                    ListActivity.this.finish();
                    break;
            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        LocationDBHelper locationDBHelper = LocationDBHelper.getInstance(getApplicationContext());
        LocationAdapter locationAdapter = new LocationAdapter(ListActivity.this, locationDBHelper.getLocations());

        if (locationAdapter.isEmpty() || locationAdapter.getCount()==0) {
            TextView tvLocal = findViewById(R.id.tvEmpty);
            tvLocal.setVisibility(View.VISIBLE);
//            Toast.makeText(ListActivity.this, String.format("Count: %s", locationAdapter.getCount()), Toast
//                    .LENGTH_SHORT).show();
        } else {
            lvLocations = findViewById(R.id.lvLocations);
            lvLocations.setAdapter(locationAdapter);
            lvLocations.setOnItemClickListener(ListActivity.this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ArrayAdapter<Location> locationAdapter = (LocationAdapter) lvLocations.getAdapter();
        Location thisLocation = locationAdapter.getItem(position);
        if (thisLocation != null) {
            //Toast.makeText(ListActivity.this, String.format("Clicked %s", thisLocation.getGoogleId()), Toast.LENGTH_SHORT).show();
            Intent editIntent = new Intent(ListActivity.this, EditActivity.class);
            //editIntent.putExtra("com.comp3617.placepickermarkermap.scooch_locations.db", thisLocation);
            //startActivityForResult(editIntent, REQUEST_CODE );
            editIntent.putExtra("google_id", thisLocation.getGoogleId());
            editIntent.putExtra("name", thisLocation.getName());
            editIntent.putExtra("address", thisLocation.getAddress());
            editIntent.putExtra("remarks", thisLocation.getRemarks());
            Bundle b = new Bundle();
            b.putDouble("latitude", thisLocation.getLatitude());
            b.putDouble("longitude",thisLocation.getLongitude());
            editIntent.putExtras(b);
            startActivity(editIntent);
            finish();
        }
        bindListViewToLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "requestCode: " + requestCode  + ": " + REQUEST_CODE);
        Log.d(LOG_TAG, "resultCode: " + resultCode  + ": " + RESULT_OK);
        if ((requestCode == REQUEST_CODE) && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Location location = extras.getParcelable("com.comp3617.placepickermarkermap.scooch_locations.db");
            LocationDBHelper locationDBHelper = LocationDBHelper.getInstance(this);
            locationDBHelper.updateLocation(location);
            bindListViewToLocation();
        } else
            Log.d(LOG_TAG, "Not OK:");
            //Toast.makeText(ListActivity.this, String.format("Clicked %s", data.toString()), Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void bindListViewToLocation() {
        Log.d(LOG_TAG, "bindListViewToLocation()");
        LocationDBHelper dbHelper = LocationDBHelper.getInstance(this);
        List<Location> myLocation = dbHelper.getLocations();
        ArrayAdapter<Location> adptr = new LocationAdapter(this, myLocation);
        lvLocations.setAdapter(adptr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Toast.makeText(this, "You have selected Settings", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.action_list:
                Toast.makeText(this, "You have selected ListActivity", Toast.LENGTH_SHORT).show();
                return false;
            case R.id.action_marker:
                Intent intent = new Intent(ListActivity.this, MapMarkerActivity.class);
                startActivity(intent);
                ListActivity.this.finish();
                return true;
            case R.id.action_places:
                intent = new Intent(ListActivity.this, GooglePlacesActivity.class);
                startActivity(intent);
                ListActivity.this.finish();
                return true;
//            case R.id.action_quit:
//                int pid = android.os.Process.myPid();
//                android.os.Process.killProcess(pid);
//                System.exit(0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}