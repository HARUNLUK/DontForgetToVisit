package com.example.locationnotification;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Map;

public class UiController {
    public static Context context;
    DrawerLayout userDrawerLayout;

    public UiController() {
        context = MapsActivity.getInstance();
        startUserDrawer();
    }


    public void openDrawer() {
        userDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer(DrawerLayout userDrawerLayout) {
        if(userDrawerLayout.isDrawerOpen(GravityCompat.START)){
            userDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    public void startUserDrawer(){
        userDrawerLayout = MapsActivity.getInstance().findViewById(R.id.user_panel_drawer_layout);
        setMapStyle(UserSettings.mapStyle);
        RadioGroup mapStyleRadioGroup = userDrawerLayout.findViewById(R.id.map_style_radiogroup);
        ImageView standardStyleImg = userDrawerLayout.findViewById(R.id.map_style_img_standard);
        ImageView retroStyleImg = userDrawerLayout.findViewById(R.id.map_style_img_retro);
        ImageView darkStyleImg = userDrawerLayout.findViewById(R.id.map_style_img_dark);

        mapStyleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.map_style_radio_standard:
                        setMapStyle(MapStyle.STANDARD);
                        break;
                    case R.id.map_style_radio_retro:
                        setMapStyle(MapStyle.RETRO);
                        break;
                    case R.id.map_style_radio_dark:
                        setMapStyle(MapStyle.DARK);
                        break;
                    default:
                        setMapStyle(MapStyle.STANDARD);
                        break;
                }
            }
        });
        standardStyleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMapStyle(MapStyle.STANDARD);
            }
        });
        retroStyleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMapStyle(MapStyle.RETRO);
            }
        });
        darkStyleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMapStyle(MapStyle.DARK);
            }
        });
    }





    public void showMarkerDetailBottomSheetDialog(DftMarker dftMarker){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.marker_detail_bottom_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        LinearLayout markerInfoTexts = bottomSheetDialog.findViewById(R.id.marker_info_text_layout);
        LinearLayout markerInfoEdits = bottomSheetDialog.findViewById(R.id.marker_info_edittext_layout);
        LinearLayout markerInfoIcons = bottomSheetDialog.findViewById(R.id.marker_icon_radio_edit_layout);
        LinearLayout markerInfoDistances = bottomSheetDialog.findViewById(R.id.marker_info_distance_edit_layout);
        RadioGroup markerInfoIconRadioGroup = bottomSheetDialog.findViewById(R.id.marker_icon_radiogroup);
        RadioGroup markerInfoDistanceRadioGroup = bottomSheetDialog.findViewById(R.id.marker_info_distance_radiogroup);
        ImageView markerInfoIconImage = bottomSheetDialog.findViewById(R.id.markerInfoIconImage);
        markerInfoIconImage.setImageResource(dftMarker.getIcon());
        TextView markerInfoTitle = bottomSheetDialog.findViewById(R.id.marker_info_title_text);
        markerInfoTitle.setText(dftMarker.getName());
        TextView markerInfoDesc = bottomSheetDialog.findViewById(R.id.marker_info_description_text);
        markerInfoDesc.setText(dftMarker.getDescription());
        EditText markerInfoEditTitle = bottomSheetDialog.findViewById(R.id.marker_info_title_edittext);
        EditText markerInfoEditDesc = bottomSheetDialog.findViewById(R.id.marker_info_description_edittext);
        TextView markerInfoDistance = bottomSheetDialog.findViewById(R.id.marker_info_distance_text);
        markerInfoDistance.setText(dftMarker.getDistanceName());
        ImageView markerInfoEditDone = bottomSheetDialog.findViewById(R.id.editDoneIcon);
        ImageView markerInfoEdit = bottomSheetDialog.findViewById(R.id.editIcon);
        ImageView markerInfoDelete = bottomSheetDialog.findViewById(R.id.deleteIcon);
        switch (dftMarker.getIcon()){
            case R.drawable.defauld:
                ((RadioButton)bottomSheetDialog.findViewById(R.id.marker_default)).setChecked(true);
                break;
            case R.drawable.home:
                ((RadioButton)bottomSheetDialog.findViewById(R.id.marker_home)).setChecked(true);
                break;
            case R.drawable.shop:
                ((RadioButton)bottomSheetDialog.findViewById(R.id.marker_shop)).setChecked(true);
                break;
            case R.drawable.medicine:
                ((RadioButton)bottomSheetDialog.findViewById(R.id.marker_medicine)).setChecked(true);
                break;
            case R.drawable.job:
                ((RadioButton)bottomSheetDialog.findViewById(R.id.marker_job)).setChecked(true);
                break;
            case R.drawable.important:
                ((RadioButton)bottomSheetDialog.findViewById(R.id.marker_important)).setChecked(true);
                break;
            default:
                ((RadioButton)bottomSheetDialog.findViewById(R.id.marker_default)).setChecked(true);
                break;
        }
        switch (dftMarker.getDistanceName()){
            case "CLOSE":
                ((RadioButton)bottomSheetDialog.findViewById(R.id.distance_close)).setChecked(true);
                break;
            case "MEDIUM":
                ((RadioButton)bottomSheetDialog.findViewById(R.id.distance_medium)).setChecked(true);
                break;
            case "FAR":
                ((RadioButton)bottomSheetDialog.findViewById(R.id.distance_far)).setChecked(true);
                break;
            default:
                ((RadioButton)bottomSheetDialog.findViewById(R.id.distance_medium)).setChecked(true);
                break;
        }
        markerInfoDistanceRadioGroup.findViewById(R.id.distance_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiController.distanceButtonSelect(bottomSheetDialog,v.getId());
            }
        });
        markerInfoDistanceRadioGroup.findViewById(R.id.distance_medium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiController.distanceButtonSelect(bottomSheetDialog,v.getId());
            }
        });
        markerInfoDistanceRadioGroup.findViewById(R.id.distance_far).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiController.distanceButtonSelect(bottomSheetDialog,v.getId());
            }
        });
        markerInfoIconRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int newIcon = DftMarker.idToIcon(checkedId);
                markerInfoIconImage.setImageResource(newIcon);
            }
        });
        markerInfoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerInfoEditDone.setVisibility(View.VISIBLE);
                markerInfoEdit.setVisibility(View.INVISIBLE);
                markerInfoTexts.setVisibility(View.GONE);
                markerInfoEdits.setVisibility(View.VISIBLE);
                markerInfoIcons.setVisibility(View.VISIBLE);
                markerInfoDistances.setVisibility(View.VISIBLE);
                markerInfoEditTitle.setText(dftMarker.getName());
                markerInfoEditDesc.setText(dftMarker.getDescription());
            }
        });
        markerInfoEditDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = markerInfoEditTitle.getText().toString();
                String newDescription = markerInfoEditDesc.getText().toString();
                int newIcon = DftMarker.idToIcon(markerInfoIconRadioGroup.getCheckedRadioButtonId());
                float newDistance = DftMarker.idToFloat(markerInfoDistanceRadioGroup.getCheckedRadioButtonId());
                MapsActivity.updateMarker(dftMarker,dftMarker.getID(),dftMarker.getLatLng(),newName,newDescription,newIcon,newDistance);

                markerInfoEditDone.setVisibility(View.INVISIBLE);
                markerInfoEdit.setVisibility(View.VISIBLE);
                markerInfoTexts.setVisibility(View.VISIBLE);
                markerInfoEdits.setVisibility(View.GONE);
                markerInfoIcons.setVisibility(View.GONE);
                markerInfoDistances.setVisibility(View.GONE);
                bottomSheetDialog.dismiss();
            }
        });
        markerInfoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.deleteMarker(dftMarker);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();

    }
    public static String poiName="";
    public void showAddMarkerBottomSheetDialog(LatLng latLng){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.marker_add_bottom_sheet);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        EditText markerName = bottomSheetDialog.findViewById(R.id.bottom_marker_name);
        if(!poiName.equals("")){
            markerName.setText(poiName);
            poiName="";
        }
        EditText markerDescription = bottomSheetDialog.findViewById(R.id.bottom_marker_Description);
        RadioGroup distanceRadioGroup = bottomSheetDialog.findViewById(R.id.marker_distance_radio_group);
        distanceRadioGroup.findViewById(R.id.distance_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiController.distanceButtonSelect(bottomSheetDialog,v.getId());
            }
        });
        distanceRadioGroup.findViewById(R.id.distance_medium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiController.distanceButtonSelect(bottomSheetDialog,v.getId());
            }
        });
        distanceRadioGroup.findViewById(R.id.distance_far).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiController.distanceButtonSelect(bottomSheetDialog,v.getId());
            }
        });
        RadioGroup iconRadioGroup = bottomSheetDialog.findViewById(R.id.marker_icon_radio_group);
        Button markerAddButton = bottomSheetDialog.findViewById(R.id.bottom_marker_Submit);
        markerAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _markerName = markerName.getText().toString();;
                String _markerDescription = markerDescription.getText().toString();
                int selectedIcon = DftMarker.idToIcon(iconRadioGroup.getCheckedRadioButtonId());
                float selectedDistance = DftMarker.idToFloat(distanceRadioGroup.getCheckedRadioButtonId());
                if(_markerName.equals("") || _markerDescription.equals("")){
                    Toast.makeText(context,"Please Enter ALL",Toast.LENGTH_SHORT).show();
                }else{
                    MapsActivity.createAndAddMarker(MapsActivity.findId(),latLng,_markerName,_markerDescription,selectedIcon,selectedDistance);
                    poiName="";
                    bottomSheetDialog.dismiss();
                }
            }
        });
        bottomSheetDialog.show();
    }


    void setMapStyle(MapStyle mapStyle){
        UserSettings.mapStyle = mapStyle;
        RadioButton standardStyleRadio = userDrawerLayout.findViewById(R.id.map_style_radio_standard);
        RadioButton retroStyleRadio = userDrawerLayout.findViewById(R.id.map_style_radio_retro);
        RadioButton darkStyleRadio = userDrawerLayout.findViewById(R.id.map_style_radio_dark);
        switch (mapStyle){
            case STANDARD:
                standardStyleRadio.setChecked(true);
                MapsActivity.changeMapStyle(R.raw.mapstandard);
                break;
            case RETRO:
                retroStyleRadio.setChecked(true);
                MapsActivity.changeMapStyle(R.raw.retromap);
                break;
            case DARK:
                darkStyleRadio.setChecked(true);
                MapsActivity.changeMapStyle(R.raw.darkmap);
                break;
            default:
                standardStyleRadio.setChecked(true);
                MapsActivity.changeMapStyle(R.raw.mapstandard);
                break;
        }
    }


    public static void distanceButtonSelect(BottomSheetDialog view, int ID){
        ((RadioButton)view.findViewById(R.id.distance_close)).setTextColor(context.getResources().getColor(R.color.blue2));
        ((RadioButton)view.findViewById(R.id.distance_medium)).setTextColor(context.getResources().getColor(R.color.blue2));
        ((RadioButton)view.findViewById(R.id.distance_far)).setTextColor(context.getResources().getColor(R.color.blue2));
        switch (ID){
            case R.id.distance_close:
                ((RadioButton)view.findViewById(R.id.distance_close)).setTextColor(context.getResources().getColor(R.color.blue2));
                break;
            case R.id.distance_medium:
                ((RadioButton)view.findViewById(R.id.distance_medium)).setTextColor(context.getResources().getColor(R.color.blue2));
                break;
            case R.id.distance_far:
                ((RadioButton)view.findViewById(R.id.distance_far)).setTextColor(context.getResources().getColor(R.color.blue2));
                break;
        }
    }
}
