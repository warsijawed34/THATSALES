package com.thatsales.Listener;

import android.support.v4.app.Fragment;

/**
 * Created by vinove on 8/6/16.
 */
public interface OnFragmentChangeListener {

    public void onFragmentChange(Fragment fragmentToReplace, String tag);

    public void addFragment(Fragment fragmentToReplace, String tag);
}
