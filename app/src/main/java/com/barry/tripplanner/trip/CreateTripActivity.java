package com.barry.tripplanner.trip;

import android.app.DatePickerDialog;
import android.content.ContentValues;
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
import com.barry.tripplanner.utils.TimeUtils;
import com.barry.tripplanner.utils.URLBuilder;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateTripActivity extends AppCompatActivity implements ThumbAdapter.ThumbCallback {

    protected FrameLayout mChoosePhotoLayout;
    protected TextView mDestination;

    ArrayList<String> mPhotos = new ArrayList<>();
    ThumbAdapter mAdapter;
    RecyclerView mRecyclerView;
    ProgressBar mPbLoading;

    EditText mStartTime;
    EditText mEndTime;

    private int mYear, mMonth, mDay, mHour, mMinute;
    ContentValues mTripValue = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_trip);

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
            if (!mTripValue.containsKey(TripProvider.FIELD_TRIP_NAME)) {
                Toast.makeText(this, R.string.error_no_trip_name, Toast.LENGTH_LONG).show();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onThumbSelect(String url, boolean isCheck) {
        mAdapter.notifyDataSetChanged();
        if (isCheck)
            mTripValue.put(TripProvider.FIELD_TRIP_PHOTO, url);
        else
            mTripValue.remove(TripProvider.FIELD_TRIP_PHOTO);
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
                Elements notice = doc.select("[href]");
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
                }
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

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(CreateTripActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // 完成選擇，顯示日期
                            editText.setText(year + "-" + (monthOfYear + 1) + "-"
                                    + dayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        }
    }
}
