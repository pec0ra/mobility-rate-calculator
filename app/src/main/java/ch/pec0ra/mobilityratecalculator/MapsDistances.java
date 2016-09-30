/*
 *     Copyright (C) 2016 Basile
 *
 *     This file is part of Mobility Rate Calculator.
 *
 *     Mobility Rate Calculator is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

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
