package com.thatsales.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thatsales.DetailedSaleScreenActivity;
//import com.thatsales.ImageLoding.ImageLoader;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Model.HomeModel;
import com.thatsales.Model.SaleDetails;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.R;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.Constants;
import com.thatsales.Utils.JSONUtils;
import com.thatsales.Utils.WebServiceApis;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * Adapter class of home
 * Created by Jawed on 11/12/15.
 */
public class SaleDetailsAdapter extends BaseAdapter implements OnWebServiceResult{

    Context mContext;
    ImageLoader imgL;
    LayoutInflater inflater;
    ArrayList<SaleDetails> saleListModelArrayList = new ArrayList<>();
    ArrayList<String> imageArray = new ArrayList<>();
    SaleDetails model;
    CallWebService callWebService;
    String strIsFav, loginType;
    int currentPosition;
    SharedPreferencesManger sharedPreferencesManger;
    FrameLayout frameLayout;
    SaleDetailsAdapter adapter;
     String profileImage;

    public SaleDetailsAdapter(Context mContext) {
        this.mContext = mContext;
        saleListModelArrayList = new ArrayList<>();
    }

    public void clear(){
        saleListModelArrayList.clear();
    }

    public SaleDetailsAdapter(Context mContext, ArrayList<SaleDetails> listModelArrayList) {
        this.saleListModelArrayList=listModelArrayList;
        this.mContext = mContext;
    }

    public void addToArrayList(SaleDetails model) {
        saleListModelArrayList.add(model);
    }


    @Override
    public int getCount() {
        return saleListModelArrayList.size();
    }

    @Override
    public SaleDetails getItem(int position) {
        return saleListModelArrayList.get(position);
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

        model = saleListModelArrayList.get(position);
        holder.tvSaleName.setText(model.getSaleName());
        holder.tvCName.setText(model.getCompneyName());

        //image loader
         ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.not_image)
                    .showImageOnFail(R.drawable.not_image).build();

            imageLoader.displayImage(model.getImage(), holder.ivSalePic, options);
        //image loader




        if (model.getIsFav().equals("1")) {
            holder.ivFav.setImageResource(R.drawable.bookmark);
            holder.tvFav.setText(R.string.bookmarked);
        } else {
            holder.ivFav.setImageResource(R.drawable.bookmarked_off);
        }


        holder.tvSaleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = (Integer) v.getTag();
                Intent iSalename = new Intent(mContext, DetailedSaleScreenActivity.class);
                iSalename.putExtra("saleId", saleListModelArrayList.get(count).getSaleId());
                mContext.startActivity(iSalename);
            }
        });


        holder.ivSalePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = (Integer) v.getTag();
                Intent iDetail = new Intent(mContext, DetailedSaleScreenActivity.class);
                iDetail.putExtra("saleId", saleListModelArrayList.get(count).getSaleId());
                mContext.startActivity(iDetail);
            }
        });


        holder.llDetailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = (Integer) v.getTag();
                Intent iDetail = new Intent(mContext, DetailedSaleScreenActivity.class);
                iDetail.putExtra("saleId", saleListModelArrayList.get(count).getSaleId());;
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
                    if (saleListModelArrayList.get(count).getIsFav().equals("1")) {
                        strIsFav = "0";
                    } else {
                        strIsFav = "1";
                    }
                    isFavApi(saleListModelArrayList.get(count).getSaleId());
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
                        saleListModelArrayList.get(currentPosition).setIsFav(strIsFav);
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
        public TextView tvSaleName, tvCName,tvFav;
        public ImageView ivSalePic, ivFav;
        public LinearLayout llDetailed, llBookmark;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
