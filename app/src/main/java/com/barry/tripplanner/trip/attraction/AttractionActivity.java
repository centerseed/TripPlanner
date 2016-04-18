package com.barry.tripplanner.trip.attraction;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.utils.AttractionUtils;
import com.barry.tripplanner.utils.TripUtils;
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
    ImageView mEditName;

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
        getSupportLoaderManager().initLoader(0, null, this);

        mEditName = (ImageView) findViewById(R.id.editName);
        mEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getSupportLoaderManager().restartLoader(0, null, this);
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
        if (mMap == null) return;

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

    private void editName() {
        final View item = LayoutInflater.from(AttractionActivity.this).inflate(R.layout.dialog_simple_input, null);
        final EditText editText = (EditText) item.findViewById(R.id.edittext);
        editText.setText(mName.getText().toString());

        new AlertDialog.Builder(AttractionActivity.this)
                .setTitle(R.string.title_edit_attraction)
                .setView(item)
                .setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AttractionUtils.editAttractionName(getApplicationContext(), getAttrationId(), editText.getText().toString());
                       // TripUtils.updateDaySnippet(getApplicationContext(), AttractionActivity.this.g);
                    }
                })
                .show();
    }
}
