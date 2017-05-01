package edu.stevens.cs522.chat.managers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.List;

import edu.stevens.cs522.chat.async.AsyncContentResolver;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chat.async.SimpleQueryBuilder;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.Peer;


/**
 * Created by dduggan.
 */

public class PeerManager extends Manager<Peer> {

    private static final String TAG_LOG = PeerManager.class.getSimpleName();

    private static final int LOADER_ID = 2;

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public PeerManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllPeersAsync(IQueryListener<Peer> listener) {
        // COMPLETED use QueryBuilder to complete this
        executeQuery(PeerContract.CONTENT_URI, LOADER_ID, creator, listener);
    }

    public void getPeerAsync(long id, IContinue<Peer> callback) {
        // TODO need to check that peer is not null (not in database)
    }

    public void persistAsync(Peer peer, IContinue<Long> callback) {
        // COMPLETED need to ensure the peer is not already in the database
        final String selection = PeerContract.COLUMN_NAME + "=?";
        final String[] selectionArgs = new String[]{String.valueOf(peer.name)};
        executeSimpleQuery(PeerContract.CONTENT_URI, null, selection, selectionArgs, creator, new SimpleQueryBuilder.ISimpleQueryListener<Peer>() {
            @Override
            public void handleResults(List<Peer> results) {
                if (results != null && results.size() != 0) {
                    long id = results.get(0).id;
                    // UPDATE
                    ContentValues values = new ContentValues();
                    results.get(0).writeToProvider(values);
                    getAsyncResolver().updateAsync(ContentUris.withAppendedId(PeerContract.CONTENT_URI, id), values, selection, selectionArgs);
                    Log.v("PeerManager", "update succeed");
                } else {
                    // INSERT
                    ContentValues values = new ContentValues();
                    getAsyncResolver().insertAsync(PeerContract.CONTENT_URI, values, new IContinue<Uri>() {
                        @Override
                        public void kontinue(Uri value) {
                            Log.v("PeerManager", "insert succeed");
                        }
                    });
                }
            }
        });
    }

}
