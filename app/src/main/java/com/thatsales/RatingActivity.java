package com.thatsales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thatsales.ImageLoding.ImageLoader;
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
 * Created by vinove on 13/5/16.
 */
public class RatingActivity extends BaseActivity implements OnWebServiceResult, View.OnClickListener {
    EditText title, description;
    ImageView profile_img;
    TextView profile_name, submit;
    RatingBar ratingBar;
    Context mContext;
    CallWebService webService;
    ImageView ivBack,ivLogout;
    TextView tvTitle;
    ImageLoader imgL;
    String strName, strimage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        mContext=RatingActivity.this;
        getView();
        ivLogout.setVisibility(View.INVISIBLE);
        tvTitle.setText(getString(R.string.rating));

        strimage= SharedPreferencesManger.getPrefValue(mContext, Constants.IMAGES, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        strName=SharedPreferencesManger.getPrefValue(mContext, Constants.USERNAME, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        profile_name.setText(strName);
        imgL = new ImageLoader(mContext);
        imgL.DisplayImage(strimage, profile_img);
    }

    private void getView() {
        title = (EditText) findViewById(R.id.ed_title);
        description = (EditText) findViewById(R.id.ed_description);
        profile_img = (ImageView) findViewById(R.id.im_profile_img);
        profile_name = (TextView) findViewById(R.id.tv_profile_name);
        submit = (TextView) findViewById(R.id.btn_submit);
        ivBack= (ImageView) findViewById(R.id.iv_back);
        ivLogout= (ImageView) findViewById(R.id.iv_logout);
        tvTitle= (TextView) findViewById(R.id.tv_title);
        submit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                if(new ConnectionDetector(mContext).isConnectingToInternet()) {

                    if(validateRatingForm()){
                        Toast.makeText(mContext,"App Rated",Toast.LENGTH_LONG).show();
                        Intent iRated = new Intent(mContext,MyProfileActivity.class);
                        startActivity(iRated);
                    }

                }
                else {
                    Toast.makeText(mContext,getString(R.string.internetConnection),Toast.LENGTH_LONG).show();
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

    private void ratingApi() {
        try {
            Hashtable<String, String> param = new Hashtable<>();
            param.put("method", "rating");
            param.put("", "");
            param.put("", "");
            param.put("", "");
            System.out.println("REQUEST : " + param);
            webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.RATING, this);
            webService.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //validation
    private boolean validateRatingForm() {

        if (title.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_tittle));
            return false;
        }

        if (description.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_description));
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
                    String message = JSONUtils.getStringFromJSON(jsonObject, "message");
                    if (code == 200) {
                        finish();
                    } /*else {
                                CommonUtils.showToast(con, "Invalid credentials or wrong login type");
                            }*/
                } catch (Exception e) {
                    e.printStackTrace();
                    CommonUtils.showToast(mContext, result);
                }
                break;
            default:
                break;
        }
    }
}
