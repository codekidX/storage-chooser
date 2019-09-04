package com.codekidlabs.storagechooser.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codekidlabs.storagechooser.R;

import java.io.File;

public class ThumbnailUtil {

    // Constant extensions
    private static final String TEXT_FILE = "txt";
    private static final String LOG_FILE = "log";
    private static final String CSV_FILE = "csv";
    private static final String PROP_FILE = "prop";
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
    private static final String GIF_FILE = "gif";
    //archive files
    private static final String APK_FILE = "apk";
    private static final String ZIP_FILE = "zip";
    private static final String RAR_FILE = "rar";
    private static final String TAR_GZ_FILE = "gz";
    private static final String TAR_FILE = "tar";
    // office files
    private static final String DOC_FILE = "doc";
    private static final String PPT_FILE = "ppt";
    private static final String EXCEL_FILE = "xls";
    private static final String PDF_FILE = "pdf";
    // font files
    private static final String TTF_FILE = "ttf";
    private static final String OTF_FILE = "otf";
    // torrent files
    private static final String TORRENtT_FILE = "torrent";
    //web files
    private static final String HTML_FILE = "html";
    private static final String PHP_FILE = "php";
    private static final String CSS_FILE = "css";
    private static final String CR_DL_FILE = "crdownload";
    private Context mContext;

    public ThumbnailUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void init(ImageView imageView, ImageView videoPlayIcon, String filePath) {
        thumbnailPipe(imageView, videoPlayIcon, filePath);
    }

    private void thumbnailPipe(ImageView imageView, ImageView videoPlayIcon, final String filePath) {
        String extension = getExtension(filePath);


        switch (extension) {
            case TEXT_FILE:
            case CSV_FILE:
            case DOC_FILE:
            case PROP_FILE:
            case LOG_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.ic_ic_unknown_file));
                break;
            case VIDEO_FILE:
            case VIDEO_AVI_FILE:
            case VIDEO_MOV_FILE:
            case VIDEO_MKV_FILE:
                this.loadMedia(imageView, videoPlayIcon, filePath, true);
                break;
            case APK_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.apk));
                break;
            case AUDIO_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.music));
                break;
            case ZIP_FILE:
            case RAR_FILE:
            case TAR_FILE:
            case TAR_GZ_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.zip));
                break;
            case JPEG_FILE:
            case JPG_FILE:
            case PNG_FILE:
            case GIF_FILE:
                this.loadMedia(imageView, videoPlayIcon, filePath, false);
                break;
            case TTF_FILE:
            case OTF_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.font));
                break;
            case HTML_FILE:
            case PHP_FILE:
            case CSS_FILE:
            case CR_DL_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.web));
                break;
            case TORRENtT_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.torrent));
                break;
            case PDF_FILE:
                imageView.setImageDrawable(getDrawableFromRes(R.drawable.pdf));
                break;
        }
    }

    private void loadMedia(ImageView imageView, ImageView videoPlayIcon, final String filePath, boolean isVideo) {
        Glide.with(this.mContext)
                .load(Uri.fromFile(new File(filePath)))
                .transform(new CenterCrop(), new RoundedCorners(32))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(imageView);

        if(isVideo) {
            videoPlayIcon.setVisibility(View.VISIBLE);
        }

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        FileProvider.getUriForFile(
                                ThumbnailUtil.this.mContext,
                                "com.codekidlabs.storagechooser.fileprovider",
                                new File(filePath)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ThumbnailUtil.this.mContext.startActivity(intent);
                return true;
            }
        });
    }

    private Drawable getDrawableFromRes(int id) {
        return ContextCompat.getDrawable(mContext, id);
    }

    private String getExtension(String path) {
        return path.substring(path.lastIndexOf(".") + 1, path.length());
    }
}
