package com.comp3617.placepickermarkermap;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Project: COMP3617 FinalProject File: EditActivity.java
 * Date: Nov. 26, 2016 Time: 10:34:43 PM
 * Author: G.E. Eidsness
 * <p/>
 * Displays locations. Make use of Google Places API.
 * Add Parameters to selected locations
 */

public class EditActivity extends GoogleApiClientActivity implements View.OnClickListener {

    private static final int EDIT_REQUEST = 200;

    private Location location;
    private LocationDBHelper locationDBHelper;

    private final static String LOG_TAG = EditActivity.class.getSimpleName();
    private TextView mName, mAddress, mGoogleId;
    private EditText mRemarks;
    private Button btnUpdate, btnSave, btnDelete, btnExit;

    private static String name, address, googleId, remarks;
    private static double lat, lng;
    private String minMsg, maxMsg;
    private int maxChar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Modify Record");
        setContentView(R.layout.activity_edit);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mName = (TextView) findViewById(R.id.textView);
        mAddress = (TextView) findViewById(R.id.textView2);
        mGoogleId = (TextView) findViewById(R.id.textView3);
        mRemarks = (EditText) findViewById(R.id.editText);

        btnSave = (Button) findViewById(R.id.buttonSave);
        btnDelete = (Button) findViewById(R.id.buttonDelete);
        btnUpdate = (Button) findViewById(R.id.buttonUpdate);
        btnExit = (Button) findViewById(R.id.buttonExit);

        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        googleId = intent.getStringExtra("google_id");
        remarks = intent.getStringExtra("remarks");
        Bundle b = getIntent().getExtras();
        lat = b.getDouble("latitude");
        lng = b.getDouble("longitude");

        mName.setText(name);
        mAddress.setText(address);
        mGoogleId.setText(googleId);
        mRemarks.setText(remarks);

        minMsg = "Please enter some text";
        maxChar  = getResources().getInteger(R.integer.charCount);
        maxMsg = "The username must be at most " + maxChar + " characters!";

        mRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mRemarks.isFocused() && mRemarks.getText().length() >= maxChar)
                    new AlertDialog.Builder(EditActivity.this).setTitle("Character limit exceeded").setMessage
                            (maxMsg).setPositiveButton(android.R.string.ok, null).show();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    new AlertDialog.Builder(EditActivity.this).setTitle("Character minimum required").setMessage
                            (minMsg).setPositiveButton(android.R.string.ok, null).show();
                    return;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSave:
                saveLocation();
                //updateLocation();
                break;
            case R.id.buttonDelete:
                deleteLocation();
                break;
            case R.id.buttonUpdate:
                updateLocation();
                break;
            case R.id.buttonExit:
                returnHome();
                break;
            default:
                break;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    Toast.makeText(EditActivity.this, String.format("Clicked %s", R.string.title_home), Toast
                            .LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);
                    Toast.makeText(EditActivity.this, String.format("Clicked %s", R.string.title_dashboard), Toast
                            .LENGTH_SHORT)
                            .show();
                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    Toast.makeText(EditActivity.this, String.format("Clicked %s", R.string.title_notifications), Toast
                            .LENGTH_SHORT)
                            .show();
                    return true;
            }
            return false;
        }

    };

    private void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), MapMarkerActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
        finish();
    }

    private void saveLocation() {
        Log.d(LOG_TAG, "saveLocation(): " + googleId);
        String remark = mRemarks.getText().toString().trim();
        if(remark.isEmpty() || remark.trim().length()==0){
            //Toast.makeText(getApplicationContext(),"Enter remark", Toast.LENGTH_SHORT).show();
            mRemarks.setText(null);
            mRemarks.setHint("Enter note");
            return;
        }
        location = new Location();
        location.setGoogleId(googleId);
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setName(name);
        location.setAddress(address);
        location.setRemarks(remark);
        locationDBHelper = LocationDBHelper.getInstance(getApplicationContext());
        locationDBHelper.createLocation(location);
        //locationDBHelper.addOrUpdateLocation(location);
        //Log.d(LOG_TAG, "Values:" + location.toString());
        returnHome();
    }
    /* No worky */
    private void updateLocation() {
        Log.d(LOG_TAG, "updateLocation()");
        String item  = mRemarks.getText().toString().trim();
        Log.d(LOG_TAG, "Item: " + item);
        if(item.isEmpty() || item.trim().length()==0){
            Toast.makeText(getApplicationContext(),"Enter Title", Toast.LENGTH_SHORT).show();
            mRemarks.setText(null);
            mRemarks.setHint("Enter note");
            return;
        }
        Bundle b = getIntent().getExtras();
        location = b.getParcelable("com.comp3617.placepickermarkermap.scooch_locations.db");
        if (location != null) {
            location.setRemarks(item);
            mRemarks.setTextColor(Color.CYAN);
        }
        locationDBHelper = LocationDBHelper.getInstance(getApplicationContext());
        locationDBHelper.updateLocation(location);
        Log.d(LOG_TAG, "Updated: " + location.getGoogleId());
        finish();
    }

    private void deleteLocation() {
        Log.d(LOG_TAG, "btnDelete Clicked");
        locationDBHelper = LocationDBHelper.getInstance(getApplicationContext());
        Log.d(LOG_TAG, "locationDBHelper: " + googleId);
        locationDBHelper.deleteLocationWithGoogleId(googleId);
        Log.d(LOG_TAG, "Deleted: " + googleId);
        returnHome();
    }

    private void exitProgram() {
        Log.d(LOG_TAG, "Exit finish()");
        finish();
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
                Intent intent = new Intent(EditActivity.this, ListActivity.class);
                startActivity(intent);
                EditActivity.this.finish();
                return true;
            case R.id.action_marker:
                intent = new Intent(EditActivity.this, MapMarkerActivity.class);
                startActivity(intent);
                EditActivity.this.finish();
                return true;
            case R.id.action_places:
                intent = new Intent(EditActivity.this, GooglePlacesActivity.class);
                startActivity(intent);
                EditActivity.this.finish();
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
