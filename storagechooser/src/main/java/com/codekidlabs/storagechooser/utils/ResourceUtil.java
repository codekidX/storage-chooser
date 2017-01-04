package com.codekidlabs.storagechooser.utils;


import android.content.Context;
import android.support.v4.content.ContextCompat;

public class ResourceUtil {

    private Context context;

    public ResourceUtil(Context context) {
        this.context = context;
    }

    public int getColor(int id) {
        return ContextCompat.getColor(context, id);
    }
}
