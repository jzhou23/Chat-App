package edu.stevens.cs522.chat.rest;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.ResultReceiver;
import android.util.Log;

import static android.content.Intent.ACTION_SEND;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RequestService extends IntentService {

    private static final String TAG_LOG = RequestService.class.getSimpleName();

    public static final String SERVICE_REQUEST_KEY = "edu.stevens.cs522.chat.rest.extra.REQUEST";

    public static final String RESULT_RECEIVER_KEY = "edu.stevens.cs522.chat.rest.extra.RECEIVER";

    private RequestProcessor processor;

    public RequestService() {
        super("RequestService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        processor = new RequestProcessor(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Request request = intent.getParcelableExtra(SERVICE_REQUEST_KEY);
        Log.v(TAG_LOG, "get request");
        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER_KEY);
        Log.v(TAG_LOG, "get receiver");

        Response response = processor.process(request);

        if (receiver != null) {
            // COMPLETED UI should display a toast message on completion of the operation
            int status = response.httpResponseCode;
            if (status < 200 || status >= 300) {
                Log.v("RequestService", "response failed");
                receiver.send(Activity.RESULT_CANCELED, null);
            } else {
                Log.v("RequestService", "response succeed");
                receiver.send(Activity.RESULT_OK, null);
            }
        }
    }

}
