package com.example.locationnotification;

import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class DftMarker{
    Marker marker;


    public static final float DISTANCE_CLOSE = 25f;
    public static final float DISTANCE_MEDIUM = 75f;
    public static final float DISTANCE_FAR = 200f;

    private LatLng latLng;
    private int ID;
    private String name;
    private String description;
    private int icon;
    private float distance;
    private boolean isCompleted;
    private boolean isFlat;
    private Circle circle;

    public DftMarker(LatLng latLng, int ID, String name, String description, int markerIcon,  float distance) {
        this.latLng = latLng;
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.icon = markerIcon;
        this.distance = distance;
    }
    public static void database_addMarker(DftMarker marker,String tableName){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MapsActivity.getInstance(),tableName);
        boolean success = dataBaseHelper.addOne(marker);
        Toast.makeText(MapsActivity.getInstance(),"Success +"+success,Toast.LENGTH_SHORT).show();
    }
    public static List<DftMarker> database_getAll(String tableName){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MapsActivity.getInstance(),tableName);
        List<DftMarker> everyone = dataBaseHelper.getEveryone();
        return everyone;
    }
    public static boolean database_delete_Marker(DftMarker marker, String tableName){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(MapsActivity.getInstance(),tableName);
        return dataBaseHelper.deleteOne(marker);
    }
    public static float idToFloat(int ID){
        switch (ID){
            case R.id.distance_close:
                return  DISTANCE_CLOSE;
            case R.id.distance_medium:
                return  DISTANCE_MEDIUM;
            case R.id.distance_far:
                return  DISTANCE_FAR;
            default:
                return  DISTANCE_MEDIUM;
        }
    }
    public static int idToIcon(int ID){
        switch (ID){
            case R.id.marker_default:
                return R.drawable.defauld;
            case R.id.marker_home:
                return R.drawable.home;
            case R.id.marker_shop:
                return R.drawable.shop;
            case R.id.marker_medicine:
                return R.drawable.medicine;
            case R.id.marker_job:
                return R.drawable.job;
            case R.id.marker_important:
                return R.drawable.important;
            default:
                return R.drawable.defauld;
        }
    }





    public MarkerOptions getMarkerOption(){
        MarkerOptions markerOption = new MarkerOptions()
                .title(name)
                .snippet(Integer.toString(ID))
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(getIcon()))
                .anchor(0.5f, 0.5f);
        return markerOption;
    }
    @Override
    public String toString() {
        return "dftMarker{" +
                "latLng=" + latLng +
                ", ID=" + ID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", icon=" + icon +
                ", distance=" + distance +
                ", isCompleted=" + isCompleted +
                '}';
    }
    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIcon() {
        switch (icon){
            case R.drawable.defauld:
                return R.drawable.defauld;
            case R.drawable.home:
                return R.drawable.home;
            case R.drawable.shop:
                return R.drawable.shop;
            case R.drawable.medicine:
                return R.drawable.medicine;
            case R.drawable.job:
                return R.drawable.job;
            case R.drawable.important:
                return R.drawable.important;
            default:
                return R.drawable.defauld;
        }
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }

    public float getDistance() {
        return distance;
    }
    public String getDistanceName() {
        switch ((int)getDistance()){
            case 25:
                return "CLOSE";
            case 75:
                return "MEDIUM";
            case 200:
                return "FAR";
            default:
                return "CLOSE";
        }
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    public boolean isFlat() {
        return isFlat;
    }

    public void setFlat(boolean flat) {
        isFlat = flat;
    }
    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }
}
