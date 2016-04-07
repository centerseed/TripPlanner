package com.barry.tripplanner.trip;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.provider.TripProvider;

public class AttractionAdapter extends AbstractRecyclerCursorAdapter<Cursor> {

    AttractionAdapterListener mListener;

    public interface AttractionAdapterListener {
        void onAttractionClick(Cursor cursor);
    }

    public AttractionAdapter(Context context, Cursor c, AttractionAdapterListener listener) {
        super(context, c);
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        AttractionViewHolder vh = (AttractionViewHolder) viewHolder;
        vh.mName.setText(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_ATTRACTION_NAME)));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.card_attraction, parent, false);
        return new AttractionViewHolder(v);
    }

    class AttractionViewHolder extends RecyclerView.ViewHolder {
        TextView mName;

        public AttractionViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) mListener.onAttractionClick((Cursor) getItem(getAdapterPosition()));
                }
            });
        }
    }
}
