package com.barry.tripplanner.map;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.TripActivity;
import com.barry.tripplanner.trip.contentvalues.AttractionContent;
import com.barry.tripplanner.utils.TripUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PlaceSelectionListener {

    public static final String ARG_TRIP_ID = "trip_id";
    public static final String ARG_TRIP_DESTINATION = "trip_destination";
    private GoogleMap mMap;
    Button mAttraction;
    AttractionContent mAttractionContent = new AttractionContent();
    RadioGroup mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("輸入景點 例:晴空塔");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_NAME, place.getName().toString());
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_LAT, place.getLatLng().latitude);
        mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_LNG, place.getLatLng().longitude);
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
        if (mGroup.getCheckedRadioButtonId() == R.id.typeLandscape)
            mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_TYPE, TripProvider.TYPE_ATTARCTION_LANDSCAPE);
        else if (mGroup.getCheckedRadioButtonId() == R.id.typeRestaurant) {
            mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_TYPE, TripProvider.TYPE_ATTARCTION_RESTAURANT);
        } else {
            mAttractionContent.getContentValues().put(TripProvider.FIELD_ATTRACTION_TYPE, TripProvider.TYPE_ATTARCTION_HOTEL);
        }

        if (day == 0)
        TripUtils.addAttraction(this, getTripId(), mAttractionContent);
    }
}
