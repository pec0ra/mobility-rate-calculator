/*
 *     Copyright (C) 2017 Basile Maret
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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class ItineraryService extends IntentService {
    private static final String ACTION_CALCULATE_DISTANCE = "ch.pec0ra.mobilityratecalculator.action.CALCULATE_DISTANCE";

    private static final String EXTRA_ORIGIN = "ch.pec0ra.mobilityratecalculator.extra.ORIGIN";
    private static final String EXTRA_DESTINATION = "ch.pec0ra.mobilityratecalculator.extra.DESTINATION";

    public ItineraryService() {
        super("ItineraryService");
    }

    public static void startActionCalculateDistance(Context context, LatLng origin, LatLng destination) {
        Intent intent = new Intent(context, ItineraryService.class);
        intent.setAction(ACTION_CALCULATE_DISTANCE);
        intent.putExtra(EXTRA_ORIGIN, new double[]{origin.latitude, origin.longitude});
        intent.putExtra(EXTRA_DESTINATION, new double[]{destination.latitude, destination.longitude});
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CALCULATE_DISTANCE.equals(action)) {
                double[] array = intent.getDoubleArrayExtra(EXTRA_ORIGIN);
                final com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(array[0], array[1]);
                array = intent.getDoubleArrayExtra(EXTRA_DESTINATION);
                final com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(array[0], array[1]);
                handleActionCalculateDistance(origin, destination);
            }
        }
    }

    private void handleActionCalculateDistance(com.google.maps.model.LatLng origin, com.google.maps.model.LatLng destination) {

        try {
            DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING)
                    .origin(origin)
                    .destination(destination)
                    .await();
            EventBus.getDefault().post(new DirectionsResultEvent(result));
        } catch (ApiException e) {
            EventBus.getDefault().post(new DirectionsResultEvent(null));
            e.printStackTrace();
        } catch (InterruptedException e) {
            EventBus.getDefault().post(new DirectionsResultEvent(null));
            e.printStackTrace();
        } catch (IOException e) {
            EventBus.getDefault().post(new DirectionsResultEvent(null));
            e.printStackTrace();
        }
    }

    private GeoApiContext getGeoContext() {
        return new GeoApiContext.Builder()
                .queryRateLimit(3)
                .apiKey(getString(R.string.google_maps_key))
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    class DirectionsResultEvent {
        final DirectionsResult directionsResult;

        DirectionsResultEvent(DirectionsResult directionsResult) {
            this.directionsResult = directionsResult;
        }
    }

    enum Waypoint {
        ORIGIN,
        DESTINATION
    }
}
