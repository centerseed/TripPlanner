package com.barry.tripplanner.map;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.attraction.AttractionContent;
import com.barry.tripplanner.utils.TripUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PlaceSelectionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String ARG_TRIP_ID = "trip_id";
    public static final String ARG_TRIP_DESTINATION = "trip_destination";
    private GoogleMap mMap;
    Button mAttraction;
    AttractionContent mAttractionContent = new AttractionContent();
    RadioGroup mGroup;
    PlaceAutocompleteFragment mAutocompleteFragment;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGroup = (RadioGroup) findViewById(R.id.attractionGroup);

        mAttraction = (Button) findViewById(R.id.addAttraction);
        mAttraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentDays = 0;
                Uri dayUri = TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_DAY);
                Cursor c = getContentResolver().query(dayUri, null, TripProvider.FIELD_DAY_BELONG_TRIP + "=?", new String[]{getTripId() + ""}, null);
                if (c != null && c.moveToFirst()){
                    currentDays = c.getCount();
                    c.close();
                }

                final CharSequence[] items = new CharSequence[currentDays + 1];
                for (int i = 1; i <= currentDays; i++){
                    String day = "第" + i + "天";
                    items[i] = day;
                }
                items[0] = "先不選擇日期";

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        addAttractionInTrip(item);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        mAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        mAutocompleteFragment.setOnPlaceSelectedListener(this);
        mAutocompleteFragment.setHint("輸入景點 例:晴空塔");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                /* TODO: get place id
                Places.GeoDataApi.getPlaceById(mGoogleApiClient, "")
                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                    final Place myPlace = places.get(0);
                                    Log.i("MapsActivity", "Place found: " + myPlace.getName());
                                } else {
                                    Log.e("MapsActivity", "Place not found");
                                }
                                places.release();
                            }
                        });
                        */
            }
        });
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.d("Place detail", " -> " + place.toString());

        mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place.getLatLng())
                        .zoom(12)
                        .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ID, place.getId().hashCode());
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_NAME, place.getName().toString());
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_LAT, place.getLatLng().latitude);
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_LNG, place.getLatLng().longitude);
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_TYPE, place.getPlaceTypes().get(0));
    }

    @Override
    public void onError(Status status) {

    }

    private int getTripId() {
        return getIntent().getIntExtra(ARG_TRIP_ID, 0);
    }
    private String getTripDestination() {
        return getIntent().getStringExtra(ARG_TRIP_DESTINATION);
    }

    private void addAttractionInTrip(int day) {
        if (day > 0) {
            TripUtils.addStrokeWithAttraction(this, getTripId(), day - 1, mAttractionContent);
        } else {
            TripUtils.addAttraction(this, getTripId(), mAttractionContent);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class getPlaceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            return null;
        }
    }
}
