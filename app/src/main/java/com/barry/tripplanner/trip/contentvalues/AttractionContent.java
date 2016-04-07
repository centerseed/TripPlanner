package com.barry.tripplanner.trip.contentvalues;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.barry.tripplanner.provider.TripProvider;

public class AttractionContent implements Parcelable {
    ContentValues mValues;

    public AttractionContent() {
        mValues = new ContentValues();
    }

    public void withCursor(Cursor cursor) {
        mValues = new ContentValues();
        mValues.put(TripProvider.FIELD_ID, cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)));
        mValues.put(TripProvider.FIELD_ATTRACTION_NAME, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_NAME)));
        mValues.put(TripProvider.FIELD_ATTRACTION_LAT, cursor.getDouble(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_LAT)));
        mValues.put(TripProvider.FIELD_ATTRACTION_LNG, cursor.getDouble(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_LNG)));
        mValues.put(TripProvider.FIELD_ATTRACTION_SNAPSHOT, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_SNAPSHOT)));
        mValues.put(TripProvider.FIELD_ATTRACTION_RANK, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_RANK)));
        mValues.put(TripProvider.FIELD_ATTRACTION_TYPE, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_TYPE)));
    }

    public ContentValues getContentValues() {
        return mValues;
    }

    /**
     *  Default method
     */
    public AttractionContent(Parcel in) {
        mValues = in.readParcelable(ContentValues.class.getClassLoader());
    }

    public static final Creator<AttractionContent> CREATOR = new Creator<AttractionContent>() {
        @Override
        public AttractionContent createFromParcel(Parcel in) {
            return new AttractionContent(in);
        }

        @Override
        public AttractionContent[] newArray(int size) {
            return new AttractionContent[size];
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
