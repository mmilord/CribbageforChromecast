package cast.chrome.cribbage.cribbageforchromecast.NewStuff;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cast.chrome.cribbage.cribbageforchromecast.Interfaces.CastReceiver;
import cast.chrome.cribbage.cribbageforchromecast.PrimaryActivity;
import cast.chrome.cribbage.cribbageforchromecast.R;


/**
 * Created by interns on 5/10/15.
 */
public class ChromecastControl extends MediaRouter.Callback implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = PrimaryActivity.class.getSimpleName();

    private static final String GAME_NAMESPACE = "cast.chrome.cribbage.cribbageforchromecast";
    private static final String KEY_COMMAND = "command";

    private Context context;
    private CastReceiver castReceiver;
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private CastDevice mSelectedDevice;
    private GoogleApiClient mApiClient;
    private Cast.Listener mCastListener;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private ConnectionFailedListener mConnectionFailedListener;
    private ChromecastManagementChannel mChromecastManagementChannel;
    private boolean mApplicationStarted;
    private boolean mWaitingForReconnect;
    private String mSessionId;

    private CastToGSListener castToGSListener;


    public ChromecastControl (Context c, String appID, String tag) {
        context = c;
        //this.castReceiver = castReceiver;

        // Configure Cast device discovery
        mMediaRouter = MediaRouter.getInstance(context);
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(appID)).build();
        mMediaRouterCallback = new MyMediaRouterCallback();
    }

    public void setCastToGSListener (CastToGSListener castToGSListener) {
        this.castToGSListener = castToGSListener;
    }

    public MediaRouteSelector getmMediaRouteSelector() { return mMediaRouteSelector; }


    public void addCallback () {
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    public void removeCallback () {
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    /**
     * Callback for MediaRouter events
     */
    private class MyMediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRouteSelected");
            // Handle the user route selection.
            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());

            launchReceiver();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            Log.d(TAG, "onRouteUnselected: info=" + info);
            teardown();
            mSelectedDevice = null;
        }
    }

    /**
     * Start the receiver app
     */
    private void launchReceiver() {
        try {
            mCastListener = new Cast.Listener() {

                @Override
                public void onApplicationDisconnected(int errorCode) {
                    Log.d(TAG, "application has stopped");
                    teardown();
                }

            };
            // Connect to Google Play services
            mConnectionCallbacks = new ConnectionCallbacks();
            mConnectionFailedListener = new ConnectionFailedListener();
            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(mSelectedDevice, mCastListener);
            mApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mConnectionFailedListener)
                    .build();

            mApiClient.connect();
        } catch (Exception e) {
            Log.e(TAG, "Failed launchReceiver", e);
        }
    }

    /**
     * Send a text message to the receiver
     *
     * @param message
     */
    public void sendMessage(String message) {
        if (mApiClient != null && mChromecastManagementChannel != null) {
            try {
                Cast.CastApi.sendMessage(mApiClient,
                        mChromecastManagementChannel.getNamespace(), message)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (!result.isSuccess()) {
                                    Log.e(TAG, "Sending message failed");
                                }
                            }
                        });
            } catch (Exception e) {
                Log.e(TAG, "Exception while sending message", e);
            }
        } else {
            Toast.makeText(PrimaryActivity.getAppContext(), message, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Custom message channel
     */
    public class ChromecastManagementChannel implements Cast.MessageReceivedCallback {

        /**
         * @return custom namespace
         */
        public String getNamespace() {
            return context.getString(R.string.namespace);
        }

        /*
         * Receive message from the receiver app
         */
        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
            Log.d(TAG, "onMessageReceived: " + message);
            if (message != null) {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    ChromecastParser.onMessageReceived(jsonObject, castToGSListener);
                } catch (JSONException e) {
                    Log.e(TAG, "fail recieveing message from cats", e);
                }
            }
        }

    }

    private class ConnectionCallbacks implements
            GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(Bundle connectionHint) {
            Log.d(TAG, "onConnected");

            if (mApiClient == null) {
                // We got disconnected while this runnable was pending
                // execution.
                return;
            }

            try {
                if (mWaitingForReconnect) {
                    mWaitingForReconnect = false;

                    // Check if the receiver app is still running
                    if ((connectionHint != null)
                            && connectionHint
                            .getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
                        Log.d(TAG, "App  is no longer running");
                        teardown();
                    } else {
                        // Re-create the custom message channel
                        try {
                            Cast.CastApi.setMessageReceivedCallbacks(
                                    mApiClient,
                                    mChromecastManagementChannel.getNamespace(),
                                    mChromecastManagementChannel);
                        } catch (IOException e) {
                            Log.e(TAG, "Exception while creating channel", e);
                        }
                    }
                } else {
                    // Launch the receiver app
                    Cast.CastApi
                            .launchApplication(mApiClient,
                                    context.getString(R.string.app_id), false)
                            .setResultCallback(
                                    new ResultCallback<Cast.ApplicationConnectionResult>() {
                                        @Override
                                        public void onResult(
                                                Cast.ApplicationConnectionResult result) {
                                            Status status = result.getStatus();
                                            Log.d(TAG,
                                                    "ApplicationConnectionResultCallback.onResult: statusCode"
                                                            + status.getStatusCode());
                                            if (status.isSuccess()) {
                                                ApplicationMetadata applicationMetadata = result
                                                        .getApplicationMetadata();
                                                mSessionId = result
                                                        .getSessionId();
                                                String applicationStatus = result
                                                        .getApplicationStatus();
                                                boolean wasLaunched = result
                                                        .getWasLaunched();
                                                Log.d(TAG,
                                                        "application name: "
                                                                + applicationMetadata
                                                                .getName()
                                                                + ", status: "
                                                                + applicationStatus
                                                                + ", sessionId: "
                                                                + mSessionId
                                                                + ", wasLaunched: "
                                                                + wasLaunched);
                                                mApplicationStarted = true;

                                                // Create the custom message
                                                // channel
                                                mChromecastManagementChannel = new ChromecastManagementChannel();
                                                try {
                                                    Cast.CastApi
                                                            .setMessageReceivedCallbacks(
                                                                    mApiClient,
                                                                    mChromecastManagementChannel
                                                                            .getNamespace(),
                                                                    mChromecastManagementChannel);
                                                } catch (IOException e) {
                                                    Log.e(TAG,
                                                            "Exception while creating channel",
                                                            e);
                                                }

                                                // set the initial instructions
                                                // on the receiver
                                                sendMessage("hi");
                                            } else {
                                                Log.e(TAG,
                                                        "application could not launch");
                                                teardown();
                                            }
                                        }
                                    });
                    //if (myTestListener != null)
                    //    myTestListener.initGame();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to launch application", e);
            }
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "onConnectionSuspended");
            mWaitingForReconnect = true;
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        if (mApiClient == null) {
            // We got disconnected while this runnable was pending
            // execution.
            return;
        }

        try {
            if (mWaitingForReconnect) {
                mWaitingForReconnect = false;

                // Check if the receiver app is still running
                if ((connectionHint != null) && connectionHint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
                    teardown();
                } else {
                    // Re-create the custom message channel
                    try {
                        Cast.CastApi.setMessageReceivedCallbacks(mApiClient, getNamespace(), mChromecastManagementChannel);
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while creating channel", e);
                    }
                }
            } else {
                // Launch the receiver app
                Cast.CastApi.launchApplication(mApiClient, context.getString(R.string.app_id), false)
                        .setResultCallback(
                                new ResultCallback<Cast.ApplicationConnectionResult>() {
                                    @Override
                                    public void onResult(Cast.ApplicationConnectionResult result) {
                                        onConnectedResult(result);
                                    }
                                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch application", e);
        }
    }

    public void onConnectedResult(Cast.ApplicationConnectionResult result){
        Log.d(TAG, "ApplicationConnectionResultCallback.onResult: statusCode" + result.getStatus().getStatusCode());

        if (result.getStatus().isSuccess()) {

            Log.d(TAG, "application name: " + result.getApplicationMetadata().getName()
                    + ", status: " + result.getApplicationStatus()
                    + ", sessionId: " + result.getSessionId()
                    + ", wasLaunched: " + result.getWasLaunched());

            mApplicationStarted = true;

            // Create the custom message
            try {
                Cast.CastApi.setMessageReceivedCallbacks(mApiClient, this.getNamespace(), mChromecastManagementChannel);
            } catch (IOException e) {
                Log.e(TAG, "Exception while creating channel", e);
            }

            // set the initial instructions
            // on the receiver
            //sendMessage("hi");
        } else {
            Log.e(TAG, "application could not launch");
            teardown();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended");
        mWaitingForReconnect = true;
    }


    private class ConnectionFailedListener implements
            GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.e(TAG, "onConnectionFailed ");

            teardown();
        }
    }

    public String getNamespace() {
        return context.getString(R.string.namespace);
    }

    public void teardown() {
        Log.d(TAG, "teardown");
        if (mApiClient != null) {
            if (mApplicationStarted) {
                if (mApiClient.isConnected()  || mApiClient.isConnecting()) {
                    try {
                        Cast.CastApi.stopApplication(mApiClient, mSessionId);
                        if (mChromecastManagementChannel != null) {
                            Cast.CastApi.removeMessageReceivedCallbacks(
                                    mApiClient,
                                    mChromecastManagementChannel.getNamespace());
                            mChromecastManagementChannel = null;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while removing channel", e);
                    }
                    mApiClient.disconnect();
                }
                mApplicationStarted = false;
            }
            mApiClient = null;
        }
        mSelectedDevice = null;
        mWaitingForReconnect = false;
        mSessionId = null;
    }
}
