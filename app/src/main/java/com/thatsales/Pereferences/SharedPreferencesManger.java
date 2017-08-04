package com.thatsales.Pereferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesManger {
	
	public static final String PREF_NAME = "newage_global_pref";
    public static final String LOGIN_TYPE_PREF_NAME = "newage_global_pref_login_type";
    public static final String REMEMBER_PREF_NAME = "newage_global_pref_remember";
	public enum PREF_DATA_TYPE{
		BOOLEAN, STRING, INT, FLOAT, LONG
	}
	
	public static SharedPreferences getSharedPref(Context mContext) {
		return mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

    public static SharedPreferences getLoginTypeSharedPref(Context mContext) {
        return mContext.getSharedPreferences(LOGIN_TYPE_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getRememberSharedPref(Context mContext) {
        return mContext.getSharedPreferences(REMEMBER_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void setRememberPrefValue(Context mContext, String key, Object value, PREF_DATA_TYPE type){
        Editor edit = getRememberSharedPref(mContext).edit();
        switch(type){
            case BOOLEAN:
                edit.putBoolean(key, (Boolean)value);
                break;
            case STRING:
                edit.putString(key, (String)value);
                break;
            case INT:
                edit.putInt(key, (Integer)value);
                break;
            case FLOAT:
                edit.putFloat(key, (Float)value);
                break;
            case LONG:
                edit.putLong(key, (Long)value);
                break;
            default:
                break;
        }
        edit.commit();
    }

    public static void setLoginTypePrefValue(Context mContext, String key, Object value, PREF_DATA_TYPE type){
        Editor edit = getLoginTypeSharedPref(mContext).edit();
        switch(type){
            case BOOLEAN:
                edit.putBoolean(key, (Boolean)value);
                break;
            case STRING:
                edit.putString(key, (String)value);
                break;
            case INT:
                edit.putInt(key, (Integer)value);
                break;
            case FLOAT:
                edit.putFloat(key, (Float)value);
                break;
            case LONG:
                edit.putLong(key, (Long)value);
                break;
            default:
                break;
        }
        edit.commit();
    }

	public static void setPrefValue(Context mContext, String key, Object value, PREF_DATA_TYPE type){
		Editor edit = getSharedPref(mContext).edit();
		switch(type){
			case BOOLEAN:
				edit.putBoolean(key, (Boolean)value);
				break;
			case STRING:
				edit.putString(key, (String)value);
				break;
			case INT:
				edit.putInt(key, (Integer)value);
				break;
			case FLOAT:
				edit.putFloat(key, (Float)value);
				break;
			case LONG:
				edit.putLong(key, (Long)value);
				break;
			default:
				break;
		}
		edit.commit();
	}

    public static Object getRememberPrefValue(Context mContext, String key, PREF_DATA_TYPE type){
        Object resultant = null;
        SharedPreferences pref = getRememberSharedPref(mContext);
        switch(type){
            case BOOLEAN:
                resultant = pref.getBoolean(key, false);
                break;
            case STRING:
                resultant = pref.getString(key, "");
                break;
            case INT:
                resultant = pref.getInt(key, 0);
                break;
            case FLOAT:
                resultant = pref.getFloat(key, 0.0f);
                break;
            case LONG:
                resultant = pref.getLong(key, 0L);
                break;
            default:
                break;
        }
        return resultant;
    }

    public static Object getLoginTypePrefValue(Context mContext, String key, PREF_DATA_TYPE type){
        Object resultant = null;
        SharedPreferences pref = getLoginTypeSharedPref(mContext);
        switch(type){
            case BOOLEAN:
                resultant = pref.getBoolean(key, false);
                break;
            case STRING:
                resultant = pref.getString(key, "");
                break;
            case INT:
                resultant = pref.getInt(key, 0);
                break;
            case FLOAT:
                resultant = pref.getFloat(key, 0.0f);
                break;
            case LONG:
                resultant = pref.getLong(key, 0L);
                break;
            default:
                break;
        }
        return resultant;
    }

	public static Object getPrefValue(Context mContext, String key, PREF_DATA_TYPE type){
		Object resultant = null;
		SharedPreferences pref = getSharedPref(mContext);
		switch(type){
			case BOOLEAN:
				resultant = pref.getBoolean(key, false);
				break;
			case STRING:
				resultant = pref.getString(key, "");
				break;
			case INT:
				resultant = pref.getInt(key, 0);
				break;
			case FLOAT:
				resultant = pref.getFloat(key, 0.0f);
				break;
			case LONG:
				resultant = pref.getLong(key, 0L);
				break;
			default:
				break;
		}
		return resultant;
	}

    public static void removeRememberPrefValue(Context mContext, String key){
        Editor edit = getRememberSharedPref(mContext).edit();
        edit.remove(key);
        edit.commit();
    }

    public static void removeLoginTypePrefValue(Context mContext, String key){
        Editor edit = getLoginTypeSharedPref(mContext).edit();
        edit.remove(key);
        edit.commit();
    }

    public static void removePrefValue(Context mContext, String key){
        Editor edit = getSharedPref(mContext).edit();
        edit.remove(key);
        edit.commit();
    }

	public static void removeAllPrefValue(Context mContext){
		Editor edit = getSharedPref(mContext).edit();
		edit.clear();
		edit.commit();
	}
}