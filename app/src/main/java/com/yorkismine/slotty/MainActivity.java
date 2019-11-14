package com.yorkismine.slotty;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yorkismine.slotty.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.yorkismine.slotty.utils.Constants.TIMEOUT;

public class MainActivity extends AppCompatActivity {
    private int myCoins = 1000;
    private long bet = 5;
    private long jackpot = 10000;
    private List<SpinnerView> spinnerViews;

    private Button mSpinButton;
    private Dialog winDialog;
    private SpinnerView mSpinner1;
    private SpinnerView mSpinner2;
    private SpinnerView mSpinner3;
    private TextView mBetTextView;
    private Button mPlusButton;
    private TextView mMyCoinsTextView;
    private Button mMinusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mSpinner1 = findViewById(R.id.spinner1);
        mSpinner2 = findViewById(R.id.spinner2);
        mSpinner3 = findViewById(R.id.spinner3);
        mSpinButton = findViewById(R.id.spinButton);
        mBetTextView = findViewById(R.id.betTextView);
        mPlusButton = findViewById(R.id.plusButton);
        mMyCoinsTextView = findViewById(R.id.myCoinsTextView);
        mMinusButton = findViewById(R.id.minusButton);

        spinnerViews = Arrays.asList(mSpinner1, mSpinner2, mSpinner3);
        winDialog = new Dialog(this);


        startPosition();
    }

    public void minus(View view){
        if (bet == 100){
            bet = 10;
            mBetTextView.setText(bet + "");
        }else{
            bet = 5;
            mBetTextView.setText(bet + "");
        }
    }

    public void plus(View view){
        if (bet == 5){
            bet = 10;
            mBetTextView.setText(bet + "");
        }else{
            bet = 100;
            mBetTextView.setText(bet + "");
        }
    }

    public void spin(View view) {
        mSpinButton.setClickable(false);
        mPlusButton.setClickable(false);
        mMinusButton.setClickable(false);
        int maxScroll = 0;

        for (SpinnerView spinnerView : spinnerViews) {
            spinnerView.setSequence(getRandomSequence());
            maxScroll = Math.max(maxScroll, spinnerView.getMaxScrollPosition());
        }

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofInt(mSpinner1, "scrollPosition", 0, maxScroll),
                ObjectAnimator.ofInt(mSpinner2, "scrollPosition", 0, maxScroll),
                ObjectAnimator.ofInt(mSpinner3, "scrollPosition", 0, maxScroll)
        );
        set.setDuration(3000);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                long coefficient = bet / 5;
                int selectedImage1 = mSpinner1.getResultedImageIndex();
                int selectedImage2 = mSpinner2.getResultedImageIndex();
                int selectedImage3 = mSpinner3.getResultedImageIndex();

                myCoins -= bet;

                if (isNoCombinations(selectedImage1, selectedImage2, selectedImage3)) {
                    jackpot += bet;
                }

                if (isTwoHorseshoes(selectedImage1, selectedImage2, selectedImage3, 6)) {
                    myCoins += 50 * coefficient;
                    jackpot -= bet;
                    showWinDialog(50 * coefficient);
                } else if (isOneHorseshoe(selectedImage1, selectedImage2, selectedImage3, 6)) {
                    myCoins += 25 * coefficient;
                    jackpot -= bet;
                    showWinDialog(25 * coefficient);
                }

                if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 6)) {
                    myCoins += jackpot;
                    jackpot = 0;
                    showWinJackpot(jackpot);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 5)) {
                    myCoins += 75 * coefficient;
                    showWinDialog(75 *coefficient);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 4)) {
                    myCoins += 50 * coefficient;
                    showWinDialog(50 * coefficient);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 3)) {
                    myCoins += 35 * coefficient;
                    showWinDialog(35 * coefficient);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 2)) {
                    myCoins += 25 * coefficient;
                    showWinDialog(25 * coefficient);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 1)) {
                    myCoins += 15 * coefficient;
                    showWinDialog(15 * coefficient);
                } else if (isWinCombinations(selectedImage1, selectedImage2, selectedImage3, 0)) {
                    myCoins += 10 * coefficient;
                    showWinDialog(10 * coefficient);
                }

                bet = 5;
                mBetTextView.setText(String.valueOf(bet));
                mMyCoinsTextView.setText(String.valueOf(myCoins));
                mPlusButton.setClickable(true);
                mSpinButton.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private ArrayList<Integer> getRandomSequence() {
        int minLen = 30;
        int maxLen = 50;

        int len = 30 + (int) Math.round(Math.random() * (maxLen - minLen));

        ArrayList<Integer> sequence = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            sequence.add((int) Math.round(Math.random() * 6));
        }
        return sequence;
    }

    private void startPosition() {
        ArrayList<Integer> start = new ArrayList<>();
        start.add(6);
        start.add(5);
        start.add(4);
        for (SpinnerView spinnerView : spinnerViews) {
            spinnerView.setSequence(start);
        }
    }

    private boolean isWinCombinations(int selectedImage1, int selectedImage2, int selectedImage3, int positionOfImage) {
        if (selectedImage1 == positionOfImage && selectedImage2 == positionOfImage && selectedImage3 == positionOfImage) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isTwoHorseshoes(int selectedImage1, int selectedImage2, int selectedImage3, int positionOfImage) {
        if ((selectedImage1 == positionOfImage && selectedImage2 == positionOfImage) ||
                (selectedImage1 == positionOfImage && selectedImage3 == positionOfImage) ||
                (selectedImage3 == positionOfImage && selectedImage2 == positionOfImage)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isOneHorseshoe(int selectedImage1, int selectedImage2, int selectedImage3, int positionOfImage) {
        if (selectedImage1 == positionOfImage ||
                selectedImage2 == positionOfImage ||
                selectedImage3 == positionOfImage) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNoCombinations(int selectedImage1, int selectedImage2, int selectedImage3) {
        if (selectedImage1 != selectedImage2 || selectedImage2 != selectedImage3) {
            return true;
        } else {
            return false;
        }
    }

    private void showWinDialog(long coins) {
        winDialog.setContentView(R.layout.popup);
        TextView mPopupTextView = (TextView) winDialog.findViewById(R.id.popupTextView);
        winDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupTextView.setText("You win:\n" + coins);
        winDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                winDialog.dismiss();
            }
        }, TIMEOUT);
    }

    private void showWinJackpot(long coins) {
        winDialog.setContentView(R.layout.popup);
        TextView mPopupTextView = (TextView) winDialog.findViewById(R.id.popupTextView);
        winDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupTextView.setText("JACKPOT\n" + coins);
        winDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                winDialog.dismiss();
            }
        }, TIMEOUT);
    }

}
