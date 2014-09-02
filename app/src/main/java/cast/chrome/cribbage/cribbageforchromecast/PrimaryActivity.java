package cast.chrome.cribbage.cribbageforchromecast;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class PrimaryActivity extends Activity {


    Deck d;

    String[][] players;

    //hard for now
    int myPosition = 1;
    int handCount = 5;

    int selectedCard;
    boolean playActive = false;

    TextView[] hand = new TextView[handCount];

    Button btnDropCards, btnDisplayCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);

        btnDropCards = (Button) findViewById(R.id.btnDropCards);
        btnDisplayCards = (Button) findViewById(R.id.btnDisplayCards);

        hand[0] = (TextView) findViewById(R.id.card1);
        hand[1] = (TextView) findViewById(R.id.card2);
        hand[2] = (TextView) findViewById(R.id.card3);
        hand[3] = (TextView) findViewById(R.id.card4);
        hand[4] = (TextView) findViewById(R.id.card5);
    }

    public void newDeal (View view) {
        //set deck obj
        d = new Deck();

        int playerCount = 3;   //hard for now



        if (playerCount == 2)
            handCount = 6;
        else
            handCount = 5;

        players = new String[playerCount][handCount];

        //pass player count var from game init
        dealer(playerCount);



        btnDisplayCards.setVisibility(View.VISIBLE);

        for (int i = 0; i < handCount; i++) {
            hand[i].setText(players[myPosition][i].toString());
            hand[i].setVisibility(View.INVISIBLE);
        }
    }

    public void dropCard (View view) {

        replaceCard(selectedCard, myPosition);

        hand[selectedCard].setVisibility(View.INVISIBLE);
        btnDropCards.setVisibility(View.INVISIBLE);

        playActive = true;
    }

    public void replaceCard (int replacedCard, int playerNumber) {
        /*String[] temp = new String[handCount];
        for (int i = 0; i < handCount; i++) {
            temp[i] = players[playerNumber][i].toString();
        }*/

        if (selectedCard != handCount - 1)
            for (int i = 0; i < handCount; i++)
                if (i == replacedCard && i + 1 < handCount)
                    players[playerNumber][i] = players[playerNumber][i + 1];
        players[playerNumber][handCount - 1] = null;

        System.out.println("");
        for (int i = 0; i < 4; i++)
            System.out.print(players[playerNumber][i].toString() + ", ");
    }

    public void displayHand (View view) {

        resetCardPosition();

        for (int i = 0; i < handCount; i++) {
            hand[i].setText(players[myPosition][i].toString());
            hand[i].setVisibility(View.VISIBLE);
        }

        btnDisplayCards.setVisibility(View.INVISIBLE);
    }

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

    public void selectCard (View view) {

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();

        if (params.topMargin != 5) {

            //reset card position to lower any other cards, tag current as selected, then shift up
            resetCardPosition();
            tagSelectedCard(view);
            params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            params.topMargin = 5;
            view.setLayoutParams(params);

            //drop button only needed prior to play
            if (!playActive)
                btnDropCards.setVisibility(View.VISIBLE);
        }
        else {
            //shift card back to standard position and hide drop button
            params.topMargin = 75;
            view.setLayoutParams(params);

            btnDropCards.setVisibility(View.INVISIBLE);
        }

    }

    public void resetCardPosition () {

        RelativeLayout.LayoutParams tempParams;
        for (int i = 0; i < handCount; i++) {
            tempParams = (RelativeLayout.LayoutParams) hand[i].getLayoutParams();
            tempParams.topMargin = 75;
            hand[i].setLayoutParams(tempParams);
        }

    }

    public void dealer(int playerCount) {
        System.out.println("Total: " + d.getTotalCards());

        int numToBeDealt;

        if (playerCount > 2)
            numToBeDealt = 5;
        else
            numToBeDealt = 6;

        for (int j = playerCount - 1; j > -1; j-- ) {
            for (int i = 0; i < numToBeDealt; i++) {
                players[j][i] = d.drawFromDeck() + "";
            }
        }

        /*
        for (int i = 0; i < 5; i++) {
            playerOne[i] = d.drawFromDeck() + "";
            playerTwo[i] = d.drawFromDeck() + "";
            playerThree[i] = d.drawFromDeck() + "";
        }
        */

        System.out.println("Total: " + d.getTotalCards());

        for (int j = playerCount - 1; j > -1; j--) {
            System.out.print("Player " + j + "'s hand: ");
            for (int i = 0; i < numToBeDealt; i++)
                System.out.print(players[j][i].toString() + ", ");
            System.out.println();
        }

        playActive = false;
    }

    public String[][] getHands () {
        return players;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.primary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
