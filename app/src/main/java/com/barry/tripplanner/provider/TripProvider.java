package com.barry.tripplanner.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.barry.tripplanner.base.BaseContentProvider;

public class TripProvider extends BaseContentProvider {

    public final static String TABLE_TRIP = "_trip";
    public final static String TABLE_DAY = "_day";
    public final static String TABLE_ATTRACTION = "_attraction";

    public final static String FIELD_TRIP_NAME = "_trip_name";

    @Override
    public boolean onCreate() {
        mDb = new TripDatabase(getContext());
        return false;
    }

    private class TripDatabase extends SQLiteOpenHelper {

        private final static int _DBVersion = 2;
        private final static String _DBName = "trip.db";

        public TripDatabase(Context context) {
            super(context, _DBName, null, _DBVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRIP + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_TRIP_NAME + " TEXT, "
                    + FIELD_SORT_ID + " INTEGER "
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTRACTION);
            onCreate(db);
        }
    }
}
