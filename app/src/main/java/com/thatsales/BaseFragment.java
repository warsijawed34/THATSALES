package com.thatsales;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.thatsales.Utils.GPSTracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by vinove on 20/7/16.
 */
public class BaseFragment extends Fragment {
    public GPSTracker gpsTracker;

    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTracker = new GPSTracker(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
