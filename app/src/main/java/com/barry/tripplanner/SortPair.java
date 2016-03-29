package com.barry.tripplanner;

public class SortPair {
    private int mId;
    private int mSortId;

    public SortPair(int id, int sortId) {
        this.mId = id;
        this.mSortId = sortId;
    }

    public void setSortId(int sortId) {
        mSortId = sortId;
    }

    public int getId() {
        return mId;
    }

    public int getSortId() {
        return mSortId;
    }
}
