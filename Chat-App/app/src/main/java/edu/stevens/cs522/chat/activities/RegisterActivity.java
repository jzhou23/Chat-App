/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class RegisterActivity extends Activity implements OnClickListener, ResultReceiverWrapper.IReceive {

	final static public String TAG_LOG = RegisterActivity.class.getSimpleName();

    private static final String SERVER_URI = "http://jiahuangs-mbp.fios-router.home:8080/chat";
		
    /*
     * Widgets for dest address, message text, send button.
     */
    @BindView(R.id.client_id_text) TextView clientIdText;
    @BindView(R.id.chat_name_text) EditText userNameText;
    @BindView(R.id.server_uri_text) EditText serverUriText;
    @BindView(R.id.register_button) Button registerButton;

    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when registered.
     */
    private ResultReceiverWrapper registerResultReceiver;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Initialize settings to default values.
         */
		if (Settings.isRegistered(this)) {
			finish();
            return;
		}

        setContentView(R.layout.register);
        ButterKnife.bind(this);
        // COMPLETED instantiate helper for service
        helper = new ChatHelper(this);
        // COMPLETED initialize registerResultReceiver
        registerResultReceiver = new ResultReceiverWrapper(new Handler());

        // COMPLETED get references to views
        clientIdText.setText(Settings.getClientId(this).toString());
        userNameText = (EditText) findViewById(R.id.chat_name_text);

        //DELETE
        serverUriText.setText(SERVER_URI);
        registerButton.setOnClickListener(this);
    }

	public void onResume() {
        super.onResume();
        registerResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        registerResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * Callback for the REGISTER button.
     */
    public void onClick(View v) {
        if (helper != null) {

            String userName = null;
            String serverUri = null;

            // COMPLETED get server URI and userName from UI, and use helper to register
            userName = userNameText.getText().toString();
            serverUri = serverUriText.getText().toString();

            // COMPLETED set registered in settings upon completion
            helper.register(userName, registerResultReceiver);

            Settings.saveChatName(this, userName);
            Log.v(TAG_LOG, "userName: " + userName);
            Settings.saveServerUri(this, serverUri);
            Log.v(TAG_LOG, "serverUri: " + serverUri);
            // End

            Log.i(TAG_LOG, "Registered: " + userName);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case RESULT_OK:
                // COMPLETED set registered in settings upon completion
                Log.v(TAG_LOG, "setRegistered");
                Settings.setRegistered(this, true);
                Settings.saveServerUri(this, serverUriText.getText().toString());
                // COMPLETED show a success toast message
                Toast.makeText(this, "Registered Succeed", Toast.LENGTH_SHORT).show();
                break;

            default:
                // COMPLETED show a failure toast message
                Toast.makeText(this, "Registered Failed", Toast.LENGTH_SHORT).show();
                break;
        }

        finish();
    }

}