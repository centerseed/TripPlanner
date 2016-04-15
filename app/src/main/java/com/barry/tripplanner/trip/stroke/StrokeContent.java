package com.barry.tripplanner.trip.stroke;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.trip.attraction.AttractionContent;

public class StrokeContent implements Parcelable {
    AttractionContent mAttraction;
    ContentValues mValues;
    Context mContext;
    Uri mUri;

    public StrokeContent(Context context) {
        mValues = new ContentValues();
        mUri = TripProvider.getProviderUri(context.getString(R.string.auth_provider_trip), TripProvider.TABLE_ATTRACTION);
        mContext = context;
    }

    public void withCursor(Cursor cursor) {
        mValues = new ContentValues();
        mValues.put(TripProvider.FIELD_ID, cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)));
        mValues.put(TripProvider.FIELD_SORT_ID, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_SORT_ID)));
        mValues.put(TripProvider.FIELD_STROKE_ATTRACTION_ID, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_STROKE_ATTRACTION_ID)));
        mValues.put(TripProvider.FIELD_STROKE_BELONG_DAY, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_STROKE_BELONG_DAY)));
        mValues.put(TripProvider.FIELD_STROKE_BELONG_TRIP, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_STROKE_BELONG_TRIP)));
        mValues.put(TripProvider.FIELD_STROKE_TIME, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_STROKE_TIME)));

        Cursor cursorAttr = mContext.getContentResolver().query(mUri, null, TripProvider.FIELD_ID + "=?", new String[]{getAttractionID() + ""}, null);
        if (cursorAttr != null && cursorAttr.moveToFirst()) {
            mAttraction = new AttractionContent();
            mAttraction.withCursor(cursorAttr);
            cursorAttr.close();
        }
    }

    public ContentValues getContentValues() {
        return mValues;
    }

    public int getAttractionID() {
        return mValues.getAsInteger(TripProvider.FIELD_STROKE_ATTRACTION_ID);
    }

    public int getBelongDay() {
        return mValues.getAsInteger(TripProvider.FIELD_STROKE_BELONG_DAY);
    }

    public int getTime() {
        return mValues.getAsInteger(TripProvider.FIELD_STROKE_TIME);
    }

    public AttractionContent getAtraction() {
        return mAttraction;
    }

    /**
     *  Default method
     */
    public StrokeContent(Parcel in) {
        mValues = in.readParcelable(ContentValues.class.getClassLoader());
    }

    public static final Creator<StrokeContent> CREATOR = new Creator<StrokeContent>() {
        @Override
        public StrokeContent createFromParcel(Parcel in) {
            return new StrokeContent(in);
        }

        @Override
        public StrokeContent[] newArray(int size) {
            return new StrokeContent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mValues, i);
    }
}
