package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;

import edu.stevens.cs522.chat.entities.ChatMessage;

/**
 * Created by dduggan.
 */

public class SynchronizeRequest extends Request {

//    public ChatMessage message;

    // Added by request processor
    public long lastSequenceNumber;

//    public SynchronizeRequest(ChatMessage message) {
//        super();
//        this.message = message;
//    }

    public SynchronizeRequest(long seqnum) {
        this.lastSequenceNumber = seqnum;
    }

    @Override
    public String getRequestEntity() throws IOException {
        // We stream output for SYNC, so this always returns null
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        assert rd == null;
        return new SynchronizeResponse(connection);
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
//        dest.writeParcelable(message, flags);
        dest.writeLong(lastSequenceNumber);
    }

    public SynchronizeRequest() {
        super();
    }

    public SynchronizeRequest(Parcel in) {
        super(in);
        // COMPLETED
//        message = (ChatMessage) in.readParcelable(ChatMessage.class.getClassLoader());
        lastSequenceNumber = in.readLong();
    }

    public static Creator<SynchronizeRequest> CREATOR = new Creator<SynchronizeRequest>() {
        @Override
        public SynchronizeRequest createFromParcel(Parcel source) {
            return new SynchronizeRequest(source);
        }

        @Override
        public SynchronizeRequest[] newArray(int size) {
            return new SynchronizeRequest[size];
        }
    };

}
