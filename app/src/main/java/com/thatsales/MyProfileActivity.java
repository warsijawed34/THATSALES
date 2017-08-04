package com.thatsales;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thatsales.ImageLoding.ImageLoader;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.Constants;
import com.thatsales.Pereferences.SharedPreferencesManger;

/**
 * Created by vinove on 8/6/16.
 */
public class MyProfileActivity extends BaseActivity implements View.OnClickListener {
    Context mContext;
    TextView tvMyFavStore,tvMyInterest,tvUpdateProfile,tvRating,tvFeedback;
    ImageView ivBack,ivLogout, ivProfile;
    TextView tvTitle, tvUserName,tvEmailId,tvNumber, tvLogout;
    String strName,strNumber, strEmail, data, strImage, PackageName;
    ImageLoader imgL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mContext = MyProfileActivity.this;
        getView();
        strName=SharedPreferencesManger.getPrefValue(mContext, Constants.USERNAME, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        strNumber=SharedPreferencesManger.getPrefValue(mContext, Constants.MOBILENUMBER, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        strEmail=SharedPreferencesManger.getPrefValue(mContext, Constants.EMAILID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        tvUserName.setText(strName);
        tvNumber.setText(strNumber);
        tvEmailId.setText(strEmail);

        strImage=SharedPreferencesManger.getPrefValue(mContext, Constants.IMAGES, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        if (!strImage.isEmpty()){
            imgL = new ImageLoader(mContext);
            imgL.DisplayImage(strImage, ivProfile);
        }else {
            ivProfile.setImageResource(R.drawable.user_1);
        }

    }

    private void getView() {
        ivProfile= (ImageView) findViewById(R.id.iv_profile);
        tvMyFavStore= (TextView) findViewById(R.id.tv_my_favorite_stoes);
        tvMyInterest= (TextView) findViewById(R.id.tv_my_interested);
        tvUpdateProfile= (TextView) findViewById(R.id.tv_update_profile);
        tvRating= (TextView) findViewById(R.id.tv_app_rating);
        tvFeedback= (TextView) findViewById(R.id.tv_feedback);
        ivBack= (ImageView) findViewById(R.id.iv_back);
        ivLogout= (ImageView) findViewById(R.id.iv_logout);
        tvTitle= (TextView) findViewById(R.id.tv_title);
        tvUserName= (TextView) findViewById(R.id.tv_user_name);
        tvEmailId= (TextView) findViewById(R.id.tv_emailid);
        tvNumber= (TextView) findViewById(R.id.tv_number);
        tvLogout= (TextView) findViewById(R.id.tv_rightTittle);
        ivLogout.setVisibility(View.INVISIBLE);
        tvLogout.setVisibility(View.VISIBLE);
        tvLogout.setText(getString(R.string.logout));
        tvTitle.setText(getString(R.string.my_profile));
        tvMyFavStore.setOnClickListener(this);
        tvMyInterest.setOnClickListener(this);
        tvUpdateProfile.setOnClickListener(this);
        tvRating.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        super.onBackPressed();
        this.finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_my_favorite_stoes:
                Intent iFav=new Intent(MyProfileActivity.this,MyFavoriteSalesActivity.class);
                startActivity(iFav);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.tv_my_interested:
                Intent iInterested=new Intent(MyProfileActivity.this,MyInterestActivity.class);
                startActivity(iInterested);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.tv_update_profile:
                Intent iUpdate = new Intent(MyProfileActivity.this,UpdateProfileActivity.class);
                iUpdate.putExtra("userName",strName);
                iUpdate.putExtra("userEmail",strEmail);
                iUpdate.putExtra("userNumber", strNumber);
                startActivity(iUpdate);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.tv_feedback:
                Intent iFeed=new Intent(MyProfileActivity.this,FeedBackActivity.class);
                startActivity(iFeed);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.tv_app_rating:
           /*     Intent iRate=new Intent(MyProfileActivity.this,RatingActivity.class);
                startActivity(iRate);
                overridePendingTransition(0, R.anim.exit_slide_right);*/

                //giving rating in play store market..
                //rating();
                rating();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_rightTittle:
                SharedPreferencesManger.removeAllPrefValue(mContext);
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                finish();
                break;

        }
    }

    private void rating() {


        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            mContext.startActivity(goToMarket);
            ((Activity) mContext).overridePendingTransition(0, R.anim.exit_slide_right);
        } catch (ActivityNotFoundException e) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                public void run() {
                    CommonUtils.showToast(mContext, "Could not open Android market, please install the market app.");
                }
            });
        }
   /*     try {
            PackageName = getApplicationContext().getPackageName();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + PackageName)));
        }*/
    }
}
