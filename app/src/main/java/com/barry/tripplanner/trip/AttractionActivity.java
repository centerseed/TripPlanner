package com.barry.tripplanner.trip;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AttractionActivity extends AppCompatActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ATTRACTION_ID = "attraction_id";
    EditText mName;
    Uri mUri;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUri = TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_ATTRACTION);
        setContentView(R.layout.activity_attraction);

        mName = (EditText) findViewById(R.id.name);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        getSupportLoaderManager().initLoader(0, null, this);
        mMap = googleMap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getAttrationId() {
        return getIntent().getIntExtra(ARG_ATTRACTION_ID, 0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = new CursorLoader(this);
        cl.setUri(mUri);
        cl.setSelection(TripProvider.FIELD_ID + "=?");
        cl.setSelectionArgs(new String[]{getAttrationId() + ""});
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            mName.setText(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_NAME)));
            getSupportActionBar().setTitle(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_NAME)));

            LatLng latLng = new LatLng(cursor.getDouble(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_LAT)), cursor.getDouble(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_LNG)));
            mMap.addMarker(new MarkerOptions().position(latLng).title(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_NAME))));

            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(12)
                            .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 10, null);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
