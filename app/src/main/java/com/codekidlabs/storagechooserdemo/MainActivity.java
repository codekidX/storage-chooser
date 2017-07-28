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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.codekidlabs.storagechooser.Content;
import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.utils.DiskUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private CheckBox dirCheckBox, fileCheckBox;

    private StorageChooser.Builder builder = new StorageChooser.Builder();
    private StorageChooser chooser;
    private String TAG = getClass().getName();

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

        ((CheckBox) findViewById(R.id.checkbox_session)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.shouldResumeSession(isChecked);
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

        ((CheckBox) findViewById(R.id.checkbox_dark)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                builder.setTheme(getScTheme(isChecked));
            }
        });


        Spinner spinner = (Spinner) findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        builder.crunch();
                        builder.filter(null);
                        break;
                    case 1:
                        builder.crunch();
                        builder.filter(StorageChooser.FileType.VIDEO);
                        break;
                    case 2:
                        builder.crunch();
                        builder.filter(StorageChooser.FileType.AUDIO);
                        break;
                    case 3:
                        builder.crunch();
                        builder.filter(StorageChooser.FileType.DOCS);
                        break;
                    case 4:
                        builder.crunch();
                        builder.filter(StorageChooser.FileType.IMAGES);
                        break;
                    case 5:

                        ArrayList<String> formats = new ArrayList<>();
                        formats.add("txt");
                        formats.add("mkv");
                        builder.customFilter(formats);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // ----------------- Localization -------------------
        Content c = new Content();
        c.setCreateLabel("Create");
        c.setInternalStorageText("My Storage");
        c.setCancelLabel("Cancel");
        c.setSelectLabel("Select");
        c.setOverviewHeading("Choose Drive");

        builder.withActivity(this)
                .withFragmentManager(getFragmentManager())
                .withContent(c);


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

                chooser.setOnCancelListener(new StorageChooser.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Storage Chooser Cancelled.", Toast.LENGTH_SHORT).show();
                    }
                });

                chooser.setOnMultipleSelectListener(new StorageChooser.OnMultipleSelectListener() {
                    @Override
                    public void onDone(ArrayList<String> selectedFilePaths) {
                        for(String s: selectedFilePaths) {
                            Log.e(TAG, s);
                        }
                    }
                });

                chooser.show();
            }

        });
    }

    private StorageChooser.Theme getScTheme(boolean isChecked) {
        StorageChooser.Theme theme = new StorageChooser.Theme(getApplicationContext());
        theme.setScheme((isChecked) ? getResources().getIntArray(R.array.paranoid_theme) : theme.getDefaultScheme());
        return theme;
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
