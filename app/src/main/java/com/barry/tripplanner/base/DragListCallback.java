package com.barry.tripplanner.base;

import android.database.Cursor;

public interface DragListCallback {
    void onMoveItem(int fromPos, int toPos);
    void onItemClick(Cursor cursor);
}
