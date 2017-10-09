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
 *     Mobility Rate Calculator is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Mobility Rate Calculator.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.pec0ra.mobilityratecalculator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class DistanceCalculator {

    private final String from;
    private final String to;
    private final boolean isTwoWay;
    private final Context context;


    public DistanceCalculator(String from, String to, boolean isTwoWay, Context context) {
        this.from = from;
        this.to = to;
        this.isTwoWay = isTwoWay;
        this.context = context;
    }

    public Itinerary calculate() {
        if(!checkConnection()){
            return new Itinerary(context.getString(R.string.connexion_error));
        }

        try {
            URL url = new URL(buildImageUrl());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            String ret = downloadUrl(buildDistancesUrl());
            return new Itinerary(new JSONObject(ret), isTwoWay, bmp);
        } catch (Exception e) {
            e.printStackTrace();
            return new Itinerary(context.getString(R.string.connexion_error));
        }
    }

    private String buildDistancesUrl() {
        String ret = MapsDistances.BASE_DISTANCES_URL;
        ret += MapsDistances.ORIGINS + Uri.encode(from);
        ret += MapsDistances.AND;
        ret += MapsDistances.DESTINATIONS + Uri.encode(to);
        ret += MapsDistances.AND;
        ret += MapsDistances.KEY + MapsDistances.distancesKey;
        return ret;
    }
    private String buildDirectionsUrl() {
        String ret = MapsDistances.BASE_DIRECTIONS_URL;
        ret += MapsDistances.ORIGIN + Uri.encode(from);
        ret += MapsDistances.AND;
        ret += MapsDistances.DESTINATION + Uri.encode(to);
        ret += MapsDistances.AND;
        ret += MapsDistances.KEY + MapsDistances.directionsKey;
        return ret;
    }
    private String buildImageUrl() throws IOException, JSONException {

        String polyLines = getOverviewPolylines();

        String ret = MapsDistances.BASE_MAP_URL;
        ret += MapsDistances.SIZE_OPTION + MapsDistances.WIDTH + MapsDistances.X + MapsDistances.HEIGHT;
        ret += MapsDistances.AND;
        ret += MapsDistances.MAP_TYPE;
        ret += MapsDistances.AND;
        ret += MapsDistances.MARKERS_A + Uri.encode(from);
        ret += MapsDistances.AND;
        ret += MapsDistances.MARKERS_B + Uri.encode(to);
        ret += MapsDistances.AND;
        ret += MapsDistances.PATH + polyLines;
        ret += MapsDistances.AND;
        ret += MapsDistances.KEY + MapsDistances.staticMapKey;
        return ret;
    }

    private String getOverviewPolylines() throws IOException, JSONException {
        JSONObject object = new JSONObject(downloadUrl(buildDirectionsUrl()));
        String status = object.getString("status");
        if(!status.equals("OK")){
            throw new RuntimeException();
        }

        JSONObject route = object.getJSONArray("routes").getJSONObject(0);
        return route.getJSONObject("overview_polyline").getString("points");
    }

    public class Itinerary{
        public final Bitmap image;
        public final int distance;
        public final String from;
        public final String to;
        public final boolean isTwoWay;

        public boolean hasError = false;
        public String errorMessage = null;

        public Itinerary(String errorMessage){
            image = null;
            distance = 0;
            from = null;
            to = null;
            isTwoWay = false;
            hasError = true;
            this.errorMessage = errorMessage;
        }
        public Itinerary(JSONObject jsonObject, boolean isTwoWay, Bitmap image) throws JSONException {
            this.isTwoWay = isTwoWay;
            String status = jsonObject.getString("status");
            if(!status.equals("OK")){
                if(status.equals("INVALID_REQUEST")){
                    this.image = null;
                    distance = 0;
                    from = null;
                    to = null;
                    hasError = true;
                    this.errorMessage = context.getString(R.string.invalid_request);
                    return;
                } else {
                    throw new RuntimeException();
                }
            }
            from = jsonObject.getJSONArray("origin_addresses").getString(0);
            to = jsonObject.getJSONArray("destination_addresses").getString(0);

            JSONObject row = jsonObject.getJSONArray("rows").getJSONObject(0);
            JSONObject element = row.getJSONArray("elements").getJSONObject(0);
            status = element.getString("status");
            if(status.equals("ZERO_RESULTS")){
                this.image = null;
                distance = 0;
                hasError = true;
                errorMessage = context.getString(R.string.no_result_found);
            } else if(status.equals("OK")) {
                distance = element.getJSONObject("distance").getInt("value");
                this.image = image;
            } else {
                throw new RuntimeException();
            }
        }
    }

    private boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            conn.getResponseCode();
            is = conn.getInputStream();

            return convertStreamToString(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
