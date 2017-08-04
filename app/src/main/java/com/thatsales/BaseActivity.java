package com.thatsales;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thatsales.Utils.GPSTracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class BaseActivity extends AppCompatActivity {

    //last change in 19/10/2016

    public SimpleDateFormat dateFormatter;
    public Calendar newCalendar;
    public GPSTracker gpsTracker;

    public BaseActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTracker = new GPSTracker(this);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}
