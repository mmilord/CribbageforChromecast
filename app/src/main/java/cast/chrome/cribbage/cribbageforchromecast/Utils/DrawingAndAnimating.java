package cast.chrome.cribbage.cribbageforchromecast.Utils;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ResourceBundle;

import cast.chrome.cribbage.cribbageforchromecast.PrimaryActivity;
import cast.chrome.cribbage.cribbageforchromecast.R;

/**
 * Created by milord on 15-Dec-14.
 */
public class DrawingAndAnimating {



    /**
     * method to animate margins up or down for the cards;
     * @param UpOrDown: true to animate up, false animate down
     */
    public static void animateCardMargin(Boolean UpOrDown, final View view, Resources res) {

        final LinearLayout.LayoutParams tempParams = (LinearLayout.LayoutParams) view.getLayoutParams();

        ValueAnimator topMarginAnimation, bottomMarginAnimation;

        if (UpOrDown) {
            topMarginAnimation = ValueAnimator.ofInt((int)res.getDimension(R.dimen.card_margin_top), (int)res.getDimension(R.dimen.card_margin_top_shifted));
            bottomMarginAnimation = ValueAnimator.ofInt((int)res.getDimension(R.dimen.card_margin_bottom), (int)res.getDimension(R.dimen.card_margin_bottom_shifted));
        } else {
            topMarginAnimation = ValueAnimator.ofInt((int)res.getDimension(R.dimen.card_margin_top_shifted), (int)res.getDimension(R.dimen.card_margin_top));
            bottomMarginAnimation = ValueAnimator.ofInt((int)res.getDimension(R.dimen.card_margin_bottom_shifted), (int)res.getDimension(R.dimen.card_margin_bottom));
        }

        topMarginAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                tempParams.topMargin = (int) (valueAnimator.getAnimatedValue());
                view.requestLayout();
            }
        });
        topMarginAnimation.setDuration(5);


        bottomMarginAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                tempParams.bottomMargin = (int) valueAnimator.getAnimatedValue();
                view.requestLayout();
            }
        });
        bottomMarginAnimation.setDuration(5);

        topMarginAnimation.start();
        bottomMarginAnimation.start();
    }


    public static class cardFlipListener implements Animation.AnimationListener {
        RelativeLayout card;
        TextView topLeftLabel;
        TextView topRightLabel;
        TextView centerLabel;
        private Animation anim1;
        private Animation anim2;
        private boolean isBackOfCardShowing;
        Resources resources;

        public void attachViews(RelativeLayout card, TextView topLeftLabel, TextView topRightLabel, TextView centerLabel, Animation anim1, Animation anim2, Resources resources, boolean isBackOfCardShowing) {
            this.card = card;
            this.topLeftLabel = topLeftLabel;
            this.topRightLabel = topRightLabel;
            this.centerLabel = centerLabel;
            this.anim1 = anim1;
            this.anim2 = anim2;
            this.resources = resources;
            this.isBackOfCardShowing = isBackOfCardShowing;
        }


        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animation == anim1) {

                if (isBackOfCardShowing) {

                    card.setBackground(resources.getDrawable(R.drawable.card_back));
                    centerLabel.setVisibility(View.VISIBLE);
                    topLeftLabel.setVisibility(View.VISIBLE);
                    topRightLabel.setVisibility(View.VISIBLE);

                    LayoutTransition lt = new LayoutTransition();
                    lt.enableTransitionType(LayoutTransition.CHANGING);
                    card.setLayoutTransition(lt);

                } else {

                    card.setBackground(resources.getDrawable(R.drawable.card_front_design));
                    centerLabel.setVisibility(View.INVISIBLE);
                    topLeftLabel.setVisibility(View.INVISIBLE);
                    topRightLabel.setVisibility(View.INVISIBLE);

                    LayoutTransition lt = new LayoutTransition();
                    lt.enableTransitionType(LayoutTransition.CHANGING);
                    card.setLayoutTransition(lt);
                }

                card.clearAnimation();

                card.setAnimation(anim2);

                card.startAnimation(anim2);

            } else {

                isBackOfCardShowing=!isBackOfCardShowing;


            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    /*public static class slideListener implements Animation.AnimationListener {


        @Override
        public void onAnimationStart(Animation animation) {  }

        @Override
        public void onAnimationEnd(Animation animation) {

            .setVisibility(View.GONE);
            for (RelativeLayout card : cardNumber) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();

                params.leftMargin = MASTER_SIDE_PADDING * 2;
                params.rightMargin = MASTER_SIDE_PADDING * 2;

                card.setLayoutParams(params);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) { }
        }
    }*/
}
