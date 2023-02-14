package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import com.google.firebase.analytics.FirebaseAnalytics;

public class LocationTrack extends Service implements LocationListener {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 60000;
    boolean canGetLocation = false;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    double latitude;
    Location loc;
    protected LocationManager locationManager;
    double longitude;
    /* access modifiers changed from: private */
    public final Context mContext;

    public LocationTrack(Context mContext2) {
        this.mContext = mContext2;
        getLocation();
    }

    private Location getLocation() {
        try {
            LocationManager locationManager2 = (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);
            this.locationManager = locationManager2;
            this.checkGPS = locationManager2.isProviderEnabled("gps");
            boolean isProviderEnabled = this.locationManager.isProviderEnabled("network");
            this.checkNetwork = isProviderEnabled;
            boolean z = this.checkGPS;
            if (z || isProviderEnabled) {
                this.canGetLocation = true;
                if (z) {
                    if (ActivityCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION") != 0) {
                        ActivityCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION");
                    }
                    this.locationManager.requestLocationUpdates("gps", 60000, 10.0f, this);
                    LocationManager locationManager3 = this.locationManager;
                    if (locationManager3 != null) {
                        Location lastKnownLocation = locationManager3.getLastKnownLocation("gps");
                        this.loc = lastKnownLocation;
                        if (lastKnownLocation != null) {
                            this.latitude = lastKnownLocation.getLatitude();
                            this.longitude = this.loc.getLongitude();
                        }
                    }
                }
                return this.loc;
            }
            Toast.makeText(this.mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();
            return this.loc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getLongitude() {
        Location location = this.loc;
        if (location != null) {
            this.longitude = location.getLongitude();
        }
        return this.longitude;
    }

    public double getLatitude() {
        Location location = this.loc;
        if (location != null) {
            this.latitude = location.getLatitude();
        }
        return this.latitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.mContext);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LocationTrack.this.mContext.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void stopListener() {
        if (this.locationManager == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            this.locationManager.removeUpdates(this);
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onLocationChanged(Location location) {
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    public void onProviderEnabled(String s) {
    }

    public void onProviderDisabled(String s) {
    }
}