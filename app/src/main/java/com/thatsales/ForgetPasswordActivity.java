package com.thatsales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.JSONUtils;
import com.thatsales.Utils.WebServiceApis;

import org.json.JSONObject;

import java.util.Hashtable;

/**
 * Created by vinove on 29/9/15.
 * done json api here.. for testing
 */
public class ForgetPasswordActivity extends BaseActivity implements OnWebServiceResult, View.OnClickListener {
    EditText emailId;
    TextView submit, tvTittle;
    Context mContext;
    CallWebService webService;
    ImageView ivBack, ivLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mContext = ForgetPasswordActivity.this;
        emailId = (EditText) findViewById(R.id.ed_email_address);
        submit = (TextView) findViewById(R.id.btn_submit);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        tvTittle = (TextView) findViewById(R.id.tv_title);
        ivLogout.setVisibility(View.INVISIBLE);
        tvTittle.setText("Forgot Password");
        submit.setOnClickListener(this);
        ivBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                    if (validateForm()) {
                        ForgetPwdAPi();

                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    private void ForgetPwdAPi() {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "forgotPassword");
                jObject.put("email", CommonUtils.convertUTF(emailId.getText().toString()));
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.FORGOT_PASSWORD, this);
                webService.execute();
            } else {
                Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type) {
            case FORGOT_PASSWORD:
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.exit_slide_right);
                    } else {
                        CommonUtils.showToast(mContext, getString(R.string.msg_emailnotregistered));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private boolean validateForm() {
        if (emailId.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_emailId));
            return false;
        }
        if (!CommonUtils.isValidEmailAddress(emailId.getText().toString().trim())) {
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


}
