package edu.stevens.cs522.chat.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chat.contracts.BaseContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.ChatMessage;

public class ChatProvider extends ContentProvider {

    private static final String TAG_LOG = ChatProvider.class.getSimpleName();

    public ChatProvider() {

    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String MESSAGE_CONTENT_PATH_SYNC = MessageContract.CONTENT_PATH_SYNC;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 1;

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int MESSAGES_SYNC = 3;
    private static final int PEERS_ALL_ROWS = 4;
    private static final int PEERS_SINGLE_ROW = 5;

    public static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // COMPLETED initialize database tables
            final String SQL_CREATE_PEERS_TABLE = "CREATE TABLE " + PEERS_TABLE + " ( " +
                    PeerContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PeerContract.COLUMN_NAME + " TEXT NOT NULL, " +
                    PeerContract.COLUMN_TIMESTAMP + " INTEGER, " +
                    PeerContract.COLUMN_LONGITUDE + " REAL, " +
                    PeerContract.COLUMN_LATITUDE + " READ);";

            final String SQL_CREATE_MESSAGES_TABLE = "CREATE TABLE " + MESSAGES_TABLE + " ( " +
                    MessageContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MessageContract.COLUMN_SEQUENCE_NUMBER + " INTEGER NOT NULL DEFAULT 0, " +
                    MessageContract.COLUMN_MESSAGE_TEXT + " TEXT, " +
                    MessageContract.COLUMN_CHAT_ROOM + " TEXT NOT NULL, " +
                    MessageContract.COLUMN_TIMESTAMP + " INTEGER, " +
                    MessageContract.COLUMN_LONGITUDE + " REAL, " +
                    MessageContract.COLUMN_LATITUDE + " REAL, " +
                    MessageContract.COLUMN_SENDER + " INTEGER NOT NULL);";

            db.execSQL(SQL_CREATE_PEERS_TABLE);
            db.execSQL(SQL_CREATE_MESSAGES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // COMPLETED upgrade database if necessary
            if (newVersion != oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + PEERS_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
                onCreate(db);
            }
        }
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_SYNC, MESSAGES_SYNC);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // COMPLETED: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + MESSAGE_CONTENT_PATH;
            case MESSAGES_SINGLE_ROW:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + MESSAGE_CONTENT_PATH;
            case MESSAGES_SYNC:
                // COMPLETED
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + MESSAGE_CONTENT_PATH_SYNC;
            case PEERS_ALL_ROWS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PEER_CONTENT_PATH;
            case PEERS_SINGLE_ROW:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PEER_CONTENT_PATH;
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // COMPLETED: Implement this to handle requests to insert a new message.
                // Make sure to notify any observers
                id = db.insert(MESSAGES_TABLE, null, values);
                if (id == -1) {
                    throw new UnsupportedOperationException("Not yet implemented");
                }
                break;

            case PEERS_ALL_ROWS:
                // COMPLETED: Implement this to handle requests to insert a new peer.
                // Make sure to notify any observers
                id = db.insert(PEERS_TABLE, null, values);
                if (id == -1) {
                    throw new UnsupportedOperationException("Not yet implemented");
                }
                break;

            case MESSAGES_SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // COMPLETED: Implement this to handle query of all messages.
                cursor = db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PEERS_ALL_ROWS:
                // COMPLETED: Implement this to handle query of all peers.
                cursor = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MESSAGES_SINGLE_ROW:
                // COMPLETED: Implement this to handle query of a specific message.
                selection = MessageContract.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
//                throw new UnsupportedOperationException("Not yet implemented");

            case PEERS_SINGLE_ROW:
                // COMPLETED: Implement this to handle query of a specific peer.
                selection = PeerContract.COLUMN_NAME + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
//                throw new UnsupportedOperationException("Not yet implemented");

            default:
                throw new IllegalStateException("insert: bad case");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // COMPLETED Implement this to handle requests to update one or more rows.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRow = 0;
        int match = uriMatcher.match(uri);
        if (values.size() == 0) {
            return 0;
        }

        switch (match) {
            case MESSAGES_ALL_ROWS:
                updatedRow = db.update(MESSAGES_TABLE, values, selection, selectionArgs);

            case MESSAGES_SINGLE_ROW:
                selection = MessageContract.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                updatedRow = db.update(MESSAGES_TABLE, values, selection, selectionArgs);

            case PEERS_ALL_ROWS:
                updatedRow = db.update(PEERS_TABLE, values, selection, selectionArgs);

            case PEERS_SINGLE_ROW:
                selection = PeerContract.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                updatedRow = db.update(PEERS_TABLE, values, selection, selectionArgs);

        }

        if (updatedRow != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRow;
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // COMPLETED Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = 0;
        int match = uriMatcher.match(uri);

        switch (match) {
            case MESSAGES_ALL_ROWS:
                deletedRows = db.delete(MESSAGES_TABLE, selection, selectionArgs);
                break;

            case PEERS_ALL_ROWS:
                deletedRows = db.delete(PEERS_TABLE, selection, selectionArgs);
                break;

            case MESSAGES_SINGLE_ROW:
                selection = MessageContract.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = db.delete(MESSAGES_TABLE, selection, selectionArgs);
                break;

            case PEERS_SINGLE_ROW:
                selection = PeerContract.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = db.delete(PEERS_TABLE, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
//            throw new UnsupportedOperationException("Not yet implemented");
        }

        return deletedRows;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] records) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numReplacedMessages = 0;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_SYNC:
                /*
                 * Do all of this in a single transaction.
                 */
                db.beginTransaction();
                try {

                    /*
                     * Delete the first N messages with sequence number = 0, where N = records.length.
                     */
                    numReplacedMessages = Integer.parseInt(uri.getLastPathSegment());

                    String[] columns = {MessageContract.COLUMN_ID};
                    String selection = MessageContract.COLUMN_SEQUENCE_NUMBER + "=0";
                    Cursor cursor = db.query(MESSAGES_TABLE, columns, selection, null, null, null, MessageContract.COLUMN_TIMESTAMP);
                    Log.v(TAG_LOG, "deleting the messages");
                    try {
                        if (numReplacedMessages > 0 && cursor.moveToFirst()) {
                            do {
                                String deleteSelection = MessageContract.COLUMN_ID + "=" + Long.toString(cursor.getLong(0));
                                db.delete(MESSAGES_TABLE, deleteSelection, null);
                                numReplacedMessages--;
                            } while (numReplacedMessages > 0 && cursor.moveToNext());
                        }
                    } finally {
                        cursor.close();
                    }

                    /*
                     * Insert the messages downloaded from server, which will include replacements for deleted records.
                     */
                    Log.v(TAG_LOG, "inserting the updated messages");
                    for (ContentValues values: records) {
                        Log.v(TAG_LOG, "sequence number: " + values.get(MessageContract.COLUMN_SEQUENCE_NUMBER));
                        Log.v(TAG_LOG, "messageText: " + values.get(MessageContract.COLUMN_MESSAGE_TEXT));
                        Log.v(TAG_LOG, "chatRoom: " + values.get(MessageContract.COLUMN_CHAT_ROOM));
                        Log.v(TAG_LOG, "timestamp.getTime(): " + values.get(MessageContract.COLUMN_TIMESTAMP));
                        Log.v(TAG_LOG, "longitude: " + values.get(MessageContract.COLUMN_LONGITUDE));
                        Log.v(TAG_LOG, "latitude: " + values.get(MessageContract.COLUMN_LATITUDE));
                        Log.v(TAG_LOG, "sender: " + values.get(MessageContract.COLUMN_SENDER));
                    }

                    for (ContentValues record : records) {
//                        if (db.insert(MESSAGES_TABLE, null, record) != 1) {
//                            Log.v(TAG_LOG, "messageText: " + record.get(MessageContract.COLUMN_MESSAGE_TEXT));
//                            throw new IllegalStateException("Failure to insert updated chat message record!");
//                        }
                        if (db.insert(MESSAGES_TABLE, null, record) == -1) {
                            throw new IllegalStateException("Failure to insert updated chat message record!");
                        }
                    }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                // COMPLETED Make sure to notify any observers
                getContext().getContentResolver().notifyChange(uri, null);
                break;

            default:
                throw new IllegalStateException("insert: bad case");
        }

        return numReplacedMessages;
    }

}
