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

    ArrayList<String> activeCards = new ArrayList<String>();

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

    public Boolean canAddToActiveCards(int playerPosition, int cardPosition) {
        if (!activeCards.isEmpty()) {
            if (countActiveCards() + cardToInt(players[playerPosition][cardPosition].toString()) <= 31)
                return true;
            else
                return false;
        }
        else { return true; }
    }

    public void doAddToActiveCards (int playerPosition, int cardPosition) {
        if (!activeCards.isEmpty()) {
            activeCards.add(players[playerPosition][cardPosition]);
        }
        else {
            activeCards = new ArrayList<String>();
            activeCards.add(players[playerPosition][cardPosition]);
        }
    }

    public int countActiveCards () {
        int count = 0;

        for (int i = 0; i < activeCards.size(); i++) {
            int cardToAdd = cardToInt(activeCards.get(0));

            if (count <= 20 && cardToAdd == 1)
                count += cardToAdd + 10;
            else
                count += cardToAdd;
        }

        return 0;
    }

    public int cardToInt(String card) {
        if (card.contains(" "))
            card = card.substring(0, card.indexOf(" "));

        if (card.equals("King") || card.equals("Queen") || card.equals("Jack"))
            return 10;
        else if (card.equals("Ace"))
            return 1;
        else
            return Integer.parseInt(card);
    }


    public void replaceCard(int playerPosition, int replacedCard) {
        players[playerPosition][replacedCard] = null;
        //ArrayList<String> temp = new ArrayList<String>();


        ArrayList<String> list = new ArrayList<String>();
        for (int j = 0; j < players[playerPosition].length; j++) {
            if (players[playerPosition][j] != null) {
                list.add(players[playerPosition][j]);
            }
        }
        players[playerPosition] = list.toArray(new String[list.size()]);

        //players[playerPosition] = temp.toArray(new String[temp.size()]);
    }


    public int doPairCheck(int playerPosition, int cardPosition) {
        String card = getCardRankString(playerPosition, cardPosition);

        if (activeCards.get(activeCards.size() - 1).equals(card)) {
            if (activeCards.get(activeCards.size() - 2).equals(card)) {
                if (activeCards.get(activeCards.size() - 3).equals(card)) {
                    return 4;
                } else {
                    return 3;
                }
            }
            else {
                return 2;
            }
        }
        else { return 0; }
    }

    public int doRunCheck(int playerPosition, int cardPosition) {
        int card = getCardRankInteger(getCardRankString(playerPosition, cardPosition));

        // temps?
        // idea: compare active to card then active-1 to active and card then active -2 to active-1/active/card, etc
        // easier way?
        if (getCardRankInteger(activeCards.get(activeCards.size() - 1)) == card - 1 || getCardRankInteger(activeCards.get(activeCards.size() - 1)) == card + 1) {

        }

        return 0;
    }

    public String getCardRankString(int playerPosition, int cardPosition) {
        return players[playerPosition][cardPosition].toString().substring(0, players[playerPosition][cardPosition].toString().indexOf(" "));
    }

    public int getCardRankInteger(String card) {

        if (card.equals("King"))
            return 13;
        else if (card.equals("Queen"))
            return 12;
        else if (card.equals("Jack"))
            return 11;
        else if (card.equals("Ace"))
            return 1;
        else
            return Integer.parseInt(card);
    }
}
