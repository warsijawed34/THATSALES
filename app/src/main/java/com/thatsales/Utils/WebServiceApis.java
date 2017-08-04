package com.thatsales.Utils;

import android.content.Context;
import android.content.Intent;

import com.thatsales.LoginActivity;
import com.thatsales.R;


import org.json.JSONObject;


public class WebServiceApis {



    Context mContext;

    public WebServiceApis(Context context) {
        mContext = context;
    }

    public String callApi() {

        return mContext.getString(R.string.api1);
    }

    public String callApiUpdate() {

        return mContext.getString(R.string.api_url1);
    }

}
