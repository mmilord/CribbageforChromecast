package cast.chrome.cribbage.cribbageforchromecast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.*;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.widget.Toast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastMediaControlIntent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class PrimaryActivity extends ActionBarActivity {

    DealerCardManagement cardManager;
    ChromecastManagement castManager;

    private static final String TAG = PrimaryActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1;
    private static Context context;
    private static final int RESULT_SETTINGS = 1;

    int MASTER_HEIGHT, MASTER_WIDTH, PADDING_SIDE = 64, PADDING_TOP = 16, PADDING_BOT = 16, FOUR_CARD_SIZE, FIVE_CARD_SIZE, SIX_CARD_SIZE;

    //hard for now
    int myPosition = 1;
    int handCount = 5;

    int selectedCard;

    TextView[] hand = new TextView[handCount];
    Button btnDropCards, btnDisplayCards, btnPlayCard;


    /**
     * Setters and getters
     */
    public String[][] getHands () { return cardManager.getPlayers(); }

    public String getPlayedCard (int playerPosition, int cardPosition) {
        return cardManager.getPlayersCardToString(playerPosition, cardPosition);
    }

    public String[] getCrib () { return cardManager.getCrib(); }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);
        PrimaryActivity.context = getApplicationContext();

        castManager = new ChromecastManagement(getAppContext(), getResources());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(
                android.R.color.transparent));

        RelativeLayout mainLayout;
        mainLayout = new RelativeLayout(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        MASTER_HEIGHT = metrics.heightPixels;
        MASTER_WIDTH = metrics.widthPixels;

        System.out.println(MASTER_HEIGHT + " " + MASTER_WIDTH);

        btnDropCards = (Button) findViewById(R.id.btnDropCards);
        btnDisplayCards = (Button) findViewById(R.id.btnDisplayCards);
        btnPlayCard = (Button) findViewById(R.id.btnPlayCard);

        //procedurally generate views later
        hand[0] = (TextView) findViewById(R.id.card1);
        hand[1] = (TextView) findViewById(R.id.card2);
        hand[2] = (TextView) findViewById(R.id.card3);
        hand[3] = (TextView) findViewById(R.id.card4);
        hand[4] = (TextView) findViewById(R.id.card5);
    }

    public static Context getAppContext() {
        return PrimaryActivity.context;
    }

    /**
     * Redeal the hands and setup new game
     * @param view
     */
    public void newDeal (View view) {
        cardManager = new DealerCardManagement(3);

        btnDisplayCards.setVisibility(View.VISIBLE);
        btnPlayCard.setVisibility(View.INVISIBLE);
        btnDropCards.setVisibility(View.INVISIBLE);

        //castManager.sendHands(cardManager.players);
        castManager.sendScoresDuringPlay(18);

    }

    /**
     * Play currently selected card; remove from selectability and reset position
     * @param view
     */
    public void playCard (View view) {
        System.out.println(cardManager.getPlayersCardToString(myPosition, selectedCard));
        //sendPlayToCast(players[myPosition][selectedCard]);

        if (cardManager.canAddToActiveCards(myPosition, selectedCard)) {
            cardManager.doAddToActiveCards(myPosition, selectedCard);
            hand[selectedCard].setClickable(false);
            resetCardPosition();
            hand[selectedCard].setTextColor(Color.GRAY);

            //btnPlayCard.setClickable(false);
            //btnPlayCard.setTextColor(Color.GRAY);

            castManager.sendCardPlayed(cardManager.getPlayersCardToString(myPosition, selectedCard));
        }
        else
            Toast.makeText(context, "can not drop", Toast.LENGTH_LONG);
    }

    /**
     * Drop card from players hand
     * @param view
     */
    public void dropCard (View view) {
        castManager.sendCardDropped(cardManager.getPlayersCardToString(myPosition, selectedCard));

        replaceCard(selectedCard, myPosition);
        //sendCribToCast(players[myPosition][selectedCard]);

        hand[selectedCard].setVisibility(View.INVISIBLE);
        btnDropCards.setVisibility(View.INVISIBLE);

        cardManager.setPlayState(true);

        btnPlayCard.setVisibility(View.VISIBLE);

        //btnPlayCard.setClickable(false);
        btnPlayCard.setTextColor(Color.WHITE);

    }

    /**
     * Remove requested card from players hand
     * @param replacedCard
     * @param playerPosition
     */
    public void replaceCard (int replacedCard, int playerPosition) {

        boolean dropped = false;

        cardManager.addToCrib(cardManager.getPlayersCardToString(myPosition, replacedCard));

        cardManager.replaceCard(playerPosition, replacedCard);

        /*
        if (selectedCard != handCount - 1)
            for (int i = 0; i < handCount; i++)
                if (i == replacedCard && i + 1 < handCount) {
                    if (!dropped) {
                        cardManager.addToCrib(cardManager.players[playerPosition][handCount - 1]);
                        dropped = true;
                    }
                    cardManager.players[playerPosition][i] = cardManager.players[playerPosition][i + 1];
                }
        else if (!dropped)
            cardManager.addToCrib(cardManager.players[playerPosition][handCount - 1]);
        */

        System.out.println("");
        for (int i = 0; i < 4; i++)
            System.out.println(cardManager.getPlayersCardToString(myPosition, i) + ", ");
    }

    /**
     * Display players hand on screen
     * @param view
     */
    public void displayHand (View view) {

        resetCardPosition();

        for (int i = 0; i < handCount; i++) {
            hand[i].setText(cardManager.getPlayersCardToString(myPosition, i));
            hand[i].setVisibility(View.VISIBLE);
            castManager.sendMessage("1 " + cardManager.getPlayersCardToString(myPosition, i));
        }

        btnDisplayCards.setVisibility(View.INVISIBLE);

        btnDropCards.setVisibility(View.VISIBLE);
        btnDropCards.setClickable(false);
        btnDropCards.setTextColor(Color.GRAY);
    }

    //cycle to selected card and mark as tagged to be played/dropped;
    public void tagSelectedCard (View view) {
        switch (view.getId()) {
            case R.id.card1:
                selectedCard = 0;
                break;
            case R.id.card2:
                selectedCard = 1;
                break;
            case R.id.card3:
                selectedCard = 2;
                break;
            case R.id.card4:
                selectedCard = 3;
                break;
            case R.id.card5:
                selectedCard = 4;
                break;
        }
    }

    /**
     * Switch card to selected state and attach to selectedCard var
     * @param view
     */
    public void selectCard (View view) {

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();

        if (params.topMargin != 5) {

            //reset card position to lower any other cards, tag current as selected, then shift up
            resetCardPosition();
            tagSelectedCard(view);
            params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            params.topMargin = 5;
            view.setLayoutParams(params);

            //drop button only needed prior to play; change from visibility to unclickable in future
            if (!cardManager.getPlayState()) {
                //btnDropCards.setVisibility(View.VISIBLE);
                btnDropCards.setTextColor(Color.BLACK);
                btnDropCards.setClickable(true);
            }
            else {
                //btnPlayCard.setVisibility(View.VISIBLE);
                btnPlayCard.setTextColor(Color.BLACK);
                btnPlayCard.setClickable(true);
            }
        }
        else {
            //shift card back to standard position and hide drop button
            params.topMargin = 75;
            view.setLayoutParams(params);

            btnDropCards.setTextColor(Color.GRAY);
            btnDropCards.setClickable(false);

            btnPlayCard.setTextColor(Color.GRAY);
            btnPlayCard.setClickable(false);

            //btnDropCards.setVisibility(View.INVISIBLE);
            //btnPlayCard.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * reset all card positions back to unselected state
     */
    public void resetCardPosition () {

        RelativeLayout.LayoutParams tempParams;
        for (int i = 0; i < handCount; i++) {
            tempParams = (RelativeLayout.LayoutParams) hand[i].getLayoutParams();
            tempParams.topMargin = 75;
            hand[i].setLayoutParams(tempParams);
        }
    }


    public void viewCreation(Bundle savedInstance)  {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start media router discovery
        castManager.addCallback();
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            // End media router discovery
            castManager.removeCallback();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        teardown();
        super.onDestroy();
    }

    @Override  // Inflate the menu; this adds items to the action bar if it is present.
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.primary, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat
                .getActionProvider(mediaRouteMenuItem);
        // Set the MediaRouteActionProvider selector for device discovery.
        mediaRouteActionProvider.setRouteSelector(castManager.getmMediaRouteSelector());
        //getMenuInflater().inflate(R.menu.primary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent i = new Intent(this, AppPreferences.class);
                startActivityForResult(i, RESULT_SETTINGS);
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                break;

        }

    }

    private void displayUserSettings()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String  settings = "";

        settings=settings+"Password: " + sharedPrefs.getString("prefUserPassword", "NOPASSWORD");

        settings=settings+"\nRemind For Update:"+ sharedPrefs.getBoolean("prefLockScreen", false);

        settings=settings+"\nUpdate Frequency: "
                + sharedPrefs.getString("prefUpdateFrequency", "NOUPDATE");

        //TextView textViewSetting = (TextView) findViewById(R.id.textViewSettings);

        //textViewSetting.setText(settings);
    }


    private void teardown() {
        castManager.teardown();
    }



}
