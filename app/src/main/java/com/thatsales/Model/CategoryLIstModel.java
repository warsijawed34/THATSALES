package com.thatsales.Model;

import org.json.JSONArray;

/**
 * Created by vinove on 29/6/16.
 */
public class CategoryLIstModel {
    String id;
    String name;
    String fav;
    String image;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

