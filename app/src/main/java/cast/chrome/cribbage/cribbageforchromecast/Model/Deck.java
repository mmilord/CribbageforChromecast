package cast.chrome.cribbage.cribbageforchromecast.Model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import cast.chrome.cribbage.cribbageforchromecast.Model.Card;

/**
 * Created by milord on 26-Aug-14.
 */
public class Deck {
    private ArrayList<Card> cards;

    Deck() {
        cards = new ArrayList<Card>();
        int i1, i2;
        Random generator = new Random();
        Card temp;

        for (int a = 0; a <= 3; a++)
        {
            for (int b = 0; b <= 12; b++)
            {
                cards.add(new Card(a, b));
            }
        }


        for (int i=0; i<100; i++)
        {
            i1 = generator.nextInt(cards.size() - 1);
            i2 = generator.nextInt(cards.size() - 1);

            temp = (Card) cards.get(i2);
            cards.set(i2, cards.get(i1));
            cards.set(i1, temp);
        }
    }

    public Card drawFromDeck()
    {
        return cards.remove( 0 );
    }

    public int getTotalCards()
    {
        return cards.size();
    }

}
