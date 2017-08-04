package com.thatsales;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * checkbox validation
 */
public class SignupActivity extends BaseActivity implements OnWebServiceResult, View.OnClickListener {
    EditText profileName, emailAddress, password, mobileNumber;
    TextView signUp, tvTermsAndCondition, tvTittle;
    CheckBox checkBox;
    Context mContext;
    SharedPreferencesManger sharedPreferencesManger;
    String loginType = Constants.SE, profileStr, emailStr, passwordStr, mobileStr;
    CallWebService webService;
    boolean showPwd;
    ImageView ivBack, ivLogout, visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mContext = SignupActivity.this;
        profileName = (EditText) findViewById(R.id.ed_profileName);
        emailAddress = (EditText) findViewById(R.id.ed_email_address);
        password = (EditText) findViewById(R.id.ed_password);
        mobileNumber = (EditText) findViewById(R.id.ed_mobile_number);
        visible = (ImageView) findViewById(R.id.btn_visible_active);
        signUp = (TextView) findViewById(R.id.btn_signup);
        checkBox = (CheckBox) findViewById(R.id.btn_checkBox);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        tvTittle = (TextView) findViewById(R.id.tv_title);
        tvTermsAndCondition = (TextView) findViewById(R.id.tv_term_condition);
        ivLogout.setVisibility(View.INVISIBLE);
        tvTittle.setText(getString(R.string.signup));
        signUp.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        visible.setOnClickListener(this);
        tvTermsAndCondition.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                    if (validateSignUpForm()) {
                        signupApi();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_term_condition:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://i.vinove.com/shazially_v1.2/frontend/web/index.php/site/terms-and-conditions")));
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
        }

    }


    private void signupApi() {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "signup");
                jObject.put("userName", CommonUtils.convertUTF(profileName.getText().toString()));
                jObject.put("email", CommonUtils.convertUTF(emailAddress.getText().toString()));
                jObject.put("password", CommonUtils.convertUTF(password.getText().toString()));
                jObject.put("mobile", CommonUtils.convertUTF(mobileNumber.getText().toString()));
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.SIGNUP, this);
                webService.execute();
            } else {
                Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type) {
            case SIGNUP:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        SharedPreferencesManger.setPrefValue(mContext, Constants.USERID, JSONUtils.getStringFromJSON(jsonObject, "userID"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.USERNAME, JSONUtils.getStringFromJSON(jsonObject, "userName"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.EMAILID, JSONUtils.getStringFromJSON(jsonObject, "email"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.MOBILENUMBER, JSONUtils.getStringFromJSON(jsonObject, "mobile"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.IMAGES, JSONUtils.getStringFromJSON(jsonObject, "image"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.exit_slide_right);
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));

                    } else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }


    //validation
    private boolean validateSignUpForm() {

        if (profileName.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_profilename));
            return false;
        }

        if (emailAddress.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_emailaddress));
            return false;
        }
        if (password.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_password));
            return false;
        }

        if (password.getText().toString().trim().length() < 6) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_length_password));
            return false;
        }
       if(!mobileNumber.getText().toString().isEmpty()) {
           if ((mobileNumber.getText().toString().trim().length() < 6) || (mobileNumber.getText()
                   .toString().trim().length() > 15)) {
               CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_mobile));
               return false;
           }
       }


        //check the email enter is correct or not
        if (!CommonUtils.isValidEmailAddress(emailAddress.getText().toString().trim())) {
            //show your message if not matches with email pattern
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_invalid_email));
            return false;
        }
        if (checkBox.isChecked() != true) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.checkbox_msg_validation));
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


}