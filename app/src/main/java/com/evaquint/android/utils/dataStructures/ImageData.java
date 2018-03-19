package com.evaquint.android.utils.dataStructures;

import android.graphics.Bitmap;

import android.net.Uri;

import java.io.Serializable;


/**
 * Created by amrmahmoud on 2018-03-18.
 */

public class ImageData implements Serializable{
    public  Bitmap icon;
    public Uri uri;

    public ImageData(){

    }
    public ImageData(Bitmap icon, Uri uri ){
        this.icon = icon;
        this.uri =uri;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
