package com.barry.tripplanner.trip.attraction;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.provider.TripProvider;

import java.util.HashMap;

public class AttractionChooseAdapter extends AbstractRecyclerCursorAdapter<Cursor> {
    HashMap<Integer, Integer> mSelectedHash;

    public AttractionChooseAdapter(Context context, Cursor c) {
        super(context, c);
        mSelectedHash = new HashMap<>();
    }

    public HashMap<Integer, Integer> getSelectedHashe() {
        return mSelectedHash;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        AttrChooseViewHolder vh = (AttrChooseViewHolder) viewHolder;
        vh.mName.setText(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_NAME)));
        vh.setId(cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_ID)));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.card_select_item, parent, false);
        return new AttrChooseViewHolder(v);
    }

    class AttrChooseViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        CheckBox mCheckBox;
        int mAttractionId;

        public void setId(int id) { mAttractionId = id;}

        public AttrChooseViewHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.name);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        mSelectedHash.put(getAdapterPosition(), mAttractionId);
                    } else {
                        mSelectedHash.remove(getAdapterPosition());
                    }
                }
            });
        }
    }
}
