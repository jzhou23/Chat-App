package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.settings.Settings;


/**
 * Created by dduggan.
 */

public class ChatHelper {

    private static final String TAG_LOG = ChatHelper.class.getSimpleName();

    public static final String DEFAULT_CHAT_ROOM = "_default";

    private Context context;

    public ChatHelper(Context context) {
        this.context = context;
    }

    // COMPLETED provide a result receiver that will display a toast message upon completion
    public void register (String chatName, ResultReceiver receiver) {
        if (chatName != null && !chatName.isEmpty()) {
            Settings.saveChatName(context, chatName);
            RegisterRequest request = new RegisterRequest(chatName);
            Log.v(TAG_LOG, "RegisterRequest created");
            addRequest(request, receiver);
        }
    }

    // COMPLETED provide a result receiver that will display a toast message upon completion
    public void postMessage (String chatRoom, String text, ResultReceiver receiver) {
        if (text != null && !text.isEmpty()) {
            if (chatRoom == null || chatRoom.isEmpty()) {
                chatRoom = DEFAULT_CHAT_ROOM;
            }
            ChatMessage message = new ChatMessage(0, text, chatRoom, new Date(), 0.0, 0.0, Settings.getChatName(context));
            Log.v(TAG_LOG, "user name: " + Settings.getChatName(context));
            PostMessageRequest request = new PostMessageRequest(message);
            addRequest(request, receiver);
        }
    }

    private void addRequest(Request request, ResultReceiver receiver) {
        Log.v(TAG_LOG, "start Service");
        context.startService(createIntent(context, request, receiver));
    }

    private void addRequest(Request request) {
        addRequest(request, null);
    }

    /**
     * Use an intent to send the request to a background service. The request is included as a Parcelable extra in
     * the intent. The key for the intent extra is in the RequestService class.
     */
    public static Intent createIntent(Context context, Request request, ResultReceiver receiver) {
        Intent requestIntent = new Intent(context, RequestService.class);
        requestIntent.putExtra(RequestService.SERVICE_REQUEST_KEY, request);
        if (receiver != null) {
            requestIntent.putExtra(RequestService.RESULT_RECEIVER_KEY, receiver);
        }
        return requestIntent;
    }

    public static Intent createIntent(Context context, Request request) {
        return createIntent(context, request, null);
    }

}
