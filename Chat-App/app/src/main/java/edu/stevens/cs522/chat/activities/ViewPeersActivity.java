package edu.stevens.cs522.chat.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.adapter.PeerCursorAdapter;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, QueryBuilder.IQueryListener<Peer> {

    /*
     * COMPLETED See ChatActivity for example of what to do, query peers database instead of messages database.
     */

    @BindView(R.id.peerList) ListView peerList;
    @BindView(R.id.peerList_empty_view) TextView emptyView;

    private PeerManager peerManager;

    private PeerCursorAdapter peerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);
        ButterKnife.bind(this);

        // COMPLETED initialize peerAdapter with empty cursor (null)

        peerAdapter = new PeerCursorAdapter(this, null);
        peerList.setAdapter(peerAdapter);
        peerList.setEmptyView(emptyView);

        peerManager = new PeerManager(this);
        peerManager.getAllPeersAsync(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }

    @Override
    public void handleResults(TypedCursor<Peer> results) {
        // COMPLETED
        peerAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // COMPLETED
        peerAdapter.swapCursor(null);
    }
}
