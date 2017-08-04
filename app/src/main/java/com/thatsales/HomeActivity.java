package com.thatsales;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.thatsales.Fragment.ClosetFragment;
import com.thatsales.Fragment.FavouriteFragment;
import com.thatsales.Fragment.HomeFragment;
import com.thatsales.Fragment.MapFragment;
import com.thatsales.Fragment.PopularFragment;
import com.thatsales.GcmPushNotisfication.RegistrationIntentService;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnFragmentChangeListener;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.Utils.GPSTracker;

import java.util.List;

/**
 * demo class
 * Created by vinove on 2/5/16.
 */
public class HomeActivity extends FragmentActivity implements View.OnClickListener, OnFragmentChangeListener, FragmentManager.OnBackStackChangedListener {

    // push notifications
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    //push notification

    public TextView homeTab, favTab, popularTab, mapTab, closestTab;
    public ImageView ivBack, ivSigned, ivLogo, ivLogin, ivUserRequest, ivUserProfile;
    public TextView tvTitle;
    public LinearLayout llCatSearch;
    public EditText etItemCat, etShopping;
    FrameLayout frameLayout;
    Context mContext;
    Handler handler;
    Runnable runnable;
    boolean doubleBackToExitPressedOnce = false;
    boolean homePressed = true;
    SharedPreferencesManger sharedPreferencesManger;
    String loginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        mContext = HomeActivity.this;
        getView();
        HomeFragment homeFragment = new HomeFragment();
        onFragmentChange(new HomeFragment(), Constants.HOME_PAGE);
        tokenRecieve();

    }

    private void getView() {

        frameLayout = (FrameLayout) findViewById(R.id.realtabcontent);
        llCatSearch = (LinearLayout) findViewById(R.id.ll_category);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivLogin = (ImageView) findViewById(R.id.iv_login);
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        tvTitle = (TextView) findViewById(R.id.tv_tittle);
        homeTab = (TextView) findViewById(R.id.tab_home);
        favTab = (TextView) findViewById(R.id.tab_favorites);
        popularTab = (TextView) findViewById(R.id.tab_popular);
        closestTab = (TextView) findViewById(R.id.tab_closest);
        mapTab = (TextView) findViewById(R.id.tab_map);
        etItemCat = (EditText) findViewById(R.id.ed_itemcat);
        etShopping = (EditText) findViewById(R.id.ed_shoppingcenter);
        ivUserRequest = (ImageView) findViewById(R.id.iv_user_request);
        ivUserProfile = (ImageView) findViewById(R.id.iv_user_profile);

        homeTab.setOnClickListener(this);
        favTab.setOnClickListener(this);
        popularTab.setOnClickListener(this);
        mapTab.setOnClickListener(this);
        closestTab.setOnClickListener(this);

        ivBack.setOnClickListener(this);
        ivLogin.setOnClickListener(this);
        ivUserRequest.setOnClickListener(this);
        ivUserProfile.setOnClickListener(this);

        homeTab.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.white_spacing_color));
        favTab.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.white_spacing_color));
        popularTab.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.white_spacing_color));
        mapTab.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.white_spacing_color));
        closestTab.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.white_spacing_color));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_home:
                removeFragmentFromStack(Constants.HOME_PAGE);
                onFragmentChange(new HomeFragment(), Constants.HOME_PAGE);
                break;
            case R.id.tab_favorites:
                removeFragmentFromStack(Constants.FAVORITES);
                loginType = sharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
                if (loginType.isEmpty()) {
                    Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, R.anim.exit_slide_right);
                } else {
                    onFragmentChange(new FavouriteFragment(), Constants.FAVORITES);
                }

                break;
            case R.id.tab_popular:
                removeFragmentFromStack(Constants.POPULAR);
                onFragmentChange(new PopularFragment(), Constants.POPULAR);
                break;
            case R.id.tab_map:
                removeFragmentFromStack(Constants.MAP);
                onFragmentChange(new MapFragment(), Constants.MAP);
                break;
            case R.id.tab_closest:
                removeFragmentFromStack(Constants.CLOSET);
                onFragmentChange(new ClosetFragment(), Constants.CLOSET);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_login:
                Intent iLogin = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(iLogin);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.iv_user_profile:
                Intent iProfile = new Intent(HomeActivity.this, MyProfileActivity.class);
                startActivity(iProfile);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.iv_user_request:
                Intent iRequest = new Intent(HomeActivity.this, RequestForSaleActivity.class);
                startActivity(iRequest);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            default:
                break;
        }
        getSupportFragmentManager().executePendingTransactions();

    }


    private void ClearBackStack() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    public void removeFragmentFromStack(String tag) {
        int backCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backCount; i++) {
            if (getSupportFragmentManager().getBackStackEntryAt(i).getName().equalsIgnoreCase(tag)) {
                getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return;
            }
        }
    }


    private Boolean FindCurrentVisibleFragment(String fragmentName) {
        Boolean isCurrentFrag = false;
        try {
            Fragment currentFrag = getSupportFragmentManager().findFragmentByTag(fragmentName);
            if (currentFrag != null) {
                isCurrentFrag = true;
            }
        } catch (Exception e) {

        }
        return isCurrentFrag;
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            if (homePressed) {
                if (doubleBackToExitPressedOnce) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
//                    super.onBackPressed();
//                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, R.string.pressBack, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                homePressed = true;
            }
        }
    }

    /*    Fragment currentFrag = getSupportFragmentManager().findFragmentByTag(Constants.HOME_PAGE);
        if(currentFrag != null){
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_left);
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }else
        if(getSupportFragmentManager().getBackStackEntryCount() >= 2){
            onFragmentChange(new HomeFragment(), Constants.HOME_PAGE);
        }*/
        /*else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {

            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_left);
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }*/


    private void completeFragmentTask() {
        try {
            getSupportFragmentManager().executePendingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackStackChanged() {
        completeFragmentTask();
        handler.postDelayed(runnable, 500);
    }

    private Runnable customBackStackChanged() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                    Fragment fragment = fragmentList.get(getSupportFragmentManager().getBackStackEntryCount() - 1);
                    fragment.onResume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return runnable;
    }

    @Override
    public void onFragmentChange(Fragment fragmentToReplace, String tag) {
        getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(), fragmentToReplace).addToBackStack(tag).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void addFragment(Fragment fragmentToReplace, String tag) {
        getSupportFragmentManager().beginTransaction().add(frameLayout.getId(), fragmentToReplace, tag).addToBackStack(tag).commit();
        getSupportFragmentManager().executePendingTransactions();
    }



    // push notification
    public void tokenRecieve(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                } else {
                }
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Constants.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

///push notification

}
