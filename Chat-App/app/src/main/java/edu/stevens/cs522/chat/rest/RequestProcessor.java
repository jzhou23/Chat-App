package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.database.Cursor;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.RequestManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.util.StringUtils;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private static final String TAG_LOG = RequestProcessor.class.getSimpleName();

    private Context context;

    private RestMethod restMethod;

    private RequestManager requestManager;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod =  new RestMethod(context);
        this.requestManager = new RequestManager(context);
    }

    public Response process(Request request) {
        return request.process(this);
    }

    public Response perform(RegisterRequest request) {
        return restMethod.perform(request);
    }

    public Response perform(PostMessageRequest request) {
        // We will just insert the message into the database, and rely on background sync to upload
        // return restMethod.perform(request)
        requestManager.persist(request.message);
        return request.getDummyResponse();
    }

    public Response perform(SynchronizeRequest request) {
        request.lastSequenceNumber = requestManager.getLastSequenceNumber();
        Log.v(TAG_LOG, "lastSequenceNumber: " + request.lastSequenceNumber);
        RestMethod.StreamingResponse response = null;
        final TypedCursor<ChatMessage> messages = requestManager.getUnsentMessages();
        final int numMessagesReplaced = messages.getCount();
        try {
            RestMethod.StreamingOutput out = new RestMethod.StreamingOutput() {
                @Override
                public void write(final OutputStream os) throws IOException {
                    try {
                        JsonWriter wr = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
                        wr.beginArray();
                        /*
                         * TODO stream unread messages to the server:
                         * {
                         *   chatroom : ...,
                         *   timestamp : ...,
                         *   latitude : ...,
                         *   longitude : ....,
                         *   text : ...
                         * }
                         */
                        Cursor cursor = messages.getCursor();
                        cursor.moveToFirst();
                        for (int i = 0; i < numMessagesReplaced; i++) {
                            wr.beginObject();
                            wr.name("chatroom").value(MessageContract.getChatRoom(cursor));
                            wr.name("timestamp").value(MessageContract.getTimeStamp(cursor));
                            wr.name("latitude").value(MessageContract.getLatitude(cursor));
                            wr.name("longitude").value(MessageContract.getLongitude(cursor));
                            wr.name("text").value(MessageContract.getMessageText(cursor));

                            Log.v(TAG_LOG, "num: " + i);
                            Log.v(TAG_LOG, "chatroom: " + MessageContract.getChatRoom(cursor));
                            Log.v(TAG_LOG, "timestamp: " + MessageContract.getTimeStamp(cursor));
                            Log.v(TAG_LOG, "latitude: " + MessageContract.getLatitude(cursor));
                            Log.v(TAG_LOG, "longitude: " + MessageContract.getLongitude(cursor));
                            Log.v(TAG_LOG, "text: " + MessageContract.getMessageText(cursor));

                            wr.endObject();

                            cursor.moveToNext();
                        }

                        wr.endArray();
                        Log.v("wrrrrrrrrrr", wr.toString());
                        wr.flush();
                    } finally {
                        messages.close();
                    }
                }
            };
            response = restMethod.perform(request, out);

            JsonReader rd = new JsonReader(new InputStreamReader(new BufferedInputStream(response.getInputStream()), StringUtils.CHARSET));
            // TODO parse data from server (messages and peers) and update database
            // See RequestManager for operations to help with this.

            try {
                List<Peer> peerList;
                List<ChatMessage> chatMessagesList;
                rd.beginObject();
                while(rd.hasNext()) {
                    String name = rd.nextName();
                    if (name.equals("clients")) {
                        peerList = readPeerList(rd);
                        requestManager.deletePeers();
                        requestManager.persistPeerList(peerList);
                    } else if (name.equals("messages")) {
                        chatMessagesList = readChatMessageList(rd);
                        requestManager.syncMessages(numMessagesReplaced, chatMessagesList);
                    } else {
                        rd.skipValue();
                    }
                }
                rd.endObject();

            } finally {
                rd.close();
            }

            return response.getResponse();

        } catch (IOException e) {
            return new ErrorResponse(request.id, e);

        } finally {
            if (response != null) {
                response.disconnect();
            }
        }
    }

    public Peer readPeer(JsonReader reader) throws IOException {
//        username:"joe",timestamp:...,latitude:..., longitude:...
        String username = null;
        long timestamp = 0;
        double latitude = 0.0;
        double longitude = 0.0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("username")) {
                username = reader.nextString();
            } else if (name.equals("timestamp")) {
                timestamp = reader.nextLong();
            } else if (name.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if (name.equals("longtitude")) {
                longitude = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return new Peer(username, timestamp, longitude, latitude);
    }

    public ChatMessage readMessage(JsonReader reader) throws IOException {
//        "chatroom":"_default",timestamp:...,latitude:..., longitude:..., "seqnum":1,"sender":"joe","text":"hello"}
        String chatroom = null;
        long timestamp = 0;
        double latitude = 0.0;
        double longtitude = 0.0;
        long seqnum = 0;
        String sender = null;
        String text = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("chatroom")) {
                chatroom = reader.nextString();
            } else if (name.equals("timestamp")) {
                timestamp = reader.nextLong();
            } else if (name.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if (name.equals("longtitude")) {
                longtitude = reader.nextDouble();
            } else if (name.equals("seqnum")) {
                seqnum = reader.nextLong();
            } else if (name.equals("sender")) {
                sender = reader.nextString();
            } else if (name.equals("text")) {
                text = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return new ChatMessage(seqnum, text, chatroom, new Date(timestamp), longtitude, latitude, sender);
    }


    public List<Peer> readPeerList(JsonReader reader) throws IOException {
        List<Peer> peerList = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()) {
            peerList.add(readPeer(reader));
        }
        reader.endArray();
        return peerList;
    }

    public List<ChatMessage> readChatMessageList(JsonReader reader) throws IOException {
        List<ChatMessage> chatMessageList = new ArrayList<>();

        reader.beginArray();
        while(reader.hasNext()) {
            chatMessageList.add(readMessage(reader));
        }
        reader.endArray();;
        return chatMessageList;
    }
}
