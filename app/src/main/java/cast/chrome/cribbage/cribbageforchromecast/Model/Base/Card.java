package cast.chrome.cribbage.cribbageforchromecast.Model.Base;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by milord on 26-Aug-14.
 */
public class Card {
    protected int cardRank, cardSuit;

    protected static String[] suitsArray = { "♠", "♥", "♦", "♣" };
    protected static String[] ranksArray  = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };

    public Card(int cardSuit, int cardRank)
    {
        this.cardRank = cardRank;
        this.cardSuit = cardSuit;
    }

    public @Override String toString(){
        return ranksArray[cardRank] + " of " + suitsArray[cardSuit];
    }

    public int getRank() {
        return cardRank;
    }

    public int getSuit() {
        return cardSuit;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("rank", cardRank);
            jsonObject.put("suit", cardSuit);
        } catch (JSONException e) {
            Log.d("JSONSerializable", "Could Not Generate JSON for Card");
        }
        return jsonObject;
    }

}
