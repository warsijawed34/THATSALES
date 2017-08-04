package com.thatsales.Model;

import org.json.JSONArray;

/**
 * this is  setter getter class of Home Class
 * Created by Jawed on 10/5/16.
 */
public class HomeModel {
    String saleName, compneyName, saleId, description, contactNo, address,  latitude, longitude,
            startDate, endDate, image, isFav;//fulladdress
    JSONArray jsonArray;
    int id;
    String llFav;

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }


    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }

    public String getCompneyName() {
        return compneyName;
    }

    public void setCompneyName(String compneyName) {
        this.compneyName = compneyName;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//    public String getFulladdress() {
//        return fulladdress;
//    }
//
//    public void setFulladdress(String fulladdress) {
//        this.fulladdress = fulladdress;
//    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitudem) {
        this.longitude = longitudem;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsFav(String isFav) {
        this.isFav = isFav;
    }

    public String getIsFav() {
        return isFav;
    }


    public String getLlFav() {
        return llFav;
    }

    public void setLlFav(String llfav) {
        this.llFav = llfav;
    }
}
