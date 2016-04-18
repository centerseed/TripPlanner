package com.barry.tripplanner.trip.stroke;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.DragListCallback;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.utils.AttractionUtils;
import com.barry.tripplanner.utils.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

public class StrokeAdapter extends AbstractRecyclerCursorAdapter<StrokeAdapter.MyViewHolder>
        implements DraggableItemAdapter<StrokeAdapter.MyViewHolder> {

    private static final String TAG = "StrokeAdapter";
    static StrokeListCallback mCallback;
    Context mContext;

    public interface StrokeListCallback extends DragListCallback {
        void onEditTime(Cursor cursor, String time);

        void onLongClick(Cursor cursor);
    }

    public StrokeAdapter(Context context, Cursor c, StrokeListCallback callback) {
        super(context, c);
        mCallback = callback;
        mContext = context;
        setHasStableIds(true);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        MyViewHolder vh = (MyViewHolder) viewHolder;
        StrokeContent stroke = new StrokeContent(mContext);
        stroke.withCursor(cursor);

        vh.mStroke.setText(stroke.getAtraction().getName());
        vh.mTime.setText(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_STROKE_TIME)));

        if (AttractionUtils.getAttractionTypeIconRes(stroke.getAtraction().getContentValues().getAsInteger(TripProvider.FIELD_ATTRACTION_TYPE)) != 0)
            vh.mType.setImageResource(AttractionUtils.getAttractionTypeIconRes(stroke.getAtraction().getContentValues().getAsInteger(TripProvider.FIELD_ATTRACTION_TYPE)));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.card_drag_stroke, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public boolean onCheckCanStartDrag(StrokeAdapter.MyViewHolder holder, int position, int x, int y) {
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(StrokeAdapter.MyViewHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");
        if (fromPosition == toPosition) {
            return;
        }

        if (mCallback != null) mCallback.onMoveItem(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class MyViewHolder extends AbstractDraggableItemViewHolder {
        public View mContainer;
        public View mDragHandle;
        public TextView mStroke;
        public TextView mTime;
        public ImageView mType;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            LinearLayout timeSection = (LinearLayout) v.findViewById(R.id.timeSection);
            mStroke = (TextView) v.findViewById(R.id.stroke);
            mTime = (TextView) v.findViewById(R.id.time);
            mType = (ImageView) v.findViewById(R.id.type);

            timeSection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null)
                        mCallback.onEditTime((Cursor) getItem(getAdapterPosition()), mTime.getText().toString());
                }
            });

            mContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mCallback != null)
                        mCallback.onLongClick((Cursor) getItem(getAdapterPosition()));
                    return false;
                }
            });

            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
