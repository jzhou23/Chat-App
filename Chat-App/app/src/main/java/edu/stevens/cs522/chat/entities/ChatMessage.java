package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable{

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    // Sender username and FK (in local database)
    public String sender;

    public ChatMessage() {

    }

    public ChatMessage(long seqNum, String messageText, String chatRoom, Date timestamp, Double longitude, Double latitude, String sender) {
        this.seqNum = seqNum;
        this.messageText = messageText;
        this.chatRoom = chatRoom;
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.sender = sender;
    }

    // COMPLETED add operations for parcels (Parcelable), cursors and contentvalues

    public ChatMessage(Cursor cursor) {
        // COMPLETED
        this.id = MessageContract.getId(cursor);
        this.seqNum = MessageContract.getSequenceNumber(cursor);
        this.messageText = MessageContract.getMessageText(cursor);
        this.chatRoom = MessageContract.getChatRoom(cursor);
        this.timestamp = new Date(MessageContract.getTimeStamp(cursor));
        this.longitude = MessageContract.getLongitude(cursor);
        this.latitude = MessageContract.getLatitude(cursor);
        this.sender = MessageContract.getSender(cursor);
    }

    protected ChatMessage(Parcel in) {
        id = in.readLong();
        seqNum = in.readLong();
        messageText = in.readString();
        chatRoom = in.readString();
        timestamp = new Date(in.readLong());
        longitude = in.readDouble();
        latitude = in.readDouble();
        sender = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(seqNum);
        dest.writeString(messageText);
        dest.writeString(chatRoom);
        dest.writeLong(timestamp.getTime());
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(sender);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    public void writeToProvider(ContentValues values) {
        // COMPLETED
        values.put(MessageContract.COLUMN_SEQUENCE_NUMBER, seqNum);
        values.put(MessageContract.COLUMN_MESSAGE_TEXT, messageText);
        values.put(MessageContract.COLUMN_CHAT_ROOM, chatRoom);
        values.put(MessageContract.COLUMN_TIMESTAMP, timestamp.getTime());
        values.put(MessageContract.COLUMN_LONGITUDE, longitude);
        values.put(MessageContract.COLUMN_LATITUDE, latitude);
        values.put(MessageContract.COLUMN_SENDER, sender);
    }

}
