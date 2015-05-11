package cast.chrome.cribbage.cribbageforchromecast.NewStuff;

import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cast.chrome.cribbage.cribbageforchromecast.PrimaryActivity;

/**
 * Created by interns on 5/10/15.
 */
public class JSONHandler {

    private static final String TAG = PrimaryActivity.class.getSimpleName();
    private static final String KEY_COMMAND = "command";

    public static JSONObject PackageGenericJson(String command, String key, String value) {
        JSONObject payload = new JSONObject();
        try {
            payload.put(KEY_COMMAND, command);
            payload.put(key, value);
        } catch (JSONException e) {
            Log.e(TAG, "failed final json package", e);
        }
        return payload;
    }

    public static JSONObject PackageGenericJson(String command) {
        JSONObject payload = new JSONObject();
        try {
            payload.put(KEY_COMMAND, command);
        } catch (JSONException e) {
            Log.e(TAG, "failed final json package", e);
        }
        return payload;
    }

    /*public void sendHands(String[][] playersHands) {
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
    }*/


    public static void onReceivedNewRound (JSONObject jsonObject, CastToGSListener castToGSListener) {
        int round = 0;
        try {
            round = Integer.parseInt(jsonObject.getString("round_number"));
            castToGSListener.ReceiveNewRound(round);
        } catch (JSONException e) {
            Log.e (TAG, "fail grabbing received card", e);
        }
    }

    public static void onReceivedPlayerTurn (JSONObject jsonObject, CastToGSListener castToGSListener) {
        try {
            castToGSListener.ReceivedPlayerTurn(jsonObject.getInt(ReceiveKeys.PLAYER_TURN));
        } catch (JSONException e) {
            Log.e(TAG, "fail getting player turn", e);
        }
    }

    public static void onReceivedPlayerJoined (JSONObject jsonObject, CastToGSListener castToGSListener) {
        /*try {
            //create player object
            Player player = new Player(jsonObject.getInt("id"), (Color)jsonObject.getString("color"),
                jsonObject.getString("name"));
            castToGSListener.ReceivedPlayerJoined(jsonObject.getInt(ReceiveKeys.PLAYER_TURN));
        } catch (JSONException e) {
            Log.e(TAG, "fail getting player turn", e);
        }*/
    }

    public static void onReceivedPlayerDisconnected (JSONObject jsonObject, CastToGSListener castToGSListener) {
        try {
            castToGSListener.ReceivedPlayerDisconnected(jsonObject.getInt(ReceiveKeys.PLAYER_DROPPED));
        } catch (JSONException e) {
            Log.e(TAG, "fail on player drop", e);
        }
    }

    public static void onReceivedRoundEnd (JSONObject jsonObject, CastToGSListener castToGSListener) {
        castToGSListener.ReceivedEndRound();
    }

    public static void onReceivedGameOver (JSONObject jsonObject, CastToGSListener castToGSListener) {
        try {
            castToGSListener.ReceivedGameOver(jsonObject.getInt(ReceiveKeys.WINNER_ID));
        } catch (JSONException e) {
            Log.e(TAG, "fail on game end", e);
        }
    }

    /*public static void onReceivedDeck (JSONObject jsonObject, CastToGSListener castToGSListener) {
        Deck deck;
        try {

            deck = new Deck(jsonObject.getJSONObject("deck"));
            castToGSListener.ReceiveNewRound(round);
        } catch (JSONException e) {
            Log.e (TAG, "fail grabbing received card", e);
        }
    }*/


}
