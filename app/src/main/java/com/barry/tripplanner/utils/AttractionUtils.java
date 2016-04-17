package com.barry.tripplanner.utils;

import com.barry.tripplanner.R;
import com.google.android.gms.location.places.Place;

public class AttractionUtils {
    public static int getAttractionTypeIconRes(int type[]) {
        for (int i = 0; i < type.length; i++) {
            if (Place.TYPE_RESTAURANT == type[i] || Place.TYPE_FOOD == type[i]) {
                return R.mipmap.ic_restaurant_black_36dp;
            }

            if (Place.TYPE_LODGING == type[i]) {
                return R.mipmap.ic_hotel_black_36dp;
            }
        }

        return R.mipmap.ic_photo_camera_black_36dp;
    }

    public static int getAttractionTypeIconRes(int type) {
        if (Place.TYPE_RESTAURANT == type || Place.TYPE_FOOD == type) {
            return R.mipmap.ic_restaurant_black_36dp;
        }

        if (Place.TYPE_LODGING == type) {
            return R.mipmap.ic_hotel_black_36dp;
        }

        return R.mipmap.ic_photo_camera_black_36dp;
    }
}
