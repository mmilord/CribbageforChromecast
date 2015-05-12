package cast.chrome.cribbage.cribbageforchromecast.NewStuff;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.media.MediaRouteSelector;

import java.util.ArrayList;
import java.util.List;

import cast.chrome.cribbage.cribbageforchromecast.Model.Card;
import cast.chrome.cribbage.cribbageforchromecast.Model.Deck;
import cast.chrome.cribbage.cribbageforchromecast.Model.Player;

/**
 * Created by interns on 5/9/15.
 */
public class GameState implements CastToGSListener {
    private List<Player> playerList = new ArrayList<>();
    private int systemPlayerId;
    private List<Card> activeCards = new ArrayList<>();
    private Deck deck;
    private int activePlayer;
    private int round;
    ChromecastControl chromecastControl;

    public GameState (Context context, String appID, String tag) {
        chromecastControl = new ChromecastControl(context, appID, tag);
        chromecastControl.setCastToGSListener(this);
    }

    public List<Card> getActiveCards() {
        return activeCards;
    }

    public MediaRouteSelector getMediaRouteSelector() {
        return chromecastControl.getmMediaRouteSelector();
    }

    public void addCallback () {
        chromecastControl.addCallback();
    }

    public void removeCallback () {
        chromecastControl.removeCallback();
    }

    public void teardown () {
        chromecastControl.teardown();
    }

    public void JoinGame () {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.JOIN).toString());
    }

    public void StartGame (int numberOfPlayers) {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.START_GAME).toString());
    }

    public void EndGame () {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.GAME_OVER, SendKeys.WINNER_ID, Integer.toString(systemPlayerId)).toString());
    }

    public void ReadySetNewRound () {
        //call chromecast newround()
    }

    public void PlayCard (int card) {
        //tell chromecast playcard(card)
        //call drop method on view
        playerList.get(systemPlayerId).removeCard(card);
    }

    public void ChangeColor (Color color) {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.CHANGE_COLOR, SendKeys.COLOR, color.toString()).toString());
    }

    public void ChangeName (String name) {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.CHANGE_NAME, SendKeys.NAME, name).toString());
    }

    //called for each player ripped from json pack
    public void SetHands (int playerId, Card[] hands) {
        this.playerList.get(playerId).setHand(hands);
    }

    public void ReceiveNewRound (int round) {
        this.round = round;

        for (int i = 0; i < playerList.size(); i++)
            playerList.get(i).setHasPlayedThisRound(false);

        //start round now
    }

    public void RecieveDeck (Deck deck) {
        this.deck = deck;
    }

    public void RecievedCardPlayed (int playerId, int card) {
        playerList.get(playerId).removeCard(card);
        playerList.get(playerId).setHasPlayedThisRound(true);//hasplayed;
    }

    public void ReceivedPlayerJoined (Player player) {

    }

    public void ReceivedPlayerDisconnected (int playerId) {
        //pause and wait for reconnect
    }

    public void RecievedStartCard (Card card) {
        activeCards.add(card);
    }

    public void ReceivedEndRound() {
        //display round stats
        //prompt ready for next round
    }

    public void ReceivedGameOver (int winPlayerId) {
        //display round stats
        //prompt ready for next round
    }

    public void ReceivedPlayerTurn(int playerId) {
        if (systemPlayerId == playerId) {
            SystemPlayerControl.canPlay(activeCards, playerList.get(systemPlayerId).getHand());
        }
        else {
            //display "currently playerlist(playerid).getName()'s turn"
        }
    }
}
