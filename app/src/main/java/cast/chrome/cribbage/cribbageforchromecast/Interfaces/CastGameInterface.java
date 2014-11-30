package cast.chrome.cribbage.cribbageforchromecast.Interfaces;

import org.json.JSONException;
import cast.chrome.cribbage.cribbageforchromecast.Model.*;
/**
 * Created by milord on 28-Nov-14.
 */
public interface CastGameInterface {
    public void sendJoinedGame(DealerCardManagement player);

    public void sendDropCard(DealerCardManagement player, Card card);

    public void sendPlayedCard(DealerCardManagement player, Card card, int pegging);

    public void sendPassTurn(DealerCardManagement player);

    public void sendEndHandScoreReview(DealerCardManagement player);

    public void sendEndCribScoreReview(DealerCardManagement player);

    public void sendPlaySetup(String[][] userHands, Card cutCard, String cribOwner);

    public void onReceivedJoinedGame(DealerCardManagement player) throws Exception;

    public void onReceivedSetupPlay(String[][] userHands, Card cutCard, String cribOwner);

    public void onReceivedCardPlayed(Card card, int pegging);

    public void onReceivedCardDropped(Card card);

    public void onReceivedCribScoreReviewEnded();

    public void onReceivedHandScoreReviewEnded();

    public void onReceivedStartedGame();

}
