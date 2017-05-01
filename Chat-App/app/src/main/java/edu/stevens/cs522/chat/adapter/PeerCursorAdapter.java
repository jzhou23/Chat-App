package edu.stevens.cs522.chat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Date;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.util.DateUtils;

/**
 * Created by jhzhou on 4/8/17.
 */

public class PeerCursorAdapter extends CursorAdapter {
    public PeerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.view_peer, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.view_user_name);
        String name = PeerContract.getName(cursor);
        nameTextView.setText(name);

        TextView timestampTextView = (TextView) view.findViewById(R.id.view_timestamp);
        String timeStamp = DateUtils.toDisplayDate(new Date(PeerContract.getTimeStamp(cursor)));
        timestampTextView.setText(timeStamp);
    }
}
