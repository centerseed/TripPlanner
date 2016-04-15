package com.barry.tripplanner.base;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.barry.tripplanner.R;

import java.util.List;

public abstract class BaseContentProvider extends ContentProvider {

    private static final String TAG = "BaseContentProvider";
    public final static String FIELD_ID = "_id";
    public final static String FIELD_SORT_ID = "_sort_id";

    protected static SQLiteOpenHelper mDb;

    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {
        return mDb.getWritableDatabase().delete(getTable(uri), whereClause, whereArgs);
    }

    @Override
    public String getType(Uri uri) {
        return this.getClass().getName();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = mDb.getWritableDatabase().insertWithOnConflict(getTable(uri), null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String orderBy) {
        String table = getTable(uri);
        if (orderBy == null)
            orderBy = FIELD_ID + " ASC";
        Cursor c = mDb.getReadableDatabase().query(table, columns, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /*
    public Cursor queryJoin(Uri uri, String joinTable, String oriID, String joinID, String joinWhere, String[] selectionArgs, String orderBy) {
        String table = getTable(uri);
        if (orderBy == null)
            orderBy = FIELD_ID + " ASC";
        Cursor c = mDb.getReadableDatabase().rawQuery("SELECT * FROM " + table + " a INNER JOIN " + joinTable + " b ON + a." + oriID + "=b." + joinID + " WHERE b." + joinWhere, new String[]{selectionArgs});
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    } */

    @Override
    public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
        return mDb.getWritableDatabase().update(getTable(uri), values, whereClause, whereArgs);
    }

    public static void moveItem(Uri uri, int fromID, int toID, int fromPos, int toPos) {
        String whereClause = FIELD_ID + "=? AND " + FIELD_SORT_ID + "=?";
        String[] whereArgs = new String[]{fromID + "", fromPos + ""};

        ContentValues fromValues = new ContentValues();
        fromValues.put(FIELD_SORT_ID, toPos);
        mDb.getWritableDatabase().update(getTable(uri), fromValues, whereClause, whereArgs);

        whereArgs = new String[]{toID + "", toPos + ""};
        ContentValues toValues = new ContentValues();
        toValues.put(FIELD_SORT_ID, fromPos);

        mDb.getWritableDatabase().update(getTable(uri), toValues, whereClause, whereArgs);
    }

    protected static String getTable(Uri uri) {
        List<String> paths = uri.getPathSegments();
        if (paths != null && paths.size() > 0)
            return paths.get(0);
        return null;
    }

    public static Uri getProviderUri(String authority, String table) {
        Uri.Builder ub = new Uri.Builder()
                .scheme("content")
                .authority(authority)
                .appendPath(table);
        return ub.build();
    }

    public static Uri getProviderUri(Context context, String table) {
        Uri.Builder ub = new Uri.Builder()
                .scheme("content")
                .authority(context.getResources().getString(R.string.auth_provider_trip))
                .appendPath(table);
        return ub.build();
    }
}
