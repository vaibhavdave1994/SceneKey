package com.scenekey.model;

import android.graphics.Bitmap;

import com.scenekey.helper.WebServices;
import com.scenekey.util.Utility;

import java.io.Serializable;

/**
 * Created by mindiii on 15/2/18.
 */

public class ImagesUpload implements Serializable {

    public Bitmap bitmap;
    public String path;
    public String key;

    public ImagesUpload(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ImagesUpload( String key) {
        Utility.e("KEY : _only",key);
        this.key = key;
        this.path = WebServices.USER_IMAGE+key;
    }
    public ImagesUpload( String key ,Bitmap bitmap) {
        Utility.e("KEY : _bitmap",key);
        this.key = key;
        this.path = WebServices.USER_IMAGE+key;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
