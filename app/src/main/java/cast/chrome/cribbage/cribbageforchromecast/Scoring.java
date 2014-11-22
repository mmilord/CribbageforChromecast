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

        // sortHand(); // make sure drawCard is properly sorted after adding to players array in DealerCardManagement

        int[] tempCardRanks = new int[5];

        for (int i = 0; i < playerHand.length; i++)
            tempCardRanks[i] = getCardRankInteger(playerHand[i]);

        tempCardRanks = sortHand(tempCardRanks);

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

    static int[] sortHand (int[] playerHand) {
        int temp = 0;
        boolean sorted = false;

        //Arrays.sort(playerHand);

        while (!sorted) {
            sorted = true;
            for (int i = 0; i < playerHand.length - 1; i++) {
                if (playerHand[i] > playerHand[i + 1]) {
                    temp = playerHand[i];
                    playerHand[i] = playerHand[i + 1];
                    playerHand[i + 1] = temp;
                    sorted = false;
                }
            }
        }

        return playerHand;
    }

    /*
    public void test(String[] cards) {
        for (int i = 0; i < cards.Length; i++) {
            if (i + 1 != cards.Length) {
                if (cards[i].Ordinal + 1 == cards[i + 1].Ordinal) {
                    isNextCardInSequence = true;
                }
                else if(!isPlay && (cards[i].Ordinal == cards[i + 1].Ordinal)) {
                    isNextCardInSequence = true;
                    scoreMultipler++;

                    //Counter Acts Double Rule
                    runLoop-;
                } else {
                    isNextCardInSequence = false;
                }

                if (isNextCardInSequence)
                    runLoop++;

                if (!isNextCardInSequence && runLoop >= 3) {
                    List<Card> tempList = new List<Card>();
                    int startOfRun = (i - runLoop);

                    for (int k = runLoop; k >= startOfRun; k-)
                        tempList.Add(cards[k]);

                    runs.Add(tempList);
                }

                if (!isNextCardInSequence)
                    runLoop = 0;

            }
        }
    }
    */

}
