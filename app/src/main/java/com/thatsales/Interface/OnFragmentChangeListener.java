package com.thatsales.Interface;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Aashutosh @ vinove on 8/3/2015.
 */
public interface OnFragmentChangeListener {
    void onFragmentAdd(Fragment fragment, String TAG);
    void onFragmentReplace(Fragment fragment, String TAG);
    void onFragmentReplace(Fragment fragment);
    void onFragmentRemove(Fragment fragment, String TAG);
    void clearFragmentStack();
    void onFragmentReplaceWithArgument(Fragment fragment, String TAG, Bundle arguments);
}
