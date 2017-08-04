package com.thatsales;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thatsales.Adapter.CatListAdapter;
import com.thatsales.Adapter.StoreListAdapter;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.WebServiceApis;
import com.thatsales.Utils.JSONUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Created by vinove on 16/5/16.
 */
public class RequestForSaleActivity extends BaseActivity implements OnWebServiceResult, View.OnClickListener {

    EditText productName, productCategory, description, storeName, estimateDate;
    TextView placeRequest, tvTittle;
    String estimateDateFrom,catId, storeId ;
    Context mContext;
    CallWebService webService;
    Calendar newCalendar;
    SimpleDateFormat dateFormatter;
    ImageView ivBack, ivLogout;
    ListPopupWindow catListPopupWindow, storeListPopupWindow;
    SharedPreferencesManger sharedPreferencesManger;
    String loginType, strDateFormat;
    CatListAdapter adapter;
    StoreListAdapter storeAdapter;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_for_sale);
        mContext = RequestForSaleActivity.this;
        productName = (EditText) findViewById(R.id.et_product_name);
        productCategory = (EditText) findViewById(R.id.ed_product_category);
        description = (EditText) findViewById(R.id.ed_product_description);
        storeName = (EditText) findViewById(R.id.ed_store_name);
        estimateDate = (EditText) findViewById(R.id.ed_expected_date);
        placeRequest = (TextView) findViewById(R.id.btn_place_request);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTittle = (TextView) findViewById(R.id.tv_title);
        ivLogout.setVisibility(View.INVISIBLE);
        tvTittle.setText(getString(R.string.request_for_sale));

        placeRequest.setOnClickListener(this);
        productCategory.setOnClickListener(this);
        storeName.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        estimateDate.setOnClickListener(this);

        loginType = sharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

        adapter = new CatListAdapter(mContext);
        catListPopupWindow = new ListPopupWindow(mContext);


        storeAdapter = new StoreListAdapter(mContext);
        storeListPopupWindow = new ListPopupWindow(mContext);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_place_request:
                if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                    if (validateRequestForSaleForm()) {
                        if (loginType.isEmpty()) {
                            Toast.makeText(mContext, R.string.loginfirst, Toast.LENGTH_LONG).show();
                        } else {
                            requestForSaleApi();
                        }
                    }

                } else {
                    Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.ed_expected_date:
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DateDialog();
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

    private void requestForSaleApi() {

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        newCalendar = Calendar.getInstance();
        strDateFormat=dateFormatter.format( new Date() );
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "requestForSale");
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("productName", CommonUtils.convertUTF(productName.getText().toString()));
                jsonObject.put("category", CommonUtils.convertUTF(productCategory.getText().toString()));
                jsonObject.put("store", CommonUtils.convertUTF(storeName.getText().toString()));
                jsonObject.put("description", CommonUtils.convertUTF(description.getText().toString()));
                jsonObject.put("estimatedDate", strDateFormat);
                params.put("json_data", jsonObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.REQUESTSALE, this);
                webService.execute();
            } else {
                Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CategoryListAPi() {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "categoryList");
                jObject.put("keyword", "");
                jObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jObject.put("startLimit", "");
                jObject.put("endLimit", "");
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.CATLIST, this);
                webService.execute();
            } else {
                Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void storeListAPi() {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "storesList");
                jObject.put("keyword", "");
                jObject.put("startLimit", "");
                jObject.put("endLimit", "");
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.STORELIST, this);
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
            case REQUESTSALE:
                try {
                    //showing two or more parameters missing
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        productName.setText("");
                        productCategory.setText("");
                        description.setText("");
                        storeName.setText("");
                        estimateDate.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private boolean validateRequestForSaleForm() {

        if (productName.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_product_name));
            return false;

        }

        if (productCategory.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_product_cat));
            return false;
        }

        if (description.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_product_desc));
            return false;
        }


        if (storeName.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_storename));
            return false;
        }
/*        if (estimateDate.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_expected_date));
            return false;
        }*/
        return true;
    }
    public void DateDialog(){

        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,int selectedDay)
            {
                year = selectedYear;
                month = selectedMonth;
                day = selectedDay;
               // strDateFormat=new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).toString();
                strDateFormat=new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).toString();
                estimateDate.setText(strDateFormat);

            }};
        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, year, month, day);
        dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpDialog.show();
    }
}