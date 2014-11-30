package cast.chrome.cribbage.cribbageforchromecast.Utils;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cast.chrome.cribbage.cribbageforchromecast.Interfaces.*;
import cast.chrome.cribbage.cribbageforchromecast.Model.*;
/*
/**
 * Created by milord on 28-Nov-14.
 */
/*public class CastMessageParserLocal implements CastReceiver, CastGameInterface {

    private static final String TAG = cast.chrome.cribbage.cribbageforchromecast.PrimaryActivity.class.getSimpleName();
    private static final String KEY_COMMAND = "command";
    private ChromecastManagement _chromeCast;
    private MessageService _messagingService;

    private final class SEND_MESSAGES {
        public static final String PASS_TURN = "send_pass_turn";
        public static final String PLAYED_CARD = "send_card_played";
        public static final String DROP_CARD = "send_card_dropped";
        public static final String END_HAND_SCORE_REVIEW = "send_end_hand_score_review";
        public static final String END_CRIB_SCORE_REVIEW = "send_end_crib_score_review";
        public static final String JOIN_GAME = "send_join_game";
        public static final String START_GAME = "send_start_game";
        public static final String SETUP_GAME = "send_setup_game";
    }

    private final class RECIEVE_MESSAGES {
        public static final String PASS_TURN = "get_pass_turn";
        public static final String PLAYED_CARD = "get_card_played";
        public static final String DROP_CARD = "get_card_dropped";
        public static final String END_HAND_SCORE_REVIEW = "get_end_hand_score_review";
        public static final String END_CRIB_SCORE_REVIEW = "get_end_crib_score_review";
        public static final String JOIN_GAME = "get_join_game";
        public static final String START_GAME = "get_start_game";
        public static final String SETUP_GAME = "get_setup_game";
        public static final String IS_OWNER = "get_is_owner";
    }


    public CastMessageParserLocal(Context c, String appID, MessageService messagingService){
        _messagingService = messagingService;
        _chromeCast = new ChromecastManagement(c,appID, TAG, this);
    }

    @Override
    public void sendJoinedGame(DealerCardManagement player) {
        JSONObject payload = new JSONObject();
        try {
            payload.put(KEY_COMMAND, SEND_MESSAGES.JOIN_GAME);
            payload.put(DealerCardManagement.class.getSimpleName(), player.toJson());
            _chromeCast.sendMessage(payload.toString());
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed To: " + SEND_MESSAGES.JOIN_GAME, e);
        }

    }

    @Override
    public void sendDropCard(DealerCardManagement player, Card card) {
        JSONObject payload = new JSONObject();
        try {
            payload.put(KEY_COMMAND, SEND_MESSAGES.DROP_CARD);
            payload.put(Card.class.getSimpleName(), card.toJson());
            _chromeCast.sendMessage(payload.toString());
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed To: " + SEND_MESSAGES.DROP_CARD, e);
        }
    }

    @Override
    public void sendPlayedCard(DealerCardManagement player, Card card, int pegging) {
        JSONObject payload = new JSONObject();
        try {
            payload.put(KEY_COMMAND, SEND_MESSAGES.PLAYED_CARD);
            payload.put(Card.class.getSimpleName(), card.toJson());
            payload.put("pegging", pegging);
            _chromeCast.sendMessage(payload.toString());
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed To: " + SEND_MESSAGES.PLAYED_CARD, e);
        }
    }

    @Override
    public void sendPassTurn(DealerCardManagement player) {
        JSONObject payload = new JSONObject();
        try {
            payload.put(KEY_COMMAND, SEND_MESSAGES.PASS_TURN);
            payload.put("pass", true);
            _chromeCast.sendMessage(payload.toString());
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed To: " + SEND_MESSAGES.PASS_TURN, e);
        }
    }

    @Override
    public void sendEndHandScoreReview(DealerCardManagement player) {
        JSONObject payload = new JSONObject();
        try {
            payload.put(KEY_COMMAND, SEND_MESSAGES.END_HAND_SCORE_REVIEW);
            payload.put("endHandScoreReview", true);
            _chromeCast.sendMessage(payload.toString());
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed To: " + SEND_MESSAGES.END_HAND_SCORE_REVIEW, e);
        }
    }

    @Override
    public void sendEndCribScoreReview(DealerCardManagement player) {
        JSONObject payload = new JSONObject();
        try {
            payload.put(KEY_COMMAND, SEND_MESSAGES.END_CRIB_SCORE_REVIEW);
            payload.put("endCribScoreReview", true);
            _chromeCast.sendMessage(payload.toString());
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed To: " + SEND_MESSAGES.END_CRIB_SCORE_REVIEW, e);
        }
    }

    @Override
    public void sendPlaySetup(String[][] userHands, Card cutCard, String cribOwner) {
/*        JSONObject payload = new JSONObject();
        try {
            payload.put(KEY_COMMAND, SEND_MESSAGES.SETUP_GAME);
            payload.put("cutCard", cutCard.toJson());
            payload.put("cribOwner", cribOwner);

            JSONObject userHandsJSON = new JSONObject();

            for(String key : userHands.keySet()) {
                JSONArray userHandJSONAry  = new JSONArray();

                for(Card cardInHand : userHands.get(key))
                    userHandJSONAry.put(cardInHand.toJson().toString());

                userHandsJSON.put(key, userHandJSONAry);
            }

            payload.put("userHands", userHandsJSON.toString());
            _chromeCast.sendMessage(payload.toString());
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed To: " + SEND_MESSAGES.SETUP_GAME, e);
        }
*//*    }

    @Override
    public void onReceiveJSON(JSONObject jsonObject) throws Exception {

        if (jsonObject.has(RECIEVE_MESSAGES.JOIN_GAME)) {
            Log.d(TAG, RECIEVE_MESSAGES.JOIN_GAME);
            onReceivedJoinedGame(jsonObject);
        }

        else if (jsonObject.has(RECIEVE_MESSAGES.START_GAME)) {
            Log.d(TAG, RECIEVE_MESSAGES.START_GAME);
            onReceivedStartedGame(jsonObject);
        }

        else if (jsonObject.has(RECIEVE_MESSAGES.SETUP_GAME)) {
            Log.d(TAG, RECIEVE_MESSAGES.START_GAME);
            onReceivedSetupPlay(jsonObject);
        }

        else if (jsonObject.has(RECIEVE_MESSAGES.PLAYED_CARD)) {
            Log.d(TAG, RECIEVE_MESSAGES.PLAYED_CARD);
            onReceivedCardPlayed(jsonObject);
        }

        else if (jsonObject.has(RECIEVE_MESSAGES.DROP_CARD)) {
            Log.d(TAG, RECIEVE_MESSAGES.DROP_CARD);
            onReceivedCardDropped(jsonObject);
        }

        else if (jsonObject.has(RECIEVE_MESSAGES.END_HAND_SCORE_REVIEW)) {
            Log.d(TAG, RECIEVE_MESSAGES.END_HAND_SCORE_REVIEW);
            onReceivedHandScoreReviewEnded(jsonObject);
        }

        else if (jsonObject.has(RECIEVE_MESSAGES.END_CRIB_SCORE_REVIEW)) {
            Log.d(TAG, RECIEVE_MESSAGES.END_CRIB_SCORE_REVIEW);
            onReceivedCribScoreReviewEnded(jsonObject);
        }

        else if (jsonObject.has(RECIEVE_MESSAGES.IS_OWNER)) {
            Log.d(TAG, RECIEVE_MESSAGES.IS_OWNER);
            onReceivedIsGameOwner(jsonObject);
        }

        else {
            Log.d(TAG, "Invalid key command");
        }
    }

    private void onReceivedJoinedGame(JSONObject jsonObject) throws JSONException {
//       /_messagingService.onReceivedJoinedGame(new DealerCardManagement(jsonObject));
    }

    private void onReceivedStartedGame(JSONObject jsonObject) throws JSONException{
        _messagingService.onReceivedStartedGame();
    }

    private void onReceivedSetupPlay(JSONObject jsonObject) throws Exception {
        //Map<String, Card[]> userHands, Card cutCard, String cribOwner

        JSONObject cutCardJSONObj = jsonObject.getJSONObject(Card.class.getSimpleName());
        int cardNumber = cutCardJSONObj.getInt("cardNumber");
        Card cutCard = new Card(cardNumber);

        String cribOwnerID = jsonObject.getString("cribOwner");

        JSONObject userHandsJSON = jsonObject.getJSONObject("userHands");
        String[][] userHands = new String[2][userHandsJSON.length()];

/*        while(keys.hasNext()) {
            String key = (String) keys.next();

            if (userHandsJSON.get(key) instanceof JSONArray) {
                JSONArray userHandJSONAry = userHandsJSON.getJSONArray(key);
                Card[] cards = new Card[userHandJSONAry.length()];

                for (int i = 0; i < userHandJSONAry.length(); i++) {
                    JSONObject card = userHandJSONAry.getJSONObject(i);
                    cards[i] = new Card(card.getInt("cardNumber"));
                }

                userHands.put(key, cards);
            }
        }

*/ /*       _messagingService.onReceivedSetupPlay(userHands,cutCard, cribOwnerID);

    }

    private void onReceivedCardPlayed(Card card, int pegging) {
        //Card card, int pegging
        //int pegging = jsonObject.getInt("pegging");
        //JSONObject card = jsonObject.getJSONObject(Card.class.getSimpleName());
        //int cardNumber = card.getInt("cardNumber");
        _messagingService.onReceivedCardPlayed(card, pegging);
    }

    private void onReceivedCardDropped(JSONObject jsonObject) throws Exception {
        JSONObject card = jsonObject.getJSONObject(Card.class.getSimpleName());
        int cardNumber = card.getInt("cardNumber");
        _messagingService.onReceivedCardDropped(new Card(cardNumber));
    }

    private void onReceivedCribScoreReviewEnded(JSONObject jsonObject) throws JSONException{
        _messagingService.onReceivedCribScoreReviewEnded();
    }

    private void onReceivedHandScoreReviewEnded(JSONObject jsonObject) throws JSONException{
        _messagingService.onReceivedHandScoreReviewEnded();
    }

}
*/