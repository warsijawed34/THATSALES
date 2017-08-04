package com.thatsales.Model;

import org.json.JSONArray;

/**
 * this is  setter getter class of interest class
 * Created by Jawed on 10/5/16.
 */
public class InterestModel {
    String strSaleName;
    String images;
    String isFav;
    String saleId;
    JSONArray jsonArray;

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public String getStrSaleName() {
        return strSaleName;
    }

    public void setStrSaleName(String strSalename) {
        this.strSaleName = strSalename;
    }

    public String getIsFav() {
        return isFav;
    }

    public void setIsFav(String isFav) {
        this.isFav = isFav;
    }

    public String getSaleID() {
        return saleId;
    }

    public void setSaleID(String saleID) {
        this.saleId = saleID;
    }
}
