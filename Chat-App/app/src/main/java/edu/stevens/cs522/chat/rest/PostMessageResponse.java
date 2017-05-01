package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.util.JsonReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.UUID;

import edu.stevens.cs522.chat.util.StringUtils;

/**
 * Created by dduggan.
 */

public class PostMessageResponse extends Response {

    public static final String ID_LABEL = "id";

    private long seqNum;

    public PostMessageResponse(HttpURLConnection connection, JsonReader rd) throws IOException {
        super(connection);
        try {
            rd.beginObject();
            String label = rd.nextName();
            if (!ID_LABEL.equals(label)) {
                throw new IllegalStateException("Expected label "+ID_LABEL+", encountered "+label);
            };
            seqNum = rd.nextLong();
            rd.endObject();
        } finally {
            rd.close();
        }
    }

    @Override
    public boolean isValid() { return true; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
    }

    public PostMessageResponse(Parcel in) {
        super(in);
        // TODO
    }

    public static Creator<PostMessageResponse> CREATOR = new Creator<PostMessageResponse>() {
        @Override
        public PostMessageResponse createFromParcel(Parcel source) {
            return new PostMessageResponse(source);
        }

        @Override
        public PostMessageResponse[] newArray(int size) {
            return new PostMessageResponse[size];
        }
    };
}
