package com.codekidlabs.storagechooser.filters;

import android.util.Log;

import com.codekidlabs.storagechooser.StorageChooser;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by codekid on 21/07/17.
 */

public class UniversalFileFilter implements FileFilter {
    protected static final String TAG = "UniversalFileFilter";
    private final boolean allowDirectories = true;

    private StorageChooser.FileType fileType;
    private boolean customEnumLock = false;
    private List<String> customEnum;

    public UniversalFileFilter(StorageChooser.FileType fileType) {
        this.fileType = fileType;
    }

    public UniversalFileFilter(boolean customLock, List<String> customEnum) {
        this.customEnumLock = customLock;
        this.customEnum = customEnum;
    }

    private String getFileExtension(File f) {
        return getFileExtension(f.getName());
    }

    private String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i + 1);
        } else
            return null;
    }

    @Override
    public boolean accept(File f) {
        if (f.isHidden() || !f.canRead()) {
            return false;
        }

        if (f.isDirectory()) {
            return findInDirectory(f);
        }
        return isFileExtension(f);
    }

    private boolean isFileExtension(File f) {
        String ext = getFileExtension(f);
        if (ext == null) return false;
        try {
            if (customEnumLock) {
                return customEnum.contains(ext);
            } else {
                if (getFormatExtention(ext) != null) {
                    return true;
                }
            }
        } catch (IllegalArgumentException e) {
            //Not known enum value
            return false;
        }
        return false;
    }

    private Object getFormatExtention(String ext) {
        switch (fileType) {
            case VIDEO:
                return VideoFormat.valueOf(ext.toUpperCase());
            case AUDIO:
                return AudioFormat.valueOf(ext.toUpperCase());
            case IMAGES:
                return ImageFormat.valueOf(ext.toUpperCase());
            case DOCS:
                return DocsFormat.valueOf(ext.toUpperCase());
            case ARCHIVE:
                return ArchiveFormat.valueOf(ext.toUpperCase());
            default:
                return null;
        }
    }

    private boolean findInDirectory(File dir) {
        if (!allowDirectories) {
            return false;
        } else {
            final ArrayList<File> sub = new ArrayList<File>();
            int indexInList = dir.listFiles(new FileFilter() {

                @Override
                public boolean accept(File file) {
                    if (file.isFile()) {
                        if (file.getName().equals(".nomedia"))
                            return false;

                        return isFileExtension(file);
                    } else if (file.isDirectory()) {
                        sub.add(file);
                        return false;
                    } else
                        return false;
                }
            }).length;

            if (indexInList > 0) {
                Log.i(TAG, "findInDirectory => " + dir.getName() + " return true for => " + indexInList);
                return true;
            }

            for (File subDirectory : sub) {
                if (findInDirectory(subDirectory)) {
                    Log.i(TAG, "findInDirectory => " + subDirectory.toString());
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * ArchiveFormat for storage-chooser
     */
    public enum ArchiveFormat {
        ZIP("zip"),
        RAR("rar");

        private String filesuffix;

        ArchiveFormat(String filesuffix) {
            this.filesuffix = filesuffix;
        }

    }

    /**
     * ImageFormat for storage-chooser
     */
    public enum ImageFormat {
        JPG("jpg"),
        JPEG("jpeg"),
        PNG("png"),
        TIFF("tiff"),
        GIF("gif");

        private String filesuffix;

        ImageFormat(String filesuffix) {
            this.filesuffix = filesuffix;
        }
    }

    /**
     * VideoFormat for storage-chooser
     */
    public enum VideoFormat {
        MP4("mp4"),
        TS("ts"),
        MKV("mkv"),
        AVI("avi"),
        FLV("flv");

        private String filesuffix;

        VideoFormat(String filesuffix) {
            this.filesuffix = filesuffix;
        }

    }

    /**
     * AudioFormat for storage-chooser
     */
    public enum AudioFormat {
        MP3("mp3"),
        OGG("ogg");

        private String filesuffix;

        AudioFormat(String filesuffix) {
            this.filesuffix = filesuffix;
        }

    }

    /**
     * DocsFormat for storage-chooser
     */
    public enum DocsFormat {
        PDF("pdf"),
        PPT("ppt"),
        DOC("doc"),
        DOCX("docx"),
        EXCEL("xls");

        private String filesuffix;

        DocsFormat(String filesuffix) {
            this.filesuffix = filesuffix;
        }

    }
}


