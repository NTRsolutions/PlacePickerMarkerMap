package com.comp3617.placepickermarkermap;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Project: COMP3617 FinalProject File: GooglePlacesActivity.java
 * Date: Nov. 26, 2016 Time: 10:34:43 PM
 * Author: G.E. Eidsness
 * <p/>
 * Allows the user to select locations using Google Places API
 */

public class GooglePlacesActivity extends GoogleApiClientActivity implements View.OnClickListener {

    private static final int PLACE_PICKER_REQUEST = 100;
    private static final int EDIT_REQUEST = 200;
    private final static String LOG_TAG = GooglePlacesActivity.class.getSimpleName();

    protected LocationDBHelper locationDBHelper;
    private TextView mName, mAddress, mGoogleId, mRemarks;
    private Button btnEdit, btnExit;

    private static double mLatitude, mLongitude;

    private static double swLat = 49.2706048, swLong = -123.13579559;
    private static double neLat = 49.28997818, neLong = -123.1193161;
    private static LatLng southWest = new LatLng(swLat, swLong);
    private static LatLng northEast = new LatLng(neLat, neLong);
    private static final LatLngBounds BOUNDS_WESTEND_VIEW = new LatLngBounds(southWest, northEast);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "onCreate()");

        mName = (TextView) findViewById(R.id.textView);
        mAddress = (TextView) findViewById(R.id.textView2);
        mGoogleId = (TextView) findViewById(R.id.textView3);
        mRemarks = (TextView) findViewById(R.id.textView4);
        //mLatitude = (TextView) findViewById(R.id.textLat);
        //mLongitude = (TextView) findViewById(R.id.textLng);

        btnEdit = (Button) findViewById(R.id.buttonEdit);
        btnExit = (Button) findViewById(R.id.buttonExit);

        btnEdit.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(BOUNDS_WESTEND_VIEW);
            Intent intent = intentBuilder.build(GooglePlacesActivity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonEdit:
                editLocation();
                break;
            case R.id.buttonExit:
                exitProgram();
                break;
            default:
                break;
        }
    }

    private void editLocation() {
        Log.d(LOG_TAG, "Edit Clicked");
        Intent editIntent = new Intent(getApplicationContext(), EditActivity.class);
        editIntent.putExtra("name", mName.getText().toString());
        editIntent.putExtra("address", mAddress.getText().toString());
        editIntent.putExtra("google_id", mGoogleId.getText().toString());
        editIntent.putExtra("remarks", mRemarks.getText().toString());
        Bundle b = new Bundle();
        b.putDouble("latitude", mLatitude);
        b.putDouble("longitude", mLongitude);
        editIntent.putExtras(b);
        Log.d(LOG_TAG, "Intent Values: " + mGoogleId.getText().toString());
        startActivityForResult(editIntent, EDIT_REQUEST);
    }

    private void exitProgram() {
        Log.d(LOG_TAG, "Exit finish()");
        Intent exitIntent = new Intent(GooglePlacesActivity.this, MapMarkerActivity.class);
        startActivity(exitIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.d(LOG_TAG, "RESULT_OK");
            getPlaceFromPicker(data);
        } else {
            Log.d(LOG_TAG, "onActivityResult Error");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getPlaceFromPicker(Intent data) {
        Log.d(LOG_TAG, "getPlaceFromPicker()");
        final Place place = PlacePicker.getPlace(this, data);
        if (place != null && place.getId().length() != 0) {
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            mLatitude = place.getLatLng().latitude;
            mLongitude = place.getLatLng().longitude;
            final CharSequence googleId = place.getId();
            String remarks = getLocationRemarksWithGoogleId(String.valueOf(googleId));
            btnEdit.setVisibility(View.VISIBLE);
            btnExit.setVisibility(View.VISIBLE);
            mName.setText(name);
            mAddress.setText(address);
            mGoogleId.setText(googleId);
            mRemarks.setText(remarks);
        }
    }

    private String getLocationRemarksWithGoogleId(String googleId) {
        locationDBHelper = LocationDBHelper.getInstance(getApplicationContext());
        Log.d(LOG_TAG, "locationDBHelper: " + googleId);
        String result = null;
        if (locationDBHelper.doesLocationExist(googleId)) {
            result = locationDBHelper.getLocationByGoogleId(googleId);
        } else
            result = "No Data!";
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "You have selected Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_list:
                Intent intent = new Intent(GooglePlacesActivity.this, ListActivity.class);
                startActivity(intent);
                GooglePlacesActivity.this.finish();
                return true;
            case R.id.action_marker:
                intent = new Intent(GooglePlacesActivity.this, MapMarkerActivity.class);
                startActivity(intent);
                GooglePlacesActivity.this.finish();
                return true;
            case R.id.action_places:
                intent = new Intent(GooglePlacesActivity.this, GooglePlacesActivity.class);
                startActivity(intent);
                GooglePlacesActivity.this.finish();
                return true;
            case R.id.action_quit:
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                System.exit(0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
