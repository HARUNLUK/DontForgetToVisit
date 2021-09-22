package com.example.locationnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int ID = intent.getIntExtra("ID",0);
        DftMarker dftMarker = MapsActivity.isIDFound(ID);
        if(dftMarker != null){
            MapsActivity.deleteMarker(dftMarker);
            Toast.makeText(MapsActivity.instance,dftMarker.getName()+" deleted",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MapsActivity.instance," not found",Toast.LENGTH_SHORT).show();
        }
    }
}
