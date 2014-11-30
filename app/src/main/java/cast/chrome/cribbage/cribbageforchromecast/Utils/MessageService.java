package cast.chrome.cribbage.cribbageforchromecast.Utils;

import android.content.Context;

import cast.chrome.cribbage.cribbageforchromecast.Interfaces.*;
import cast.chrome.cribbage.cribbageforchromecast.Model.*;

/**
 * Created by milord on 28-Nov-14.
 */
/*public class MessageService implements CastGameInterface {
    private CastMessageParserLocal _chromeCast;
    private CastGameInterface castGameInterface;

    public MessageService(Context c, String apID, CastGameInterface networkGamePlayReceiver){
        _chromeCast = new CastMessageParserLocal(c,apID,this);
        castGameInterface = networkGamePlayReceiver;
    }

    @Override
    public void sendJoinedGame(DealerCardManagement player) {
        _chromeCast.sendJoinedGame(player);
    }

    @Override
    public void sendDropCard(DealerCardManagement player, Card card) {
        _chromeCast.sendDropCard(player, card);
    }

    @Override
    public void sendPlayedCard(DealerCardManagement player, Card card, int pegging) {
        _chromeCast.sendPlayedCard(player, card, pegging);
    }

    @Override
    public void sendPassTurn(DealerCardManagement player) {
        _chromeCast.sendPassTurn(player);
    }

    @Override
    public void sendEndHandScoreReview(DealerCardManagement player) {
        _chromeCast.sendEndHandScoreReview(player);
    }

    @Override
    public void sendEndCribScoreReview(DealerCardManagement player) {
        _chromeCast.sendEndCribScoreReview(player);
    }

    @Override
    public void sendPlaySetup(String[][] userHands, Card cutCard, String cribOwner) {
        _chromeCast.sendPlaySetup(userHands, cutCard, cribOwner);
    }

    @Override
    public void onReceivedJoinedGame(DealerCardManagement player) {
        try {
            castGameInterface.onReceivedJoinedGame(player);
        } catch (Exception e) { }
    }

    @Override
    public void onReceivedStartedGame() {
        castGameInterface.onReceivedStartedGame();
    }

    @Override
    public void onReceivedSetupPlay(String[][] userHands, Card cutCard, String cribOwner) {
        castGameInterface.onReceivedSetupPlay(userHands, cutCard, cribOwner);
    }

    @Override
    public void onReceivedCardPlayed(Card card, int pegging) {
        castGameInterface.onReceivedCardPlayed(card, pegging);
    }

    @Override
    public void onReceivedCardDropped(Card card) {
        castGameInterface.onReceivedCardDropped(card);
    }

    @Override
    public void onReceivedCribScoreReviewEnded() {
        castGameInterface.onReceivedCribScoreReviewEnded();
    }

    @Override
    public void onReceivedHandScoreReviewEnded() {
        castGameInterface.onReceivedHandScoreReviewEnded();
    }

}
*/