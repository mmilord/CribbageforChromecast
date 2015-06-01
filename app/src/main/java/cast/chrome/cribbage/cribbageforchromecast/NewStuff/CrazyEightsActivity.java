package cast.chrome.cribbage.cribbageforchromecast.NewStuff;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.ViewGroup.*;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cast.chrome.cribbage.cribbageforchromecast.Model.Card;
import cast.chrome.cribbage.cribbageforchromecast.R;

public class CrazyEightsActivity extends ActionBarActivity implements GSToActivityListener {

    GameState gameState;

    private static final String TAG = CrazyEightsActivity.class.getSimpleName();
    private static final int RESULT_SETTINGS = 1;
    private static Context context;
    RelativeLayout[] cards = new RelativeLayout[6];
    List<RelativeLayout> cards2 = new ArrayList<RelativeLayout>();
    LinearLayout card_container;
    int tempCardHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crazy_eights);

        context = getApplicationContext();
        gameState = new GameState(getApplicationContext(), getResources().getString(R.string.app_id), TAG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);

        gameState.setCastToGSListener(this);

        card_container = (LinearLayout) findViewById(R.id.card_container);
    }

    public void initCardLayouts (List<Card> cardList) {
        for (int i = 0; i < cardList.size(); i++) {
            cards2.add((RelativeLayout) View.inflate(this, R.layout.card_front, null));
            ((TextView)cards2.get(i).findViewById(R.id.cardRank)).setText(cardList.get(i).getRank());
            cards2.get(i).setTag(i);
        }
    }

    public void drawCards2() {
        for (int i = 0; i < 8; i++) {
            cards2.add((RelativeLayout) View.inflate(this, R.layout.card_front, null));
            cards2.get(i).setTag(i);
        }

        int paddingBuffer = getResources().getDimensionPixelOffset(R.dimen.default_padding);

        int usableArea = card_container.getWidth() - paddingBuffer * 2;

        int newCardWidth = (card_container.getWidth() / 4) - paddingBuffer * 2;
        int newCardHeight = (int) (newCardWidth * 1.5);

        if (newCardHeight > card_container.getHeight()) {
            newCardHeight = card_container.getHeight() - 30;
            newCardWidth = (int) (newCardHeight / 1.5);
        }

        tempCardHeight = newCardHeight;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(newCardWidth, newCardHeight);

        card_container.removeAllViews();

        for (RelativeLayout card : cards2) {
            card.setLayoutParams(layoutParams);
            card_container.addView(card);
        }

        float totalCardSpace = (cards2.size() + 1) * (newCardWidth / 2);
        float scale = getResources().getDisplayMetrics().density;
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) cards2.get(0).getLayoutParams();
        marginLayoutParams.leftMargin = (int) ((card_container.getWidth() - totalCardSpace) / 2 - 40);
        cards2.get(0).setLayoutParams(marginLayoutParams);

        reSeatCard(cards2.size());

        float initOffset = newCardWidth / 2;
        float incOffset = 0f;
        for (int i = 1; i < cards2.size(); i++) {
            cards2.get(i).setX(-(initOffset + incOffset));
            incOffset += newCardWidth / 2;
            cards2.get(i).setElevation(cards2.get(i - 1).getElevation() + 1);
        }

        int[] location = new int[2];
        cards2.get(0).getLocationOnScreen(location);
        cards2.get(0).setLeft(location[0]);

        //cards[1].animate();
    }

    public void drawCards() {
        for (int i = 0; i < cards.length; i++) {
            cards[i] = (RelativeLayout) View.inflate(this, R.layout.card_front, null);
            cards[i].setTag(i);
        }

        int paddingBuffer = getResources().getDimensionPixelOffset(R.dimen.default_padding);

        int usableArea = card_container.getWidth() - paddingBuffer * 2;

        int newCardWidth = (card_container.getWidth() / 4) - paddingBuffer * 2;
        int newCardHeight = (int) (newCardWidth * 1.5);

        if (newCardHeight > card_container.getHeight()) {
            newCardHeight = card_container.getHeight() - 30;
            newCardWidth = (int) (newCardHeight / 1.5);
        }

        tempCardHeight = newCardHeight;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(newCardWidth, newCardHeight);

        card_container.removeAllViews();

        for (RelativeLayout card : cards) {
            card.setLayoutParams(layoutParams);
            card_container.addView(card);
        }

        float totalCardSpace = (cards.length + 1) * (newCardWidth / 2);
        float scale = getResources().getDisplayMetrics().density;
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) cards[0].getLayoutParams();
        marginLayoutParams.leftMargin = (int) ((card_container.getWidth() - totalCardSpace) / 2 - 40);
        cards[0].setLayoutParams(marginLayoutParams);

        reSeatCard(cards.length);

        float initOffset = newCardWidth / 2;
        float incOffset = 0f;
        for (int i = 1; i < cards.length; i++) {
            cards[i].setX(-(initOffset + incOffset));
            incOffset += newCardWidth / 2;
            cards[i].setElevation(cards[i - 1].getElevation() + 1);
        }

        int[] location = new int[2];
        cards[0].getLocationOnScreen(location);
        cards[0].setLeft(location[0]);

        cards[1].animate();
    }

    public void reSeatCard (int cardPosition) {

        MarginLayoutParams marginLayoutParams;
        if (cardPosition == cards2.size()) {
            for (RelativeLayout card : cards2) {
                marginLayoutParams = (MarginLayoutParams) card.getLayoutParams();
                //marginLayoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.card_margin_bottom);
                marginLayoutParams.topMargin = (card_container.getHeight() - tempCardHeight) / 3 * 2;
                card.setLayoutParams(marginLayoutParams);
            }
        }
        else {
            marginLayoutParams = (MarginLayoutParams) cards2.get(cardPosition).getLayoutParams();
            marginLayoutParams.topMargin = (card_container.getHeight() - tempCardHeight) / 3 * 2;
            cards2.get(cardPosition).setLayoutParams(marginLayoutParams);
        }
    }

    public void unSeatCard (int cardPosition) {
        if (cards2.get(cardPosition).isSelected()) {
            reSeatCard(cardPosition);
            cards2.get(cardPosition).setSelected(false);
        }
        else {
            for (int i = 0; i < cards.length; i++) {
                if (cards2.get(i).isSelected()) {
                    reSeatCard(i);
                    cards2.get(i).setSelected(false);
                }
            }

            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) cards2.get(cardPosition).getLayoutParams();
            marginLayoutParams.topMargin = (card_container.getHeight() - tempCardHeight) / 3;
            cards2.get(cardPosition).setLayoutParams(marginLayoutParams);
            cards2.get(cardPosition).setSelected(true);
        }
    }

    public void playCard () {
        for (int i = 0; i < cards.length; i++) {
            if(cards2.get(i).isSelected()) {
                card_container.removeView(cards2.get(i));
                cards2.remove(i);
                gameState.playCard(i);
            }
        }
        drawCards();
    }

    public void setCardUnselectable(int cardPosition) {
        ((TextView)cards2.get(cardPosition).findViewById(R.id.cardRank)).setTextColor(Color.GRAY);
        ((TextView)cards2.get(cardPosition).findViewById(R.id.cardRankDown)).setTextColor(Color.GRAY);
        ((TextView)cards2.get(cardPosition).findViewById(R.id.cardRankBig)).setTextColor(Color.GRAY);
    }

    public void cardSelected (View view) {
        unSeatCard((int) view.getTag());
    }

    public void enableDrawCardButton () {
        Log.d(TAG, "draw card button");
        Button tempBtn = (Button) findViewById(R.id.btnDrawCard);
        tempBtn.setEnabled(true);
    }

    public void addCardToHand (int cardPosition, int ordinal) {
        cards2.add(cardPosition, (RelativeLayout) View.inflate(this, R.layout.card_front, null));
        cards2.get(cardPosition).setTag(cardPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.primary, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat
                .getActionProvider(mediaRouteMenuItem);
        // Set the MediaRouteActionProvider selector for device discovery.
        mediaRouteActionProvider.setRouteSelector(gameState.getMediaRouteSelector());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameState.addCallback();
    }

    @Override
    protected void onPause() {
        if (isFinishing())
            gameState.removeCallback();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        gameState.teardown();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                break;
        }
    }

    public void prepGameSetup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Create game");
        alert.setMessage("Name");

        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                gameState.changeName(input.getText().toString());

                System.out.println(input.getText().toString());
                //btnDeal.setVisibility(View.INVISIBLE);
                //btnDeal.animate().alpha(0f).setDuration(100);
            }
        });

        alert.show();
    }

    public void drawCard (View view) {
        gameState.drawCard();
    }

    public void joinGame (View view) {
        gameState.joinGame();
        cards = new RelativeLayout[6];
        drawCards2();
        //prepGameSetup();
    }

    public void startGame (View view) {
        gameState.startGame(2);
        //cards = new RelativeLayout[1];
        //drawCards2();
        //removeCard();
    }

    public void changeName (View view) {
        //prepGameSetup();
        //gameState.changeName("new name lalala");
        cards = new RelativeLayout[7];
        drawCards2();
    }
}
