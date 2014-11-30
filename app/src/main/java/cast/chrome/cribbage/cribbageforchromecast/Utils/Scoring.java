package cast.chrome.cribbage.cribbageforchromecast.Utils;

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
    public static int cardToScoringValue(String card) {
        if (card.contains(" "))
            card = card.substring(0, card.indexOf(" "));
        if (card.equals("King") || card.equals("Queen") || card.equals("Jack"))
            return 10;
        else if (card.equals("Ace"))
            return 1;
        else
            return Integer.parseInt(card);
    }
    public static int getCardRankInteger(String card) {
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

    public static int doPlayPairCheck (String card, ArrayList<String> activeCards) {
        int count = 0;

        for (int i = activeCards.size() - 1; i > 0 || count < 4; i--)
            if (card.equals(activeCards.get(i)))
                count++;
            else
                break;

        if (count == 1)
            return 2;
        else if (count == 2)
            return 6;
        else if (count == 3)
            return 12;
        else
            return 0;
    }

    /**
     * Compare played card with requested card
     * @return true if pair
     */
    public static boolean doPairCheck(String cardPlayed, String activeCard) {
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
    public static int doHandScoreCheck (String[] playerHand) {

        //sorthand(playerHand);

        int[] tempCardRanks = new int[5];

        for (int i = 0; i < playerHand.length; i++)
            tempCardRanks[i] = getCardRankInteger(playerHand[i]);

        int score = 0;

        //Check for pairs
        for (int i = 0; i < playerHand.length - 1; i++)
            for (int j = i + 1; j < playerHand.length; j++)
                if (doPairCheck(playerHand[i], playerHand[j]))
                    score++;



        //alt run check
        int trueRun = 0;
        int possRun = 1;
        int pairCounter = 0;
        int multiRun = 1;
        for (int i = 0; i < tempCardRanks.length - 1; i++) {
            if (tempCardRanks[i] - tempCardRanks[i + 1] == 0) {
                pairCounter++;
                if (possRun > 1 || i == 0 || (i == 1 & multiRun == 1) || (i == 2 & multiRun == 2))
                    multiRun++;
            } else if (tempCardRanks[i + 1] - tempCardRanks[i] == 1) {
                possRun++;
                if (possRun >= 3)
                    trueRun = possRun;
            } else {
                possRun = 1;
            }
        }
        //errors with multiRun {2,2,5,6,7} fails; with multiRun=0 in else then {2,3,3,4,8} fails
        score += (trueRun * multiRun);
        score += pairCounter * 2;


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

    public static String[] sorthand (String[] playerHand) {
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