package com.thatsales.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.thatsales.ImageLoding.ImageLoader;
import com.thatsales.R;

import java.util.ArrayList;

/**
 * Created by vinove 29/08/15.
 */
public class SlidingImage_Adapter extends PagerAdapter {

    ArrayList<String> arrayListImages;
    LayoutInflater inflater;
    Context mContext;
    ImageLoader imgL;


    public SlidingImage_Adapter(Context context, ArrayList<String> arrayListImages) {
        this.mContext = context;
        this.arrayListImages = arrayListImages;
       // imgL = new ImageLoader(context);

    }

    @Override
    public int getCount() {
        return arrayListImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.slidingimages_layout, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.image1);
        arrayListImages.get(position);

       // imgL.DisplayImage(arrayListImages.get(position), imageView);
        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.not_image)
                .showImageOnFail(R.drawable.not_image).build();
               // .showImageOnLoading(R.drawable.not_image).build();

        imageLoader.displayImage(arrayListImages.get(position),imageView, options);

        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}