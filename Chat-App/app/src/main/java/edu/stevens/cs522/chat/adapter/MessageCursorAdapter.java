package edu.stevens.cs522.chat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.contracts.MessageContract;

/**
 * Created by jhzhou on 4/6/17.
 */

public class MessageCursorAdapter extends CursorAdapter {
    public MessageCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.message, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView messageTextView = (TextView) view.findViewById(R.id.message_text_item);
        String messageText = MessageContract.getMessageText(cursor);
        messageTextView.setText(messageText);
    }
}
