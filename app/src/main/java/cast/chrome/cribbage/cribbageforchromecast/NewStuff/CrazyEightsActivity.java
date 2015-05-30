package cast.chrome.cribbage.cribbageforchromecast.NewStuff;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.view.ViewGroup.*;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import cast.chrome.cribbage.cribbageforchromecast.R;

public class CrazyEightsActivity extends ActionBarActivity {

    GameState gameState;

    private static final String TAG = CrazyEightsActivity.class.getSimpleName();
    private static final int RESULT_SETTINGS = 1;
    private static Context context;
    RelativeLayout[] cards = new RelativeLayout[6];
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



        card_container = (LinearLayout) findViewById(R.id.card_container);
    }

    public void drawCards2() {
        for (int i = 0; i < cards.length; i++)
            cards[i] = (RelativeLayout) View.inflate(this, R.layout.card_front, null);

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
    }

    public void drawCards() {

        for (int i = 0; i < cards.length; i++)
            cards[i] = (RelativeLayout) View.inflate(this, R.layout.card_front, null);

        int paddingBuffer = getResources().getDimensionPixelOffset(R.dimen.default_padding);

        int newCardWidth = (card_container.getWidth() / cards.length) - paddingBuffer * 2;
        int newCardHeight = (int) (newCardWidth * 1.5);

        if (newCardHeight > card_container.getHeight()) {
            newCardHeight = card_container.getHeight() - 10;
            newCardWidth = (int) (newCardHeight / 1.5);
        }

        if (cards.length > 5)
            paddingBuffer /= 2;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(newCardWidth, newCardHeight);

        card_container.removeAllViews();

        for (RelativeLayout card : cards) {
            card.setLayoutParams(layoutParams);
            card_container.addView(card);
        }

        cards[1].setBackgroundColor(Color.CYAN);
        cards[3].setBackgroundColor(Color.CYAN);

        MarginLayoutParams marginLayoutParams;

        for (RelativeLayout card : cards) {
            marginLayoutParams = (MarginLayoutParams) card.getLayoutParams();
            if (cards.length > 6)
                marginLayoutParams.leftMargin = 0 - paddingBuffer;
            else
                marginLayoutParams.leftMargin = paddingBuffer;

            marginLayoutParams.rightMargin = paddingBuffer;
            marginLayoutParams.topMargin = paddingBuffer;
            marginLayoutParams.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.card_margin_bottom);
            card.setLayoutParams(marginLayoutParams);
        }

        /*marginLayoutParams = (MarginLayoutParams) cards[0].getLayoutParams();
        marginLayoutParams.leftMargin = 150;
        cards[0].setLayoutParams(marginLayoutParams);*/

    }

    public void reSeatCard (int cardPosition) {

        MarginLayoutParams marginLayoutParams;
        if (cardPosition == cards.length)
            for (RelativeLayout card : cards) {
                marginLayoutParams = (MarginLayoutParams) card.getLayoutParams();
                //marginLayoutParams.bottomMargin = (int) getResources().getDimension(R.dimen.card_margin_bottom);
                marginLayoutParams.topMargin = (card_container.getHeight() - tempCardHeight) / 3;
                card.setLayoutParams(marginLayoutParams);
            }
        else {
            marginLayoutParams = (MarginLayoutParams) card_container.getLayoutParams();
            marginLayoutParams.bottomMargin = 40;
            cards[cardPosition].setLayoutParams(marginLayoutParams);
        }
    }

    public void unSeatCard (int cardPosition) {


        reSeatCard(cards.length);


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

    public void joinGame (View view) {
        //gameState.JoinGame();
        cards = new RelativeLayout[6];
        drawCards2();
    }

    public void startGame (View view) {
        //gameState.StartGame(3);
        cards = new RelativeLayout[1];
        drawCards2();
    }

    public void changeName (View view) {
        //gameState.ChangeName("new name lalala");
        cards = new RelativeLayout[7];
        drawCards2();
    }
}
