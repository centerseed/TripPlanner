/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.barry.tripplanner.trip;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.AbstractRecyclerCursorAdapter;
import com.barry.tripplanner.base.DragListCallback;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.utils.DrawableUtils;
import com.barry.tripplanner.utils.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

public class DragDayAdapter
        extends AbstractRecyclerCursorAdapter<DragDayAdapter.MyViewHolder>
        implements DraggableItemAdapter<DragDayAdapter.MyViewHolder> {

    private static final String TAG = "DragDayAdapter";
    static DragListCallback mCallback;

    public DragDayAdapter(Context context, Cursor c, DragListCallback callback) {
        super(context, c);
        mCallback = callback;
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.card_drag_day, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final Cursor cursor) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        int day = cursor.getInt(cursor.getColumnIndex(TripProvider.FIELD_SORT_ID));
        holder.mTextView.setText(++day + "");
        holder.mHighLight.setText(cursor.getString(cursor.getColumnIndex(TripProvider.FIELD_DAY_HIGHLIGHT)));

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
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

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    public class MyViewHolder extends AbstractDraggableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;
        public TextView mHighLight;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = (TextView) v.findViewById(R.id.day);
            mHighLight = (TextView) v.findViewById(R.id.highlight);

            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) mCallback.onItemClick((Cursor) getItem(getAdapterPosition()));
                }
            });
        }
    }
}
