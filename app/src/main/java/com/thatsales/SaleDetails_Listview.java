package com.thatsales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.thatsales.Adapter.SaleDetailsAdapter;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Model.SaleDetails;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.Constants;
import com.thatsales.Utils.JSONUtils;
import com.thatsales.Utils.WebServiceApis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;

/**
 * Created by vinove on 20/7/16.
 */
public class SaleDetails_Listview extends AppCompatActivity implements OnWebServiceResult,View.OnClickListener {
    Context mContext;
    ListView listView;
    ImageView ivBack, ivLogo,ivLogin;
    SaleDetailsAdapter adapter;
    SaleDetails model;
    CallWebService callWebService;
    JSONArray jsonArray;
    int startLimit = 0, endLimit = 10, currentPosition;
    boolean loadingMore = true;
    String saleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saledetails_activity);
        mContext = SaleDetails_Listview.this;
        getView();
        Intent intent = getIntent();
        saleId = intent.getStringExtra("saleId");
        adapter = new SaleDetailsAdapter(mContext);
      //  allApi(saleId, startLimit,endLimit);
   }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        adapter.notifyDataSetChanged();
        startLimit = 0;
        endLimit = 10;
        allApi(saleId, startLimit,endLimit);
    }

    private void getView() {
        listView= (ListView) findViewById(R.id.lv_sale_details);
        ivBack= (ImageView) findViewById(R.id.iv_back);
        ivLogo= (ImageView) findViewById(R.id.iv_logo);
        ivLogin= (ImageView) findViewById(R.id.iv_login);
        ivLogin.setVisibility(View.INVISIBLE);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }

    }


    public void allApi(String saleId, int startLimit, int endLimit) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "salesList");
                jsonObject.put("catId", "");
                jsonObject.put("storeId", saleId);
                jsonObject.put("keyword", "");
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("Type", "All");
                jsonObject.put("latitude", "");
                jsonObject.put("longitude", "");
                jsonObject.put("startLimit", String.valueOf(startLimit));
                jsonObject.put("endLimit", String.valueOf(endLimit));
                param.put("json_data", jsonObject.toString());
                System.out.println("Request: " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.SALE_DETAILS, this);
                callWebService.execute();
            } else {

                 CommonUtils.showToast(mContext, getResources().getString(R.string.internetConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {

        System.out.println("<<<<<<<<<RESULT>>>>>>>>>>" + result);

        switch (type) {
            case SALE_DETAILS:
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
           /*        adapter.clear();
                    adapter.notifyDataSetChanged();*/

                    if (code == 200) {
                        jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "data");
                        SaleDetails model;
                        JSONObject resultObject;

                        if (jsonArray.length() >= 10) {
                            loadingMore = false;
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            resultObject = jsonArray.getJSONObject(i);
                            model = new SaleDetails();
                            model.setSaleId(JSONUtils.getStringFromJSON(resultObject, "saleId"));
                            model.setSaleName(JSONUtils.getStringFromJSON(resultObject, "saleName"));
                            model.setCompneyName(JSONUtils.getStringFromJSON(resultObject, "companyName"));
                            model.setDescription(JSONUtils.getStringFromJSON(resultObject, "description"));
                            model.setContactNo(JSONUtils.getStringFromJSON(resultObject, "contactNo"));
                            model.setAddress(JSONUtils.getStringFromJSON(resultObject, "address"));
                            model.setLatitude(JSONUtils.getStringFromJSON(resultObject, "latitude"));
                            model.setLongitude(JSONUtils.getStringFromJSON(resultObject, "longitude"));
                            model.setStartDate(JSONUtils.getStringFromJSON(resultObject, "startDate"));
                            model.setEndDate(JSONUtils.getStringFromJSON(resultObject, "endDate"));
                            model.setIsFav(JSONUtils.getStringFromJSON(resultObject, "isFav"));
                            JSONArray imageArray = JSONUtils.getJSONArrayFromJSON(resultObject, "images");
                            model.setJsonArray(imageArray);
                            try {
                                model.setImage(imageArray.getString(0));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            adapter.addToArrayList(model);
                        }
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        listView.setSelection(currentPosition);
                        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView view, int scrollState) {

                            }

                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                int totalCount = firstVisibleItem + visibleItemCount;
                                if (totalCount == adapter.getCount() && !loadingMore) {
                                    loadingMore = true;
                                    startLimit = endLimit;
                                    endLimit = endLimit + 10;
                                    currentPosition = firstVisibleItem;
                                    allApi(saleId, startLimit,endLimit);
                                }

                            }
                        });

                    } else {

                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "data"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }


}
