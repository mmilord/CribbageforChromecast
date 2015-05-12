package cast.chrome.cribbage.cribbageforchromecast.NewStuff;

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

    public void ReceiveNewRound (int round);

    public void ReceivedEndRound();

    public void ReceivedGameOver (int winPlayerId);

    public void ReceivedPlayerTurn(int playerId);
    
    public void ReceivedPlayerJoined (Player player);

    public void ReceivedPlayerDisconnected (int playerId);
}
