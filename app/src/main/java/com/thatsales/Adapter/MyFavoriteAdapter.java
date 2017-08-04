package com.thatsales.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Model.MyFavoritesModel;
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
 * Adapter class of Myfavorites
 * Created by Jawed on 11/12/15.
 */

public class MyFavoriteAdapter extends BaseAdapter implements OnWebServiceResult {

    Context mContext;
    CallWebService callWebService;
    LayoutInflater inflater;
    ArrayList<MyFavoritesModel> favListModelArrayList = new ArrayList<>();
    MyFavoritesModel model;
    String strIsFav;
    int currentPosition;


    public MyFavoriteAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void clear(){
        favListModelArrayList.clear();
    }
    public void addToArrayList(MyFavoritesModel model) {
        favListModelArrayList.add(model);
    }

    @Override
    public int getCount() {
        return favListModelArrayList.size();
    }

    @Override
    public MyFavoritesModel getItem(int position) {
        return favListModelArrayList.get(position);
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
        convertView = inflater.inflate(R.layout.row_favorite, parent, false);

        holder.tvSaleName = (TextView) convertView.findViewById(R.id.tv_pumasale);
        holder.tvSaleDesc = (TextView) convertView.findViewById(R.id.tv_offoncasualshoes);
        holder.ivfav = (ImageView) convertView.findViewById(R.id.iv_favheart);
        holder.ivfav.setTag(position);


          model = favListModelArrayList.get(position);

     //   holder.tvSaleName.setText(model.getStrSaleName());
      //  holder.tvSaleDesc.setText(model.getStrSaleDesc());
      holder.tvSaleName.setText(model.getStrSaleDesc());
        holder.tvSaleDesc.setText(model.getStrSaleName());
        if(model.getIsFav().equals("1")){
            holder.ivfav.setImageResource(R.drawable.fav_on);

        }else{
            holder.ivfav.setImageResource(R.drawable.fav_off);
        }

        holder.ivfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = (Integer)v.getTag();
                currentPosition = count;
                if(favListModelArrayList.get(count).getIsFav().equals("1")){
                    strIsFav = "0";
                }else{
                    strIsFav = "1";
                }
                isFavApi(favListModelArrayList.get(count).getSaleID());
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
                callWebService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), param, Constants.SERVICE_TYPE.ISFAVMYFAV, this);
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
            case ISFAVMYFAV:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                     //   CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject,                         "message"));
                        favListModelArrayList.get(currentPosition).setIsFav(strIsFav);
                        notifyDataSetChanged();
                    } else {

                    //    CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject,"message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    class ViewHolder {
        public TextView tvSaleName, tvSaleDesc;
        public ImageView ivfav;


    }
}
