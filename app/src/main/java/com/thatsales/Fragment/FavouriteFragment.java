package com.thatsales.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.thatsales.Adapter.FragmentFavoriteAdapter;
import com.thatsales.Adapter.HomeAdapter;
import com.thatsales.BaseFragment;
import com.thatsales.HomeActivity;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Model.HomeModel;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.R;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.GPSTracker;
import com.thatsales.Utils.JSONUtils;
import com.thatsales.Utils.WebServiceApis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by vinove on 31/5/16.
 * this is all fragment...
 */
public class FavouriteFragment extends BaseFragment implements OnWebServiceResult {
    Context mContext;
    ListView lvFavorite;
    FragmentFavoriteAdapter adapter;
    CallWebService callWebService;
    GPSTracker gps;
    HomeActivity mainActivity;
    SharedPreferencesManger sharedPreferencesManger;
    String searchKeyword,loginType;
    JSONArray jsonArray;
    ArrayList<HomeModel> FavListModelArrayList;
    int startLimit = 0, endLimit = 10, currentPosition;
    boolean loadingMore = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favorites_listview, container, false);
        mContext = getActivity();
        mainActivity = (HomeActivity) mContext;
        mainActivity.llCatSearch.setVisibility(View.GONE);
        loginType = sharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        if (loginType.isEmpty()) {
            mainActivity.ivLogin.setVisibility(View.VISIBLE);
            mainActivity.ivBack.setVisibility(View.VISIBLE);
            mainActivity.ivLogo.setVisibility(View.VISIBLE);
            mainActivity.tvTitle.setVisibility(View.INVISIBLE);
        } else {
            mainActivity.tvTitle.setVisibility(View.INVISIBLE);
            mainActivity.ivLogo.setVisibility(View.VISIBLE);
            mainActivity.ivLogin.setVisibility(View.INVISIBLE);
            mainActivity.ivBack.setVisibility(View.INVISIBLE);
            mainActivity.ivUserRequest.setVisibility(View.VISIBLE);
            mainActivity.ivUserProfile.setVisibility(View.VISIBLE);
        }
        lvFavorite = (ListView) view.findViewById(R.id.lv_row_home);
        mainActivity.favTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.view_color));
        mainActivity.favTab.setTextColor(ContextCompat.getColor(mContext, R.color.fav_toolbar));
        mainActivity.favTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.favourite_active, 0, 0);

        mainActivity. homeTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.homeTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity. homeTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.home_grey, 0, 0);

        mainActivity.popularTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.popularTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.popularTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.popular, 0, 0);

        mainActivity.mapTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity. mapTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.mapTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.map, 0, 0);

        mainActivity.closestTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.closestTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.closestTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.closest, 0, 0);
        FavListModelArrayList = new ArrayList<>();
        adapter = new FragmentFavoriteAdapter(mContext);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.clear();
        adapter.notifyDataSetChanged();
        startLimit = 0;
        endLimit = 10;
        allApi(startLimit, endLimit);
    }

    public void allApi(int startLimit, int endLimit) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "salesList");
                jsonObject.put("catId", "");
                jsonObject.put("storeId", "");
                jsonObject.put("keyword", "");
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("Type", "favorite");
                jsonObject.put("latitude", "");
                jsonObject.put("longitude", "");
                jsonObject.put("startLimit", String.valueOf(startLimit));
                jsonObject.put("endLimit", String.valueOf(endLimit));
                param.put("json_data", jsonObject.toString());
                System.out.println("Request: " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.HOME_LIST, this);
                callWebService.execute();
            } else {
                alert();
               // CommonUtils.showToast(mContext, getResources().getString(R.string.internetConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {

        System.out.println("<<<<<<<<<RESULT>>>>>>>>>>" + result);

        switch (type) {
            case HOME_LIST:
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                  /*  FavListModelArrayList = new ArrayList<>();
                    FavListModelArrayList.clear();*/

                    if (code == 200) {
                        jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "data");
                        HomeModel model;
                        JSONObject resultObject;


                        if (jsonArray.length() >= 10) {
                            loadingMore = false;
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            resultObject = jsonArray.getJSONObject(i);
                            model = new HomeModel();
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

                        lvFavorite.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        lvFavorite.setSelection(currentPosition);
                        lvFavorite.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                                    allApi(startLimit, endLimit);
                                }

                            }
                        });

                    } else {
                        if (!adapter.hasArrayItems())
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
    public void alert(){
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();

        alertDialog.setMessage(getString(R.string.noInternet));

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }



}


