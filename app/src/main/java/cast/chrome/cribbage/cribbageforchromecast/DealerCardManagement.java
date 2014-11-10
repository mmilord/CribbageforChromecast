package cast.chrome.cribbage.cribbageforchromecast;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by milord on 08-Nov-14.
 */
public class DealerCardManagement {

    String[][] players;
    String[] crib;
    Deck deck;

    ArrayList<String> activeCards;

    Boolean playState;

    public DealerCardManagement(int playerCount) {
        deck = new Deck();
        crib = new String[4];
        playState = false;

        if (playerCount == 2)
            players = new String[playerCount][6];
        else
            players = new String[playerCount][5];

        dealer();
    }


    ///////////////////////////////////
    ///**     Setters/Getters     **///
    ///////////////////////////////////
    public void setPlayState(Boolean playState) { this.playState = playState; }

    public String[][] getPlayers() { return players; }

    public String[] getCrib() { return crib; }

    public Boolean getPlayState() { return playState; }

    public String getPlayersCardToString(int playerPosition, int cardPosition) {
        return players[playerPosition][cardPosition].toString();
    }

    public ArrayList<String> getActiveCards() { return activeCards; }



    public void addToCrib (String card) {
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(crib));
        temp.add(card);
        crib = temp.toArray(new String[4]);
    }

    public void dealer() {
        System.out.println("Total: " + deck.getTotalCards());

        for (int j = players.length - 1; j > -1; j-- ) {
            for (int i = 0; i < players[0].length; i++) {
                players[j][i] = deck.drawFromDeck() + "";
            }
        }

        System.out.println("Total: " + deck.getTotalCards());

        for (int j = players.length - 1; j > -1; j--) {
            System.out.print("Player " + j + "'s hand: ");
            for (int i = 0; i < players[0].length; i++)
                System.out.print(players[j][i].toString() + ", ");
            System.out.println();
        }
    }

    public Boolean attemptToAddToActiveCards(int playerPosition, int cardPosition) {

        if (!activeCards.isEmpty()) {
            int count = countActiveCards();

            count += cardToInt(players[playerPosition][cardPosition].toString());

            if (count <= 31) {
                activeCards.add(players[playerPosition][cardPosition].toString());
                return true;
            }
            else
                return false;
        }
        else {
            activeCards = new ArrayList<String>();
            activeCards.add(players[playerPosition][cardPosition].toString());

            return true;
        }

    }

    public int countActiveCards () {
        int count = 0;

        for (int i = 0; i < activeCards.size(); i++) {
            count += cardToInt(activeCards.get(0));
        }

        return 0;
    }

    public int cardToInt(String card) {
        if (card.contains(" "))
            card = card.substring(0, card.indexOf(" "));

        if (card == "King" || card == "Queen" || card == "Jack")
            return 10;
        else if (card == "Ace")
            return 1;
        else
            return Integer.parseInt(card);
    }
}
