package com.thatsales.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.thatsales.DetailedSaleScreenActivity;
import com.thatsales.Fragment.ClosetFragment;
import com.thatsales.Fragment.FavouriteFragment;
import com.thatsales.HomeActivity;
import com.thatsales.ImageLoding.ImageLoader;
import com.thatsales.Listener.OnFragmentChangeListener;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Model.HomeModel;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.R;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.JSONUtils;
import com.thatsales.Utils.WebServiceApis;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;


/**
 * Adapter class of home
 * Created by Jawed on 11/12/15.
 */
public class HomeAdapter extends BaseAdapter implements OnWebServiceResult {

    Context mContext;
    ImageLoader imgL;
    LayoutInflater inflater;
    ArrayList<HomeModel> homeListModelArrayList = new ArrayList<>();
    ArrayList<String> imageArray = new ArrayList<>();
    HomeModel model;
    CallWebService callWebService;
    String strIsFav, loginType;
    int currentPosition;
    SharedPreferencesManger sharedPreferencesManger;
    FrameLayout frameLayout;
    HomeAdapter adapter;


    public HomeAdapter(Context mContext) {
        this.mContext = mContext;
        homeListModelArrayList = new ArrayList<>();
    }

    public void clear() {
        homeListModelArrayList.clear();
    }

    public Boolean hasArrayItems() {
        if (homeListModelArrayList.size() > 0) {
            return true;

        }
        return false;
    }

    public HomeAdapter(Context mContext, ArrayList<HomeModel> homeListModelArrayList) {
        this.homeListModelArrayList = homeListModelArrayList;
        this.mContext = mContext;
    }

    public void addToArrayList(HomeModel model) {
        homeListModelArrayList.add(model);
    }


    @Override
    public int getCount() {
        return homeListModelArrayList.size();
    }

    @Override
    public HomeModel getItem(int position) {
        return homeListModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.row_home, parent, false);
        holder.tvSaleName = (TextView) convertView.findViewById(R.id.tv_getdiscount_txt);
        holder.tvCName = (TextView) convertView.findViewById(R.id.tv_cname);
        holder.ivSalePic = (ImageView) convertView.findViewById(R.id.iv_sale_images);
        holder.llDetailed = (LinearLayout) convertView.findViewById(R.id.ll_detailedinfo);
        holder.llBookmark = (LinearLayout) convertView.findViewById(R.id.ll_bookmark);
        holder.tvFav = (TextView) convertView.findViewById(R.id.tv_fav);
        holder.ivFav = (ImageView) convertView.findViewById(R.id.iv_isfav);
        holder.llDetailed.setTag(position);
        holder.llBookmark.setTag(position);
        holder.ivSalePic.setTag(position);
        holder.tvSaleName.setTag(position);

        model = homeListModelArrayList.get(position);
        holder.tvSaleName.setText(model.getSaleName());
        holder.tvCName.setText(model.getCompneyName());
/*
        if (model.getImage() != null) {
            imgL = new ImageLoader(mContext);
            imgL.DisplayImage(model.getImage(), holder.ivSalePic);
        } else {
            holder.ivSalePic.setImageResource(R.drawable.not_image);
        }*/

        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.not_image)
                .showImageOnFail(R.drawable.not_image).build();

            imageLoader.displayImage(model.getImage(), holder.ivSalePic, options);



        if (model.getIsFav().equals("1")) {
            holder.ivFav.setImageResource(R.drawable.bookmark);
            holder.tvFav.setText(R.string.bookmarked);
        } else {
            holder.ivFav.setImageResource(R.drawable.bookmarked_off);//change to icon colorless heart
        }


        holder.tvSaleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = (Integer) v.getTag();
                Intent iSaleName = new Intent(mContext, DetailedSaleScreenActivity.class);
                iSaleName.putExtra("saleId", homeListModelArrayList.get(count).getSaleId());
                mContext.startActivity(iSaleName);
            }
        });


        holder.ivSalePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = (Integer) v.getTag();
                Intent iDetail = new Intent(mContext, DetailedSaleScreenActivity.class);
                iDetail.putExtra("saleId", homeListModelArrayList.get(count).getSaleId());
                mContext.startActivity(iDetail);
            }
        });


        holder.llDetailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = (Integer) v.getTag();
         /*       try {
                    for (int i = 0; i < homeListModelArrayList.get(count).getJsonArray().length(); i++) {
                        imageArray.add(homeListModelArrayList.get(count).getJsonArray().getString(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                Intent iDetail = new Intent(mContext, DetailedSaleScreenActivity.class);
                iDetail.putExtra("saleId", homeListModelArrayList.get(count).getSaleId());
      /*          iDetail.putExtra("saleName", homeListModelArrayList.get(count).getSaleName());
                iDetail.putExtra("companyName", homeListModelArrayList.get(count).getCompneyName());
                iDetail.putExtra("description", homeListModelArrayList.get(count).getDescription());
                iDetail.putExtra("contactNumber", homeListModelArrayList.get(count).getContactNo());
                iDetail.putExtra("address", homeListModelArrayList.get(count).getAddress());
                iDetail.putExtra("latitude", homeListModelArrayList.get(count).getLatitude());
                iDetail.putExtra("longitude", homeListModelArrayList.get(count).getLongitude());
                iDetail.putExtra("startDate", homeListModelArrayList.get(count).getStartDate());
                iDetail.putExtra("endDate", homeListModelArrayList.get(count).getEndDate());
                iDetail.putExtra("isFav", homeListModelArrayList.get(count).getIsFav());
                iDetail.putStringArrayListExtra("arrayList", imageArray);*/
                mContext.startActivity(iDetail);
            }
        });

        holder.llBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginType = sharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
                int count = (Integer) v.getTag();
                currentPosition = count;
                if (loginType.isEmpty()) {
                    Toast.makeText(mContext, R.string.loginfirst, Toast.LENGTH_LONG).show();

                } else {
                    if (homeListModelArrayList.get(count).getIsFav().equals("1")) {
                        strIsFav = "0";
                    } else {
                        strIsFav = "1";
                    }
                    isFavApi(homeListModelArrayList.get(count).getSaleId());
                }

            }
        });
        convertView.setTag(holder);
        return convertView;
    }

    public void isFavApi(String salesId) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "setFav");
                jsonObject.put("saleId", salesId);
                jsonObject.put("catId", "");
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("setFav", strIsFav);
                param.put("json_data", jsonObject.toString());
                System.out.println("Request: " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.ISFAVHOME, this);
                callWebService.execute();
            } else {
                CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.internetConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type) {
            case ISFAVHOME:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
//                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject,                          "message"));
                        homeListModelArrayList.get(currentPosition).setIsFav(strIsFav);
                        notifyDataSetChanged();
                    } else {

//                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject,"message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }


    class ViewHolder {
        public TextView tvSaleName, tvCName, tvFav;
        public ImageView ivSalePic, ivFav;
        public LinearLayout llDetailed, llBookmark;
    }


}
