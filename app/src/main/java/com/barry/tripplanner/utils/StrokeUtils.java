package com.barry.tripplanner.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.barry.tripplanner.provider.TripProvider;

public class StrokeUtils {
    public static void updateStrokeTime(Context context, int strokeId, String time) {
        Uri strokeUri = TripProvider.getProviderUri(context, TripProvider.TABLE_STROKE);
        ContentValues values = new ContentValues();
        values.put(TripProvider.FIELD_STROKE_TIME, time);

        context.getContentResolver().update(strokeUri, values, TripProvider.FIELD_ID + "=?", new String[]{strokeId + ""});
        context.getContentResolver().notifyChange(strokeUri, null);
    }
}
