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
    private static final LatLng VANCOUVER_ART_GALLERY = new LatLng(49.2829607, -123.1204715); // Art Gallery
    private static final String mArtGallery = "Vancouver Art Gallery - Default Marker";
    private static final String mDefaultMsg = "Click Google Places API to select location";
    //private static final LatLng VANCOUVER = new LatLng(49.2847052, -123.1341435); // Davie/Burrard

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Toast.makeText(MapMarkerActivity.this, String.format("Clicked %s", R.string.title_home), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.navigation_location_list:
                    Intent intent = new Intent(MapMarkerActivity.this, ListActivity.class);
                    startActivity(intent);
                    MapMarkerActivity.this.finish();
                    break;
                case R.id.navigation_location_search:
                    intent = new Intent(MapMarkerActivity.this, GooglePlacesActivity.class);
                    startActivity(intent);
                    MapMarkerActivity.this.finish();
                    break;
            }
            return true;
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
        //final List<LatLng> locations = new ArrayList<>();
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //LatLngBound will cover all your markers on Google Maps
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if (!markers.isEmpty() || markers.size() > 0) {
                    for (int i = 0; i < markers.size(); i++) {
                        Log.d(TAG, markers.get(i).toString());
                        Double lat = markers.get(i).getLatitude();
                        Double lng = markers.get(i).getLongitude();
                        String theName = markers.get(i).getName();
                        String theSnip = markers.get(i).getRemarks();
                        LatLng thePosition = new LatLng(lat, lng);
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(thePosition)
                                .title(theName)
                                .snippet(theSnip));
                        builder.include(thePosition);
                    }
                    LatLngBounds bounds = builder.build();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM), 2000, null);

                } else {
                    mGoogleMap.addMarker(new MarkerOptions().position(VANCOUVER_ART_GALLERY).title(mArtGallery).snippet
                            (mDefaultMsg));
                    //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(VANCOUVER_ART_GALLERY));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(VANCOUVER_ART_GALLERY, ZOOM));
                }
            }
        });


        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (int i = 0; i < markers.size(); i++) {
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
//            case R.id.action_settings:
//                Toast.makeText(this, "You have selected Settings", Toast.LENGTH_SHORT).show();
//                return true;
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
