package com.codekidlabs.storagechooser.utils;


import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.codekidlabs.storagechooser.R;

public class ResourceUtil {

    private Context context;

    public ResourceUtil(Context context) {
        this.context = context;
    }

    public int getColor(int id) {
        return ContextCompat.getColor(context, id);
    }

    public int getAppliedAlpha(int color) {
        return ColorUtils.setAlphaComponent(color, 50);
    }

    public int getPrimaryColorWithAlpha() {
        return getAppliedAlpha(getColor(R.color.colorPrimary));
    }
}
