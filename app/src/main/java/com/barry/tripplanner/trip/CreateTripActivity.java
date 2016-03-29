package com.barry.tripplanner.trip;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.barry.tripplanner.R;
import com.barry.tripplanner.base.ToolbarActivity;
import com.barry.tripplanner.utils.URLBuilder;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CreateTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_trip);

        new getPhotoListTask("北海道").execute();
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
        return super.onOptionsItemSelected(item);
    }

    class getPhotoListTask extends AsyncTask<Void, Void, Void> {
        String mKeyword;

        public getPhotoListTask(String keyword) {
            mKeyword = keyword;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            String url = new URLBuilder(getApplicationContext()).host("https://www.google.com.tw").path("search")
                    .query("q", mKeyword, "tbs", "isz:l,itp:photo", "tbm", "isch").build().toString();
            try {
                Connection.Response response = Jsoup.connect(url).timeout(3000).execute();
                Document doc = response.parse();
                Elements notice = doc.getElementsContainingText("[href]");
                for (Element element : notice) {
                    Log.d(CreateTripActivity.class.getName(), element.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }
}
