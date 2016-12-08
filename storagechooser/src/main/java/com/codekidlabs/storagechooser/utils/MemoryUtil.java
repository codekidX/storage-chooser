package com.codekidlabs.storagechooser.utils;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryUtil {

    private static final String ERROR = "error";
    public static final String SELF_DIR_NAME = "self";
    public static final String EMULATED_DIR_NAME = "emulated";
    public static final String SDCARD0_DIR_NAME = "sdcard0";

    public static boolean isExternalStoragePresent() {
        return getStorageListSize() == 0;
    }

    public static int getStorageListSize() {
        File storageDir = new File("/storage");
        List<File> volumeList = new ArrayList<File>();
        Collections.addAll(volumeList, storageDir.listFiles());
        // seggregate the list
        for(int i=0;i < volumeList.size(); i++) {
            if(volumeList.get(i).getName().equals(SELF_DIR_NAME)) {
                volumeList.remove(i);
            }
            if(volumeList.get(i).getName().equals(EMULATED_DIR_NAME)) {
                volumeList.remove(i);
            }
            if(volumeList.get(i).getName().equals(SDCARD0_DIR_NAME)) {
                volumeList.remove(i);
            }
        }

        return volumeList.size();
    }




    public static String getAvailableMemorySize(File file) {
            StatFs stat = new StatFs(file.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalMemorySize(File file) {
            StatFs stat = new StatFs(file.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GiB";
                    size /= 1024;
                }
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}
