package com.thatsales.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.thatsales.ImageLoding.ImageLoader;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Model.CategoryLIstModel;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.R;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.JSONUtils;
import com.thatsales.Utils.WebServiceApis;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * Adapter class of My interests
 * Created by Jawed on 11/12/15.
 */

public class MyInterestAdapter extends BaseAdapter implements OnWebServiceResult {

    Context mContext;
    ImageLoader imgL;
    LayoutInflater inflater;
    ArrayList<CategoryLIstModel> interestListModelArrayList = new ArrayList<CategoryLIstModel>();
    CategoryLIstModel model;
    CallWebService callWebService;
    String strIsFav;
    int currentPosition;

    public MyInterestAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void clear(){
        interestListModelArrayList.clear();
    }

    public Boolean hasArrayItems() {
        if (interestListModelArrayList.size() > 0) {
            return true;

        }
        return false;
    }
    public void addToArrayList(CategoryLIstModel model) {
        interestListModelArrayList.add(model);
    }

    @Override
    public int getCount() {
        return interestListModelArrayList.size();
    }

    @Override
    public CategoryLIstModel getItem(int position) {
        return interestListModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.row_interests, parent, false);
        holder.tvSaleName = (TextView) convertView.findViewById(R.id.tv_interest_productname);
        holder.ivSale = (ImageView) convertView.findViewById(R.id.iv_sale_interest);
        holder.ivFav = (ImageView) convertView.findViewById(R.id.iv_interestheart);
        holder.ivFav.setTag(position);
        model = interestListModelArrayList.get(position);

       holder.tvSaleName.setText(model.getName());

        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.not_image)
                .showImageOnFail(R.drawable.not_image).build();

        imageLoader.displayImage(model.getImage(), holder.ivSale, options);

        if (model.getFav().equals("1")) {
            holder.ivFav.setImageResource(R.drawable.fav_on);

        } else {
            holder.ivFav.setImageResource(R.drawable.fav_off);
        }


        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = (Integer) v.getTag();
                currentPosition = count;
                if (interestListModelArrayList.get(count).getFav().equals("1")) {
                    strIsFav = "0";
                } else {
                    strIsFav = "1";
                }
                isFavApi(interestListModelArrayList.get(count).getId());
            }
        });
        convertView.setTag(holder);
       // notifyDataSetChanged();
        return convertView;
    }

    public void isFavApi(String catId) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> param = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "setFav");
                jsonObject.put("saleId", "");
                jsonObject.put("catId", catId);
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                jsonObject.put("setFav", strIsFav);
                param.put("json_data", jsonObject.toString());
                System.out.println("Request: " + param);
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.ISFAVMYINTEREST, this);
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
            case ISFAVMYINTEREST:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
//                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject,"message"));
                        interestListModelArrayList.get(currentPosition).setFav(strIsFav);
                        notifyDataSetChanged();
                    } else {

//                       CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject,"message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    class ViewHolder {
        public TextView tvSaleName;
        public ImageView ivSale,ivFav;


    }

  //add new ImageLoader....and verify by Mirza
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