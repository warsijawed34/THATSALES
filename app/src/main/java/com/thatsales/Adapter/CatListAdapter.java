package com.thatsales.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.thatsales.Model.CategoryLIstModel;
import com.thatsales.R;

import java.util.ArrayList;

public class CatListAdapter extends BaseAdapter {

    Context mContext;
    String tag;
    CategoryLIstModel model;
    LayoutInflater inflater;
    ArrayList<CategoryLIstModel> catListModelArrayList;

    public CatListAdapter(Context mContext){
        this.mContext = mContext;
        catListModelArrayList = new ArrayList<CategoryLIstModel>();
    }

    public void addToArrayList(CategoryLIstModel model){
        catListModelArrayList.add(model);
    }

    public void clear(){
        catListModelArrayList.clear();
    }

    @Override
    public CategoryLIstModel getItem(int position) {
        return catListModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return catListModelArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        holder = new ViewHolder();
        convertView = inflater.inflate(R.layout.list_item, parent, false);
        holder.tvCatList = (TextView) convertView.findViewById(R.id.rowTextView);

        if(catListModelArrayList.size()>0) {
            model = catListModelArrayList.get(position);
            holder.tvCatList.setText(model.getName());
        }



        return convertView;
    }

    class ViewHolder {
        TextView tvCatList;
    }
}
