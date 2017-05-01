package edu.stevens.cs522.chat.rest;

import android.os.Parcel;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by dduggan.
 */

public class RegisterResponse extends Response {

    public RegisterResponse(HttpURLConnection connection) throws IOException {
        super(connection);
    }

    @Override
    public boolean isValid() { return true; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // COMPLETED
        super.writeToParcel(dest, flags);
    }

    public RegisterResponse(Parcel in) {
        // COMPLETED
        super(in);
    }

    public static Creator<RegisterResponse> CREATOR = new Creator<RegisterResponse>() {
        @Override
        public RegisterResponse createFromParcel(Parcel source) {
            return new RegisterResponse(source);
        }

        @Override
        public RegisterResponse[] newArray(int size) {
            return new RegisterResponse[size];
        }
    };
}
