package com.codekidlabs.storagechooser.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.codekidlabs.storagechooser.R;

public class ThumbnailUtil {

    Context mContext;

    // Constant extensions
    private static final String TEXT_FILE = "txt";
    // video files
    private static final String VIDEO_FILE = "mp4";
    private static final String VIDEO_MOV_FILE = "mov";
    private static final String VIDEO_AVI_FILE = "avi";
    private static final String VIDEO_MKV_FILE = "mkv";
    // audio files
    private static final String AUDIO_FILE = "mp3";
    // image files
    private static final String JPEG_FILE = "jpeg";
    private static final String JPG_FILE = "jpg";
    private static final String PNG_FILE = "png";
    //archive files
    private static final String APK_FILE = "apk";
    private static final String ZIP_FILE = "zip";
    private static final String RAR_FILE = "rar";

    // office files
    private static final String DOC_FILE = "doc";
    private static final String PPT_FILE = "ppt";
    private static final String EXCEL_FILE = "xls";
    private static final String PDF_FILE = "pdf";

    public ThumbnailUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void init(ImageView imageView, String filePath) {
        thumbnailPipe(imageView, filePath);
    }

    private void thumbnailPipe(ImageView imageView, String filePath) {
        String extension = getExtension(filePath);


        switch (extension) {
            case TEXT_FILE:
            case DOC_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.doc));
                break;
            case VIDEO_FILE:
            case VIDEO_AVI_FILE:
            case VIDEO_MOV_FILE:
            case VIDEO_MKV_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.mov));
                break;
            case APK_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.apk));
                break;
            case AUDIO_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.music));
                break;
            case ZIP_FILE:
            case RAR_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.zip));
                break;
            case JPEG_FILE:
            case JPG_FILE:
            case PNG_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.pic));
                break;
            case PDF_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.pdf));
                break;
        }
    }

    private Drawable getDrawableFromRes(int id) {
        return ContextCompat.getDrawable(mContext, id);
    }

    private String getExtension(String path) {
        return path.substring(path.lastIndexOf(".") + 1, path.length());
    }
}
