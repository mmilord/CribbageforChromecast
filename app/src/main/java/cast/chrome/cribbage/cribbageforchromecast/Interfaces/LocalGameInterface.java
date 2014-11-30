package cast.chrome.cribbage.cribbageforchromecast.Interfaces;

import org.json.JSONObject;

import cast.chrome.cribbage.cribbageforchromecast.Model.*;

/**
 * Created by milord on 28-Nov-14.
 */
public interface LocalGameInterface {
    public void onReceiveDealtNewHand();

    public void onReceivePlayerTurn();

    public void onEndPlay();

    public void onGameEnd();

    public void createPlayer(String name);

    public DealerCardManagement getPlayer();

    public Card getCutCard();

    public DealerCardManagement[] getOtherPlayers();
}
