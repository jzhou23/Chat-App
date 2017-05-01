package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import edu.stevens.cs522.chat.async.AsyncContentResolver;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<ChatMessage> {

    private static final String TAG_LOG = MessageManager.class.getSimpleName();

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<ChatMessage> creator = new IEntityCreator<ChatMessage>() {
        @Override
        public ChatMessage create(Cursor cursor) {
            return new ChatMessage(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllMessagesAsync(IQueryListener<ChatMessage> listener) {
        // COMPLETED use QueryBuilder to complete this
        QueryBuilder.executeQuery(TAG_LOG, (Activity) context, MessageContract.CONTENT_URI, LOADER_ID, creator, listener);
    }

    public void persistAsync(ChatMessage message) {
        // COMPLETED
        ContentValues contentValues = new ContentValues();
        message.writeToProvider(contentValues);

        contentResolver.insertAsync(MessageContract.CONTENT_URI, contentValues, null);
    }

}
