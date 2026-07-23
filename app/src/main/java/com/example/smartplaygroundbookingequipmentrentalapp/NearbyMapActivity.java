package com.example.smartplaygroundbookingequipmentrentalapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NearbyMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String selectedSport = "";
    private LatLng lastKnownLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupFilters();
    }

    private void setupFilters() {
        findViewById(R.id.filterAll).setOnClickListener(v -> { selectedSport = ""; refreshMap(); });
        findViewById(R.id.filterCricket).setOnClickListener(v -> { selectedSport = "Cricket"; refreshMap(); });
        findViewById(R.id.filterFootball).setOnClickListener(v -> { selectedSport = "Football"; refreshMap(); });
        findViewById(R.id.filterBadminton).setOnClickListener(v -> { selectedSport = "Badminton"; refreshMap(); });
    }

    private void refreshMap() {
        if (lastKnownLatLng != null) {
            fetchNearbyPlaygrounds(lastKnownLatLng.latitude, lastKnownLatLng.longitude);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        // Only move camera if it's the first time
                        if (mMap.getCameraPosition().zoom < 5) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 14));
                        }
                        refreshMap();
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void fetchNearbyPlaygrounds(double lat, double lng) {
        String url = "http://10.0.2.2/smart_playground/get_playgrounds.php?lat=" + lat + "&lng=" + lng;
        if (!selectedSport.isEmpty()) {
            url += "&sport=" + selectedSport;
        }

        findViewById(R.id.loader).setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    findViewById(R.id.loader).setVisibility(View.GONE);
                    try {
                        JSONArray array = response.getJSONArray("data");
                        mMap.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            LatLng pos = new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude"));
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .title(obj.getString("name"))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            marker.setTag(obj);
                        }

                        mMap.setOnMarkerClickListener(marker -> {
                            if (marker.getTag() != null) {
                                try {
                                    showDetailsCard((JSONObject) marker.getTag());
                                } catch (JSONException e) { e.printStackTrace(); }
                            }
                            return false;
                        });

                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> {
                    findViewById(R.id.loader).setVisibility(View.GONE);
                    Toast.makeText(this, "API Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void showDetailsCard(JSONObject data) throws JSONException {
        View card = findViewById(R.id.detailsCard);
        card.setVisibility(View.VISIBLE);
        card.setTag(data); // Store data for "Book Now" click

        TextView name = findViewById(R.id.groundName);
        TextView distance = findViewById(R.id.groundDistance);
        TextView time = findViewById(R.id.travelTime);
        TextView sports = findViewById(R.id.groundSports);
        TextView pricing = findViewById(R.id.groundPricing);

        name.setText(data.getString("name"));
        double d = data.getDouble("distance");
        distance.setText(String.format("%.2f KM away", d));
        
        // Est. travel time: assume 30 km/h average in city
        int mins = (int) ((d / 30.0) * 60) + 2; // +2 for buffer
        time.setText("Est. " + mins + " mins travel");

        sports.setText("Sports: " + data.getString("sports"));
        pricing.setText("Rating: " + data.getString("rating") + " | ₹" + data.getString("price_per_hour") + "/hr");

        findViewById(R.id.btnNavigate).setOnClickListener(v -> {
            try {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + data.getDouble("latitude") + "," + data.getDouble("longitude"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } catch (JSONException e) { e.printStackTrace(); }
        });

        findViewById(R.id.btnBookNow).setOnClickListener(v -> {
            try {
                // Prepare playground model for the booking screen
                com.example.smartplaygroundbookingequipmentrentalapp.model.Playground pg = 
                    new com.example.smartplaygroundbookingequipmentrentalapp.model.Playground(
                        data.getString("id"),
                        data.getString("name"),
                        data.getString("address"),
                        data.getDouble("rating"),
                        128, // reviews_count
                        data.getInt("price_per_hour"),
                        0, // imageResId
                        java.util.Collections.singletonList(data.getString("sports")),
                        "Free"
                    );
                
                // Set the selected playground in global state
                com.example.smartplaygroundbookingequipmentrentalapp.model.GlobalState.INSTANCE.setSelectedPlayground(pg);
                
                // Return to MainActivity with a signal to open booking screen
                Intent resultIntent = new Intent();
                resultIntent.putExtra("action", "open_booking");
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        }
    }
}
