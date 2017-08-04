package com.thatsales.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thatsales.BaseFragment;
import com.thatsales.DetailedSaleScreenActivity;
import com.thatsales.HomeActivity;
import com.thatsales.SaleDetails_Listview;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
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


public class MapFragment extends BaseFragment implements OnWebServiceResult{

    Context mContext;
    HomeActivity mainActivity;
    MapView mMapView;
    GoogleMap googleMap;
    GPSTracker gps;
    double latitude;
    double longitude;
    String strLatitude, strLongitude, loginType;
    CallWebService callWebService;
    Marker marker1;
    SharedPreferencesManger sharedPreferencesManger;
    JSONObject resultObject;
    CameraPosition cameraPosition;
    String[] snippet;
    static int count=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_fragment, container, false);
        mContext = getActivity();
        mainActivity = (HomeActivity) mContext;
        mainActivity.llCatSearch.setVisibility(View.GONE);
        mainActivity.tvTitle.setText(getResources().getString(R.string.salenear_you));
        loginType = sharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        if (loginType.isEmpty()) {
            mainActivity.ivLogin.setVisibility(View.VISIBLE);
            mainActivity.ivBack.setVisibility(View.VISIBLE);
            mainActivity.ivLogo.setVisibility(View.INVISIBLE);
            mainActivity.tvTitle.setVisibility(View.VISIBLE);
        } else {
            mainActivity.tvTitle.setVisibility(View.VISIBLE);
            mainActivity.ivLogo.setVisibility(View.INVISIBLE);
            mainActivity.ivLogin.setVisibility(View.INVISIBLE);
            mainActivity.ivBack.setVisibility(View.INVISIBLE);
            mainActivity.ivUserRequest.setVisibility(View.VISIBLE);
            mainActivity.ivUserProfile.setVisibility(View.VISIBLE);
        }
        mainActivity.mapTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.view_color));
        mainActivity.mapTab.setTextColor(ContextCompat.getColor(mContext, R.color.fav_toolbar));
        mainActivity.mapTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.map_active, 0, 0);

        mainActivity.homeTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.homeTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.homeTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.home_grey, 0, 0);

        mainActivity.favTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.favTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.favTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.favourite, 0, 0);

        mainActivity.popularTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.popularTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity. popularTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.popular, 0, 0);

        mainActivity.closestTab.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_spacing_color));
        mainActivity.closestTab.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        mainActivity.closestTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.closest, 0, 0);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        gpsTracker();
        mapApi();

        try {
            MapsInitializer.initialize(mContext.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mMapView.getMap();
        return view;
    }

    public void gpsTracker() {
        gps = new GPSTracker(mContext);

      if (gps.canGetLocation()) {

        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

      } else {
           // gps.showSettingsAlert();
       enableGps();
        }

    }
    public void mapApi() {
        strLatitude = Double.toString(latitude);
        strLongitude = Double.toString(longitude);

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "mapStores");
                jsonObject.put("latitude",strLatitude);
                jsonObject.put("longitude",strLongitude);
                param.put("json_data", jsonObject.toString());
                System.out.println("Request: " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.MAP_LIST, this);
                callWebService.execute();
            } else {
                alert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        System.out.println("<<<<<<<<<RESULT>>>>>>>>>>" + result);

        switch (type) {
            case MAP_LIST:
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        JSONArray jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "data");
                        LatLng storeInfo;
                        double storeLatitude, storeLongitude;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            resultObject = jsonArray.getJSONObject(i);
                            storeLatitude = Double.parseDouble(resultObject.getString("storeLat"));
                            storeLongitude = Double.parseDouble(resultObject.getString("storeLong"));
                            if (storeLatitude != 0.00 && storeLongitude != 0.00) {
                                try {
                                    storeInfo = new LatLng(storeLatitude, storeLongitude);
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(storeInfo)
                                            .title(resultObject.getString("storeName")+"-"+resultObject.getString("storeId"))
                                            .snippet(resultObject.getString("storeAddress"))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                                    cameraPosition = new CameraPosition.Builder().target(new LatLng(storeLatitude, storeLongitude)).zoom(5).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                        @Override
                                        public View getInfoWindow(Marker marker) {
                                            return null;
                                        }

                                        @Override
                                        public View getInfoContents(Marker marker) {
                                            View v = getActivity().getLayoutInflater().inflate(R.layout.info_windowgooglemap, null);

                                            try {
                                                TextView tvCname = (TextView) v.findViewById(R.id.tv_cname);
                                                TextView tvCAdress = (TextView) v.findViewById(R.id.tv_caddress);
                                                String text = marker.getTitle();
                                                 snippet = text.split("-");
                                                tvCname.setText(snippet[0]);
                                                tvCAdress.setText(marker.getSnippet());
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            return v;
                                        }
                                    });
                                    googleMap.addMarker(markerOptions);
                                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker arg0) {
                                            count++;
                                    /*        if(count==1){

                                            }
                                            //double click
                                            if(count==2){
                                                Intent iSaleDetails = new Intent(mContext, SaleDetails_Listview.class);
                                                iSaleDetails.putExtra("saleId", snippet[1]);
                                                getActivity().startActivity(iSaleDetails);
                                                getActivity().overridePendingTransition(0, R.anim.exit_slide_right);
                                                count=0;
                                            }*/
                                            Intent iSaleDetails = new Intent(mContext, SaleDetails_Listview.class);
                                            iSaleDetails.putExtra("saleId", snippet[1]);
                                            getActivity().startActivity(iSaleDetails);
                                            getActivity().overridePendingTransition(0, R.anim.exit_slide_right);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } else {
                        CommonUtils.showToast(getActivity(), JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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
