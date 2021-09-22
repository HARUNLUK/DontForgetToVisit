package com.example.locationnotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.example.locationnotification.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnPoiClickListener {


    private static final int LOCATION_REQUEST_CHECK_CODE = 8989;
    static MapsActivity instance;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ActivityMapsBinding binding;
    private static final String TAG = "MapsActivity";

    //Location Objects
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Location lastLocation;

    public static List<DftMarker> markers = new ArrayList<DftMarker>();

    //Ui object
    UiController uiController;

    //Data Base
    public static final String MARKERS_TABLE_NAME = "MARKERS_TABLE";

    //Shared Preferences
    private SharedPreferencesManager sharedPreferencesMapStyle;
    private String shMapStyle= "MapStyle";

    //Permission Codes
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private int ACCESS_COARSE_LOCATION_REQUEST_CODE = 1003;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        instance = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        sharedPreferencesMapStyle = new SharedPreferencesManager(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLng eiffel = new LatLng(48.8589, 2.29365);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel, 16));
        requestAllPermissions();
        UserSettings.mapStyle = UserSettings.mapStyleString(sharedPreferencesMapStyle.getString(shMapStyle,MODE_PRIVATE));
        ImageView locationImage = ((ImageView) mapFragment.getView().findViewWithTag("GoogleMapMyLocationButton"));
        locationImage.setImageResource(R.drawable.my_location_icon);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnPoiClickListener(this);
        fetchLastLocation();
        updateLocation();
        enableUserLocation();
        for (DftMarker marker : DftMarker.database_getAll(MARKERS_TABLE_NAME)) {
            addMarker(marker);
        }
        uiController = new UiController();
        startService();
    }
    void startService(){
        Intent serviceIntent = new Intent(this,ForegroundService.class);
        serviceIntent.putExtra("inputExtra","Looking for places to remind you");
        ContextCompat.startForegroundService(this,serviceIntent);
    }
    void stopService(){
        Intent serviceIntent = new Intent(this,ForegroundService.class);
        stopService(serviceIntent);
    }
    public static MapsActivity getInstance() {
        return instance;
    }

    private void requestAllPermissions() {
        /*if (ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
        +ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
        +ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        != PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Those Permissions Needed");
                builder.setMessage("App should access your location to work");
                builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MapsActivity.this,
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                }, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("CANCEL", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else{
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        }, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
            }
        }else{
            mMap.setMyLocationEnabled(true);
        }



        */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fetchLastLocation();
            updateLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //should show to user why access needed
                ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)){
                    ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
                }
            }
        }
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("Background access permission needed");
                    builder.setMessage("App should access your background location to work");
                    builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                        }
                    });
                    builder.setNegativeButton("CANCEL", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        }
    }

    private void updateLocation() {
        buildLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    LocationSettingsRequest.Builder locationSettingsBuilder;
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

        locationSettingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).
                checkLocationSettings(locationSettingsBuilder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    task.getResult(ApiException.class);
                } catch (ApiException e) {
                    switch (e.getStatusCode()){
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MapsActivity.this,LOCATION_REQUEST_CHECK_CODE);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            } catch (ClassCastException ex){

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOCATION_REQUEST_CHECK_CODE){
            switch (resultCode){
                case Activity.RESULT_OK:
                    fetchLastLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }
    public static boolean firstZoom=true;
    public void updateLocationView(Location location) {
        MapsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                if(firstZoom){
                    firstZoom = false;
                    zoomLatLang(latLng,16);
                }else{
                    zoomLatLang(latLng);
                }
                //Toast.makeText(MapsActivity.this, value, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lastLocation = location;
                    zoomUserLocation();
                } else {

                }
            }
        });
    }

    private void zoomUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (lastLocation == null) {
            return;
        }
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        float zoom = 16;
        zoomLatLang(latLng, zoom);
    }

    private void zoomLatLang(LatLng latLng, float zoom) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    private void zoomLatLang(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void enableUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    @SuppressLint({"MissingSuperCall", "MissingPermission"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        /*if(requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE){
            if((grantResults.length > 0) && (grantResults[0]+grantResults[1]+grantResults[2] == PackageManager.PERMISSION_GRANTED)){
                zoomUserLocation();
                fetchLastLocation();
                Toast.makeText(instance,"ssss1",Toast.LENGTH_SHORT).show();
                mMap.setMyLocationEnabled(true);
            }else{

            }
        }*/


        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "This access fine ", Toast.LENGTH_SHORT).show();
                fetchLastLocation();
                mMap.setMyLocationEnabled(true);
                updateLocation();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Locaiton access permission needed");
                builder.setMessage("App should access your location to work");
                builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MapsActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapsActivity.this, "This access neccesarry", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        }
        if (requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "This Coarse access fine ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "This access coarse neccesarry", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "This access fine", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "This access backgorund neccesarry", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        clearAllCircles();
        uiController.showAddMarkerBottomSheetDialog(latLng);
    }

    private void addGeofence(LatLng latLng, float radius, int ID) {
        Geofence geofence = geofenceHelper.getGeofence(Integer.toString(ID), latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });

    }
    void addMarker(DftMarker newMarker){
        System.out.println(newMarker.toString());
        newMarker.setIcon(newMarker.getIcon());
        newMarker.setMarker(mMap.addMarker(newMarker.getMarkerOption()));
        newMarker.setCircle(addCircle(newMarker.getLatLng(),newMarker.getDistance()));
        markers.add(newMarker);
    }
    public static void createAndAddMarker(int ID,LatLng latLng, String name, String description, int markerIcon, float distance){
        DftMarker newMarker = new DftMarker(latLng,ID,name,description,markerIcon,distance);
        System.out.println(newMarker.toString());
        newMarker.setMarker(instance.mMap.addMarker(newMarker.getMarkerOption()));
        newMarker.setCircle(instance.addCircle(newMarker.getLatLng(),newMarker.getDistance()));
        instance.addGeofence(newMarker.getLatLng(),newMarker.getDistance(),newMarker.getID());
        markers.add(newMarker);
        DftMarker.database_addMarker(newMarker,MARKERS_TABLE_NAME);
    }
    public static void updateMarker(DftMarker marker,int ID,LatLng latLng, String name, String description, int markerIcon, float distance){
        marker.setID(ID);
        marker.setLatLng(latLng);
        marker.setName(name);
        marker.setDescription(description);
        marker.setIcon(markerIcon);
        marker.setDistance((int) distance);
        marker.marker.setTitle(name);
        marker.marker.setIcon(BitmapDescriptorFactory.fromResource(markerIcon));
        marker.getCircle().setRadius(distance);
        List<String> markersID = new ArrayList<String>();
        markersID.add(Integer.toString(marker.getID()));
        instance.geofencingClient.removeGeofences(markersID);
        instance.addGeofence(marker.getLatLng(), marker.getDistance(), ID);
        DftMarker.database_delete_Marker(marker,MARKERS_TABLE_NAME);
        DftMarker.database_addMarker(marker,MARKERS_TABLE_NAME);
    }
    public static void deleteMarker(DftMarker marker){
        DftMarker.database_delete_Marker(marker,MARKERS_TABLE_NAME);
        marker.getMarker().remove();
        marker.getCircle().setVisible(false);
        List<String> markersID = new ArrayList<String>();
        markersID.add(Integer.toString(marker.getID()));
        markers.remove(marker);
        instance.geofencingClient.removeGeofences(markersID);
    }
    public static int findId(){
        int ID;
        do {
            ID = instance.randomFromTo(1000,10000);
        }while (instance.isIDFound(ID) != null);
        return ID;
    }
    private String findAdress(LatLng latLng){
        String address = "unknown";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            address = addresses.get(0).getAddressLine(0);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"ADDRESS NOT FOUND",Toast.LENGTH_SHORT).show();
        }
        return address;
    }
    int randomFromTo(int a, int b){
        Random rnd = new Random();
        return rnd.nextInt(b-a)+a;
    }
    public static DftMarker isIDFound(int ID){
        for (DftMarker dftMarker : markers){
            if(dftMarker.getID() == ID){
                return dftMarker;
            }
        }
       return null;
    }

    private Circle addCircle(LatLng latLng, float radius){
        clearAllCircles();
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(170,0,0,255));
        circleOptions.fillColor(Color.argb(64,51,153,255));
        circleOptions.strokeWidth(4);
        return (mMap.addCircle(circleOptions));
    }
    public static void clearAllCircles(){
        for (DftMarker marker : markers){
            marker.getCircle().setVisible(false);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void MyButton(View view){
        uiController.openDrawer();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(Location location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 16));
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        clearAllCircles();
        for (DftMarker markerDft : markers){
            if(markerDft.getMarker().equals(marker)){
                uiController.showMarkerDetailBottomSheetDialog(markerDft);
                markerDft.getCircle().setVisible(true);
            }
        }
        marker.hideInfoWindow();
        return true;
    }
    public  static void changeMapStyle(int mapStyle){
        instance.mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(instance ,mapStyle));
        instance.sharedPreferencesMapStyle.addString(instance.shMapStyle,UserSettings.mapStyle.toString(),MODE_PRIVATE);
    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        clearAllCircles();
    }

    @Override
    public void onPoiClick(@NonNull PointOfInterest pointOfInterest) {
        UiController.poiName=pointOfInterest.name;
        uiController.showAddMarkerBottomSheetDialog(pointOfInterest.latLng);
    }
}