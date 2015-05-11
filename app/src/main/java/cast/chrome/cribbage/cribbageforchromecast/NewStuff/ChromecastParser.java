package cast.chrome.cribbage.cribbageforchromecast.NewStuff;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cast.chrome.cribbage.cribbageforchromecast.Interfaces.CastGameInterface;
import cast.chrome.cribbage.cribbageforchromecast.PrimaryActivity;

/**
 * Created by interns on 5/10/15.
 */
public class ChromecastParser {

    private static final String TAG = PrimaryActivity.class.getSimpleName();
    private static final String KEY_COMMAND = "command";

    /*
     * Receive message from the receiver app
     */
    public static void onMessageReceived(JSONObject jsonObject, CastToGSListener castToGSListener) {
        try {
            String keyCommand = jsonObject.getString(KEY_COMMAND);

            if (keyCommand.equals(ReceiveKeys.NEW_ROUND)) {
                Log.d(TAG, "POSITION RECEIVEd");
                JSONHandler.onReceivedNewRound(jsonObject, castToGSListener);
            } else if (keyCommand.equals(ReceiveKeys.PLAYER_TURN)) {
                Log.d(TAG, "player turn received");
                JSONHandler.onReceivedPlayerTurn(jsonObject, castToGSListener);
            } else if (keyCommand.equals(ReceiveKeys.PLAYER_JOINED)) {
                Log.d(TAG, "Played joined recieved");
                JSONHandler.onReceivedPlayerJoined(jsonObject, castToGSListener);
            } else if (keyCommand.equals(ReceiveKeys.PLAYER_DROPPED)) {
                Log.d(TAG, "player drop recieved");
                JSONHandler.onReceivedPlayerDisconnected(jsonObject, castToGSListener);
            } else if (keyCommand.equals(ReceiveKeys.ROUND_END)) {
                Log.d(TAG, "round end receieved");
                JSONHandler.onReceivedRoundEnd(jsonObject, castToGSListener);
            } else if (keyCommand.equals(ReceiveKeys.GAME_OVER)) {
                Log.d(TAG, "game over receieved");
                JSONHandler.onReceivedGameOver(jsonObject, castToGSListener);
            } else {
                Log.d(TAG, "Invalid key command");
            }

            System.out.println(keyCommand);

        } catch (JSONException e) {
            Log.e(TAG, "fail recieveing message from cats", e);
        }
    }



}
