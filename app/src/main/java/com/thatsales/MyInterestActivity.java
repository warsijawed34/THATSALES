package com.thatsales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thatsales.Adapter.CatListAdapter;
import com.thatsales.Adapter.MyFavoriteAdapter;
import com.thatsales.Adapter.MyInterestAdapter;
import com.thatsales.Adapter.StoreListAdapter;
import com.thatsales.ImageLoding.ImageLoader;
import com.thatsales.Model.StoreListModel;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Model.CategoryLIstModel;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.JSONUtils;
import com.thatsales.Utils.WebServiceApis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by vinove on 9/5/16.
 */
public class MyInterestActivity extends BaseActivity implements OnWebServiceResult, View.OnClickListener {
    EditText et_search;
    ImageView ivProfile, ivBack, ivLogout;
    TextView tvUserName, tvTittle;
    Context mContext;
    ListView lvInterest;
    CallWebService callWebService;
    MyInterestAdapter adapter;
    String strImage, strName, storeId;
    ImageLoader imgL;
    boolean callService = false, loadingMore = true;
    int startLimit = 0, endLimit = 10, currentPosition;
    String searchKeyword="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interests);
        mContext = MyInterestActivity.this;
        getView();
        tvTittle.setText(getString(R.string.my_interest));
        ivLogout.setVisibility(View.INVISIBLE);

        myInterestApi(searchKeyword,startLimit,endLimit);
        adapter = new MyInterestAdapter(mContext);

        strName = SharedPreferencesManger.getPrefValue(mContext, Constants.USERNAME, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        strImage = SharedPreferencesManger.getPrefValue(mContext, Constants.IMAGES, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        tvUserName.setText(strName);

        if(!strImage.isEmpty()){
            imgL = new ImageLoader(mContext);
            imgL.DisplayImage(strImage, ivProfile);

        }else {
            ivProfile.setImageResource(R.drawable.user_1);
        }

        addListner();
    }

    public void getView() {
       et_search= (EditText) findViewById(R.id.ed_interest_searchstore);
        ivProfile = (ImageView) findViewById(R.id.iv_interest_profile);
        tvUserName = (TextView) findViewById(R.id.tv_interest_user);
        lvInterest = (ListView) findViewById(R.id.lv_interest);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        tvTittle = (TextView) findViewById(R.id.tv_title);
        ivBack.setOnClickListener(this);
        ivLogout.setOnClickListener(this);

    }

    private void addListner() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchKeyword = s.toString();
                if (searchKeyword.length() > 2) {
                    et_search.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    if(!callService) {
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        startLimit = 0;
                        endLimit = 10;
                        myInterestApi(searchKeyword, startLimit,endLimit);
                    }

                } else if (searchKeyword.length() == 0) {
                    final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
                    searchKeyword ="";
                    startLimit = 0;
                    endLimit = 10;
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    myInterestApi(searchKeyword,startLimit,endLimit);
                    et_search.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search, 0);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callService = false;
                if (et_search.hasFocus()){
                    et_search.setCursorVisible(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_logout:
                SharedPreferencesManger.removeAllPrefValue(mContext);
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                finish();
                break;
        }
    }

    public void myInterestApi(String searchKeyword, int startLimit, int endLimit) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "categoryList");
                jsonObject.put("keyword",  CommonUtils.convertUTF(searchKeyword));
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("startLimit",String.valueOf(startLimit));
                jsonObject.put("endLimit", String.valueOf(endLimit));
                param.put("json_data", jsonObject.toString());
                System.out.println("Request: " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.INTEREST_LIST, this);
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
            case INTEREST_LIST:
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        JSONArray jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "productCategory");
                        JSONObject categoryObject;
                        CategoryLIstModel model;

                        if (jsonArray.length() >= 10) {
                            loadingMore = false;
                        }

                            for (int i = 0; i < jsonArray.length(); i++) {
                            categoryObject = jsonArray.getJSONObject(i);
                            model = new CategoryLIstModel();
                            model.setId(JSONUtils.getStringFromJSON(categoryObject, "id"));
                            model.setName(JSONUtils.getStringFromJSON(categoryObject, "name"));
                            model.setImage(JSONUtils.getStringFromJSON(categoryObject, "image"));
                            model.setFav(JSONUtils.getStringFromJSON(categoryObject, "fav"));
                            adapter.addToArrayList(model);

                        }
                        lvInterest.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                     /*   final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);*/


                        lvInterest.setSelection(currentPosition);
                        lvInterest.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                                    myInterestApi(searchKeyword,startLimit, endLimit);
                                }
                                }
                        });

                    } else {
                        if (!adapter.hasArrayItems())
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

}