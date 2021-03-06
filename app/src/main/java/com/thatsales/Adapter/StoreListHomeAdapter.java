package com.thatsales.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thatsales.Model.StoreListModel;
import com.thatsales.R;

import java.util.ArrayList;

public class StoreListHomeAdapter extends BaseAdapter {

    Context mContext;
    StoreListModel model;
    LayoutInflater inflater;
    ArrayList<StoreListModel> storeListModelArrayList;

    public StoreListHomeAdapter(Context mContext){
        this.mContext = mContext;
        storeListModelArrayList = new ArrayList<>();
    }
    public void clear(){
        storeListModelArrayList.clear();
    }

    public void addToArrayList(StoreListModel model){
        storeListModelArrayList.add(model);
    }

    @Override
    public StoreListModel getItem(int position) {
        return storeListModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return storeListModelArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.listitem_storelist, parent, false);
        holder.tvStoreList = (TextView) convertView.findViewById(R.id.tv_storelist);

        if(storeListModelArrayList.size()>0){
             model = storeListModelArrayList.get(position);
            holder.tvStoreList.setText(model.getName());
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvStoreList;
    }
}
