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
    public final static String FIELD_TRIP_PHOTO = "_trip_photo";
    public final static String FIELD_TRIP_START_DAY = "_trip_start_day";
    public final static String FIELD_TRIP_END_DAY = "_trip_end_day";
    public final static String FIELD_TRIP_DAY_IDS = "_trip_day_ids";
    public final static String FIELD_TRIP_DAY_SORTS = "_trip_day_sorts";

    public final static String FIELD_DAY_BELONG_TRIP = "_day_belong_trip";
    public final static String FIELD_DAY_HIGHLIGHT = "_day_highlight";
    public final static String FIELD_DAY_ATTRACTION_IDS = "_day_attraction_ids";
    public final static String FIELD_DAY_ATTRACTION_SORTs = "_day_attraction_sorts";

    public final static String FIELD_ATTRACTION_NAME = "_attraction_name";
    public final static String FIELD_ATTRACTION_TYPE = "_attraction_type";

    @Override
    public boolean onCreate() {
        mDb = new TripDatabase(getContext());
        return false;
    }

    private class TripDatabase extends SQLiteOpenHelper {

        private final static int _DBVersion = 4;
        private final static String _DBName = "trip.db";

        public TripDatabase(Context context) {
            super(context, _DBName, null, _DBVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRIP + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_TRIP_NAME + " TEXT, "
                    + FIELD_SORT_ID + " INTEGER, "
                    + FIELD_TRIP_DAY_IDS + " TEXT, "
                    + FIELD_TRIP_DAY_SORTS + " TEXT "
                    + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DAY + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_DAY_BELONG_TRIP + " INTEGER, "
                    + FIELD_SORT_ID + " INTEGER, "
                    + FIELD_DAY_HIGHLIGHT + " TEXT, "
                    + FIELD_DAY_ATTRACTION_IDS + " TEXT, "
                    + FIELD_DAY_ATTRACTION_SORTs + " TEXT "
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
