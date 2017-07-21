package com.codekidlabs.storagechooser.filters;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * Created by codekid on 21/07/17.
 */

public class DocsFileFilter implements FileFilter {

    protected static final String TAG = "DocsFileFilter";
    private final boolean allowDirectories = true;

    /**
     * Video file formats for storage-chooser
     */
    public enum Format
    {
        PDF("pdf"),
        PPT("ppt"),
        DOC("doc"),
        DOCX("docx"),
        EXCEL("xls");

        private String filesuffix;

        Format( String filesuffix ) {
            this.filesuffix = filesuffix;
        }

        public String getFilesuffix() {
            return filesuffix;
        }
    }

    private String getFileExtension(File f) {
        return getFileExtension( f.getName() );
    }

    private String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        } else
            return null;
    }


    public DocsFileFilter() {
    }

    @Override
    public boolean accept(File f) {
        if ( f.isHidden() || !f.canRead() ) {
            return false;
        }

        if ( f.isDirectory() ) {
            return findInDirectory( f );
        }
        return isFileExtension( f );
    }

    private boolean isFileExtension( File f ) {
        String ext = getFileExtension(f);
        if ( ext == null) return false;
        try {
            if ( Format.valueOf(ext.toUpperCase()) != null ) {
                return true;
            }
        } catch(IllegalArgumentException e) {
            //Not known enum value
            return false;
        }
        return false;
    }

    private boolean findInDirectory( File dir ) {
        if ( !allowDirectories ) {
            return false;
        } else {
            final ArrayList<File> sub = new ArrayList<File>();
            int indexInList = dir.listFiles( new FileFilter() {

                @Override
                public boolean accept(File file) {
                    if ( file.isFile() ) {
                        if ( file.getName().equals( ".nomedia" ) )
                            return false;

                        return isFileExtension( file );
                    } else if ( file.isDirectory() ){
                        sub.add( file );
                        return false;
                    } else
                        return false;
                }
            } ).length;

            if ( indexInList > 0 ) {
                Log.i(TAG, "findInDirectory => " + dir.getName() + " return true for => " + indexInList);
                return true;
            }

            for( File subDirectory: sub ) {
                if ( findInDirectory( subDirectory ) ) {
                    Log.i(TAG, "findInDirectory => " + subDirectory.toString());
                    return true;
                }
            }
            return false;
        }
    }
}

