package com.barry.tripplanner.trip;

import android.os.Bundle;
import android.view.View;

public class EditTripActivity extends CreateTripActivity {
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDestination.setOnClickListener(null);
        mChoosePhotoLayout.setVisibility(View.GONE);
    }
}
