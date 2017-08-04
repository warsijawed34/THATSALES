package com.thatsales.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.thatsales.Adapter.CatListAdapter;
import com.thatsales.Adapter.HomeAdapter;
import com.thatsales.Adapter.StoreListHomeAdapter;
import com.thatsales.BaseFragment;
import com.thatsales.GcmPushNotisfication.RegistrationIntentService;
import com.thatsales.HomeActivity;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Model.CategoryLIstModel;
import com.thatsales.Model.HomeModel;
import com.thatsales.Model.StoreListModel;
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
 * Created by vinove on 24/6/16.
 * this is home fragment...
 */
public class HomeFragment extends BaseFragment implements OnWebServiceResult {
    // push notis
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;
    //
    public EditText etItemCat, etShopping;
    Context mContext;
    ListView lvHome;
    HomeAdapter adapter;
    CallWebService callWebService;
    GPSTracker gps;
    HomeActivity mainActivity;
    SharedPreferencesManger sharedPreferencesManger;
    String loginType, storeId = "", catId = "";
    String searchKeyword;
    JSONArray jsonArray;
    ListPopupWindow storeListListPopupWindow, catListPopupWindow;
    StoreListHomeAdapter storeAdapter;
    CatListAdapter catAdapter;
    LinearLayout llCatSearch;
    ArrayList<HomeModel> homeListModelArrayList;
    boolean callService = false, loadingMore = true, storeloadingMore = true;
    int startLimit = 0, endLimit = 10, currentPosition;
    int storeStartLimit = 0, StoreEndLimit = 25, storeCurrentPosition;
    ListView storeListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favorites_listview, container, false);
        mContext = getActivity();
        mainActivity = (HomeActivity) mContext;
        llCatSearch = (LinearLayout) view.findViewById(R.id.ll_category);
        etItemCat = (EditText) view.findViewById(R.id.ed_itemcat);
        etShopping = (EditText) view.findViewById(R.id.ed_shoppingcenter);
        llCatSearch.setVisibility(View.VISIBLE);

        loginType = sharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        if (loginType.isEmpty()) {
            mainActivity.ivLogin.setVisibility(View.VISIBLE);
            mainActivity.ivBack.setVisibility(View.INVISIBLE);
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
        mainActivity.homeTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.view_color));
        mainActivity.homeTab.setTextColor(ContextCompat.getColor(mContext, R.color.fav_toolbar));
        mainActivity.homeTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.home_red, 0, 0);

        mainActivity.favTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.favTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.favTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.favourite, 0, 0);

        mainActivity.popularTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.popularTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.popularTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.popular, 0, 0);

        mainActivity.mapTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.mapTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.mapTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.map, 0, 0);

        mainActivity.closestTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.closestTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.closestTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.closest, 0, 0);

        lvHome = (ListView) view.findViewById(R.id.lv_row_home);

        homeListModelArrayList = new ArrayList<>();
        adapter = new HomeAdapter(mContext);

        storeAdapter = new StoreListHomeAdapter(mContext);
        catAdapter = new CatListAdapter(mContext);


        storeListListPopupWindow = new ListPopupWindow(mContext);
        catListPopupWindow = new ListPopupWindow(mContext);

        addListner();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.clear();
        adapter.notifyDataSetChanged();
        startLimit = 0;
        endLimit = 10;
        allApi(catId, storeId, startLimit, endLimit);
    }

    public void addListner() {
        etShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeAdapter.clear();
                storeListAPi(storeStartLimit,StoreEndLimit);
                storeListListPopupWindow.setAnchorView(etShopping);
                storeListListPopupWindow.setModal(true);
//              storeListListPopupWindow.setHeight(300);
                storeListListPopupWindow.show();
            }
        });

        storeListListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                storeId = storeAdapter.getItem(position).getId();
                etShopping.setText(storeAdapter.getItem(position).getName());
                storeListListPopupWindow.dismiss();
                startLimit = 0;
                endLimit = 10;
                adapter.clear();
                adapter.notifyDataSetChanged();
                loadingMore = true;
                allApi(catId, storeId,startLimit, endLimit);
            }
        });
        catListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                callService = true;
                catId = catAdapter.getItem(position).getId();
                etItemCat.setText(catAdapter.getItem(position).getName());
                catListPopupWindow.dismiss();
                startLimit = 0;
                endLimit = 10;
                adapter.clear();
                adapter.notifyDataSetChanged();
                loadingMore = true;
                allApiSearch(catId, storeId,startLimit, endLimit);

            }
        });

        etItemCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callService = false;
                if (etItemCat.hasFocus()){
                    etItemCat.setCursorVisible(true);
                }
            }
        });

        etItemCat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              catListPopupWindow.dismiss();
                searchKeyword = s.toString();
                if (searchKeyword.length() > 2) {
                    etItemCat.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    if(!callService) {
                        searchItemCatAPi(searchKeyword);
                    }

                } else if (searchKeyword.length() == 0) {
                    catListPopupWindow.dismiss();
                    catId = "";
                //    storeId = "";
                    startLimit = 0;
                    endLimit = 10;
                    allApi(catId, storeId, startLimit, endLimit);
                    adapter.clear();
                    adapter.notifyDataSetChanged();
               //     etShopping.setText("");
                    etItemCat.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void storeListAPi(int storestartLimit, int storeendLimit) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "storesList");
                jObject.put("keyword", "");
                jObject.put("startLimit", String.valueOf(storestartLimit));
                jObject.put("endLimit", String.valueOf(storeendLimit));
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.STORELIST, this);
                callWebService.execute();
            } else {
                CommonUtils.showToast(mContext, getResources().getString(R.string.internetConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchItemCatAPi(String keyword) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                if (callWebService != null) {
                    callWebService.cancel(true);
                    callWebService = null;
                }
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "categoryList");
                jsonObject.put("keyword", CommonUtils.convertUTF(keyword));
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("startLimit", "");
                jsonObject.put("endLimit", "");
                params.put("json_data", jsonObject.toString());
                System.out.println("Request123: " + jsonObject.toString());
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.SEARCH_ITEMCAT, this, false);
                callWebService.execute();
            } else {
               CommonUtils.showToast(mContext, getResources().getString(R.string.internetConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void allApiSearch(String catId, String storeId , int startLimit, int endLimit) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "salesList");
                jsonObject.put("catId", catId);
                jsonObject.put("storeId", storeId);
                jsonObject.put("keyword", "");
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("Type", "All");
                jsonObject.put("latitude", "");
                jsonObject.put("longitude", "");
                jsonObject.put("startLimit", String.valueOf(startLimit));
                jsonObject.put("endLimit", String.valueOf(endLimit));
                param.put("json_data", jsonObject.toString());
                System.out.println("Request:>>>>>> " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.HOME_LIST, this);
                callWebService.execute();
            } else {
                alert();
                //  CommonUtils.showToast(mContext, getResources().getString(R.string.noInternet));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void allApi(String catId, String storeId , int startLimit, int endLimit) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "salesList");
                jsonObject.put("catId", catId);
                jsonObject.put("storeId", storeId);
                jsonObject.put("keyword", "");
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("Type", "All");
                jsonObject.put("latitude", "");
                jsonObject.put("longitude", "");
                jsonObject.put("startLimit", String.valueOf(startLimit));
                jsonObject.put("endLimit", String.valueOf(endLimit));
                param.put("json_data", jsonObject.toString());
                System.out.println("Request:>>>>>> " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.HOME_LIST, this);
                callWebService.execute();
            } else {
                    alert();
            //  CommonUtils.showToast(mContext, getResources().getString(R.string.noInternet));

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
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            adapter.addToArrayList(model);

                        }

                        lvHome.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                     lvHome.setSelection(currentPosition);
                     lvHome.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                                    allApiSearch(catId, storeId, startLimit, endLimit);
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
            case STORELIST:
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        JSONArray jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "stores");
                        JSONObject storeObject;
                        StoreListModel model;

                        if (jsonArray.length() >= 25) {
                            storeloadingMore = false;
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            storeObject = jsonArray.getJSONObject(i);
                            model = new StoreListModel();
                            model.setId(JSONUtils.getStringFromJSON(storeObject, "id"));
                            model.setName(JSONUtils.getStringFromJSON(storeObject, "name"));
                            storeAdapter.addToArrayList(model);
                        }
                        storeListListPopupWindow.setAdapter(storeAdapter);
                        storeAdapter.notifyDataSetChanged();
                      if (storeListListPopupWindow.isShowing()) {
                            storeListView = storeListListPopupWindow.getListView();
                            storeListView.setSelection(storeCurrentPosition);
                            storeListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(AbsListView view, int scrollState) {

                                }

                                @Override
                                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                    int totalCount = firstVisibleItem + visibleItemCount;

                                    System.out.print("<<<<<<totalCount>>>>>>>>"+totalCount+"storeAdapter="+storeAdapter.getCount());

                                    if (totalCount == storeAdapter.getCount() && !storeloadingMore) {
                                        System.out.print("totalCount="+totalCount+"storeAdapter="+storeAdapter.getCount());
                                        storeloadingMore = true;
                                        storeStartLimit = StoreEndLimit;
                                        StoreEndLimit = StoreEndLimit + 25;
                                        storeCurrentPosition = firstVisibleItem;
                                        storeListAPi(storeStartLimit, StoreEndLimit);
                                    }
                                }
                            });

                        }else
                          storeloadingMore=false;
                    } else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SEARCH_ITEMCAT:
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    catAdapter.clear();
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
                            model.setFav(JSONUtils.getStringFromJSON(catObject, "fav"));
                            catAdapter.addToArrayList(model);
                        }
                        catListPopupWindow.setAdapter(catAdapter);
                        catListPopupWindow.setAnchorView(etItemCat);
                        catListPopupWindow.setModal(false);
                        catListPopupWindow.show();
                        catAdapter.notifyDataSetChanged();

                    } else {
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


