package cast.chrome.cribbage.cribbageforchromecast;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.*;
import android.widget.Toast;
import org.json.JSONObject;
import org.w3c.dom.Text;

import cast.chrome.cribbage.cribbageforchromecast.Interfaces.*;
import cast.chrome.cribbage.cribbageforchromecast.Model.*;
import cast.chrome.cribbage.cribbageforchromecast.Utils.*;


public class PrimaryActivity extends ActionBarActivity implements ChromecastManagement.MyTestListener {

    DealerCardManagement cardManager;
    ChromecastManagement castManager;

    private static final String TAG = PrimaryActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1;
    private static Context context;
    private static final int RESULT_SETTINGS = 1;

    int MASTER_SCREEN_SIZE_CATEGORY, MASTER_HEIGHT, MASTER_WIDTH, MASTER_TOP_PADDING, MASTER_SIDE_PADDING, MASTER_BOTTOM_PADDING;
    float MASTER_DENSITY;
    double PADDING_TEST;

    //hard for now
    int myPosition;
    int handCount = 5;

    int selectedCard;

    int couldNotPlayCount = 0;

    String myName;

    TextView[] handRankLabel = new TextView[handCount];
    TextView[] handRankLabelUpsideDown = new TextView[handCount];
    TextView[] handRankLabelBig = new TextView[handCount];
    RelativeLayout[] cardNumber = new RelativeLayout[handCount];

    Button btnDropCards, btnDisplayCards, btnPlayCard, btnDeal;
    TextView txtCurrentScore;

    Typeface font;

    ProgressDialog pd;

    private static final String[] numberToText = {
            "",
            "ONE",
            "TWO",
            "THREE",
            "FOUR",
            "FIVE",
            "SIX",
            "SEVEN",
            "EIGHT",
            "NINE",
            "TEN",
    };

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

        SharedPreferences prefs = getSharedPreferences("cast.chrome.cribbage.cribbageforchromecast", MODE_PRIVATE);
        String theme = prefs.getString("theme", null);

        if (theme != null)
            if (theme.equals("dark"))
                setTheme(R.style.AppTheme_Dark);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);
        PrimaryActivity.context = getApplicationContext();

        //font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        CastReceiver c = new CastReceiver() {
            @Override
            public void onReceiveJSON(JSONObject jsonObject) throws Exception {

            }
        };

        castManager = new ChromecastManagement(getAppContext(), getResources().getString(R.string.app_id), TAG, c);

        //Attach listener for interface
        castManager.setMyTestListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);

        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(
                android.R.color.transparent));*/

        initVars();

        btnDeal.setText("Setup Game");
        btnDeal.setOnClickListener(new  Button.OnClickListener() {
            public void onClick(View v) {
                createNewGame();
            }
        });

        for (int i = 0; i < cardNumber.length; i++) {
            //card.setHeight(CARD_HEIGHT);
            //card.setWidth(CARD_WIDTH);
            cardNumber[i].setVisibility(View.VISIBLE);
            cardNumber[i].setClickable(false);
            //card.
            ViewGroup layout = cardNumber[i];
            LayoutTransition layoutTransition = layout.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            layoutTransition.setStartDelay(LayoutTransition.CHANGING, 0);

            cardNumber[i].setBackground(getResources().getDrawable(R.drawable.card_front_design));
            handRankLabelBig[i].setVisibility(View.INVISIBLE);
            handRankLabel[i].setVisibility(View.INVISIBLE);
            handRankLabelUpsideDown[i].setVisibility(View.INVISIBLE);
        }

        //txtCurrentScore.setTypeface(font);
        newDeal(btnDeal);


    }


    public void switchTheme(View view) {
        //SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String theme;

        TypedValue outVal = new TypedValue();
        getTheme().resolveAttribute(R.attr.themeName, outVal, true);

        if (outVal.string.equals("teal")) {
            setTheme(R.style.AppTheme_Dark);
            theme = "dark";
        } else {
            setTheme(R.style.AppTheme_Teal);
            theme = "teal";
        }

        SharedPreferences.Editor editor = getSharedPreferences("cast.chrome.cribbage.cribbageforchromecast", MODE_PRIVATE).edit();
        editor.putString("theme", theme);
        editor.commit();

        setContentView(R.layout.activity_primary);
    }


    public void initVars() {
        //procedurally generate views later
        cardNumber[0] = (RelativeLayout) findViewById(R.id.card1);
        cardNumber[1] = (RelativeLayout) findViewById(R.id.card2);
        cardNumber[2] = (RelativeLayout) findViewById(R.id.card3);
        cardNumber[3] = (RelativeLayout) findViewById(R.id.card4);
        cardNumber[4] = (RelativeLayout) findViewById(R.id.card5);

        handRankLabel[0] = (TextView) findViewById(R.id.card1rank);
        handRankLabel[1] = (TextView) findViewById(R.id.card2rank);
        handRankLabel[2] = (TextView) findViewById(R.id.card3rank);
        handRankLabel[3] = (TextView) findViewById(R.id.card4rank);
        handRankLabel[4] = (TextView) findViewById(R.id.card5rank);

        handRankLabelUpsideDown[0] = (TextView) findViewById(R.id.card1rankDown);
        handRankLabelUpsideDown[1] = (TextView) findViewById(R.id.card2rankDown);
        handRankLabelUpsideDown[2] = (TextView) findViewById(R.id.card3rankDown);
        handRankLabelUpsideDown[3] = (TextView) findViewById(R.id.card4rankDown);
        handRankLabelUpsideDown[4] = (TextView) findViewById(R.id.card5rankDown);

        handRankLabelBig[0] = (TextView) findViewById(R.id.card1rankBig);
        handRankLabelBig[1] = (TextView) findViewById(R.id.card2rankBig);
        handRankLabelBig[2] = (TextView) findViewById(R.id.card3rankBig);
        handRankLabelBig[3] = (TextView) findViewById(R.id.card4rankBig);
        handRankLabelBig[4] = (TextView) findViewById(R.id.card5rankBig);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        MASTER_HEIGHT = metrics.heightPixels;
        MASTER_WIDTH = metrics.widthPixels;
        MASTER_DENSITY = metrics.density;

        //add to res/dimens.xml
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cardNumber[0].getLayoutParams();
        MASTER_TOP_PADDING = params.topMargin;
        MASTER_SIDE_PADDING = params.leftMargin;
        MASTER_BOTTOM_PADDING = params.bottomMargin;

        System.out.println(MASTER_HEIGHT + " " + MASTER_WIDTH);

        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                MASTER_SCREEN_SIZE_CATEGORY = 1;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                MASTER_SCREEN_SIZE_CATEGORY = 2;
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                MASTER_SCREEN_SIZE_CATEGORY = 3;
                break;
            default:
                MASTER_SCREEN_SIZE_CATEGORY = 4;
        }

        btnDropCards = (Button) findViewById(R.id.btnDropCards);
        btnDisplayCards = (Button) findViewById(R.id.btnDisplayCards);
        btnPlayCard = (Button) findViewById(R.id.btnPlayCard);
        btnDeal = (Button) findViewById(R.id.btnDeal);
        txtCurrentScore = (TextView) findViewById(R.id.scoreTextView);
    }

    public static Context getAppContext() {
        return PrimaryActivity.context;
    }

    public void createNewGame() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Create game");
        alert.setMessage("Name");

        final EditText input = new EditText(this);
        alert.setView(input);alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myName = input.getText().toString();

                System.out.println(myName);

                castManager.joinGame(myName);

                //btnDeal.setVisibility(View.INVISIBLE);
                btnDeal.animate().alpha(0f).setDuration(100);
            }
        });

        alert.show();


    }

    /**
     * Redeal the hands and setup new game
     * @param view
     */
    public void newDeal (View view) {
        cardManager = new DealerCardManagement(3);

        myPosition = 1;

        for (int i = 0; i < 5; i++) {
            resetFullLayout();
            cardNumber[i].setVisibility(View.VISIBLE);
        }

        btnDisplayCards.setVisibility(View.VISIBLE);
        btnPlayCard.setVisibility(View.INVISIBLE);
        btnDropCards.setVisibility(View.INVISIBLE);
        btnDeal.setVisibility(View.INVISIBLE);

        castManager.sendHands(cardManager.getPlayers());
        castManager.sendCrib(cardManager.getCrib());
        castManager.sendCutCard(cardManager.getCutCard());
    }

    /**
     * Play currently selected card; remove from selectability and reset position
     * @param view
     */
    public void playCard (View view) {
        System.out.println(cardManager.getPlayersCardToString(myPosition, selectedCard));

        if (cardManager.canAddToActiveCards(myPosition, selectedCard)) {
            castManager.sendPegPoints(cardManager.doAddToActiveCards(myPosition, selectedCard));
            cardNumber[selectedCard].setClickable(false);
            resetCardPosition();

            handRankLabel[selectedCard].setTextColor(Color.GRAY);
            handRankLabelUpsideDown[selectedCard].setTextColor(Color.GRAY);

            cardNumber[selectedCard].setSelected(true);

            btnPlayCard.setClickable(false);
            btnPlayCard.setTextColor(Color.GRAY);

            //castManager.sendPlayerPositionAndCardPositionToCast(myPosition, selectedCard, "send_card_played");
            castManager.sendCardPlayed(cardManager.getPlayersCardToString(myPosition, selectedCard), myPosition, selectedCard);

            castManager.sendIntToCast(cardManager.getCurrentScore(), "scores_during_play");

            lockCards();
        }
        else
            Toast.makeText(context, "can not drop", Toast.LENGTH_LONG);
    }

    /**
     * Drop card from players hand
     * @param view
     */
    public void dropCard (View view) {
        castManager.sendPlayerPositionAndCardPositionToCast(myPosition, selectedCard, "card_dropped");

        replaceCard(selectedCard, myPosition);
        //sendCribToCast(players[myPosition][selectedCard]);

        DrawingAndAnimating.cardFlipListener animListener = new DrawingAndAnimating.cardFlipListener();
        Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.to_middle);
        final Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.from_middle);
        //animListener.attachViews(cardNumber[selectedCard], handRankLabel[selectedCard], handRankLabelUpsideDown[selectedCard], handRankLabelBig[selectedCard], anim1, anim2, getResources(), false);
        //anim1.setAnimationListener(animListener);
        //anim2.setAnimationListener(animListener);

        final Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_top);
        slide.setInterpolator(getApplicationContext(), android.R.anim.accelerate_decelerate_interpolator);
        slide.setDuration(400);


        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }

            @Override
            public void onAnimationEnd(Animation animation) {
                LayoutTransition lt = new LayoutTransition();
                lt.disableTransitionType(LayoutTransition.DISAPPEARING);

                cardNumber[selectedCard].setLayoutTransition(lt);
                handRankLabelBig[selectedCard].setVisibility(View.INVISIBLE);
                handRankLabel[selectedCard].setVisibility(View.INVISIBLE);
                handRankLabelUpsideDown[selectedCard].setVisibility(View.INVISIBLE);
                cardNumber[selectedCard].setBackground(getResources().getDrawable(R.drawable.card_front_design));
                cardNumber[selectedCard].startAnimation(anim2);

                lt.enableTransitionType(LayoutTransition.DISAPPEARING);
                cardNumber[selectedCard].setLayoutTransition(lt);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardNumber[selectedCard].startAnimation(slide);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        slide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }

            @Override
            public void onAnimationEnd(Animation animation) {

                cardNumber[selectedCard].setVisibility(View.GONE);
                for (RelativeLayout card : cardNumber) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();

                    params.leftMargin = MASTER_SIDE_PADDING * 2;
                    params.rightMargin = MASTER_SIDE_PADDING * 2;

                    card.setLayoutParams(params);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        cardNumber[selectedCard].clearAnimation();
        cardNumber[selectedCard].startAnimation(anim1);


        //btnDropCards.setVisibility(View.INVISIBLE);
        btnDropCards.animate().alpha(0f).setDuration(100);

        cardManager.setPlayState(true);

        btnPlayCard.setVisibility(View.VISIBLE);

        //btnPlayCard.setClickable(false);
        btnPlayCard.setTextColor(Color.WHITE);

        if (myPosition == 1)
            lockCards();

        txtCurrentScore.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cardNumber[0].getLayoutParams();
        MASTER_SIDE_PADDING = params.leftMargin;
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

        System.out.println("");
        for (int i = 0; i < 4; i++)
            System.out.println(cardManager.getPlayersCardToString(myPosition, i) + ", ");
    }


    public void lockCards () {
        for (int i = 0; i < 4; i++)
            cardNumber[i].setClickable(false);
    }


    /**
     * Display players hand on screen
     */
    public void displayHand (View view) {

        resetCardPosition();

        drawHands();

        //Scoring.doHandScoreCheck(cardManager.players[1]);
    }

    public void drawHands() {
        for (int i = 0; i < handCount; i++) {
            cardNumber[i].setGravity(Gravity.LEFT | Gravity.TOP);

            String[] parts = cardManager.getPlayersCardToString(myPosition, i).split(" of ");

            if (parts[1].equals("♣") || parts[1].equals("♠")) {
                handRankLabel[i].setTextColor(Color.BLACK);
                handRankLabelUpsideDown[i].setTextColor(Color.BLACK);
                handRankLabelBig[i].setTextColor(Color.BLACK);
            }

            handRankLabel[i].setText(parts[0] + System.getProperty("line.separator") + parts[1]);
            handRankLabel[i].setTextSize(MASTER_SCREEN_SIZE_CATEGORY * 10);
            //handRankLabel[i].setTypeface(font);
            handRankLabelUpsideDown[i].setText(parts[0] + System.getProperty("line.separator") + parts[1]);
            handRankLabelUpsideDown[i].setTextSize(MASTER_SCREEN_SIZE_CATEGORY * 10);
            //handRankLabelUpsideDown[i].setTypeface(font);

            String numInText;

            if (parts[0].equals("A"))
                numInText = "ACE";
            else if (parts[0].equals("J"))
                numInText = "JACK";
            else if (parts[0].equals("Q"))
                numInText = "QUEEN";
            else if (parts[0].equals("K"))
                numInText = "KING";
            else
                numInText = numberToText[Integer.parseInt(parts[0])];

            String temp = "";

            for (int j = 0; j < numInText.length(); j++) {
                temp += numInText.substring(j, j + 1);
                if (j != numInText.length() - 1)
                    temp += System.getProperty("line.separator");
            }

            handRankLabelBig[i].setText(temp);

            //handRankLabelBig[i].setTypeface(font);
            handRankLabelBig[i].setTextSize(MASTER_SCREEN_SIZE_CATEGORY * (15 - numInText.length()));

            cardNumber[i].setVisibility(View.VISIBLE);
            cardNumber[i].setClickable(true);

            LayoutTransition lt = new LayoutTransition();
            lt.disableTransitionType(LayoutTransition.APPEARING);
            cardNumber[i].setLayoutTransition(lt);

            DrawingAndAnimating.cardFlipListener animListener = new DrawingAndAnimating.cardFlipListener();
            Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.to_middle);
            Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.from_middle);
            animListener.attachViews(cardNumber[i], handRankLabel[i], handRankLabelUpsideDown[i], handRankLabelBig[i], anim1, anim2, getResources(), true);
            anim1.setAnimationListener(animListener);
            anim2.setAnimationListener(animListener);

            cardNumber[i].clearAnimation();
            cardNumber[i].setAnimation(anim1);
            cardNumber[i].startAnimation(anim1);
        }

        btnDisplayCards.animate().alpha(0f).setDuration(100);
        //btnDisplayCards.setVisibility(View.INVISIBLE);


        btnDropCards.setVisibility(View.VISIBLE);
        btnDropCards.setClickable(false);
        btnDropCards.setTextColor(Color.GRAY);
    }

    /**
     * Establish players turn, if
     */
    public void myTurn() {
        boolean canPlay = false;

        for (int i = 0; i < 4; i++) {
            if (cardManager.canAddToActiveCards(myPosition, i)) {
                if (!cardNumber[i].isSelected()) {
                    cardNumber[i].setClickable(true);
                    handRankLabel[i].setTextColor(Color.RED);
                    handRankLabelUpsideDown[i].setTextColor(Color.RED);
                    canPlay = true;
                }
            } else {
                cardNumber[i].setClickable(false);
                handRankLabel[i].setTextColor(Color.GRAY);
                handRankLabelUpsideDown[i].setTextColor(Color.GRAY);
            }
        }

        if (couldNotPlayCount == 2) {
            castManager.sendPrepNewPlay();
            cardManager.resetActiveCards();
            cardManager.setCurrentScore(0);
            drawCurrentScore();
        } else if (canPlay) {
            btnPlayCard.setClickable(true);
            btnPlayCard.setTextColor(Color.WHITE);
            couldNotPlayCount = 0;
        } else {
            btnPlayCard.setClickable(false);
            btnPlayCard.setTextColor(Color.GRAY);
            castManager.sendNextPlayerTurn(couldNotPlayCount++);
            System.out.println("could not play");
        }
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

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();


        if (params.topMargin == MASTER_TOP_PADDING) {

            resetCardPosition();
            tagSelectedCard(view);

            DrawingAndAnimating.animateCardMargin(true, view, getResources());

            view.animate().translationZ(20f).setDuration(200);

            //drop button only needed prior to play; change from visibility to unclickable in future
            if (!cardManager.getPlayState()) {
                //btnDropCards.setVisibility(View.VISIBLE);
                btnDropCards.setTextColor(Color.WHITE);
                btnDropCards.setClickable(true);
                btnDropCards.animate().translationZ(10f).setDuration(200);
            }
            else {
                //btnPlayCard.setVisibility(View.VISIBLE);
                btnPlayCard.setTextColor(Color.WHITE);
                btnPlayCard.setClickable(true);
                btnPlayCard.animate().translationZ(10f).setDuration(200);
            }
        }
        else {
            //shift card back to standard position and hide drop button
            resetCardPosition();

            btnDropCards.setTextColor(Color.GRAY);
            btnDropCards.setClickable(false);
            btnDropCards.animate().translationZ(1f).setDuration(200);

            btnPlayCard.setTextColor(Color.GRAY);
            btnPlayCard.setClickable(false);
            btnPlayCard.animate().translationZ(1f).setDuration(200);

            //btnDropCards.setVisibility(View.INVISIBLE);
            //btnPlayCard.setVisibility(View.INVISIBLE);
        }
    }


    public void drawCurrentScore() {
        txtCurrentScore.setText("Current score :" + cardManager.getCurrentScore());
    }


    /**
     * reset all card positions back to unselected state
     */
    public void resetCardPosition () {

        LinearLayout.LayoutParams tempParams;
        for (int i = 0; i < handCount; i++) {
            tempParams = (LinearLayout.LayoutParams) cardNumber[i].getLayoutParams();
            if (tempParams.topMargin != MASTER_TOP_PADDING) {
                DrawingAndAnimating.animateCardMargin(false, cardNumber[i], getResources());
                //cardNumber[i].setElevation(20);
                cardNumber[i].animate().translationZ(0f).setDuration(200);

                cardNumber[i].setLayoutParams(tempParams);
            }
        }
    }

    public void resetFullLayout() {
        for (int i = 0; i < 5; i++) {
            cardNumber[i].setSelected(false);
            handRankLabel[i].setTextColor(Color.RED);
            handRankLabelUpsideDown[i].setTextColor(Color.RED);
            cardNumber[i].setClickable(true);
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


    /**
     * Initiates new game based on hands received
     * @param playerHandsTemp
     */
    public void receiveHands(String[][] playerHandsTemp) {
        pd.dismiss();
        cardManager = new DealerCardManagement(playerHandsTemp, "5 of Clubs");
        myPosition = 0;
        drawHands();
    }

    public void receiveCardPlayed(int playerPosition, int cardPosition) {
        int j = cardManager.doAddToActiveCards(playerPosition, cardPosition);
        myTurn();
    }

    public void receiveCribCard(int playerPosition, int cardPlayed) {
        cardManager.addToCrib(cardManager.getPlayersCardToString(playerPosition, cardPlayed));
    }

    public void receievePlayerPosition (int playerPosition) {
        myPosition = playerPosition;
    }

    public void receiveCutCard (String cutCard) { cardManager.setCutCard(cutCard); }

    public void receieveNextTurn() {
        myTurn();
    }

    public void receiveScoreDuringPlay(int scoreDuringPlay) {
        cardManager.setCurrentScore(scoreDuringPlay);
        drawCurrentScore();
    }

    public void myDeal() {
        btnDeal.setText("Deal");
        btnDeal.setVisibility(View.VISIBLE);
        btnDeal.setOnClickListener(new  Button.OnClickListener() {
            public void onClick (View v) {
                newDeal(v);
            }
        });
    }

    public void prepForDeal() {
        pd = ProgressDialog.show(PrimaryActivity.this, "", "Other player is dealing. . . ");
    }

    public void receieveCouldNotPlay(int couldNotPlayCount) {
        this.couldNotPlayCount = couldNotPlayCount;

        myTurn();
    }

    public void prepNewPlay() {
        cardManager.resetActiveCards();
        cardManager.setCurrentScore(0);
        drawCurrentScore();
    }

    public void initGame() {
        btnDeal.setVisibility(View.VISIBLE);
    }
}
