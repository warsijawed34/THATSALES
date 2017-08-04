package com.thatsales.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

	public static AlertDialog dialog;
    static DisplayMetrics displaymetrics;

	public static void showAlertDialog(Context mContext, String title, String message) {
		
		if(mContext == null) return;
		if(dialog != null && dialog.isShowing()) return;
		
		dialog = new AlertDialog.Builder(mContext).create();
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

    public static void showToast(Context mContext, String message){
        Toast toast = Toast.makeText(mContext,message,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


	public final static  boolean isValidEmailAddress(String email) {
           boolean flag;
           String expression ="[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25}" +
                        ")+" ;

           CharSequence inputStr = email.trim();
           Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
           Matcher matcher = pattern.matcher(inputStr);

        flag = matcher.matches();
           return flag;
    }

    /**
     * isValidPhoneNumber method validate mobile number
     * @param target
     * @return
     */
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target.length() != 10) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    //private static final String PHONE_REGEX = "\\d{3}-\\d{7}";
    public final static Pattern MOBILE_PATTERN = Pattern
            .compile("\\d{3}-\\d{7}");

    public static boolean checkMobile(String zip) {
        return MOBILE_PATTERN.matcher(zip).matches();
    }

    private static void getHeightWigth(Context context){
         displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
    }

    public static int getWidth(Context context){
        getHeightWigth(context);
        int width = displaymetrics.widthPixels;
        return width;
    }


    public static void hideKeyboard(Context ctx) {

        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static boolean onlySpecialCharacter(String str){
        String specialCharacters = "[" + "-/@#!*$%^&.'_+={}()"+ "]+" ;
        return !str.matches(specialCharacters);
    }

    /**
     * convertUTF method used to encode string into UTF-8
     * @param s
     * @return
     */
    public static String convertUTF(String s) {
        String data = "";
        try {
            data = URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}