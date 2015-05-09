package cast.chrome.cribbage.cribbageforchromecast.Model;

import java.util.ArrayList;

import cast.chrome.cribbage.cribbageforchromecast.Model.Base.Card;
import cast.chrome.cribbage.cribbageforchromecast.Model.Base.Deck;
import cast.chrome.cribbage.cribbageforchromecast.Model.Base.Player;

/**
 * Created by robertgross on 5/5/15.
 */
public class CribbageGame {
    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Card> activeCards = new ArrayList<Card>();
    Deck deck;
}
