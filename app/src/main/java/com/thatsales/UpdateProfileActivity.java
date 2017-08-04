package com.thatsales;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thatsales.ImageLoding.ImageLoader;
import com.thatsales.ImageLoding.ImageViewRounded;
import com.thatsales.Utils.Constants;
import com.thatsales.Listener.OnWebServiceResult;
import com.thatsales.Network.CallWebService;
import com.thatsales.Network.ConnectionDetector;
import com.thatsales.Pereferences.SharedPreferencesManger;
import com.thatsales.Utils.CommonUtils;
import com.thatsales.Utils.JSONUtils;
import com.thatsales.Utils.WebServiceApis;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

/**
 * Created by vinove on 13/5/16.
 */
public class UpdateProfileActivity extends BaseActivity implements OnWebServiceResult, View.OnClickListener {
    EditText profileName, profileEmail, profileMobile, editProfile;
    CallWebService webService;
    ImageView ivBack, ivLogout;
    TextView tvTitle, tvUploadImage, btnSaveChange;
    Context mContext;
    AlertDialog alertD;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_KITKAT_GALLERY = 2;
    private static final int REQUEST_CAMERA = 3;
    private String dateToStr, data, imagepath, encodedImage = "";
    private Bitmap bMap;
    private ImageViewRounded profileImage;
    ImageLoader imgL;
    String strImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mContext = UpdateProfileActivity.this;

        profileImage = (ImageViewRounded) findViewById(R.id.iv_update_profile);
        //editProfile = (ImageView) findViewById(R.id.iv_edit);
        profileName = (EditText) findViewById(R.id.ed_profileName);
        profileEmail = (EditText) findViewById(R.id.ed_email_address);
        profileMobile = (EditText) findViewById(R.id.ed_number);
        btnSaveChange = (TextView) findViewById(R.id.btn_save_changes);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivLogout = (ImageView) findViewById(R.id.iv_logout);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvUploadImage = (TextView) findViewById(R.id.tv_uploadImage);
        ivLogout.setVisibility(View.INVISIBLE);
        tvTitle.setText(getString(R.string.updateprofile));
        strImage = SharedPreferencesManger.getPrefValue(mContext, Constants.IMAGES, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        if(!strImage.isEmpty()){
            imgL = new ImageLoader(mContext);
            imgL.DisplayImage(strImage, profileImage);
        }else {
            profileImage.setImageResource(R.drawable.user_1);

        }


        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String userEmail = intent.getStringExtra("userEmail");
        String userNumber = intent.getStringExtra("userNumber");
        profileName.setText(userName);
        profileEmail.setText(userEmail);
        profileMobile.setText(userNumber);



        btnSaveChange.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvUploadImage.setOnClickListener(this);
        //editProfile.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_changes:
             if (validateUpdate()) {
                    updateProfileApi();
              }
                break;
            case R.id.tv_uploadImage:
                choosePopup();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }

    public void choosePopup() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.dialog_update_activitiy, null);

        alertD = new AlertDialog.Builder(this).create();


        Button btnGallery = (Button) promptView.findViewById(R.id.btn_gallary);

        Button btnCamera = (Button) promptView.findViewById(R.id.btn_camera);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getGallery();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getCamera();

            }
        });
        alertD.setView(promptView);
        alertD.show();
    }

    public void getGallery() {
        alertD.dismiss();
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image*//*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
            overridePendingTransition(0, R.anim.exit_slide_right);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_KITKAT_GALLERY);
            overridePendingTransition(0, R.anim.exit_slide_right);
        }
    }

    public void getCamera() {
        alertD.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri = null;
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (data == null || data.getData() == null)
                    return;
                imagepath = getRealPathFromURI(selectedImageUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                // options.inJustDecodeBounds = true;
                options.inScaled = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                bMap = BitmapFactory.decodeFile(imagepath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bMap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();

                profileImage.setImageBitmap(bMap);
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                break;
            case REQUEST_KITKAT_GALLERY:
                if (data == null || data.getData() == null)
                    return;
                selectedImageUri = data.getData();
                imagepath = getRealPathFromURI(selectedImageUri);
                BitmapFactory.Options options1 = new BitmapFactory.Options();
                options1.inSampleSize = 2;
                // options.inJustDecodeBounds = true;
                options1.inScaled = false;
                options1.inDither = false;
                options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bMap = BitmapFactory.decodeFile(imagepath);

                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();

                bMap.compress(Bitmap.CompressFormat.PNG, 100, baos1);
                byte[] imageBytes1 = baos1.toByteArray();

                profileImage.setImageBitmap(bMap);
                encodedImage = Base64.encodeToString(imageBytes1, Base64.DEFAULT);
                break;
            case REQUEST_CAMERA:
                try {
                    if (resultCode == Activity.RESULT_CANCELED) {
                        return;
                    }
                    bMap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

                    bMap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                    byte[] imageBytes2 = baos2.toByteArray();

                    encodedImage = Base64.encodeToString(imageBytes2, Base64.DEFAULT);
                    profileImage.setImageBitmap(bMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void updateProfileApi() {
        try {
            //  {"method":"updateProfile","image":"","name":"","email":"","mobile":"", "userId": ""}
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("method", "updateProfile");
                jsonObject.put("image", encodedImage);
                jsonObject.put("name", CommonUtils.convertUTF(profileName.getText().toString()));
                jsonObject.put("email", CommonUtils.convertUTF(profileEmail.getText().toString()));
                jsonObject.put("mobile", CommonUtils.convertUTF(profileMobile.getText().toString()));
                jsonObject.put("userId", SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING));
                params.put("json_data", jsonObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.UPDATE_PROFILE, this);
                webService.execute();
            } else {
                Toast.makeText(mContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //validation
    private boolean validateUpdate() {

        if (profileName.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_username));
            return false;
        }

        if (profileEmail.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_emailId));
            return false;
        }

        if (profileMobile.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_number));
            return false;
        }

        if ((profileMobile.getText().toString().trim().length() < 6) || (profileMobile.getText()
                .toString().trim().length() > 15)) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_mobile));
            return false;
        }

        //check the email enter is correct or not
        if (!CommonUtils.isValidEmailAddress(profileEmail.getText().toString().trim())) {
            //show your message if not matches with email pattern
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_invalid_email));
            return false;
        }
        // imageViewOne.getDrawable() == null
   /*     if(profileImage.getDrawable()==null){
            CommonUtils.showToast(mContext, "Please update Profile Picture");
            return false;
        }*/

        return true;

    }


    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type) {
            case UPDATE_PROFILE:
                try {
                    System.out.println("result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                   // String message = JSONUtils.getStringFromJSON(jsonObject, "message");
                    if (code == 200) {
                        if(jsonObject.has("image")) {
                            SharedPreferencesManger.setPrefValue(mContext, Constants.IMAGES, JSONUtils.getStringFromJSON(jsonObject, "image"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        }
                        JSONObject userData = JSONUtils.getJSONObjectFromJSON(jsonObject, "userData");
                        SharedPreferencesManger.setPrefValue(mContext, Constants.USERNAME, JSONUtils.getStringFromJSON(userData, "name"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.EMAILID, JSONUtils.getStringFromJSON(userData, "email"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.MOBILENUMBER, JSONUtils.getStringFromJSON(userData, "number"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);

                        Intent intent = new Intent(UpdateProfileActivity.this, MyProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.exit_slide_right);
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }
                    else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

}





