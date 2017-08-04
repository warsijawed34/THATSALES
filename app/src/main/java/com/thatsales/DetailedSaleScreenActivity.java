package com.thatsales;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.thatsales.Adapter.SlidingImage_Adapter;
import com.thatsales.ImageLoding.ImageViewRounded;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.Utils.GMapV2GetRouteDirection;
import com.thatsales.Utils.GPSTracker;
import com.thatsales.Utils.JSONUtils;
import com.thatsales.Utils.WebServiceApis;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;


public class DetailedSaleScreenActivity extends BaseActivity implements View.OnClickListener, OnWebServiceResult {

    private double user_latitude, provider_latitude;
    private double user_longitude, provider_longitude;
    private int i;
    Document document;
    GMapV2GetRouteDirection v2GetRouteDirection;
    LatLng provider_position, user_position;
    GoogleMap mGoogleMap;
    GPSTracker gpsTracker;
    MarkerOptions userMarker, providerMarker;
    SupportMapFragment supportMapFragment;
    TextView textView, readMore, readLess, tvDescription, tvSaleName, tvCName, tvAddress, tvContactNumber, tvStartDate, tvEndDate;
    Context mContext;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    ImageView iBack, ivLogout, ivFav, ivShare, ivCheckIn;
    TextView tvTitle;
    ArrayList<String> imageArrayList = new ArrayList<>();
    CallWebService callWebService;
    String strIsFav,loginType;
    SharedPreferencesManger sharedPreferencesManger;
    ScrollView iScroll;
    Boolean change = true;
    private GoogleApiClient client;
    DateFormat formatter = null;
    Date convertedDate = null;
    SlidingImage_Adapter pagerAdater;
    String saleId, saleName, companyName, description, contactNumber, address, latitude,
            longitude, startDate, endDate, isFav, isCheck, count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_sale);
        mContext = DetailedSaleScreenActivity.this;
        getView();
        Intent intent = getIntent();
        saleId = intent.getStringExtra("saleId");
        saleDetails(saleId);
        googleMap(provider_latitude, provider_longitude);


    }
    private void getView() {
        tvStartDate = (TextView) findViewById(R.id.tv_startdate);
        tvEndDate = (TextView) findViewById(R.id.tv_enddate);
        tvSaleName = (TextView) findViewById(R.id.tv_getflat);
        tvCName = (TextView) findViewById(R.id.tv_jack_jones);
        tvAddress = (TextView) findViewById(R.id.tv_3rd_floor);
        ivFav = (ImageView) findViewById(R.id.btn_wishlist_active);
        ivShare = (ImageView) findViewById(R.id.btn_share);
        iBack = (ImageView) findViewById(R.id.iv_back);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        ivCheckIn = (ImageView) findViewById(R.id.btn_check_in);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContactNumber = (TextView) findViewById(R.id.tv_mobile_no);
        readLess = (TextView) findViewById(R.id.tv_read_less);
        readMore = (TextView) findViewById(R.id.tv_read_more);
        tvDescription = (TextView) findViewById(R.id.tv_text_messsage);
        tvTitle.setText(getString(R.string.detailed_sale_screen1));
        ivLogout.setImageResource(R.drawable.req_sale);
        iScroll = (ScrollView) findViewById(R.id.scroll);

        ivFav.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        iBack.setOnClickListener(this);
        ivCheckIn.setOnClickListener(this);
        ivLogout.setOnClickListener(this);
        readMore.setOnClickListener(this);
        readLess.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wishlist_active:
                loginType = sharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
                if (loginType.isEmpty()) {
                    Toast.makeText(mContext, R.string.loginfirst, Toast.LENGTH_LONG).show();

                } else {
                    if (isFav.equals("1")) {
                        isFav = "0";
                    } else {
                        isFav = "1";
                    }
                    isFavApi();
                }
                break;

            case R.id.btn_check_in:
                if (change == true) {
                    ivCheckIn.setImageResource(R.drawable.check_in_active);
                    iScroll.fullScroll(View.FOCUS_DOWN);
                    change = false;
                } else {
                    ivCheckIn.setImageResource(R.drawable.check_in);
                    change = true;
                }
                break;

            case R.id.btn_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.share) +Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName()) +getString(R.string.share1)) );
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_logout:
                Intent ireq = new Intent(mContext, RequestForSaleActivity.class);
                startActivity(ireq);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.tv_read_more:
                readMore.setVisibility(View.INVISIBLE);
                readLess.setVisibility(View.VISIBLE);
                tvDescription.setMaxLines(Integer.MAX_VALUE);
                break;
            case R.id.tv_read_less:
                readLess.setVisibility(View.INVISIBLE);
                readMore.setVisibility(View.VISIBLE);
                tvDescription.setMaxLines(4);
                break;
        }
    }
    public void saleDetails(String id) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "saleDetail");
                jsonObject.put("saleId", id);
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                params.put("json_data", jsonObject.toString());
                System.out.println("Request: " + params);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.NOTIS, this);
                callWebService.execute();
            } else {
                Toast.makeText(this, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void isFavApi() {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "setFav");
                jsonObject.put("saleId", saleId);
                jsonObject.put("catId", "");
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("setFav", isFav);
                param.put("json_data", jsonObject.toString());
                System.out.println("Request: " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.ISFAVDETAILED, this);
                callWebService.execute();
            } else {
                Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type) {
            case ISFAVDETAILED:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        //CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject,"message"));
                        if (isFav.equals("1")) {
                            ivFav.setImageResource(R.drawable.heartfilled);
                        } else {
                            ivFav.setImageResource(R.drawable.wishlist);
                        }

                    } else {

                        // CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject,"message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case NOTIS:
                try {
                    System.out.println("result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    // String message = JSONUtils.getStringFromJSON(jsonObject, "message");
                    if (code == 200) {
                        saleId = JSONUtils.getStringFromJSON(jsonObject, "saleId");
                        saleName = JSONUtils.getStringFromJSON(jsonObject, "saleName");
                        companyName = JSONUtils.getStringFromJSON(jsonObject, "compneyName");
                        description = JSONUtils.getStringFromJSON(jsonObject, "description");
                        contactNumber = JSONUtils.getStringFromJSON(jsonObject, "contactNo");
                        address = JSONUtils.getStringFromJSON(jsonObject, "address");
                        latitude = JSONUtils.getStringFromJSON(jsonObject, "latitude");
                        longitude = JSONUtils.getStringFromJSON(jsonObject, "longitude");
                        startDate = JSONUtils.getStringFromJSON(jsonObject, "startDate");
                        endDate = JSONUtils.getStringFromJSON(jsonObject, "endDate");
                        isFav = JSONUtils.getStringFromJSON(jsonObject, "isFav");
                        JSONArray imageArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "images");

                        for (int i = 0; i < imageArray.length(); i++) {
                            imageArrayList.add(imageArray.getString(i));
                        }
                        pagerSlider(imageArrayList);
                        tvSaleName.setText(saleName);
                        tvCName.setText(companyName);
                        tvAddress.setText(address);
                        tvDescription.setText(description);
                        if (description.length() > 50) {
                            readMore.setVisibility(View.VISIBLE);
                        } else {
                            readMore.setVisibility(View.INVISIBLE);
                        }
                        tvContactNumber.setText(contactNumber);
                        startEndDate();
                        provider_latitude = Double.parseDouble(latitude);
                        provider_longitude = Double.parseDouble(longitude);
                        provider_position = new LatLng(provider_latitude, provider_longitude);
                        if (isFav.equals("1")) {
                            ivFav.setImageResource(R.drawable.heartfilled);
                        } else {
                            ivFav.setImageResource(R.drawable.wishlist);
                        }

                    } else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    public void pagerSlider(final ArrayList<String> imageArrayList ) {

        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdater = new SlidingImage_Adapter(mContext, imageArrayList);
        mPager.setAdapter(pagerAdater);
        pagerAdater.notifyDataSetChanged();
//        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
//        indicator.setViewPager(mPager);

        final LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dotLayout);

        for(int i = 0; i < imageArrayList.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(15,15);
            lp.setMargins(2, 0, 2, 0);
            imageView.setLayoutParams(lp);
            if(i == 0){
                imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_round_fill));
            }else {
                imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_round_border));
            }
            dotLayout.addView(imageView);
        }

        final float density = getResources().getDisplayMetrics().density;
//        indicator.setRadius(5 * density);
        NUM_PAGES = imageArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                int count = dotLayout.getChildCount();
                ImageView view = null;
                for(int i=0; i<count; i++) {
                    view = (ImageView) dotLayout.getChildAt(i);
                    //do something with your child element
                    if(i == position){
                        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_round_fill));
                    }else {
                        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_round_border));
                    }
                }



            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int position) {
//                currentPage = position;
//            }
//
//            @Override
//            public void onPageScrolled(int pos, float arg1, int arg2) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int pos) {
//            }
//        });
    }

    public void startEndDate() {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd MMM");
        try {
            Date oneWayTripDate = input.parse(startDate);
            tvStartDate.setText(output.format(oneWayTripDate));
         }catch (ParseException e) {
            e.printStackTrace();
         }

        try {
            Date oneWayTripDate = input.parse(endDate);
            tvEndDate.setText("- " + output.format(oneWayTripDate));
          } catch (ParseException e) {
            e.printStackTrace();
        }
    }
       public  void googleMap(double provider_latitude, double  provider_longitude){
           supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
           mGoogleMap = supportMapFragment.getMap();
           v2GetRouteDirection = new GMapV2GetRouteDirection();
           gpsTracker = new GPSTracker(this);
           userMarker = new MarkerOptions();
           providerMarker = new MarkerOptions();
           mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
           mGoogleMap.getUiSettings().setCompassEnabled(true);
           mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
           mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
           mGoogleMap.setTrafficEnabled(true);

           if (gpsTracker.canGetLocation()) {
               user_latitude = gpsTracker.getLatitude();
               user_longitude = gpsTracker.getLongitude();

           }else {
               enableGps();
           }
               provider_position = new LatLng(provider_latitude, provider_longitude);


           user_position = new LatLng(user_latitude, user_longitude);
           GetRouteTask getRoute = new GetRouteTask();
           getRoute.execute();
           client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
       }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DetailedSaleScreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.thatsales/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }
    //map start
    private class GetRouteTask extends AsyncTask<String, Void, String> {

        private ProgressDialog Dialog;
        String response = "";

        @Override
        protected void onPreExecute() {
            Dialog = new ProgressDialog(DetailedSaleScreenActivity.this);
            Dialog.setMessage("Loading route...");
            Dialog.show();
        }
        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            document = v2GetRouteDirection.getDocument(provider_position, user_position, GMapV2GetRouteDirection.MODE_WALKING);
            response = "Success";
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            mGoogleMap.clear();
            if (response.equalsIgnoreCase("Success")) {

                final ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                PolylineOptions rectLine = new PolylineOptions().width(5).color(getResources().getColor(R.color.polyline_color));
                for (i = 0; i < directionPoint.size(); i++) {

                    rectLine.add(directionPoint.get(i));
                }
                mGoogleMap.addPolyline(rectLine);


               // userMarker.position(user_position).title("user position ");
                userMarker.position(user_position);
                userMarker.draggable(true);
                BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                userMarker.icon(icon);
                mGoogleMap.addMarker(userMarker);

             //   providerMarker.position(provider_position).title("provider position");
                providerMarker.position(provider_position);
                providerMarker.draggable(true);
                BitmapDescriptor icon2 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                providerMarker.icon(icon2);
                mGoogleMap.addMarker(providerMarker);

                double s1 = user_latitude + provider_latitude;
                double s2 = user_longitude + provider_longitude;
                double s3 = s1 / 2;
                double s4 = s2 / 2;

                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(s3, s4)).zoom(5).build();
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            Dialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DetailedSaleScreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.thatsales/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        finish();
        client.disconnect();
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
                       // startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);;
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No",  new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}