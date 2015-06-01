package cast.chrome.cribbage.cribbageforchromecast.Model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by milord on 26-Aug-14.
 */
public class Card {
    private int cardRank, cardSuit;
    private String rank, suit;
    private static String[] suitsArray = { "♠", "♥", "♦", "♣" };
    private static String[] ranksArray  = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
    private static int ordinal;

    public Card(int cardSuit, int cardRank)
    {
        this.cardRank = cardRank;
        this.cardSuit = cardSuit;
    }

    public Card(int ordinalIn) {
        int ordinalOffset;
        ordinal = ordinalIn;

        if (ordinalIn > 0 && ordinal <= 13) {
            ordinalOffset = 0;
            suit = suitsArray[ordinalOffset];
            rank = ranksArray[ordinal - (13 * ordinalOffset)];
        } else if (ordinalIn > 13 && ordinal <= 26) {
            ordinalOffset = 1;
            suit = suitsArray[ordinalOffset];
            rank = ranksArray[ordinal - (13 * ordinalOffset)];
        } else if (ordinalIn > 26 && ordinal <= 39) {
            ordinalOffset = 2;
            suit = suitsArray[ordinalOffset];
            rank = ranksArray[ordinal - (13 * ordinalOffset)];
        } else if (ordinalIn > 39 && ordinal <= 52) {
            ordinalOffset = 3;
            suit = suitsArray[ordinalOffset];
            rank = ranksArray[ordinal - (13 * ordinalOffset)];
        }
    }

    public @Override String toString()
    {
        return ranksArray[cardRank] + " of " + suitsArray[cardSuit];
    }

    public int getRank() {
        return cardRank;
    }

    public int getSuit() {
        return cardSuit;
    }

    public int getOrdinal() { return ordinal; }

    public void initFromOrdinal (int ordinalIn) {
        int ordinalOffset;
        ordinal = ordinalIn;
        if (ordinalIn > 0 && ordinal <= 13) {
            ordinalOffset = 0;
            suit = suitsArray[ordinalOffset];
            rank = ranksArray[ordinal - (13 * ordinalOffset)];
        } else if (ordinalIn > 13 && ordinal <= 26) {
            ordinalOffset = 1;
            suit = suitsArray[ordinalOffset];
            rank = ranksArray[ordinal - (13 * ordinalOffset)];
        } else if (ordinalIn > 26 && ordinal <= 39) {
            ordinalOffset = 2;
            suit = suitsArray[ordinalOffset];
            rank = ranksArray[ordinal - (13 * ordinalOffset)];
        } else if (ordinalIn > 39 && ordinal <= 52) {
            ordinalOffset = 3;
            suit = suitsArray[ordinalOffset];
            rank = ranksArray[ordinal - (13 * ordinalOffset)];
        }
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
