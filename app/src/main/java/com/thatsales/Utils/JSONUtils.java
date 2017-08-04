package com.thatsales.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {

    public static String getStringFromJSON(JSONObject jObj, String key) {
        try {
            if (jObj != null && !jObj.getString(key).equalsIgnoreCase("null") && jObj.has(key)) {
                return jObj.getString(key);
            } else {
                return "N/A";
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static long getLongFromJSON(JSONObject jObj, String key) {

        if (jObj != null && jObj.has(key)) {
            try {
                return jObj.getLong(key);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0l;
    }

    public static boolean getBooleanFromJSON(JSONObject jObj, String key) {

        if (jObj != null && jObj.has(key)) {
            try {
                return jObj.getBoolean(key);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Double getDoubleFromJSON(JSONObject jObj, String key) {

        if (jObj != null && jObj.has(key)) {
            try {
                return jObj.getDouble(key);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    public static int getIntFromJSON(JSONObject jObj, String key) {
        if (jObj != null && jObj.has(key)) {
            try {
                return jObj.getInt(key);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static JSONArray getJSONArrayFromJSON(JSONObject jObj, String key) {

        if (jObj != null && jObj.has(key)) {
            try {
                return jObj.getJSONArray(key);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new JSONArray();
    }

    public static JSONObject getJSONObjectFromJSON(JSONObject jObj, String key) {

        if (jObj != null && jObj.has(key)) {
            try {
                return jObj.getJSONObject(key);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }
}