package com.barry.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.barry.tripplanner.account.AuthenticatorActivity;
import com.barry.tripplanner.trip.TripListFragment;
import com.barry.tripplanner.utils.AccountUtils;
import com.barry.tripplanner.utils.ConfigUtils;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView mIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mIcon = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccountUtils.getCurrentAccount(MainActivity.this) == null) {
                    Intent intent = new Intent(MainActivity.this, AuthenticatorActivity.class);
                    startActivity(intent);
                }
            }
        });

        replaceFragment(new TripListFragment());
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ConfigUtils.getLocalUsageOnly(this)) return;

        if (AccountUtils.getCurrentAccount(this) == null) {
            Intent intent = new Intent(this, AuthenticatorActivity.class);
            startActivity(intent);
        }

        if (AccountUtils.getAccountPictureURL(this).length() > 0)
            Picasso.with(this).load(AccountUtils.getAccountPictureURL(this)).into(mIcon);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment f = null;
        if (id == R.id.nav_my_trip) {
            f = new TripListFragment();
        }
        replaceFragment(f);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment f) {
        if (f == null) return;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f, null).commit();
    }
}
