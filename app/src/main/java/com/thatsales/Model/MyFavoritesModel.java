package com.thatsales.Model;

/**
 * this is  setter getter class of MyFavorites
 * Created by Jawed on 10/5/16.
 */
public class MyFavoritesModel {
     String strSaleName;
     String strSaleDesc;
     String isFav;
    String saleID;
    String images;


    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getStrSaleName() {
        return strSaleName;
    }

    public void setStrSaleName(String strSalenName) {
        this.strSaleName = strSalenName;
    }

    public String getStrSaleDesc() {
        return strSaleDesc;
    }

    public void setStrSaleDesc(String strSaleDesc) {
        this.strSaleDesc = strSaleDesc;
    }

    public String getIsFav() {
        return isFav;
    }

    public void setIsFav(String isFav) {
        this.isFav = isFav;
    }

    public String getSaleID() {
        return saleID;
    }

    public void setSaleID(String saleID) {
        this.saleID = saleID;
    }
}