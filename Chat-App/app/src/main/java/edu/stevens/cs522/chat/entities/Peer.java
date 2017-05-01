package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chat.contracts.PeerContract;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    public long id;
    // Use as PK
    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    public Peer() {

    }

    public Peer(String name, Long timestamp, Double longitude, Double latitude) {
        this.name = name;
        this.timestamp = new Date(timestamp);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // COMPLETED add operations for parcels (Parcelable), cursors and contentvalues

    public Peer(Cursor cursor) {
        // COMPLETED
        this.id = PeerContract.getId(cursor);
        this.name = PeerContract.getName(cursor);
        this.timestamp = new Date(PeerContract.getTimeStamp(cursor));
        this.longitude = PeerContract.getLongitude(cursor);
        this.latitude = PeerContract.getLatitude(cursor);
    }

    public void writeToProvider(ContentValues values) {
        values.put(PeerContract.COLUMN_NAME, name);
        values.put(PeerContract.COLUMN_TIMESTAMP, timestamp.getTime());
        values.put(PeerContract.COLUMN_LONGITUDE, longitude);
        values.put(PeerContract.COLUMN_LATITUDE, latitude);
    }

    protected Peer(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.timestamp = new Date(in.readLong());
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {
        @Override
        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // COMPLETED
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(timestamp.getTime());
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }
}
