package cast.chrome.cribbage.cribbageforchromecast.NewStuff;

import java.util.List;

import cast.chrome.cribbage.cribbageforchromecast.Model.Card;
import cast.chrome.cribbage.cribbageforchromecast.Model.Player;

/**
 * Created by interns on 5/10/15.
 */
public interface CastToGSListener {
    /*public void receiveHands(String[][] hands);

    public void receiveCardPlayed(int playerPosition, int cardPosition);

    public void receiveCribCard(int playerPosition, int cardPosition);

    public void receievePlayerPosition (int playerPosition);

    public void receiveCutCard(String cutCard);

    public void receieveNextTurn();

    public void receiveScoreDuringPlay(int scoreDuringPlay);

    public void receieveCouldNotPlay(int couldNotPlayCount);

    public void createNewGame();

    public void myDeal();

    public void prepForDeal();

    public void prepNewPlay();

    public void initGame();*/

    void receiveNewRound(int round);

    void receivedEndRound();

    void receivedGameOver(int winPlayerId);

    void receivedPlayerTurn(int playerId);
    
    void receivedPlayerJoined(Player player);

    void receivedPlayerDisconnected(int playerId);

    void receivedSystemPlayerId(int systemId);

    void receivedPlayers(List<Player> playerList);

    void prepGameSetup();

    void receivedDrawnCard(Card card);
}
