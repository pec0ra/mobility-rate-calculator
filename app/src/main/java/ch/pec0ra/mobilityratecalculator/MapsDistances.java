package ch.pec0ra.mobilityratecalculator;

/**
 * Created by basile on 26.09.16.
 */

public class MapsDistances {
    public static String distancesKey = "";
    public static String directionsKey = "";
    public static String staticMapKey = "";

    public static final String BASE_DISTANCES_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    public static final String ORIGINS = "origins=";
    public static final String DESTINATIONS = "destinations=";

    public static final String BASE_DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String ORIGIN = "origin=";
    public static final String DESTINATION = "destination=";

    public static final String BASE_MAP_URL = "https://maps.googleapis.com/maps/api/staticmap?";
    public static final String SIZE_OPTION = "size=";
    public static final String MAP_TYPE = "maptype=roadmap";
    public static final String MARKERS_A = "markers=label:A|";
    public static final String MARKERS_B = "markers=label:B|";
    public static final String PATH = "path=enc:";


    public static final String KEY = "key=";
    public static final String AND = "&";
    public static final String X = "x";

    public static final int WIDTH = 600;
    public static final int HEIGHT = 450;


}
