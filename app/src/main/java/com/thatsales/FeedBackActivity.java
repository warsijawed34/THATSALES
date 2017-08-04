package com.thatsales;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
 * Created by vinove on 16/5/16.
 */
public class FeedBackActivity extends BaseActivity implements OnWebServiceResult, View.OnClickListener {
    Context mContext;
    EditText reasonToContact, description;
    TextView btnSubmit, tvTitle, activityName;
    CallWebService webService;
    ImageView ivBack, ivLogout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
        mContext = FeedBackActivity.this;
        reasonToContact = (EditText) findViewById(R.id.ed_reasion_to_contact);
        description = (EditText) findViewById(R.id.ed_description);
        btnSubmit = (TextView) findViewById(R.id.btn_submit);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivLogout.setVisibility(View.INVISIBLE);
        tvTitle.setText(getString(R.string.feedback_small));
        btnSubmit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (validateFeedbackForm()) {
                    feedBackApi();
                }
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }

    private void feedBackApi() {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "feedback");
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("reason", CommonUtils.convertUTF(reasonToContact.getText().toString()));
                jsonObject.put("desc", CommonUtils.convertUTF(description.getText().toString()));
                params.put("json_data", jsonObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.FEEDBACK, this);
                webService.execute();
            } else {
                Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //validation
    private boolean validateFeedbackForm() {

        if (reasonToContact.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.subject_feedback));
            return false;
        }

        if (description.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.message_feedback));
            return false;
        }
        return true;
    }

    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type) {
            case FEEDBACK:
                try {
                    System.out.println("result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        reasonToContact.setText("");
                        description.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }


}
