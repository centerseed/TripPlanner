package com.barry.tripplanner.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
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

    public static void editAttractionName(Context context, int id, String name) {
        ContentValues values = new ContentValues();
        values.put(TripProvider.FIELD_ATTRACTION_NAME, name);
        Uri attrUri = TripProvider.getProviderUri(context, TripProvider.TABLE_ATTRACTION);
        context.getContentResolver().update(attrUri, values, TripProvider.FIELD_ID + "=?", new String[]{id + ""});

        context.getContentResolver().notifyChange(attrUri, null);
    }
}
