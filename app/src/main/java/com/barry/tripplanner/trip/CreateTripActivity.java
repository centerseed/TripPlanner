package com.barry.tripplanner.trip;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.barry.tripplanner.R;
import com.barry.tripplanner.provider.TripProvider;
import com.barry.tripplanner.sync.TripSyncAdapter;
import com.barry.tripplanner.utils.AccountUtils;
import com.barry.tripplanner.utils.ConfigUtils;
import com.barry.tripplanner.utils.TripUtils;
import com.barry.tripplanner.utils.URLBuilder;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateTripActivity extends AppCompatActivity implements ThumbAdapter.ThumbCallback, TripUtils.TripListener {

    protected FrameLayout mChoosePhotoLayout;
    protected TextView mDestination;
    protected TextView mName;

    ArrayList<String> mPhotos = new ArrayList<>();
    ThumbAdapter mAdapter;
    RecyclerView mRecyclerView;
    ProgressBar mPbLoading;

    protected EditText mStartTime;
    protected EditText mEndTime;

    protected int mYear, mMonth, mDay, mHour, mMinute;
    protected TripContent mTripContent = new TripContent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_trip);

        mName = (TextView) findViewById(R.id.name);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);
        mPbLoading = (ProgressBar) findViewById(R.id.progressBar);
        mChoosePhotoLayout = (FrameLayout) findViewById(R.id.choosePhotoLayout);
        mDestination = (EditText) findViewById(R.id.destination);
        mDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mDestination.getText().length() > 0)
                    new getPhotoListTask(mDestination.getText().toString()).execute();
            }
        });

        mStartTime = (EditText) findViewById(R.id.startTime);
        mStartTime.setOnClickListener(new DatePickListener());
        mEndTime = (EditText) findViewById(R.id.endTime);
        mEndTime.setOnClickListener(new DatePickListener());

        mChoosePhotoLayout.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_done) {
            mTripContent.getContentValues().put(TripProvider.FIELD_SYNC, TripProvider.SYNC_CREATE_TRIP);
            mTripContent.getContentValues().put(TripProvider.FIELD_ID, mName.getText().hashCode());
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_NAME, mName.getText().toString());
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_DESTINATION, mDestination.getText().toString());
            mTripContent.getContentValues().put(TripProvider.FIELD_SORT_ID, getLargestTripSort() + 1);
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_START_DAY, mStartTime.getText().toString());
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_END_DAY, mEndTime.getText().toString());
            if (mTripContent.getContentValues().getAsString(TripProvider.FIELD_TRIP_NAME).length() == 0) {
                Toast.makeText(this, R.string.error_no_trip_name, Toast.LENGTH_LONG).show();
                return true;
            }

            TripUtils.addTrip(this, mTripContent, this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onThumbSelect(String url, boolean isCheck) {
        mAdapter.notifyDataSetChanged();
        if (isCheck)
            mTripContent.getContentValues().put(TripProvider.FIELD_TRIP_PHOTO, url);
        else
            mTripContent.getContentValues().remove(TripProvider.FIELD_TRIP_PHOTO);
    }

    @Override
    public void onTripEditDone(int tripId, String tripName) {
        Context context = CreateTripActivity.this;
        if (ConfigUtils.getLocalUsageOnly(context)) {
            Toast.makeText(this, R.string.result_create_trip_success, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // TODO: create trip
            Account account = AccountUtils.getCurrentAccount(context);
            String userID = AccountManager.get(context).getPassword(account);
            Bundle args = new Bundle();
            args.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            args.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            args.putString(TripSyncAdapter.ARG_USER_ID, userID);
            args.putString(TripSyncAdapter.ACTION_SYNC, TripProvider.SYNC_ALL_TRIP);
            context.getContentResolver().requestSync(AccountUtils.getCurrentAccount(context), context.getString(R.string.auth_provider_trip), args);
            finish();
        }
    }

    class getPhotoListTask extends AsyncTask<Void, Void, Void> {
        String mKeyword;

        public getPhotoListTask(String keyword) {
            mKeyword = keyword;
        }

        @Override
        protected void onPreExecute() {
            mChoosePhotoLayout.setVisibility(View.VISIBLE);
            mPbLoading.setVisibility(View.VISIBLE);
            mPhotos.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url = new URLBuilder(getApplicationContext()).host("https://www.google.com.tw").path("search")
                    .query("q", mKeyword, "tbs", "isz:l,itp:photo", "tbm", "isch").build().toString();
            try {
                Connection.Response response = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36").timeout(3000).execute();
                Document doc = response.parse();
                String data = doc.toString();
                int startPoint = 0;

                while (startPoint >= 0 && data.indexOf("\"ou\":\"http", startPoint) > 0) {
                    startPoint = data.indexOf("\"ou\":\"http", startPoint);
                    int endPoint = data.indexOf("\"ow\"", startPoint);

                    String photoUrl = data.substring(startPoint + 6, endPoint - 2);
                    if (!photoUrl.contains("jpg") || photoUrl.contains("%")) {
                        startPoint = endPoint;
                        continue;
                    }
                    mPhotos.add(photoUrl);
                    Log.d(CreateTripActivity.class.getName(), photoUrl);

                    startPoint = endPoint;
                }
               /* Elements notice = doc.select("[\"ou\"]");
                int count = 0;
                for (Element element : notice) {
                    String eleString = element.toString();
                    eleString = eleString.replace("<a href=\"/imgres?imgurl=", "");
                    int end = eleString.indexOf(".jpg") + 4;
                    if (end == -1) continue;
                    if (count > 10) return null;

                    String photoUrl = eleString.substring(0, end);
                    if (!photoUrl.contains("jpg") || photoUrl.contains("%")) continue;
                    mPhotos.add(photoUrl);
                    Log.d(CreateTripActivity.class.getName(), photoUrl);
                    count++;
                } */
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mAdapter = new ThumbAdapter(getApplicationContext(), mPhotos, CreateTripActivity.this);
            mRecyclerView.setAdapter(mAdapter);
            mPbLoading.setVisibility(View.GONE);
        }
    }

    public class DatePickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            final EditText editText = (EditText) view;
            String dates[] = editText.getText().toString().split("-");
            final Calendar c = Calendar.getInstance();

            if (dates.length != 3) {
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH) + 1;
                mDay = c.get(Calendar.DATE);
            } else {
                mYear = Integer.valueOf(dates[0]);
                mMonth = Integer.valueOf(dates[1]);
                mDay = Integer.valueOf(dates[2]);
            }

            DatePickerDialog dpd = new DatePickerDialog(CreateTripActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // 完成選擇，顯示日期
                            editText.setText(year + "-" + (monthOfYear + 1) + "-"
                                    + dayOfMonth);

                        }
                    }, mYear, --mMonth, mDay);
            dpd.show();
        }
    }

    private int getLargestTripSort() {
        Uri uri = TripProvider.getProviderUri(getString(R.string.auth_provider_trip), TripProvider.TABLE_TRIP);
        Cursor c = getContentResolver().query(uri, null, TripProvider.FIELD_ID + ">?", new String[]{"0"}, TripProvider.FIELD_SORT_ID + " DESC");
        if (c != null && c.moveToFirst()) {
            int sort = c.getInt(c.getColumnIndex(TripProvider.FIELD_SORT_ID));
            c.close();
            return sort;
        }
        return 0;
    }
}
