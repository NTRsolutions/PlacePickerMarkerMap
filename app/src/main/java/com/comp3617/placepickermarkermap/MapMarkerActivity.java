package com.comp3617.placepickermarkermap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * FinalProject : com.comp3617.placepickermarkermap
 * File: MapMarkerActivity.java
 * Created by G.E. Eidsness on 04/30/2017
 */

public class MapMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String TAG = MapMarkerActivity.class.getSimpleName();
    private static final float ZOOM = 15f;
    private GoogleMap mGoogleMap;
    //private GoogleApiAvailability googleAPI;
    private static final LatLng VANCOUVER = new LatLng(49.2829607, -123.1204715); // Art Gallery
    //private static final LatLng VANCOUVER = new LatLng(49.2847052, -123.1341435); // Davie/Burrard

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Toast.makeText(MapMarkerActivity.this, String.format("Clicked %s", R.string.title_home), Toast.LENGTH_SHORT)
                            .show();
                    return true;
                case R.id.navigation_dashboard:
                     Toast.makeText(MapMarkerActivity.this, String.format("Clicked %s", R.string.title_dashboard), Toast.LENGTH_SHORT)
                            .show();
                    return true;
                case R.id.navigation_notifications:
                    Toast.makeText(MapMarkerActivity.this, String.format("Clicked %s", R.string.title_notifications), Toast.LENGTH_SHORT)
                            .show();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_marker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MapsInitializer.initialize(this);
        LocationDBHelper rs = LocationDBHelper.getInstance(MapMarkerActivity.this);
        final List<Location> markers = rs.getLocations();
        final List<LatLng> locations = new ArrayList<>();
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                for (int i = 0; i < markers.size(); i++) {
                    Log.d(TAG, markers.get(i).toString());
                    Double lat = markers.get(i).getLatitude();
                    Double lng = markers.get(i).getLongitude();
                    String theName = markers.get(i).getName();
                    String theSnip = markers.get(i).getRemarks();
                    LatLng thePosition = new LatLng(lat, lng);
                    locations.add(thePosition);
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(thePosition)
                            .title(theName)
                            .snippet(theSnip));
                }

                //LatLngBound will cover all your markers on Google Maps
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(locations.get(0)); //Taking Point A (First LatLng)
                builder.include(locations.get(locations.size() - 1)); //Taking Point B (Second LatLng)
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                mGoogleMap.moveCamera(cu);
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
            }
        });
        //mGoogleMap.addMarker(new MarkerOptions().position(VANCOUVER).title("Vancouver Art Gallery"));
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(VANCOUVER));
        //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(VANCOUVER, ZOOM));



        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (int i = 0; i < markers.size(); i++) {
                    Log.d(TAG, markers.get(i).toString());
                    LatLng editPosition = new LatLng(markers.get(i).getLatitude(), markers.get(i).getLongitude());
                    if (marker.getPosition().equals(editPosition)) {
                        Intent editIntent = new Intent(MapMarkerActivity.this, EditActivity.class);
                        editIntent.putExtra("google_id", markers.get(i).getGoogleId());
                        editIntent.putExtra("name", markers.get(i).getName());
                        editIntent.putExtra("address", markers.get(i).getAddress());
                        editIntent.putExtra("remarks", markers.get(i).getRemarks());
                        Bundle b = new Bundle();
                        b.putDouble("latitude", markers.get(i).getLatitude());
                        b.putDouble("longitude", markers.get(i).getLongitude());
                        editIntent.putExtras(b);
                        startActivity(editIntent);
                        finish();
                        break;
                    }
                }
            }
        });

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getBaseContext(), R.string.txtEditInfo, Toast.LENGTH_SHORT).show();
            }
        });
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
                Intent intent = new Intent(MapMarkerActivity.this, ListActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_marker:
                Toast.makeText(this, "You have selected MapMarkerActivity", Toast.LENGTH_SHORT).show();
                return false;
            case R.id.action_places:
                intent = new Intent(MapMarkerActivity.this, GooglePlacesActivity.class);
                startActivity(intent);
                finish();
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
