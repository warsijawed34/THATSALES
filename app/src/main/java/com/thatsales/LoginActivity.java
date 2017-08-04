package com.thatsales;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.thatsales.GcmPushNotisfication.RegistrationIntentService;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.JSONUtils;
import com.thatsales.Utils.WebServiceApis;

import org.json.JSONObject;

import java.util.Hashtable;

/**
 * Created by ajay on 5/12/2016.
 * done json api here.. for testing
 */
public class LoginActivity extends BaseActivity implements OnWebServiceResult, View.OnClickListener {

    // push notis
/*    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;*/
    //


    EditText userName, password;
    ImageView visible;
    TextView activityName, tvTitle, forgot, login, signUp;
    Context mContext;
    SharedPreferencesManger sharedPreferencesManger;
    String loginType = Constants.SE, usernameStr, passwordStr;
    CallWebService webService;
    ImageView ivBack, ivLogout;
    boolean showPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;

        userName = (EditText) findViewById(R.id.ed_emailid);
        password = (EditText) findViewById(R.id.ed_password);
        login = (TextView) findViewById(R.id.btn_login);
        signUp = (TextView) findViewById(R.id.tv_signUp);
        visible = (ImageView) findViewById(R.id.btn_visible_active);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        forgot = (TextView) findViewById(R.id.tv_forgotpass);
        login.setOnClickListener(this);
        signUp.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        visible.setOnClickListener(this);
        forgot.setOnClickListener(this);
        ivLogout.setVisibility(View.INVISIBLE);
        tvTitle.setText(getString(R.string.login_small));

  /*    mRegistrationBroadcastReceiver = new BroadcastReceiver() {
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
        }*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                    if (validateLoginForm()) {
                        loginApi();
                        CommonUtils.hideKeyboard(mContext);
                    }

                } else {
                    Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_visible_active:
                if(password.getText().toString().length() > 0) {
                    if (!showPwd) {
                        showPwd = true;
                        password.setInputType(InputType.TYPE_CLASS_TEXT);
                        password.setSelection(password.getText().length());
                        visible.setImageResource(R.drawable.visible_active);
                    } else {
                        showPwd = false;
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.setSelection(password.getText().length());
                        visible.setImageResource(R.drawable.visible);
                    }
                }
                break;
            case R.id.tv_forgotpass:
                Intent ifav = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(ifav);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.tv_signUp:
                Intent isign = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(isign);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }

    }

    private void loginApi() {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "login");
                jObject.put("email", CommonUtils.convertUTF(userName.getText().toString()));
                jObject.put("password", CommonUtils.convertUTF(password.getText().toString()));
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.LOGIN, this);
                webService.execute();
            } else {
                Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * onWebServiceResult method used for get response from server
     *
     * @param result - having coming data from webservice
     * @param type   -  having type of webservice which we set on time of sending request,
     *               it help to get correct data if multiple webservice call in same class
     */
    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type) {
            case LOGIN:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        SharedPreferencesManger.setPrefValue(mContext, Constants.USERID, JSONUtils.getStringFromJSON(jsonObject, "userId"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.USERNAME, JSONUtils.getStringFromJSON(jsonObject, "userName"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.EMAILID, JSONUtils.getStringFromJSON(jsonObject, "email"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.MOBILENUMBER, JSONUtils.getStringFromJSON(jsonObject, "mobile"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.IMAGES, JSONUtils.getStringFromJSON(jsonObject, "image"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.exit_slide_right);
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    } else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        //Account doesn't exists
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    //validation
    private boolean validateLoginForm() {

        if (userName.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_email));
            return false;
        }

        if (password.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_password));
            return false;
        }
        //check the email enter is correct or not
        if (!CommonUtils.isValidEmailAddress(userName.getText().toString().trim())) {
            //show your message if not matches with email pattern
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_invalid_email));
            return false;
        }

        return true;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }

/*    @Override
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

    *//**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *//*
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
    }*/

}
