package com.comp7082.photogallery.Presenters;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * Facade class to use when getting location.
 */
public class LocationTagger {
    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;
    private String latitude;
    private String longitude;

    public LocationTagger(Activity activity) {
        this.activity = activity;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity);

        getLocation();
    }

    /**
     * Get the user's location. If permission is not granted initially, try requesting it.
     */
    public void getLocation() {
        // Permissions denied
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(activity,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        // Get location
        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
            if (location != null) {
                latitude = String.valueOf(location.getLatitude());
                latitude = latitude.substring(0,Math.min(latitude.length(),6));

                longitude = String.valueOf(location.getLongitude());
                longitude = longitude.substring(0,Math.min(longitude.length(),6));
            }
        });
    }

    public String getLatitude() { return latitude; }

    public String getLongitude() { return longitude; }
}
