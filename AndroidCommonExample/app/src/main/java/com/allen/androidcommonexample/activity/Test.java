package com.allen.androidcommonexample.activity;

import android.animation.Animator;
import android.view.View;

/**
 * Created by Admin on 2017/9/14.
 */

public class Test {
    View view;

    public void test() {
        view.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
