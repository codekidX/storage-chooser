package com.codekidlabs.storagechooserdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codekidlabs.storagechooser.StorageChooser;
import com.codekidlabs.storagechooser.StorageChooserView;
import com.codekidlabs.storagechooserdemo.utils.TypefaceUtil;

public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(DialogFragment.STYLE_NO_TITLE);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView scTitle = (TextView) findViewById(R.id.sc_title);
        TypefaceUtil.setTypefaceBold(context, scTitle);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment reportSheet = new ReportBottomSheet();
                reportSheet.show(getSupportFragmentManager(), "report_sheet");
            }
        });

        Button storageChooserButton = (Button) findViewById(R.id.storage_chooser_button);
        TypefaceUtil.setTypefaceBold(context,storageChooserButton);
        storageChooserButton.setBackgroundColor(getColorFromResource(context,android.R.color.white));

        storageChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageChooser storageChooser = new StorageChooser.Builder()
                        .withActivity(MainActivity.this)
                        .withFragmentManager(getSupportFragmentManager())
                        .withPredefinedPath("/Downloads/OpenGApps")
                        .withPreference(sharedPreferences)
                        .build();

                storageChooser.show();
            }
        });

        Button storageChooserButtonMemorybar = (Button) findViewById(R.id.storage_chooser_button_memorybar);
        TypefaceUtil.setTypefaceBold(context,storageChooserButtonMemorybar);
        storageChooserButtonMemorybar.setBackgroundColor(getColorFromResource(context,android.R.color.white));

        storageChooserButtonMemorybar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageChooser storageChooser = new StorageChooser.Builder()
                        .withActivity(MainActivity.this)
                        .withFragmentManager(getSupportFragmentManager())
                        .withMemoryBar(true)
                        .setDialogTitle("Ashish's Chooser")
                        .setInternalStorageText("My Internal")
                        .allowCustomPath(true)
                        .allowAddFolder(true)
                        .withPredefinedPath("/Downloads/OpenGApps")
                        .withPreference(sharedPreferences)
                        .build();

                storageChooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String path) {
                        Log.e("SELECTLISTENER_PATH", path);
                    }
                });

                StorageChooserView.setViewSc(StorageChooserView.SC_LAYOUT_SHEET);

                storageChooser.show();
            }
        });

        TextView explanationText = (TextView) findViewById(R.id.explanation_text);
        TypefaceUtil.setTypefaceRegular(context, explanationText);
    }

    public static int getColorFromResource(Context context, int id) {
        return ContextCompat.getColor(context, id);
    }
}
