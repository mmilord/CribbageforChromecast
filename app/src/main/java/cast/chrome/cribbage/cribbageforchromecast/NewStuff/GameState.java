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
    private GSToActivityListener gsToActivityListener;

    public GameState (Context context, String appID, String tag) {
        chromecastControl = new ChromecastControl(context, appID, tag);
        chromecastControl.setCastToGSListener(this);

    }
    public void setCastToGSListener (GSToActivityListener gsToActivityListener) {
        this.gsToActivityListener = gsToActivityListener;
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

    public void joinGame() {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.JOIN, SendKeys.GAME_TYPE, "crazy8").toString());
    }

    public void startGame(int numberOfPlayers) {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.START_GAME).toString());
    }

    public void endGame() {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.GAME_OVER, SendKeys.WINNER_ID, Integer.toString(systemPlayerId)).toString());
    }

    public void readySetNewRound() {
        //call chromecast newround()
    }

    public void playCard(int cardPosition) {
        //tell chromecast playcard(card)
        //call drop method on view
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.CARD_PLAYED, SendKeys.CARD, Integer.toString(playerList.get(systemPlayerId).getHand().get(cardPosition).getOrdinal())).toString());
        playerList.get(systemPlayerId).removeCard(cardPosition);
    }

    public void changeColor(Color color) {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.CHANGE_COLOR, SendKeys.COLOR, color.toString()).toString());
    }

    public void changeName(String name) {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.CHANGE_NAME, SendKeys.NAME, name).toString());
    }

    public void drawCard() {
        chromecastControl.sendMessage(JSONHandler.PackageGenericJson(SendKeys.DRAW_CARD).toString());
    }

    //called for each player ripped from json pack
    public void setHands (int playerId, Card[] hands) {
        this.playerList.get(playerId).setHand(hands);
    }

    public void receiveNewRound(int round) {
        this.round = round;

        for (int i = 0; i < playerList.size(); i++)
            playerList.get(i).setHasPlayedThisRound(false);

        //start round now
    }

    public void recieveDeck(Deck deck) {
        this.deck = deck;
    }

    public void recievedCardPlayed(int playerId, int card) {
        playerList.get(playerId).removeCard(card);
        playerList.get(playerId).setHasPlayedThisRound(true);//hasplayed;
    }

    public void receivedPlayerJoined(Player player) {
        playerList.add(player);
    }

    public void receivedPlayerDisconnected(int playerId) {
        //pause and wait for reconnect
    }

    public void receivedStartCard(Card card) {
        activeCards.add(card);
    }

    public void receivedEndRound() {
        //display round stats
        //prompt ready for next round
    }

    public void receivedGameOver(int winPlayerId) {
        //display round stats
        //prompt ready for next round
    }

    public void receivedDrawnCard(Card card) {
        playerList.get(systemPlayerId).addCard(card);
        gsToActivityListener.addCardToHand(playerList.get(systemPlayerId).getHand().size() - 1, card.getOrdinal());
    }

    public void receivedPlayerTurn(int playerId) {
        if (systemPlayerId == playerId) {
            //SystemPlayerControl.canPlay(activeCards, playerList.get(systemPlayerId).getHand());
            if (!canPlayCard())
                gsToActivityListener.enableDrawCardButton();
        }
        else {
            //display "currently playerlist(playerid).getName()'s turn"
        }
    }

    public void receivedSystemPlayerId(int systemPlayerId) {
        this.systemPlayerId = systemPlayerId;
    }

    public void receivedPlayers(List<Player> playerList) {
        this.playerList = playerList;
    }

    public void prepGameSetup() {
        gsToActivityListener.prepGameSetup();
    }

    public boolean canPlayCard() {
        boolean canPlay = false;

        Card lastPlayedCard = activeCards.get(activeCards.size() - 1);
        for (int i = 0; i < playerList.get(systemPlayerId).getHand().size(); i++) {
            Card tempCard = playerList.get(systemPlayerId).getHand().get(i);
            if (lastPlayedCard.getSuit() != tempCard.getSuit() &&
                    tempCard.getRank() != lastPlayedCard.getRank() && tempCard.getRank() != 8) {
                gsToActivityListener.setCardUnselectable(i);
            }
            else {
                canPlay = true;
            }
        }

        return canPlay;
    }

    public void initCardLayouts () {
        gsToActivityListener.initCardLayouts(playerList.get(systemPlayerId).getHand());
    }
}
