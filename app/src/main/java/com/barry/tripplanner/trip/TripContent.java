package com.barry.tripplanner.trip;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.barry.tripplanner.provider.TripProvider;

import org.json.JSONObject;

public class TripContent implements Parcelable {
    ContentValues mValues;

    public TripContent() {
        mValues = new ContentValues();
    }

    public void withCursor(Cursor cursor) {
        mValues = new ContentValues();
        mValues.put(TripProvider.FIELD_ID, cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)));
        mValues.put(TripProvider.FIELD_TRIP_ID, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_ID)));
        mValues.put(TripProvider.FIELD_SORT_ID, cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_SORT_ID)));
        mValues.put(TripProvider.FIELD_TRIP_PHOTO, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_PHOTO)));
        mValues.put(TripProvider.FIELD_TRIP_NAME, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_NAME)));
        mValues.put(TripProvider.FIELD_TRIP_DESTINATION, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_DESTINATION)));
        mValues.put(TripProvider.FIELD_TRIP_START_DAY, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_START_DAY)));
        mValues.put(TripProvider.FIELD_TRIP_END_DAY, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_TRIP_END_DAY)));
        mValues.put(TripProvider.FIELD_ATTRACTION_IDS, cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_IDS)));
    }

    public int getLocalId() {
        return mValues.getAsInteger(TripProvider.FIELD_ID);
    }

    public String getTripId() {
        return mValues.getAsString(TripProvider.FIELD_TRIP_ID);
    }

    public int getSortId() {
        return mValues.getAsInteger(TripProvider.FIELD_SORT_ID);
    }

    public String getPicPhoto() {
        return mValues.getAsString(TripProvider.FIELD_TRIP_PHOTO);
    }

    public String getDestination() {
        return mValues.getAsString(TripProvider.FIELD_TRIP_DESTINATION);
    }

    public String getName() {
        return mValues.getAsString(TripProvider.FIELD_TRIP_NAME);
    }

    public String getInterval() {
        return mValues.getAsString(TripProvider.FIELD_TRIP_START_DAY) + " ~ "
                + mValues.getAsString(TripProvider.FIELD_TRIP_END_DAY);
    }

    public String getStartDay() {
        return mValues.getAsString(TripProvider.FIELD_TRIP_START_DAY);
    }

    public String getEndDay() {
        return mValues.getAsString(TripProvider.FIELD_TRIP_END_DAY);
    }

    public String getAttractionIDs() {
        return mValues.getAsString(TripProvider.FIELD_ATTRACTION_IDS);
    }

    public TripContent(Parcel in) {
        mValues = in.readParcelable(ContentValues.class.getClassLoader());
    }

    public ContentValues getContentValues() {
        return mValues;
    }

    // TODO: for update to server
    public JSONObject getJSONObj(Parcel in) {
        mValues = in.readParcelable(ContentValues.class.getClassLoader());
        return new JSONObject();
    }

    public static final Creator<TripContent> CREATOR = new Creator<TripContent>() {
        @Override
        public TripContent createFromParcel(Parcel in) {
            return new TripContent(in);
        }

        @Override
        public TripContent[] newArray(int size) {
            return new TripContent[size];
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
