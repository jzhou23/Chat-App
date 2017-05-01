package edu.stevens.cs522.chat.rest;

import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.util.DateUtils;

/**
 * Created by dduggan.
 */

public class PostMessageRequest extends Request {

    public ChatMessage message;

    public PostMessageRequest(ChatMessage message) {
        super();
        this.message = message;
    }

    @Override
    public String getRequestEntity() throws IOException {
        StringWriter wr = new StringWriter();
        JsonWriter jw = new JsonWriter(wr);
        // COMPLETED write a JSON message of the form:
        // { "room" : <chat-room-name>, "message" : <message-text> }
        write(jw);
        String res = wr.toString();
        Log.v("PostMessageRequest", "json: " + res);
        if (res == null || TextUtils.isEmpty(res)) {
            return null;
        }
        return res;
    }

    public void write(JsonWriter wr) throws IOException {
//        chatroom" : "_default", "timestamp":...,"latitude":...,"longitude":..., "text" : "hello"
        wr.beginObject();

        wr.name("chatroom");
        wr.value(message.chatRoom);

        wr.name("timestamp");
        wr.value(message.timestamp.getTime());

        wr.name("latitdue");
        wr.value(message.latitude);

        wr.name("longitude");
        wr.value(message.latitude);

        wr.name("text");
        wr.value(message.messageText);

        wr.endObject();
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        throw new IllegalStateException("PostMessage request should only return dummy response");
    }

    public Response getDummyResponse() {
        return new DummyResponse(id);
    }

    @Override
    public Response process(RequestProcessor processor) {
        return processor.perform(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // COMPLETED
        super.writeToParcel(dest, flags);
        dest.writeParcelable(message, flags);
    }

    public PostMessageRequest() {
        super();
    }

    public PostMessageRequest(Parcel in) {
        super(in);
        // COMPLETED
        message = (ChatMessage) in.readParcelable(ChatMessage.class.getClassLoader());
    }

    public static Creator<PostMessageRequest> CREATOR = new Creator<PostMessageRequest>() {
        @Override
        public PostMessageRequest createFromParcel(Parcel source) {
            return new PostMessageRequest(source);
        }

        @Override
        public PostMessageRequest[] newArray(int size) {
            return new PostMessageRequest[size];
        }
    };

}
