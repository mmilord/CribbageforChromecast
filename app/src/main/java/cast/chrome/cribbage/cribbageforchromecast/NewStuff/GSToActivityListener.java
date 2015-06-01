package cast.chrome.cribbage.cribbageforchromecast.NewStuff;

import java.util.List;

import cast.chrome.cribbage.cribbageforchromecast.Model.Card;

/**
 * Created by interns on 5/30/15.
 */
public interface GSToActivityListener {
    void prepGameSetup();

    void setCardUnselectable(int cardPosition);

    void enableDrawCardButton();

    void addCardToHand(int cardPosition, int ordinal);

    void initCardLayouts(List<Card> cardList);
}
