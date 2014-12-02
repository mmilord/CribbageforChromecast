package cast.chrome.cribbage.cribbageforchromecast.Model;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cast.chrome.cribbage.cribbageforchromecast.Interfaces.JSONSerializable;
import cast.chrome.cribbage.cribbageforchromecast.Utils.Scoring;

/**
 * Created by milord on 08-Nov-14.
 */
public class DealerCardManagement implements JSONSerializable {

    private static final String TAG = DealerCardManagement.class.getSimpleName();

    String[][] players;
    String[] crib;
    Deck deck;
    String drawCard;
    String cutCard;
    int currentScore;

    ArrayList<String> activeCards = new ArrayList<String>();

    Boolean playState;

    /**
     * Constructor for initial creation of game; creates deck object and deals hands;
     * @param playerCount   amount of players in game
     */
    public DealerCardManagement(int playerCount) {
        deck = new Deck();
        crib = new String[4];
        playState = false;
        currentScore = 0;

        if (playerCount == 2)
            players = new String[playerCount][6];
        else
            players = new String[playerCount][5];

        dealer();


        players[0] = Scoring.sorthand(players[0]);
        players[1] = Scoring.sorthand(players[1]);
        players[2] = Scoring.sorthand(players[2]);
    }

    /**
     * Constructor that is called when game is premade by another player in the game
     * @param predealtPlayers   imported players from predealt from CastManager
     * @param cribCard          imported crib card from predealt from CastManager
     */
    public DealerCardManagement (String[][] predealtPlayers, String cribCard) {
        players = predealtPlayers;
        crib = new String[4];
        crib[0] = cribCard;
        playState = false;
    }


    /**
     * Setters and getters
     */
    public void setPlayState(Boolean playState) { this.playState = playState; }

    public String[][] getPlayers() { return players; }

    public String[] getCrib() { return crib; }

    public Boolean getPlayState() { return playState; }

    public String getCutCard() { return cutCard; }

    public int getCurrentScore() { return currentScore; }

    public String getPlayersCardToString(int playerPosition, int cardPosition) {
        return players[playerPosition][cardPosition].toString();
    }

    public void setCutCard (String cutCard) { this.cutCard = cutCard; }

    public void setCurrentScore (int newScore) { currentScore = newScore; }

    public void resetActiveCards () { activeCards.clear(); }

    public ArrayList<String> getActiveCards() { return activeCards; }



    public int callPlayerHandScoreCheck(int playerPosition) {
        players[playerPosition][4] = drawCard;
        return Scoring.doHandScoreCheck(players[playerPosition]);
    }

    public void addToCrib (String card) {
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(crib));
        temp.add(card);
        crib = temp.toArray(new String[4]);
    }


    /**
     * Performs the deal action and setups up the game
     */
    public void dealer() {
        System.out.println("Total: " + deck.getTotalCards());

        for (int j = players.length - 1; j > -1; j-- ) {
            for (int i = 0; i < players[0].length; i++) {
                players[j][i] = deck.drawFromDeck() + "";
            }
        }

        System.out.println("Total: " + deck.getTotalCards());

        for (int j = players.length - 1; j > -1; j--) {
            System.out.print("Player " + j + "'s hand: ");
            for (int i = 0; i < players[0].length; i++)
                System.out.print(players[j][i].toString() + ", ");
            System.out.println();
        }

        crib[0] = deck.drawFromDeck() + "";


        cutCard = deck.drawFromDeck() + "";
    }

    /**
     * Return whether or not card is allowed to be played
     * @return true if playable, false otherwise
     */
    public Boolean canAddToActiveCards(int playerPosition, int cardPosition) {
        if (!activeCards.isEmpty()) {
            if (countActiveCards() + Scoring.cardToScoringValue(players[playerPosition][cardPosition].toString()) <= 31)
                return true;
            else
                return false;
        }
        else { return true; }
    }

    /**
     * Add card to activeCards variable
     */
    public int doAddToActiveCards (int playerPosition, int cardPosition) {
        currentScore += Scoring.cardToScoringValue(players[playerPosition][cardPosition]);

        int tempScore = 0;

        if (!activeCards.isEmpty()) {
            activeCards.add(players[playerPosition][cardPosition]);
            tempScore = Scoring.doPlayPairCheck(players[playerPosition][cardPosition], activeCards);
            //tempScore += Scoring.doRunCheck(players[playerPosition][cardPosition], activeCards);

            if (currentScore == 15)
                tempScore += 2;

            return tempScore;
        }
        else {
            activeCards.clear();
            activeCards.add(players[playerPosition][cardPosition]);
            return 0;
        }
    }

    /**
     * Count the activeCards value
     * @return count
     */
    public int countActiveCards () {
        int count = 0;

        if (!activeCards.isEmpty()) {
            for (int i = 0; i < activeCards.size(); i++) {
                count += Scoring.cardToScoringValue(activeCards.get(i));
            }
        }

        return count;
    }

    /**
     * Remove selected card from players hand
     * @param playerPosition
     * @param replacedCard      card to be removed from hand
     */
    public void replaceCard(int playerPosition, int replacedCard) {
        players[playerPosition][replacedCard] = null;
        //ArrayList<String> temp = new ArrayList<String>();


        ArrayList<String> list = new ArrayList<String>();
        for (int j = 0; j < players[playerPosition].length; j++) {
            if (players[playerPosition][j] != null) {
                list.add(players[playerPosition][j]);
            }
        }
        players[playerPosition] = list.toArray(new String[list.size()]);

        //players[playerPosition] = temp.toArray(new String[temp.size()]);
    }


    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("crib", getJSONArrayForHand(crib));
            jsonObject.put("hand", getJSONArrayForHands(players));
        } catch (JSONException e) {
            Log.d("JSONSerializable", "Could Not Generate JSON for Card");
        }
        return jsonObject;
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
            jsonArray.put((getJSONObjectForCard(card)));

        return jsonArray;
    }

    JSONObject getJSONObjectForCard(String card) {
        JSONObject jsonObject = null;
        try {
            String[] parts = card.split(" of ");
            jsonObject = new JSONObject();
            jsonObject.put("Rank", Scoring.getCardRankInteger(parts[0]));
            jsonObject.put("Suit", parts[1]);
        } catch (JSONException e) {
            Log.e(TAG, "fail attaching card to jsonObject", e);
        }
        return jsonObject;
    }
}
