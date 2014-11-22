package cast.chrome.cribbage.cribbageforchromecast;

import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by milord on 12-Nov-14.
 */
public class Scoring {
    /**
     * @return requested card as integer
     */
    static int cardToScoringValue(String card) {
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
        if (card.contains(" "))
            card = card.substring(0, card.indexOf(" "));
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
        sorthand(playerHand);
        int[] tempCardRanks = new int[5];
        for (int i = 0; i < playerHand.length; i++)
            tempCardRanks[i] = getCardRankInteger(playerHand[i]);
        int score = 0;
//Check for pairs
        for (int i = 0; i < playerHand.length - 1; i++)
            for (int j = i + 1; j < playerHand.length; j++)
                if (doPairCheck(playerHand[i], playerHand[j]))
                    score++;
        int runCount = 0;
//alt run check
        for (int i = 0; i < playerHand.length; i++) {
            if (tempCardRanks[i] != tempCardRanks[i + 1]) {
                if (tempCardRanks[i + 1] - tempCardRanks[i] <= 1) {
                    runCount++;
                }
            }
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
    static String[] sorthand (String[] playerHand) {
        String temp;
        boolean sorted = false;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < playerHand.length - 1; i++) {
                if (getCardRankInteger(playerHand[i]) > getCardRankInteger(playerHand[i + 1])) {
                    temp = playerHand[i];
                    playerHand[i] = playerHand[i + 1];
                    playerHand[i + 1] = temp;
                    sorted = false;
                }
            }
        }
        return playerHand;
    }
}