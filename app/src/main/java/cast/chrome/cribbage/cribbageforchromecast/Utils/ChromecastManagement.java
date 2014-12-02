package cast.chrome.cribbage.cribbageforchromecast.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cast.chrome.cribbage.cribbageforchromecast.Interfaces.CastGameInterface;
import cast.chrome.cribbage.cribbageforchromecast.Interfaces.CastReceiver;
import cast.chrome.cribbage.cribbageforchromecast.PrimaryActivity;
import cast.chrome.cribbage.cribbageforchromecast.R;
import cast.chrome.cribbage.cribbageforchromecast.Utils.Scoring;

/**
 * Created by milord on 08-Nov-14.
 */
public class ChromecastManagement extends MediaRouter.Callback implements GoogleApiClient.ConnectionCallbacks, Cast.MessageReceivedCallback {

    private static final String TAG = PrimaryActivity.class.getSimpleName();

    private static final String GAME_NAMESPACE = "cast.chrome.cribbage.cribbageforchromecast";
    private static final String KEY_COMMAND = "command";
    private static final String KEY_EVENT = "event";

    private static final String KEY_HANDS = "hands";
    private static final String KEY_CRIB = "crib";
    private static final String KEY_CARD_PLAYED = "card_played";
    private static final String KEY_CARD_DROPPED = "card_dropped";
    private static final String KEY_CUT_CARD = "cut_card";
    private static final String KEY_SCORES_DURING_PLAY = "scores_during_play";
    private static final String KEY_SCORES_DURING_SHOW = "scores_during_show";
    private static final String KEY_PEG_POINTS = "peg_points";
    private static final String KEY_PREP_NEW_PLAY = "prep_new_play";
    private static final String KEY_PLAYER_POSITION = "player_position";
    private static final String KEY_CARD_POSITION = "card_position";


    private static final String KEY_NEXT_PLAYER_TURN = "next_player_turn";
    private static final String KEY_JOIN = "join";
    private static final String KEY_NAME = "name";

    private static final String KEY_MY_POSITION = "my_position";

    private static final String KEY_COULD_NOT_PLAY = "could_not_play";

    private Context context;

    private CastReceiver castReceiver;

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private CastDevice mSelectedDevice;
    private GoogleApiClient mApiClient;
    private Cast.Listener mCastListener;
    private ConnectionCallbacks mConnectionCallbacks;
    private ConnectionFailedListener mConnectionFailedListener;
    private ChromecastManagementChannel mChromecastManagementChannel;
    private boolean mApplicationStarted;
    private boolean mWaitingForReconnect;
    private String mSessionId;


    public ChromecastManagement(Context c, String appID, String tag, CastReceiver castReceiver) {
        context = c;
        this.castReceiver = castReceiver;

        // Configure Cast device discovery
        mMediaRouter = MediaRouter.getInstance(context);
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(appID)).build();
        mMediaRouterCallback = new MyMediaRouterCallback();

        /*SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);

        String strUserName = SP.getString("username", "NA");
        boolean bAppUpdates = SP.getBoolean("applicationUpdates",false);
        String downloadType = SP.getString("downloadType","1");*/
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
            mApiClient = new GoogleApiClient.Builder(PrimaryActivity.getAppContext())
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mConnectionFailedListener)
                    .build();

            mApiClient.connect();
        } catch (Exception e) {
            Log.e(TAG, "Failed launchReceiver", e);
        }
    }


    public void sendHands(String[][] playersHands) {
        String[][] temp = new String[3][5];

        try {
            JSONObject payload = new JSONObject();
            JSONArray jsonData = getJSONArrayForHands(playersHands);
            payload.put(KEY_COMMAND, KEY_HANDS);
            payload.put(KEY_HANDS, jsonData);
            sendMessage(payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "fail sending cards to cast", e);
        }
    }

    JSONArray getJSONArrayForHands(String[][] playerHands) {
        JSONArray jsonArray = new JSONArray();

        for (String[] hands : playerHands)
            jsonArray.put(getJSONArrayForHand(hands));

        return jsonArray;
    }

    JSONArray getJSONArrayForHand(String[] playerHand) {
        JSONArray jsonArray = new JSONArray();

        for(String card : playerHand)
            jsonArray.put(getJSONObjectForCard(card));

        return jsonArray;
    }

    JSONObject getJSONObjectForCard(String card) {
        JSONObject jsonObject = null;
        try {
            String[] parts = card.split(" of ");
            jsonObject = new JSONObject();
            jsonObject.put("rank", parts[0]);
            jsonObject.put("suit", parts[1]);
        } catch (JSONException e) {
            Log.e(TAG, "fail attaching card to jsonObject", e);
        }
        return jsonObject;
    }

    public void sendCrib (String[] crib) {
        try {
            JSONObject payload = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for (String card : crib)
                if (card != null)
                    jsonArray.put(getJSONObjectForCard(card));

            payload.put(KEY_COMMAND, KEY_CRIB);
            payload.put(KEY_CRIB, jsonArray);
            sendMessage(payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "fail sending crib to cast", e);
        }
    }

    public void sendIntToCast (int intToSend, String keyCommand) {
        try {
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, keyCommand);
            payload.put(keyCommand, intToSend);
            sendMessage(payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "fail sending int to cast with command: " + keyCommand, e);
        }
    }

    public void sendPlayerPositionAndCardPositionToCast (int playerPosition, int cardPosition, String keyCommand) {
        try {
            JSONObject payload = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("PlayerPosition", playerPosition);
            jsonObject.put("CardPosition", cardPosition);
            payload.put(KEY_COMMAND, keyCommand);
            payload.put(keyCommand, jsonObject);
            sendMessage(payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "fail sending player position and card position to cast with keycommand: " + keyCommand, e);
        }
    }

    public void sendNextPlayerTurn(int couldNotPlay) {
        try {
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, KEY_COULD_NOT_PLAY);
            payload.put(KEY_COULD_NOT_PLAY, couldNotPlay);
            sendMessage(payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "fail sending next turn to cast", e);
        }
    }

    public void sendCutCard (String card) {

        try {
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, KEY_CUT_CARD);
            payload.put(KEY_CUT_CARD, getJSONObjectForCard(card));
            sendMessage(payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "fail sending cut card to cast", e);
        }
    }

    public void sendCardPlayed (String card, int playerPosition, int cardPosition) {
        try {
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, KEY_CARD_PLAYED);
            payload.put(KEY_CARD_PLAYED, getJSONObjectForCard(card));
            payload.put(KEY_PLAYER_POSITION, playerPosition);
            payload.put(KEY_CARD_POSITION, cardPosition);
            sendMessage(payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "fail sending played card to cast", e);
        }
    }

    public void sendPrepNewPlay() {
        try {
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, KEY_PREP_NEW_PLAY);
            sendMessage(payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "fail prep new play", e);
        }
    }

    public void sendPegPoints(int pegPoints) {
        try {
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, KEY_PEG_POINTS);
            payload.put(KEY_PEG_POINTS, pegPoints);
            sendMessage(payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "fail send peg points");
        }
    }

     public  void joinGame(String name) {
        try {
            Log.d(TAG, "join: " + name);
            JSONObject payload = new JSONObject();
            payload.put(KEY_COMMAND, KEY_JOIN);
            payload.put(KEY_NAME, name);
            sendMessage(payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Cannot create object to join a game", e);
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
                    if (myTestListener != null)
                        myTestListener.initGame();
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
                        Cast.CastApi.setMessageReceivedCallbacks(mApiClient, getNamespace(), this);
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
                Cast.CastApi.setMessageReceivedCallbacks(mApiClient, this.getNamespace(), this);
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

                    String keyCommand = jsonObject.getString(KEY_COMMAND);

                    if (keyCommand.equals(KEY_MY_POSITION)) {
                        Log.d(TAG, "POSITION RECEIVEd");
                        onPositionReceived(jsonObject);
                    } else if (keyCommand.equals(KEY_HANDS)) {
                        Log.d(TAG, "HANDS RECEIVED");
                        onHandsReceived(jsonObject);
                    } else if (keyCommand.equals(KEY_CARD_PLAYED)) {
                        Log.d(TAG, "Played card recieved");
                        onCardPlayedReceived(jsonObject);
                    } else if (keyCommand.equals(KEY_CRIB)) {
                        Log.d(TAG, "Crib card recieved");
                        onCribCardReceived(jsonObject);
                    } else if (keyCommand.equals(KEY_CUT_CARD)) {
                        Log.d(TAG, "Cut card received");
                        onCutCardReceived(jsonObject);
                    } else if (keyCommand.equals(KEY_SCORES_DURING_PLAY)) {
                        onScoreDuringPlayReceived(jsonObject);
                    } else if (keyCommand.equals(KEY_COULD_NOT_PLAY)) {
                        onCouldNotPlayReceived(jsonObject);
                    } else if (keyCommand.equals("deal")) {
                        onMyDeal();
                    } else if (keyCommand.equals("your_turn")) {
                        onNextTurnReceived(jsonObject);
                    } else if (keyCommand.equals("prep")) {
                        onPrepForDeal();
                    } else if (keyCommand.equals("prep_new_play")) {
                        onPrepNewPlay();
                    } else {
                        Log.d(TAG, "Invalid key command");
                    }

                    System.out.println(keyCommand);

                } catch (JSONException e) {
                    Log.e(TAG, "fail recieveing message from cats", e);
                }
            }
        }

    }

    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        Log.d(TAG, "onMessageReceived: " + message);
        if (message != null) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                this.castReceiver.onReceiveJSON(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "fail recieveing message from catt", e);
            } catch (Exception e) {
                Log.e(TAG, "fail recieveing message from cast", e);
            }
        }
    }

    /**
     * Listener for catching and passing calls to primary
     */
    private MyTestListener myTestListener;

    /**
     * Interface for passing calls to primary
     */
    public interface MyTestListener {
        public void receiveHands(String[][] hands);

        public void receiveCardPlayed(int playerPosition, int cardPosition);

        public void receiveCribCard(int playerPosition, int cardPosition);

        public void receievePlayerPosition (int playerPosition);

        public void receiveCutCard(String cutCard);

        public void receieveNextTurn();

        public void receiveScoreDuringPlay(int scoreDuringPlay);

        public void receieveCouldNotPlay(int couldNotPlayCount);

        public void createNewGame();

        public void myDeal();

        public void prepForDeal();

        public void prepNewPlay();

        public void initGame();
    }

    public void setMyTestListener(MyTestListener m) {
        myTestListener = m;
    }


    /**
     * Unwrap recieved json object from chromecast and deposit into 2d array; pass 2d array
     * to primary via interface lsitener
     * @param rootObject
     */
    public void onHandsReceived(JSONObject rootObject) {
        String[][] playerHandsTemp = new String[3][5];
        try {
            JSONArray tempHands = rootObject.getJSONArray("hands");
            for (int i = 0; i < tempHands.length(); i++) {
                JSONArray jsonArray = tempHands.getJSONArray(i);
                System.out.println("jsonArray" + i + ": " + jsonArray);
                for (int j = 0; j < jsonArray.length(); j++) {
                    System.out.println(jsonArray.get(j).toString());
                    JSONObject tempJSONObj = (JSONObject) jsonArray.get(j);
                    playerHandsTemp[i][j] = tempJSONObj.get("rank") + " of " + tempJSONObj.get("suit");
                }
            }
            //String cribCard = rootObject.getString("crib_card");

            if (myTestListener != null)
                myTestListener.receiveHands(playerHandsTemp);
        } catch (JSONException e) {
            Log.e(TAG, "fail test", e);
        }
    }

    public void onCardPlayedReceived (JSONObject rootObject) {
        int playerPosition, cardPosition;
        try {
            playerPosition = Integer.parseInt(rootObject.getString("player_position"));
            cardPosition = Integer.parseInt(rootObject.getString("card_position"));

            if (myTestListener != null)
                myTestListener.receiveCardPlayed(playerPosition, cardPosition);
        } catch (JSONException e) {
            Log.e (TAG, "fail grabbing received card", e);
        }
    }

    public void onCribCardReceived (JSONObject rootObject) {
        int playerPosition, cardPosition;
        try {
            playerPosition = Integer.parseInt(rootObject.getString("player_position"));
            cardPosition = Integer.parseInt(rootObject.getString("card_position"));

            if (myTestListener != null)
                myTestListener.receiveCribCard(playerPosition, cardPosition);
        } catch (JSONException e) {
            Log.e(TAG, "fail grabbing crib card", e);
        }
    }

    public void onPositionReceived(JSONObject rootObject) {
        try {
            int playerPosition = Integer.parseInt(rootObject.getString("player_position"));

            if (myTestListener != null)
                myTestListener.receievePlayerPosition(playerPosition);
        } catch (JSONException e) {
            Log.e(TAG, "fail grabbing player position from message data", e);
        }
    }

    public void onCutCardReceived(JSONObject rootObject) {
        try {
            String cutCard = rootObject.getString("cut_card");

            if (myTestListener != null)
                myTestListener.receiveCutCard(cutCard);
        } catch (JSONException e) {
            Log.e(TAG, "fail grabbing cut card", e);
        }
    }

    public void onCouldNotPlayReceived(JSONObject rootObject) {
        try {
            int couldNotPlayCount = Integer.parseInt(rootObject.getString("next_turn"));

            if (myTestListener != null)
                myTestListener.receieveCouldNotPlay(couldNotPlayCount);
        } catch (JSONException e) {
            Log.e(TAG, "fail on could not play", e);
        }
    }

    public void onScoreDuringPlayReceived(JSONObject rootObject) {
        try {
            int scoreDuringPlay = Integer.parseInt(rootObject.getString(KEY_SCORES_DURING_PLAY));

            if (myTestListener != null)
                myTestListener.receiveScoreDuringPlay(scoreDuringPlay);
        } catch (JSONException e) {
            Log.e(TAG, "fail on catch score during play", e);
        }
    }

    public void onNextTurnReceived(JSONObject rootObject) {
        if (myTestListener != null)
            myTestListener.receieveNextTurn();
    }

    public void onMyDeal() {
        if (myTestListener != null)
            myTestListener.myDeal();
    }

    public void createNewGame() {
        if (myTestListener != null)
            myTestListener.createNewGame();
    }

    public void onPrepForDeal() {
        if (myTestListener != null)
            myTestListener.prepForDeal();
    }

    public void onPrepNewPlay() {
        if (myTestListener != null)
            myTestListener.prepNewPlay();
    }


}
