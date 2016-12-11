package com.codekidlabs.storagechooserdemo.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;

/**
 * utility to set custom typeface
 */

public class TypefaceUtil {

    private static Typeface mNunitoTypeFace;

    public static void setTypefaceRegular(Context context, TextView textView) {
        mNunitoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/NunitoSans-Regular.ttf");
        textView.setTypeface(mNunitoTypeFace);
    }

    public static void setTypefaceBold(Context context, TextView textView) {
        mNunitoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/NunitoSans-Bold.ttf");
        textView.setTypeface(mNunitoTypeFace);
    }

    public static void setTypefaceBold(Context context, Button button) {
        mNunitoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/NunitoSans-Bold.ttf");
        button.setTypeface(mNunitoTypeFace);
    }

    public static void setTypefaceSemiBold(Context context, TextView textView) {
        mNunitoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/NunitoSans-Light.ttf");
        textView.setTypeface(mNunitoTypeFace);
    }

    public static void setTypefaceSemiBold(Context context, Button button) {
        mNunitoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/NunitoSans-Light.ttf");
        button.setTypeface(mNunitoTypeFace);
    }

    public static void setTypefaceLight(Context context, TextView textView) {
        mNunitoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/NunitoSans-SemiBold.ttf");
        textView.setTypeface(mNunitoTypeFace);
    }

    public static void setTypefaceLight(Context context, Button button) {
        mNunitoTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/NunitoSans-SemiBold.ttf");
        button.setTypeface(mNunitoTypeFace);
    }
}
