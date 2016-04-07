package com.barry.tripplanner.trip.contentvalues;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.barry.tripplanner.provider.TripProvider;

public class StrokeContent implements Parcelable {
    ContentValues mValues;

    public StrokeContent() {
        mValues = new ContentValues();
    }

    public void withCursor(Cursor cursor) {
        mValues = new ContentValues();
        mValues.put(TripProvider.FIELD_ID, cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)));
        mValues.put(TripProvider.FIELD_TRIP_PHOTO, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_PHOTO)));
        mValues.put(TripProvider.FIELD_TRIP_NAME, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_NAME)));
        mValues.put(TripProvider.FIELD_TRIP_DESTINATION, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_DESTINATION)));
        mValues.put(TripProvider.FIELD_TRIP_START_DAY, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_START_DAY)));
        mValues.put(TripProvider.FIELD_TRIP_END_DAY, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_END_DAY)));
    }

    public ContentValues getContentValues() {
        return mValues;
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