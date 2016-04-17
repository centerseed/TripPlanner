package com.barry.tripplanner.trip.day;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.ToolbarActivity;
import com.barry.tripplanner.trip.stroke.StrokeListFragment;

public class DayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StrokeListFragment fragment = new StrokeListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(StrokeListFragment.ARG_TRIP_ID, getIntent().getIntExtra(StrokeListFragment.ARG_TRIP_ID, 0));
        bundle.putInt(StrokeListFragment.ARG_DAY, getIntent().getIntExtra(StrokeListFragment.ARG_DAY, 0));
        fragment.setArguments(bundle);

        getSupportActionBar().setTitle(String.format(getResources().getString(R.string.title_day_toolbar), getIntent().getIntExtra(StrokeListFragment.ARG_DAY, 0) + 1));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, null).commit();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
