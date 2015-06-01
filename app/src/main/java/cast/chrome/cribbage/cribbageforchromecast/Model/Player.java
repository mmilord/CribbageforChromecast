package cast.chrome.cribbage.cribbageforchromecast.Model;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by interns on 5/9/15.
 */
public class Player {
    private List<Card> hand = new ArrayList<Card>();
    private String color;
    private String name;
    private int id;
    private boolean hasPlayedThisRound;

    public Player (int id, String color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public String getName () {
        return name;
    }

    public List<Card> getHand () {
        return hand;
    }

    public void setHand (Card[] hand) {
        this.hand = Arrays.asList(hand);
    }

    public void addCard (Card cardToAdd) {
        hand.add(cardToAdd);
    }

    public void removeCard (int cardToRemove) {
        hand.remove(cardToRemove);
    }

    public void setHasPlayedThisRound (boolean hasPlayedThisRound) {
        this.hasPlayedThisRound = hasPlayedThisRound;
    }
}
