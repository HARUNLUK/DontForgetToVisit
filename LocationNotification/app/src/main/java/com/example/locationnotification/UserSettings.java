package com.example.locationnotification;

public class UserSettings {
    public static MapStyle mapStyle;
    public static boolean darkMode;
    public static boolean notification;

    public static MapStyle mapStyleString(String mapStyle){

        switch (mapStyle){
            case "STANDARD":
                return MapStyle.STANDARD;
            case "RETRO":
                return MapStyle.RETRO;
            case "DARK":
                return MapStyle.DARK;
            default:
                return MapStyle.STANDARD;
        }
    }

}

enum MapStyle{
    STANDARD,
    RETRO,
    DARK
}
