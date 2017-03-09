package com.codekidlabs.storagechooserdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.StorageChooserView;
import com.codekidlabs.storagechooser.utils.DiskUtil;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private Context context;
    private static FragmentManager fragmentManager;

    private StorageChooser.Builder builder = new StorageChooser.Builder();
    private StorageChooser chooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getColorFromResource(getApplicationContext(), R.color.colorPrimaryDark));
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coord);
        fragmentManager = getSupportFragmentManager();


        if(checkStoragePermission()) {
            Snackbar.make(coordinatorLayout, "Demo app needs storage permission to display file list", Snackbar.LENGTH_INDEFINITE)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 199);
                        }
                    }).show();
        }


        initCheckboxes();
        initBuilder();


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooser = builder.build();
                chooser.show();
            }
        });


    }

    private void initCheckboxes() {

        CheckBox membarCheckBox = (CheckBox) findViewById(R.id.checkbox_membar);
        CheckBox addFolderCheckBox = (CheckBox) findViewById(R.id.checkbox_add_folder);
        CheckBox hiddenCheckBox = (CheckBox) findViewById(R.id.checkbox_hidden);
        final CheckBox dirCheckBox = (CheckBox) findViewById(R.id.checkbox_dir);
        final CheckBox fileCheckBox = (CheckBox) findViewById(R.id.checkbox_file);
        CheckBox skipCheckBox = (CheckBox) findViewById(R.id.checkbox_skip);
        CheckBox thresholdCheckBox = (CheckBox) findViewById(R.id.checkbox_threshold);


        membarCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.withMemoryBar(isChecked);
            }
        });

        addFolderCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.allowAddFolder(isChecked);
            }
        });

        hiddenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.showHidden(isChecked);
            }
        });

        dirCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                builder.allowCustomPath(isChecked);
                if(isChecked) {

                    builder.setType(StorageChooser.DIRECTORY_CHOOSER);

                    if(fileCheckBox.isChecked()) {
                        fileCheckBox.setChecked(false);
                        fileCheckBox.setEnabled(false);

                    }
                } else {
                    fileCheckBox.setEnabled(true);
                }
            }
        });

        skipCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.skipOverview(isChecked);
            }
        });

        fileCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                builder.allowCustomPath(isChecked);

                if(isChecked) {

                    builder.setType(StorageChooser.FILE_PICKER);

                    if(dirCheckBox.isChecked()) {
                        dirCheckBox.setChecked(false);
                        dirCheckBox.setEnabled(false);

                    }
                } else {
                    dirCheckBox.setEnabled(true);
                }

            }
        });

        thresholdCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.applyMemoryThreshold(isChecked);
                builder.withThreshold(1, DiskUtil.IN_GB);
            }
        });
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void initBuilder() {
        builder.withActivity(this)
                .withFragmentManager(fragmentManager);

    }

    public static int getColorFromResource(Context context, int id) {
        return ContextCompat.getColor(context, id);
    }

}
