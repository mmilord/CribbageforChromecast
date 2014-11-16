package cast.chrome.cribbage.cribbageforchromecast;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by milord on 12-Nov-14.
 */
public class Scoring {

    /**
     * @return requested card as integer
     */
    static int cardToInt(String card) {
        if (card.contains(" "))
            card = card.substring(0, card.indexOf(" "));

        if (card.equals("King") || card.equals("Queen") || card.equals("Jack"))
            return 10;
        else if (card.equals("Ace"))
            return 1;
        else
            return Integer.parseInt(card);
    }

    static int getCardRankInteger(String card) {

        if (card.equals("King"))
            return 13;
        else if (card.equals("Queen"))
            return 12;
        else if (card.equals("Jack"))
            return 11;
        else if (card.equals("Ace"))
            return 1;
        else
            return Integer.parseInt(card);
    }

    /**
     * Compare played card with requested card
     * @return true if pair
     */
    static boolean doPairCheck(String cardPlayed, String activeCard) {
        // card = getCardRankString(playerPosition, cardPosition);
        if (cardPlayed.equals(activeCard))
            return true;
        else
            return false;
    }
/*
    static int doRunCheck(int cardOne, int cardTwo) {
        // card = getCardRankInteger(getCardRankString(playerPosition, cardPosition));

        // temps?
        // idea: compare active to card then active-1 to active and card then active -2 to active-1/active/card, etc
        // easier way?
        if (getCardRankInteger(activeCards.get(activeCards.size() - 1)) == cardOne - 1 || getCardRankInteger(activeCards.get(activeCards.size() - 1)) == cardOne + 1) {

        }

        return 0;
    }*/

    /**
     * Scores players hand
     * @return score of players hand
     */
    static int doHandScoreCheck (String[] playerHand) {

        // sortHand(); // make sure drawCard is properly sorted after adding to players array in DealerCardManagement

        int score = 0;

        for (int i = 0; i < playerHand.length - 1; i++)
            for (int j = i + 1; j < playerHand.length; j++)
                if (doPairCheck(playerHand[i], playerHand[j]))
                    score++;

        for (int i = 0; i < playerHand.length - 1; i++)
            if (cardToInt(playerHand[i]) == cardToInt(playerHand[i + 1] + 1) && cardToInt(playerHand[i + 1]) + 1 != 14)
                if (cardToInt(playerHand[i + 1]) == cardToInt(playerHand[i + 2] + 1) && cardToInt(playerHand[i + 2]) + 1 != 14)
                    if (cardToInt(playerHand[i + 2]) == cardToInt(playerHand[i + 3] + 1) && cardToInt(playerHand[i + 3]) + 1 != 14) {
                        if (cardToInt(playerHand[i + 3]) == cardToInt(playerHand[i + 4] + 1) && cardToInt(playerHand[i + 4]) + 1 != 14) {
                            return score += 5;
                        } else {
                            return score += 4;
                        }
                    } else if (cardToInt(playerHand[i + 2]) == cardToInt(playerHand[i + 3]) && cardToInt(playerHand[i + 3]) == cardToInt(playerHand[i + 4] + 1)) {
                        return score += 4;
                    } else {
                        return score += 3;
                    }


        int count = 0;
        for (int i = 0; i < playerHand.length; i++)
            if (playerHand[i] + 1 == playerHand[i + 1])
                count++;
            else if (playerHand[i] == playerHand[i + 1])
                count = count;
            else
                break;

        if (count == 3 || count == 4 || count == 5)
            score += count;

                        return score;
    }
}
