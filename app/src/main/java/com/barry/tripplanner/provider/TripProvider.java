package com.barry.tripplanner.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.barry.tripplanner.base.BaseContentProvider;

public class TripProvider extends BaseContentProvider {

    public final static String TYPE_ATTARCTION_LANDSCAPE = "_attr_landscape";
    public final static String TYPE_ATTARCTION_RESTAURANT = "_attr_restaurant";
    public final static String TYPE_ATTARCTION_HOTEL = "_attr_hotel";

    public final static String TABLE_TRIP = "_trip";
    public final static String TABLE_DAY = "_day";
    public final static String TABLE_STROKE = "_stroke";
    public final static String TABLE_ATTRACTION = "_attraction";

    public final static String FIELD_TRIP_NAME = "_trip_name";
    public final static String FIELD_TRIP_DESTINATION = "_trip_destination";
    public final static String FIELD_TRIP_PHOTO = "_trip_photo";
    public final static String FIELD_TRIP_START_DAY = "_trip_start_day";
    public final static String FIELD_TRIP_END_DAY = "_trip_end_day";
    public final static String FIELD_ATTRACTION_IDS = "_trip_attraction_ids";

    public final static String FIELD_DAY_BELONG_TRIP = "_day_belong_trip";
    public final static String FIELD_DAY_HIGHLIGHT = "_day_highlight";

    public final static String FIELD_STROKE_BELONG_TRIP = "_stroke_belong_trip";
    public final static String FIELD_STROKE_BELONG_DAY = "_stroke_belong_day";
    public final static String FIELD_STROKE_TIME = "_stroke_time";
    public final static String FIELD_STROKE_ATTRACTION_ID = "_stroke_attraction_ids";

    public final static String FIELD_ATTRACTION_NAME = "_attraction_name";
    public final static String FIELD_ATTRACTION_LAT = "_attraction_lat";
    public final static String FIELD_ATTRACTION_LNG = "_attraction_lng";
    public final static String FIELD_ATTRACTION_SNAPSHOT = "_attraction_snapshot";
    public final static String FIELD_ATTRACTION_RANK = "_attraction_rank";
    public final static String FIELD_ATTRACTION_TYPE = "_attraction_type";

    @Override
    public boolean onCreate() {
        mDb = new TripDatabase(getContext());
        return false;
    }

    private class TripDatabase extends SQLiteOpenHelper {

        private final static int _DBVersion = 8;
        private final static String _DBName = "trip.db";

        public TripDatabase(Context context) {
            super(context, _DBName, null, _DBVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRIP + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_SORT_ID + " INTEGER, "
                    + FIELD_TRIP_NAME + " TEXT, "
                    + FIELD_TRIP_DESTINATION + " TEXT, "
                    + FIELD_TRIP_PHOTO + " TEXT, "
                    + FIELD_TRIP_START_DAY + " TEXT, "
                    + FIELD_TRIP_END_DAY + " TEXT, "
                    + FIELD_ATTRACTION_IDS + " TEXT "
                    + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DAY + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_SORT_ID + " INTEGER, "
                    + FIELD_DAY_BELONG_TRIP + " INTEGER, "
                    + FIELD_DAY_HIGHLIGHT + " TEXT "
                    + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STROKE + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_SORT_ID + " INTEGER, "
                    + FIELD_STROKE_BELONG_TRIP + " INTEGER, "
                    + FIELD_STROKE_BELONG_DAY + " INTEGER, "
                    + FIELD_STROKE_TIME + " TEXT, "
                    + FIELD_STROKE_ATTRACTION_ID + " TEXT "
                    + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ATTRACTION + " ( "
                    + FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_ATTRACTION_NAME + " TEXT, "
                    + FIELD_ATTRACTION_LAT + " FLOAT, "
                    + FIELD_ATTRACTION_LNG + " FLOAT, "
                    + FIELD_ATTRACTION_SNAPSHOT + " TEXT, "
                    + FIELD_ATTRACTION_RANK + " TEXT, "
                    + FIELD_STROKE_TIME + " TEXT, "
                    + FIELD_ATTRACTION_TYPE + " INTEGER "
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STROKE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTRACTION);
            onCreate(db);
        }
    }
}
