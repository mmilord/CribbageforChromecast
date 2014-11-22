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
    String drawCard;

    ArrayList<String> activeCards = new ArrayList<String>();

    Boolean playState;

    /**
     * Constructor for initial creation of game; creates deck object and deals hands;
     * @param playerCount   amount of players in game
     */
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

    /**
     * Constructor that is called when game is premade by another player in the game
     * @param predealtPlayers   imported players from predealt from CastManager
     * @param predealtCrib      imported crib from predealt from CastManager
     */
    public DealerCardManagement (String[][] predealtPlayers, String[] predealtCrib) {
        players = predealtPlayers;
        crib = predealtCrib;
        playState = false;
    }


    /**
     * Setters and getters
     */
    public void setPlayState(Boolean playState) { this.playState = playState; }

    public String[][] getPlayers() { return players; }

    public String[] getCrib() { return crib; }

    public Boolean getPlayState() { return playState; }

    public String getPlayersCardToString(int playerPosition, int cardPosition) {
        return players[playerPosition][cardPosition].toString();
    }

    public ArrayList<String> getActiveCards() { return activeCards; }

    public int callPlayerHandScoreCheck(int playerPosition) {
        players[playerPosition][4] = drawCard;
        return Scoring.doHandScoreCheck(players[playerPosition]);
    }

    public void addToCrib (String card) {
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(crib));
        temp.add(card);
        crib = temp.toArray(new String[4]);
    }


    /**
     * Performs the deal action and setups up the game
     */
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

    /**
     * Return whether or not card is allowed to be played
     * @return true if playable, false otherwise
     */
    public Boolean canAddToActiveCards(int playerPosition, int cardPosition) {
        if (!activeCards.isEmpty()) {
            if (countActiveCards() + Scoring.cardToScoringValue(players[playerPosition][cardPosition].toString()) <= 31)
                return true;
            else
                return false;
        }
        else { return true; }
    }

    /**
     * Add card to activeCards variable
     */
    public void doAddToActiveCards (int playerPosition, int cardPosition) {
        if (!activeCards.isEmpty()) {
            activeCards.add(players[playerPosition][cardPosition]);
        }
        else {
            activeCards.clear();
            activeCards.add(players[playerPosition][cardPosition]);
        }
    }

    /**
     * Count the activeCards value
     * @return count
     */
    public int countActiveCards () {
        int count = 0;

        if (!activeCards.isEmpty()) {
            for (int i = 0; i < activeCards.size(); i++) {
                int cardToAdd = Scoring.cardToScoringValue(activeCards.get(0));

                if (count <= 20 && cardToAdd == 1)
                    count += cardToAdd + 10;
                else
                    count += cardToAdd;
            }
        }

        return count;
    }

    /**
     * Obtain rank/value of requested card
     * @return rank as string
     */
    public String getCardRankString(int playerPosition, int cardPosition) {
        return players[playerPosition][cardPosition].toString().substring(0, players[playerPosition][cardPosition].toString().indexOf(" "));
    }

    /**
     * Remove selected card from players hand
     * @param playerPosition
     * @param replacedCard      card to be removed from hand
     */
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

    /**
     * Compare card played with activeCards and check for pairs
     * @return value based on pair count
     */
    public int doPairCheck(int playerPosition, int cardPosition) {

        if (getPlayersCardToString(playerPosition, cardPosition).equals(activeCards.get(activeCards.size()))) {
            if (getPlayersCardToString(playerPosition, cardPosition).equals(activeCards.get(activeCards.size() - 1))) {
                if (getPlayersCardToString(playerPosition, cardPosition).equals(activeCards.get(activeCards.size() - 2))) {
                    return 4;
                } else {
                    return 3;
                }
            }
            else {
                return 2;
            }
        }
        else
            return 0;
    }




}
