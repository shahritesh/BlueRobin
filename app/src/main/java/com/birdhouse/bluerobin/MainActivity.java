package com.birdhouse.bluerobin;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private ListView _listView;
    private SimpleCursorAdapter _adapter;
    private static final String LOG_TAG_MainActivity = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _listView = (ListView) findViewById(R.id.devicelist);

        _listView.setLongClickable(true);

        displayDevices();

        /** Creating a loader for populating listview from sqlite database */
        /** This statement, invokes the method onCreatedLoader() */
        Log.d(LOG_TAG_MainActivity, "LoaderManager started");
        getSupportLoaderManager().initLoader(0, null, this);

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }*/

    /** A callback method invoked by the loader when initLoader() is called */
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri uri = DeviceContentProvider.CONTENT_URI;
        Log.d(LOG_TAG_MainActivity, "Cursor loader returned");
        return new CursorLoader(this, uri, null, null, null, null);
    }

    /** A callback method, invoked after the requested content provider returned all the data */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        _adapter.swapCursor(arg1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        _adapter.swapCursor(null);
    }

    protected void displayDevices() {

        //registerForContextMenu(_listView); //TODO:register context menu to ignore any device
        String[] fromColumns = { DeviceDB.Constants.COLUMN_NAME_NAME,
                DeviceDB.Constants.COLUMN_NAME_INFO,
                DeviceDB.Constants.COLUMN_NAME_IP };
        int[] to = { R.id.devname, R.id.devinfo, R.id.ipaddr };

        _adapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.device_item,
                null,
                fromColumns,
                to, 0);

        Log.d(LOG_TAG_MainActivity, "list adapter set");
        _listView.setAdapter(_adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
