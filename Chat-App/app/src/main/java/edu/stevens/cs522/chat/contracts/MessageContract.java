package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import static android.R.attr.id;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));

    /*
     * A special URI for replacing messages after sequence numbers are assigned by server.
     * The number in the URI specifies how many messages to be replaced after server assigns seq numbers.
     */
    private static final Uri CONTENT_URI_SYNC = withExtendedPath(CONTENT_URI, "sync");

    public static final Uri CONTENT_URI_SYNC(int id) {
        return CONTENT_URI_SYNC(Integer.toString(id));
    }

    private static final Uri CONTENT_URI_SYNC(String id) {
        return withExtendedPath(CONTENT_URI_SYNC, id);
    }

    public static final String CONTENT_PATH_SYNC = CONTENT_PATH(CONTENT_URI_SYNC("#"));


    public static final String COLUMN_ID = _ID;

    public static final String COLUMN_SEQUENCE_NUMBER = "sequence_number";

    public static final String COLUMN_MESSAGE_TEXT = "message_text";

    public static final String COLUMN_CHAT_ROOM = "chat_room";

    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String COLUMN_LATITUDE = "latitude";

    public static final String COLUMN_LONGITUDE = "longitude";

    public static final String COLUMN_SENDER = "sender";

    public static final String[] COLUMNS = {COLUMN_ID, COLUMN_SEQUENCE_NUMBER, COLUMN_MESSAGE_TEXT, COLUMN_CHAT_ROOM, COLUMN_TIMESTAMP, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_SENDER};

    // id
    public static long getId(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
    }

    // sequence number
    private static int sequenceNumberColumn = -1;

    public static long getSequenceNumber(Cursor cursor) {
        if (sequenceNumberColumn < 0) {
            sequenceNumberColumn = cursor.getColumnIndexOrThrow(COLUMN_SEQUENCE_NUMBER);
        }
        return cursor.getLong(sequenceNumberColumn);
    }

    public static void putSequenceNumberColumn(ContentValues out, String messageText) {
        out.put(COLUMN_SEQUENCE_NUMBER, messageText);
    }

    // message text
    private static int messageTextColumn = -1;

    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(COLUMN_MESSAGE_TEXT, messageText);
    }

    // COMPLETED remaining getter and putter operations for other columns

    // chat room
    public static String getChatRoom(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHAT_ROOM));
    }

    public static void putChatRoom(ContentValues values, String chatRoom) {
        values.put(COLUMN_CHAT_ROOM, chatRoom);
    }

    // timestamp
    public static long getTimeStamp(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
    }

    public static void putTimeStamp(ContentValues values, long time) {
        values.put(COLUMN_TIMESTAMP, time);
    }

    // latitude
    public static double getLatitude(Cursor cursor) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
    }

    public static void putLatitude(ContentValues values, double latitude) {
        values.put(COLUMN_LATITUDE, latitude);
    }

    // longitude
    public static double getLongitude(Cursor cursor) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));
    }

    public static void putLongitude(ContentValues values, double longitude) {
        values.put(COLUMN_LONGITUDE, longitude);
    }

    // sender
    public static String getSender(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENDER));
    }

    public static void putSender(ContentValues values, String sender) {
        values.put(COLUMN_SENDER, sender);
    }
}
