package com.codekidlabs.storagechooserdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.StorageChooserView;
import com.codekidlabs.storagechooser.utils.DiskUtil;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private CheckBox dirCheckBox, fileCheckBox;

    private StorageChooser.Builder builder = new StorageChooser.Builder();
    private StorageChooser chooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coord);
        dirCheckBox = (CheckBox) findViewById(R.id.checkbox_dir);
        fileCheckBox = (CheckBox) findViewById(R.id.checkbox_file);

        ((CheckBox) findViewById(R.id.checkbox_membar)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.withMemoryBar(isChecked);
            }
        });

        ((CheckBox) findViewById(R.id.checkbox_add_folder)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.allowAddFolder(isChecked);
            }
        });

        ((CheckBox) findViewById(R.id.checkbox_hidden)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.showHidden(isChecked);
            }
        });

        dirCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.allowCustomPath(isChecked);

                if (isChecked) {
                    builder.setType(StorageChooser.DIRECTORY_CHOOSER);
                    fileCheckBox.setEnabled(false);
                } else {
                    fileCheckBox.setEnabled(true);
                }
            }
        });

        ((CheckBox) findViewById(R.id.checkbox_skip)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.skipOverview(isChecked);
            }
        });

        fileCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.allowCustomPath(isChecked);

                if (isChecked) {
                    builder.setType(StorageChooser.FILE_PICKER);
                    dirCheckBox.setEnabled(false);
                } else {
                    dirCheckBox.setEnabled(true);
                }

            }
        });

        ((CheckBox) findViewById(R.id.checkbox_threshold)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.applyMemoryThreshold(isChecked);
                builder.withThreshold(1, DiskUtil.IN_GB);
            }
        });

        builder.withActivity(this)
                .withFragmentManager(getSupportFragmentManager());

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooser = builder.build();
                chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String path) {
                        Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();
                    }
                });
                chooser.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && coordinatorLayout != null && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(coordinatorLayout, "Demo app needs storage permission to display file list", Snackbar.LENGTH_INDEFINITE)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 199);
                        }
                    }).show();
        }
    }
}
