package com.thatsales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.thatsales.Utils.Constants;
import com.thatsales.Pereferences.SharedPreferencesManger;

/**
 * Created by vinove on 2/5/16.
 */
public class Splash extends BaseActivity {

    private boolean isLogin;
    private String loginType;
    private Context mContext;
    SharedPreferencesManger sharedPreferencesManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Splash.this;
        setContentView(R.layout.splash);
        sharedPreferencesManger = new SharedPreferencesManger();
        isLogin = (boolean) sharedPreferencesManger.getPrefValue(mContext, Constants.LOGIN, SharedPreferencesManger.PREF_DATA_TYPE.BOOLEAN);
        loginType = sharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        gotoNext(isLogin, loginType);
    }

    private void gotoNext(final boolean login, final String type) {
        Handler handler = new Handler();
        Runnable r = new Runnable() {

            @Override
            public void run() {
                if (login) {
                    Intent intent = new Intent(Splash.this, HomeActivity.class);
                    intent.putExtra("loginType", type);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Splash.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        handler.postDelayed(r, 2500);
    }
}



