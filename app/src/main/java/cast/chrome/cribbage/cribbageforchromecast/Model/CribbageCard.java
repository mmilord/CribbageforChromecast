package cast.chrome.cribbage.cribbageforchromecast.Model;

/**
 * Created by robertgross on 5/5/15.
 */
public class CribbageCard extends Card {

    private boolean played;

    public CribbageCard(int cardSuit, int cardRank)
    {
        super(cardSuit, cardRank);
    }

    public boolean wasPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }
}
