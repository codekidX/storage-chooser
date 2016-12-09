package com.codekidlabs.storagechooserdemo;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.codekidlabs.storagechooser.ExternalStoragePathFinder;
import com.codekidlabs.storagechooser.StorageChooserBuilder;
import com.codekidlabs.storagechooser.utils.MemoryUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(DialogFragment.STYLE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Button storageChooserButton = (Button) findViewById(R.id.storage_chooser_button);
        

        storageChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageChooserBuilder.Builder builder = new StorageChooserBuilder.Builder()
                        .withActivity(MainActivity.this)
                        .withFragmentManager(getSupportFragmentManager())
                        .withPredefinedPath("/Downloads/OpenGApps")
                        .actionSave(true)
                        .withPreference(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()))
                        .build();
                builder.show();
            }
        });

        Button storageChooserButtonMemorybar = (Button) findViewById(R.id.storage_chooser_button_memorybar);


        storageChooserButtonMemorybar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageChooserBuilder.Builder builder = new StorageChooserBuilder.Builder()
                        .withActivity(MainActivity.this)
                        .withFragmentManager(getSupportFragmentManager())
                        .withMemoryBar(true)
                        .withPredefinedPath("/Downloads/OpenGApps")
                        .withPreference(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()))
                        .actionSave(true)
                        .build();
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
