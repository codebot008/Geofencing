package com.example.ankitjena.geofenceapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private GeofencingClient mGeofencingClient;
    final int MY_PERMISSION_REQUEST_FINE_LOCATION = 1;
    private PendingIntent mGeofencePendingIntent;

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private List<Geofence> createGeofenceList() {
        Log.i("Message", "Inside createGeofenceList()");
        List<Geofence> geofenceList = new ArrayList<Geofence>();
        Geofence geofence = new Geofence.Builder().setRequestId("NBA").
                setCircularRegion(37.410109, -122.059732, 10).
                setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT).
                setExpirationDuration(Geofence.NEVER_EXPIRE).
                build();
        geofenceList.add(geofence);
        return geofenceList;
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        Log.i("Message", "Inside getGeofencePendingIntent()");
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        Log.i("Pending Intent", "Returning from getGeofencePendingIntent()");
        return mGeofencePendingIntent;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.i("App", "Started here.");
    }

//    private void checkInsideFence(List<Geofence> l) {
//        Geofence g
//        for(Geofence g : l) {
//
//            Location loc = g.getRequestId()
//        }
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Log : ", "App started.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Message","No permissions granted, asking for permissions now.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
        }

        mGeofencingClient = LocationServices.getGeofencingClient(this);

        List<Geofence> mGeofenceLst = createGeofenceList();

        mGeofencePendingIntent = getGeofencePendingIntent();


        Log.i("Message", "Just before actual geofence trigger.");
        mGeofencingClient.addGeofences(getGeofencingRequest(mGeofenceLst), mGeofencePendingIntent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("Success", "Geofences Added.");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.i("Failure", "Geofences could not be added.");
                    }
                });

    }
}
