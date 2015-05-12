package cast.chrome.cribbage.cribbageforchromecast.NewStuff;

import java.util.List;

import cast.chrome.cribbage.cribbageforchromecast.Model.Card;
import cast.chrome.cribbage.cribbageforchromecast.Model.Player;

/**
 * Created by interns on 5/9/15.
 */
public class SystemPlayerControl {

    public static boolean canPlay (List<Card> activeCards, List<Card> playerHand) {
        int lastSuit = activeCards.get(activeCards.size() - 1).getSuit();
        for (int i = 0; i < playerHand.size(); i++) {
            if (lastSuit == playerHand.get(i).getSuit())
                return true;
        }
        return false;
    }

}
