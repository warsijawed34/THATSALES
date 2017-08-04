package com.thatsales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thatsales.Adapter.CatListAdapter;
import com.thatsales.Adapter.MyFavoriteAdapter;
import com.thatsales.ImageLoding.ImageLoader;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Model.MyFavoritesModel;
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
 * this is main Favorites class..
 * Created by Jawed on 9/5/16.
 */
public class MyFavoriteSalesActivity extends BaseActivity implements OnWebServiceResult, View.OnClickListener {
   // EditText etSearch;
    ImageView ivProfile, ivLogout, ivBack;
    TextView tvUsername, tvTittleFav;
    ListView lvFavorites;
    Context mContext;
    MyFavoriteAdapter adapter;
    CallWebService callWebService;
    String strImage, strName, storeId;
    ImageLoader imgL;
    ListPopupWindow catListPopupWindow;
    CatListAdapter catListAdapter;
    ArrayList<MyFavoritesModel> favListModelArrayList;
    boolean callService = false, loadingMore = true;
    int startLimit = 0, endLimit = 10, currentPosition;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        mContext = this;
        getView();
        ivLogout.setVisibility(View.INVISIBLE);
        tvTittleFav.setText(getString(R.string.my_fav_store));
        myFavoriteApi(startLimit, endLimit);
        favListModelArrayList = new ArrayList<>();
        adapter = new MyFavoriteAdapter(mContext);

/*        catListAdapter = new CatListAdapter(mContext);
        catListPopupWindow = new ListPopupWindow(mContext);*/

        addListner();
    }

    public void getView() {
        //etSearch = (EditText) findViewById(R.id.ed_searchstore);
        ivProfile = (ImageView) findViewById(R.id.iv_fav_profile);
        tvUsername = (TextView) findViewById(R.id.tv_fav_user);
        lvFavorites = (ListView) findViewById(R.id.lv_favorites);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        tvTittleFav = (TextView) findViewById(R.id.tv_title);
        ivBack.setOnClickListener(this);
        ivLogout.setOnClickListener(this);

        strName = SharedPreferencesManger.getPrefValue(mContext, Constants.USERNAME, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        strImage = SharedPreferencesManger.getPrefValue(mContext, Constants.IMAGES, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        tvUsername.setText(strName);
        if(!strImage.isEmpty()){
            imgL = new ImageLoader(mContext);
            imgL.DisplayImage(strImage, ivProfile);
        }else {
            ivProfile.setImageResource(R.drawable.user_1);
        }



    }

    private void addListner() {
     /*   etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                storeId = s.toString();
                if (count > 2) {
                    etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    searchItemCatAPi(storeId);
                } else if (count == 0) {
                    myFavoriteApi();
                    etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

/*    private void searchItemCatAPi(String keyword) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                if (callWebService != null) {
                    callWebService.cancel(true);
                    callWebService = null;
                }
                //{"method":"storesList","keyword":"","startLimit":"","endLimit":""}
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "storesList");
                jsonObject.put("keyword", CommonUtils.convertUTF(keyword));
                jsonObject.put("startLimit", "0");
                jsonObject.put("endLimit", "10");
                params.put("json_data", jsonObject.toString());
                System.out.println("Request: " + params);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.SEARCH_ITEMCAT, this, false);
                callWebService.execute();
            } else {
                CommonUtils.showToast(mContext, getResources().getString(R.string.internetConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/

    public void myFavoriteApi(int startLimit, int endLimit) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "salesList");
                jsonObject.put("catId", "");
                jsonObject.put("storeId", "");
                jsonObject.put("keyword", "");
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("Type", "All");
                jsonObject.put("latitude", "");
                jsonObject.put("longitude", "");
                jsonObject.put("startLimit", String.valueOf(startLimit));
                jsonObject.put("endLimit", String.valueOf(endLimit));
                param.put("json_data", jsonObject.toString());
                System.out.println("Request: " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.FAVLIST, this);
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
            case FAVLIST:
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");

                    if (code == 200) {
                        JSONArray jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "data");
                        MyFavoritesModel model;
                        JSONObject resultObject;
                        if (jsonArray.length() >= 10) {
                            loadingMore = false;
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            resultObject = jsonArray.getJSONObject(i);
                            model = new MyFavoritesModel();
                            model.setSaleID(JSONUtils.getStringFromJSON(resultObject, "saleId"));
                            model.setStrSaleName(JSONUtils.getStringFromJSON(resultObject, "saleName"));
                            model.setStrSaleDesc(JSONUtils.getStringFromJSON(resultObject, "companyName"));
                         /*   model.setStrSaleName(JSONUtils.getStringFromJSON(resultObject, "companyName"));
                            model.setStrSaleDesc(JSONUtils.getStringFromJSON(resultObject, "saleName"));*/
                            model.setIsFav(JSONUtils.getStringFromJSON(resultObject, "isFav"));
                            adapter.addToArrayList(model);
                        }
                        lvFavorites.setAdapter(adapter);
                        lvFavorites.setSelection(currentPosition);
                        lvFavorites.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                                    myFavoriteApi(startLimit, endLimit);
                                }
                            }
                        });
                    } else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
          /*  case SEARCH_ITEMCAT:
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    catListAdapter.clear();
                    if (code == 200) {
                        JSONArray jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "productCategory");
                        CategoryLIstModel model;
                        JSONObject catObject;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            catObject = jsonArray.getJSONObject(i);
                            model = new CategoryLIstModel();
                            model.setId(JSONUtils.getStringFromJSON(catObject, "id"));
                            model.setImage(JSONUtils.getStringFromJSON(catObject, "image"));
                            model.setName(JSONUtils.getStringFromJSON(catObject, "name"));
                            catListAdapter.addToArrayList(model);
                        }
                        catListPopupWindow.setAdapter(catListAdapter);
                        catListAdapter.notifyDataSetChanged();
                        catListPopupWindow.setAnchorView(et_search);
                        catListPopupWindow.setModal(false);
                        catListPopupWindow.show();

                    } else {
                        CommonUtils.showToast(getActivity(), JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;*/
            default:
                break;
        }
    }

}
