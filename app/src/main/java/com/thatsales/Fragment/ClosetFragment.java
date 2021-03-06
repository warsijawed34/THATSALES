package com.thatsales.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

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
 * Created by vinove on 1/6/16.
 */
public class ClosetFragment extends BaseFragment implements OnWebServiceResult {
    Context mContext;
    ListView lvCloset;
    HomeAdapter adapter;
    CallWebService callWebService;
    GPSTracker gps;
    HomeActivity mainActivity;
    SharedPreferencesManger sharedPreferencesManger;
    JSONArray jsonArray;
    double latitude;
    double longitude;
    String strLatitude, strLongitude, loginType, searchKeyword;
    ArrayList<HomeModel> homeListModelArrayList;
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
        lvCloset = (ListView) view.findViewById(R.id.lv_row_home);
        mainActivity.closestTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.view_color));
        mainActivity.closestTab.setTextColor(ContextCompat.getColor(mContext, R.color.fav_toolbar));
        mainActivity.closestTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.closest_active, 0, 0);

        mainActivity.homeTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.homeTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.homeTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.home_grey, 0, 0);

        mainActivity.mapTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.mapTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.mapTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.map, 0, 0);

        mainActivity.favTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.favTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.favTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.favourite, 0, 0);

        mainActivity.popularTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.popularTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.popularTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.popular, 0, 0);

        homeListModelArrayList = new ArrayList<>();
        adapter = new HomeAdapter(mContext);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        gpsTracker();

    }
    public void gpsTracker() {
        gps = new GPSTracker(mContext);

        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            adapter.clear();
            adapter.notifyDataSetChanged();
            startLimit = 0;
            endLimit = 10;
            allApi(startLimit, endLimit);

        } else {
            enableGps();
      //    gps.showSettingsAlert();
        }
    }


    public void allApi(int startLimit, int endLimit) {
        strLatitude = Double.toString(latitude);
        strLongitude = Double.toString(longitude);

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "salesList");
                jsonObject.put("catId", "");
                jsonObject.put("storeId", "");
                jsonObject.put("keyword", "");
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(getActivity(), Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("Type", "NearBy");
                jsonObject.put("latitude", strLatitude);
                jsonObject.put("longitude", strLongitude);
                jsonObject.put("startLimit",  String.valueOf(startLimit));
                jsonObject.put("endLimit", String.valueOf(endLimit));
                param.put("json_data", jsonObject.toString());
                System.out.println("Request: " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.HOME_LIST, this);
                callWebService.execute();
            } else {
                alert();
                //CommonUtils.showToast(mContext, getResources().getString(R.string.internetConnection));
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            adapter.addToArrayList(model);
                        }
                        lvCloset.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        lvCloset.setSelection(currentPosition);
                        lvCloset.setOnScrollListener(new AbsListView.OnScrollListener() {
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

    public void enableGps()
    {
        final LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.enableGps)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

}


