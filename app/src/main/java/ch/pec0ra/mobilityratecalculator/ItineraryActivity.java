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

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static ch.pec0ra.mobilityratecalculator.AnimationUtils.ANIMATION_COLOR;
import static ch.pec0ra.mobilityratecalculator.AnimationUtils.CENTER_X_EXTRA;
import static ch.pec0ra.mobilityratecalculator.AnimationUtils.CENTER_Y_EXTRA;
import static ch.pec0ra.mobilityratecalculator.ItineraryService.Waypoint.DESTINATION;
import static ch.pec0ra.mobilityratecalculator.ItineraryService.Waypoint.ORIGIN;

public class ItineraryActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = ItineraryActivity.class.getSimpleName();

    public static final String DISTANCE_EXTRA = "DISTANCE_EXTRA";
    private static final int MAP_ITINERARY_MARGIN = 100;
    private static final float MAP_DEFAULT_ZOOM = 11f;
    private static final int REQUEST_CODE_ORIGIN = 500;
    private static final int REQUEST_CODE_DEST = 600;

    private GoogleMap mMap;
    private MapView mMapView;
    private Marker markerA;
    private Marker markerB;
    private EditText fromTV;
    private EditText toTV;
    private CheckBox twoWay;

    private final LatLng switzerlandNE = new LatLng(47.87, 10.65);
    private final LatLng switzerlandSW = new LatLng(45.9, 5.88);

    private Geocoder geocoder;
    private ProgressDialog dialog;
    private Polyline lines;
    private long distanceKM = 0;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        geocoder = new Geocoder(this, Locale.getDefault());

        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();
        mMapView.getMapAsync(this);

        fromTV = findViewById(R.id.itinerary_from);
        fromTV.setOnClickListener(view -> openPlaceAutocompleteActivity(REQUEST_CODE_ORIGIN, fromTV.getText().toString()));
        toTV = findViewById(R.id.itinerary_to);
        toTV.setOnClickListener(view -> openPlaceAutocompleteActivity(REQUEST_CODE_DEST, toTV.getText().toString()));

        twoWay = findViewById(R.id.two_way_checkbox);
        twoWay.setOnClickListener(view -> {
            fab.hide();
            View resultCard = findViewById(R.id.distance_result_card);
            resultCard.setVisibility(View.INVISIBLE);

            if (lines != null) {
                lines.remove();
                lines = null;
            }
        });

        findViewById(R.id.submit_button).setOnClickListener(view -> startCalculateJob());

        View mainContent = findViewById(R.id.main_layout);
        int centerX = getIntent().getIntExtra(CENTER_X_EXTRA, -1);
        int centerY = getIntent().getIntExtra(CENTER_Y_EXTRA, -1);
        int color = getIntent().getIntExtra(ANIMATION_COLOR, -1);
        if (centerX != -1 && centerY != -1 && color != -1) {
            AnimationUtils.RevealAnimationSetting settings = new AnimationUtils.RevealAnimationSetting(centerX, centerY, color, getResources().getColor(android.R.color.background_light));
            AnimationUtils.registerCircularRevealAnimation(getBaseContext(), mainContent, settings);
        }

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(DISTANCE_EXTRA, distanceKM);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void openPlaceAutocompleteActivity(int request_code, String address) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .zzh(address)
                    .setBoundsBias(new LatLngBounds(switzerlandSW, switzerlandNE))
                    .build(this);
            startActivityForResult(intent, request_code);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(), 0).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Snackbar.make(findViewById(R.id.main_layout), message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ORIGIN) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                fromTV.setText(place.getAddress());
                fromTV.setError(null);
                clearMarker(ORIGIN);
                markerA = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(getString(R.string.origin)));
                centerCamera();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                if (status.getStatusMessage() != null) {
                    Snackbar.make(findViewById(R.id.main_layout), status.getStatusMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        }
        if (requestCode == REQUEST_CODE_DEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                toTV.setText(place.getAddress());
                toTV.setError(null);
                clearMarker(DESTINATION);
                markerB = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(getString(R.string.destination)));
                centerCamera();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                if (status.getStatusMessage() != null) {
                    Snackbar.make(findViewById(R.id.main_layout), status.getStatusMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private void centerCamera() {
        if (markerA == null && markerB == null) {
            // Move the camera to switzerland
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(switzerlandSW, switzerlandNE), 10));
        } else if (markerA == null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerB.getPosition(), MAP_DEFAULT_ZOOM));
        } else if (markerB == null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerA.getPosition(), MAP_DEFAULT_ZOOM));
        } else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(markerA.getPosition());
            builder.include(markerB.getPosition());
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_ITINERARY_MARGIN));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void startCalculateJob() {
        if (markerA == null || markerB == null) {
            if (markerA == null) {
                fromTV.setError(getString(R.string.address_not_found));
            }
            if (markerB == null) {
                toTV.setError(getString(R.string.address_not_found));
            }
            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout), R.string.origin_and_destination_cannot_be_emtpy, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            snackbar.show();
            return;
        }

        if (lines != null) {
            lines.remove();
            lines = null;
        }
        dialog = ProgressDialog.show(ItineraryActivity.this, "", getString(R.string.calculating), true, false);

        ItineraryService.startActionCalculateDistance(this, markerA.getPosition(), markerB.getPosition());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ItineraryService.DirectionsResultEvent event) {
        dialog.dismiss();
        if (event.directionsResult == null) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout), R.string.error, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            snackbar.show();
            return;
        }

        if (event.directionsResult.routes.length > 0) {
            AnimationUtils.circleReveal(findViewById(R.id.distance_result_card));
            TextView distanceResult = findViewById(R.id.distance_result_textview);

            if (twoWay.isChecked()) {
                distanceKM = event.directionsResult.routes[0].legs[0].distance.inMeters / 500;
            } else {
                distanceKM = event.directionsResult.routes[0].legs[0].distance.inMeters / 1000;
            }
            distanceResult.setText(getResources().getString(R.string.distance_km, distanceKM));

            List<LatLng> decodedPath = PolyUtil.decode(event.directionsResult.routes[0].overviewPolyline.getEncodedPath());
            if (lines != null) {
                lines.remove();
            }
            PolylineOptions polylineOptions = new PolylineOptions().addAll(decodedPath)
                    .color(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null))
                    .width(14F);
            lines = mMap.addPolyline(polylineOptions);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng pos : lines.getPoints()) {
                builder.include(pos);
            }
            builder.include(markerA.getPosition());
            builder.include(markerB.getPosition());
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_ITINERARY_MARGIN));

            fab.show();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout), R.string.no_route_found, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
            snackbar.show();
        }
    }

    private void clearMarker(ItineraryService.Waypoint waypoint) {
        fab.hide();
        View resultCard = findViewById(R.id.distance_result_card);
        resultCard.setVisibility(View.INVISIBLE);

        if (lines != null) {
            lines.remove();
            lines = null;
        }
        switch (waypoint) {
            case ORIGIN:
                if (markerA != null) {
                    markerA.remove();
                    markerA = null;
                }
                break;
            case DESTINATION:
                if (markerB != null) {
                    markerB.remove();
                    markerB = null;
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(this::setupMap);
    }

    private void setupMap() {
        mMap.setOnMapLongClickListener(new MapLongClickListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private class MapLongClickListener implements GoogleMap.OnMapLongClickListener {

        @Override
        public void onMapLongClick(LatLng latLng) {

            CharSequence waypoints[] = new CharSequence[]{getString(R.string.set_as_origin), getString(R.string.set_as_destination)};

            String address = getAddress(latLng);

            AlertDialog.Builder builder = new AlertDialog.Builder(ItineraryActivity.this);
            builder.setTitle(address);
            builder.setItems(waypoints, (dialog, which) -> {
                switch (which) {
                    case 1:
                        toTV.setText(address);
                        clearMarker(DESTINATION);
                        markerB = mMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.destination)));
                        break;
                    case 0:
                    default:
                        fromTV.setText(address);
                        clearMarker(ORIGIN);
                        markerA = mMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.origin)));
                        break;
                }
            });
            builder.show();

        }

        private String getAddress(LatLng latLng) {

            List<Address> addresses = new ArrayList<>();
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addresses.get(0);
            return addressToString(address);
        }
    }

    private String addressToString(Address address) {
        if (address != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(address.getAddressLine(i));
            }
            return sb.toString();
        }
        return "";
    }
}
