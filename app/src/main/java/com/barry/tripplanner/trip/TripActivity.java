package com.barry.tripplanner.trip;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.ToolbarActivity;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.contentvalues.TripContent;
import com.squareup.picasso.Picasso;

public class TripActivity extends ToolbarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_TRIP_ID = "trip_id";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ViewPager mViewPager;
    private TextView mInterval;
    private TextView mDestination;
    ImageView mImageView;
    Uri mUri;
    TripContent mTripContent = new TripContent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        mUri = TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_TRIP);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.tab_collapse_toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mDestination = (TextView) findViewById(R.id.destination);
        mInterval = (TextView) findViewById(R.id.trip_interval);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mImageView = (ImageView) findViewById(R.id.imageView);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private int getTripId() {
        return getIntent().getIntExtra(ARG_TRIP_ID, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, EditTripActivity.class);
            intent.putExtra(EditTripActivity.ARG_TRIP_ID, getTripId());
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_delete) {
            Uri uri = TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_TRIP);
            getContentResolver().delete(uri, TripProvider.FIELD_ID + "=?", new String[] {getTripId() + ""});
            getContentResolver().notifyChange(uri, null);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = new CursorLoader(this);
        cl.setUri(mUri);
        cl.setSelection(TripProvider.FIELD_ID + "=?");
        cl.setSelectionArgs(new String[]{getTripId() + ""});
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            mTripContent.withCursor(cursor);
            mCollapsingToolbarLayout.setTitle(mTripContent.getName());
            mInterval.setText(mTripContent.getInterval());
            mDestination.setText(mTripContent.getDestination());
            Picasso.with(this).load(mTripContent.getPicPhoto()).into(mImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_trip, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Fragment f = new DayListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(DayListFragment.ARG_TRIP_ID, getTripId());
                f.setArguments(bundle);
                return f;
            }

            if (position == 1) {
                Fragment f = new AttractionFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(DayListFragment.ARG_TRIP_ID, getTripId());
                bundle.putString(AttractionFragment.ARG_TRIP_DESTINATION, mTripContent.getDestination());
                f.setArguments(bundle);
                return f;
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_day);
                case 1:
                    return getString(R.string.title_attraction);
                case 2:
                    return getString(R.string.title_memo);
            }
            return null;
        }
    }
}
