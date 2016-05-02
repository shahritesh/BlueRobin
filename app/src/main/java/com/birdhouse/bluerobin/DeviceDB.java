package com.birdhouse.bluerobin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by rshah on 4/29/16.
 */
public class DeviceDB extends SQLiteOpenHelper {

    public static final class Constants implements BaseColumns {
        public static final String DB_NAME = null; //in-memory db
        public static final int DB_VERSION = 1;
        public static final String TABLE_NAME = "devices";

        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SERIAL_NUM = "serialnum";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_INFO = "info";
        public static final String COLUMN_NAME_IP = "ip";

        public static final String SQL_TYPE_INT = "INTEGER";
        public static final String SQL_TYPE_TEXT = "TEXT";
        public static final String SQL_CREATE_DEV_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
                COLUMN_NAME_ID + " " + SQL_TYPE_INT + " PRIMARY KEY AUTOINCREMENT" + "," +
                COLUMN_NAME_NAME + " " + SQL_TYPE_TEXT + "," +
                COLUMN_NAME_SERIAL_NUM + " " + SQL_TYPE_TEXT + "," +
                COLUMN_NAME_TYPE + " " + SQL_TYPE_TEXT + "," +
                COLUMN_NAME_INFO + " " + SQL_TYPE_TEXT + "," +
                COLUMN_NAME_IP + " " + SQL_TYPE_TEXT +
                " )";
        public static final String SQL_DELETE_DEV_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final int DUMMY_DEVICE_ID = 1;
        public static final int REAL_DEVICE_START_ID = DUMMY_DEVICE_ID+1;
    }

    public class Device {

        public int id;
        public String name;
        public String serialnum;
        public String type;
        public String info;
        public String ip; //TODO: Convert to InetAddress
        public Device() {
            id = 0;
            name = serialnum = type = info = ip = "";
        }
        public Device(int i) {
            id = i;
            name = serialnum = type = info = ip = "";
        }
        public String toString() {
            String str = "id " + id + " name " + name + "serialnum " + serialnum +
                    "type " + type + "info " + info + "ip " + ip;
            return str;
        }
    }

    private Context _ctx;

    private static final String LOG_TAG_DeviceDB = DeviceDB.class.getSimpleName();

    public DeviceDB(Context ctx) {
        super(ctx, Constants.DB_NAME, null, Constants.DB_VERSION);
        _ctx = ctx;
    }

    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG_DeviceDB, "Database created");
        db.execSQL(Constants.SQL_CREATE_DEV_TABLE);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for discovered devices, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(Constants.SQL_DELETE_DEV_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int addDevice(Device dev) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        //Because Autoincrement is set on the key, no need to set
        //the device id
        values.put(Constants.COLUMN_NAME_NAME, dev.name);
        values.put(Constants.COLUMN_NAME_SERIAL_NUM, dev.serialnum);
        values.put(Constants.COLUMN_NAME_TYPE, dev.type);
        values.put(Constants.COLUMN_NAME_INFO, dev.info);
        values.put(Constants.COLUMN_NAME_IP, dev.ip);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = -1;

        try {

            Log.d(LOG_TAG_DeviceDB, "addDevice: inserting device " + dev.name);
            newRowId = db.insertOrThrow(
                    Constants.TABLE_NAME,
                    null,
                    values);
        } catch(SQLException e) {
            String msg = "Failed to add device entry for device : " + dev.toString() +
                    "exception : " + Log.getStackTraceString(e);

            Log.w(LOG_TAG_DeviceDB, msg);
        }

        Log.d(LOG_TAG_DeviceDB, "addDevice: device " + dev.name + " successfully");
        return (int)newRowId;

    }

    public int addDummyDevice() {
        Device dev = new Device();
        dev.name = "No Devices Found";

        return addDevice(dev);
    }

    public void addTestDevices() {

        Device dev = new Device();

        for (int i=0;i<10;i++) {
            dev.name = "item" + Integer.toString(i);
            dev.info = "Item Information ";
            dev.ip = "IP address";
            Log.d(LOG_TAG_DeviceDB, "adding device " + dev.name);
            addDevice(dev);
        }
    }

    public boolean removeDevice(int uiIndex) {
        //UI index starts with 0
        //Real devices start with 2
        return removeDevice(new Device(uiIndex + Constants.REAL_DEVICE_START_ID));
    }

    public boolean removeDevice(Device dev) {
        SQLiteDatabase db = getWritableDatabase();

        int numDeleted = db.delete(Constants.TABLE_NAME, Constants.COLUMN_NAME_ID + "=" + dev.id, null);

        if(numDeleted == 0) {
            String msg = "Failed to delete row : " + dev.toString();
            Log.w(LOG_TAG_DeviceDB, msg);
        }

        return (numDeleted > 0);

    }

    public boolean removeAllDevices() {
        SQLiteDatabase db = getWritableDatabase();
        int numDeleted = db.delete(Constants.TABLE_NAME,
                Constants.COLUMN_NAME_ID + ">=" + Integer.toString(Constants.REAL_DEVICE_START_ID),
                null);

        if(numDeleted == 0) {
            String msg = "Failed to delete any row";
            Log.w(LOG_TAG_DeviceDB, msg);
        }

        return (numDeleted > 0);

    }

    public Cursor getDeviceDBCursor() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME, null,
                Constants.COLUMN_NAME_ID + ">=" + Integer.toString(Constants.REAL_DEVICE_START_ID),
                null, null, null, null);

        if(cursor == null || cursor.getCount()==0) {
            cursor.close();

            cursor = db.query(Constants.TABLE_NAME, null,
                    Constants.COLUMN_NAME_ID + "=" + Integer.toString(Constants.DUMMY_DEVICE_ID),
                    null,null, null, null, null);
        }

        if(cursor != null) {
            Log.d(LOG_TAG_DeviceDB, "getDeviceCursor: returning NON-null cursor ");
        }else {
            Log.d(LOG_TAG_DeviceDB, "getDeviceCursor: returning null cursor ");
        }
        return cursor;

    }

}
