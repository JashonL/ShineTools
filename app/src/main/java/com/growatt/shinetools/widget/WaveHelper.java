package com.growatt.shinetools.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

public class WaveHelper {
    private WaveView mWaveView;

    private AnimatorSet mAnimatorSet;

    public WaveHelper(WaveView waveView,float f,int time) {
        mWaveView = waveView;
        initAnimation(f,time);
    }

    public WaveHelper(WaveView waveView,float f) {
        mWaveView = waveView;
        initAnimation(f);
    }

    public void start() {
        mWaveView.setShowWave(true);
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    private void initAnimation(float f,int time) {
        List<Animator> animators = new ArrayList<>();

        // horizontal animation.
        // wave waves infinitely.
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveView, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(2000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animators.add(waveShiftAnim);

        // vertical animation.
        // water level increases from 0 to center of WaveView
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(
                mWaveView, "waterLevelRatio", 0f, f);
        waterLevelAnim.setDuration(time);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        animators.add(waterLevelAnim);

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(
                mWaveView, "amplitudeRatio", 0.0001f, 0.05f);
        amplitudeAnim.setRepeatCount(ValueAnimator.INFINITE);
        amplitudeAnim.setRepeatMode(ValueAnimator.REVERSE);
        amplitudeAnim.setDuration(5000);
        amplitudeAnim.setInterpolator(new LinearInterpolator());
        animators.add(amplitudeAnim);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);
//        List<Animator> animators = new ArrayList<Animator>();
//
//        // horizontal animation.
//        // wave waves infinitely.
//        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
//                mWaveView, "waveShiftRatio", 0f, 1f);
//        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
//        waveShiftAnim.setDuration(500);
//        waveShiftAnim.setInterpolator(new LinearInterpolator());
//        animators.add(waveShiftAnim);
//
//        // vertical animation.
//        // water level increases from 0 to center of WaveView
//        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(
//                mWaveView, "waterLevelRatio", 0f,f);
//        waterLevelAnim.setDuration(time);
//        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
//        animators.add(waterLevelAnim);
//
//        // amplitude animation.
//        // wave grows big then grows small, repeatedly
//        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(
//                mWaveView, "amplitudeRatio", 0.0001f, 0.0003f);
//        amplitudeAnim.setRepeatCount(ValueAnimator.INFINITE);
//        amplitudeAnim.setRepeatMode(ValueAnimator.REVERSE);
//        amplitudeAnim.setDuration(3000);
//        amplitudeAnim.setInterpolator(new LinearInterpolator());
//        animators.add(amplitudeAnim);
//        mAnimatorSet = new AnimatorSet();
//        mAnimatorSet.playTogether(animators);
    }
    private void initAnimation(float f) {
    }
    public void cancel() {
        if (mAnimatorSet != null) {
//            mAnimatorSet.cancel();
            mAnimatorSet.end();
        }
    }
}
