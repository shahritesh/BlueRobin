package com.birdhouse.bluerobin;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by rshah on 5/1/16.
 */
public class DeviceContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = DeviceContentProvider.class.getName();

    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/devices");

    public static final String LOG_TAG_DeviceContentProvider = DeviceContentProvider.class.getSimpleName();

    private DeviceDB _deviceDB;

    private static final int DEVICES = 1;
    private static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "devices", DEVICES);
    }
    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG_DeviceContentProvider, "DeviceDB created in onCreate");
        _deviceDB = new DeviceDB(getContext());
        _deviceDB.addDummyDevice();
        _deviceDB.addTestDevices();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.d(LOG_TAG_DeviceContentProvider, "query called");
        if(uriMatcher.match(uri)==DEVICES){
            Log.d(LOG_TAG_DeviceContentProvider, "DeviceDB cursor returned in query");
            return _deviceDB.getDeviceDBCursor();
        }else{
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        return 0;
    }
}
