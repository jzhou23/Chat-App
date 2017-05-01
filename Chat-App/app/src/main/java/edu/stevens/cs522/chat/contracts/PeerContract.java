package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dduggan.
 */

public class PeerContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Peer");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    // COMPLETED define column names, getters for cursors, setters for contentvalues
    public static final String COLUMN_ID = _ID;

    public static final String COLUMN_NAME = "name";

    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String COLUMN_LONGITUDE = "longitude";

    public static final String COLUMN_LATITUDE = "latitude";

    public static final String[] COLUMNS = {COLUMN_NAME, COLUMN_TIMESTAMP, COLUMN_LONGITUDE, COLUMN_LATITUDE};

    // id
    public static long getId(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
    }

    // name
    public static String getName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
    }

    public static void putName(ContentValues values, String name) {
        values.put(COLUMN_NAME, name);
    }

    // timestamp
    public static long getTimeStamp(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
    }

    public static void putTimeStamp(ContentValues values, long time) {
        values.put(COLUMN_TIMESTAMP, time);
    }

    // longitude
    public static double getLongitude(Cursor cursor) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));
    }

    public static void putLongitude(ContentValues values, double longitude) {
        values.put(COLUMN_LONGITUDE, longitude);
    }

    // latitude
    public static double getLatitude(Cursor cursor) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
    }

    public static void putLatitude(ContentValues values, double latitude) {
        values.put(COLUMN_LATITUDE, latitude);
    }
}
