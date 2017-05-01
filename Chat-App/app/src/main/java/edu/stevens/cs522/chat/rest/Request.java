package edu.stevens.cs522.chat.rest;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.util.DateUtils;
import edu.stevens.cs522.chat.util.EnumUtils;

import static edu.stevens.cs522.chat.rest.Response.REQUEST_ID_HEADER;
import static edu.stevens.cs522.chat.rest.Response.ResponseType.POSTMESSAGE;
import static edu.stevens.cs522.chat.rest.Response.ResponseType.SYNCHRONIZE;
import static edu.stevens.cs522.chat.rest.Response.createResponse;

/**
 * Created by dduggan.
 */

public abstract class Request implements Parcelable {

    private final static String TAG = Request.class.getCanonicalName();

    public static enum RequestType {
        REGISTER("Register"),
        POST_MESSAGE("Post Message"),
        SYNCHRONIZE("Synchronize");
        private String value;
        private RequestType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    // Unique identifier (per device) for a request
    public long id;

    // Time stamp
    public Date timestamp;

    // Device coordinates
    public double longitude;

    public double latitude;

    // Output in case of errors
    public String responseMessage;


    protected Request() {
        this.id = nextRequest();
        this.timestamp = DateUtils.now();

    }

    protected Request(Request request) {
        id = request.id;
        timestamp = request.timestamp;
        longitude = request.longitude;
        latitude = request.latitude;
        responseMessage = request.responseMessage;
    }

    private static long requestCtr = 0;

    protected static synchronized long nextRequest() {
        return ++requestCtr;
    }

    protected Request(Parcel in) {
        // COMPLETED assume tag has already been read, this will be called by subclass constructor
        id = in.readLong();
        timestamp = new Date(in.readLong());
        longitude = in.readDouble();
        latitude = in.readDouble();
        responseMessage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // COMPLETED subclasses write tag, then call this, then write out their own fields
        dest.writeLong(id);
        dest.writeLong(timestamp.getTime());
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(responseMessage);
    }

    /*
     * HTTP request headers
     */
    public static String REQUEST_ID_HEADER = "X-Request-ID";

    public static String TIMESTAMP_HEADER = "X-Timestamp";

    public static String LONGITUDE_HEADER = "X-Longitude";

    public static String LATITUDE_HEADER = "X-Latitude";

    // App-specific HTTP request headers.
    public Map<String,String> getRequestHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put(REQUEST_ID_HEADER, Long.toString(id));
        headers.put(TIMESTAMP_HEADER, Long.toString(timestamp.getTime()));
        headers.put(LONGITUDE_HEADER, Double.toString(longitude));
        headers.put(LATITUDE_HEADER, Double.toString(latitude));
        return headers;
    }

    // JSON body (if not null) for request data not passed in headers.
    public abstract String getRequestEntity() throws IOException;

    // Define your own Response class, including HTTP response code.
    public abstract Response getResponse(HttpURLConnection connection,
                                         JsonReader rd /* Null for streaming */)
        throws IOException;

    public final Response getResponse(HttpURLConnection connection) throws IOException {
        return getResponse(connection, null);
    }

    public abstract Response process(RequestProcessor processor);

    public int describeContents() {
        return 0;
    }

    public static Request createRequest(Parcel in) {
        RequestType requestType = EnumUtils.readEnum(RequestType.class, in);
        switch (requestType) {
            case REGISTER:
                return new RegisterRequest(in);
            case POST_MESSAGE:
                return new PostMessageRequest(in);
            case SYNCHRONIZE:
                return new SynchronizeRequest(in);
            default:
                break;
        }
        throw new IllegalArgumentException("Unknown request type: "+requestType.name());
    }

    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
        public Request createFromParcel(Parcel in) {
            return createRequest(in);
        }

        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

}
