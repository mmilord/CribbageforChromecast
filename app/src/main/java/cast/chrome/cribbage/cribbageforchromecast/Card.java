package cast.chrome.cribbage.cribbageforchromecast;

/**
 * Created by milord on 26-Aug-14.
 */
public class Card {
    private int cardRank, cardSuit;

    private static String[] suitsArray = { "Clubs", "Spades", "Diamonds", "Hearts" };
    private static String[] ranksArray  = { "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King" };


    Card(int cardSuit, int cardRank)
    {
        this.cardRank = cardRank;
        this.cardSuit = cardSuit;
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
}
