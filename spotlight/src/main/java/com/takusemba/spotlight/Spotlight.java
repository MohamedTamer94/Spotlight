package com.takusemba.spotlight;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.graphics.Color;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Spotlight
 *
 * @author takusemba
 * @since 26/06/2017
 **/
public class Spotlight {

    /**
     * Duration of Spotlight emerging
     */
    private static final long START_SPOTLIGHT_DURATION = 500L;
    /**
     * Duration of Spotlight disappearing
     */
    private static final long FINISH_SPOTLIGHT_DURATION = 500L;

    private static final long DEFAULT_DURATION = 1000L;
    private static final TimeInterpolator DEFAULT_ANIMATION = new DecelerateInterpolator(2f);

    private static WeakReference<SpotlightView> spotlightViewWeakReference;
    private static WeakReference<Activity> contextWeakReference;
    private static ArrayList<Target> targets;
    private static long duration = DEFAULT_DURATION;
    private static TimeInterpolator animation = DEFAULT_ANIMATION;
    private static OnSpotlightStartedListener startedListener;
    private static OnSpotlightEndedListener endedListener;
    private static OnTargetClosedListener targetClosedListener;
    private static Target lastTarget;
    private static int maskColor = Color.parseColor("#E6000000");

    /**
     * Constructor
     *
     * @param activity Activity to create Spotlight
     */
    private Spotlight(Activity activity) {
        contextWeakReference = new WeakReference<>(activity);
    }

    /**
     * Create Spotlight with activity reference
     *
     * @param activity Activity to create Spotlight
     * @return This Spotlight
     */
    public static Spotlight with(Activity activity) {
        return new Spotlight(activity);
    }

    /**
     * Return context weak reference
     *
     * @return the activity
     */
    private static Context getContext() {
        return contextWeakReference.get();
    }

    /**
     * Returns {@link SpotlightView} weak reference
     *
     * @return the SpotlightView
     */
    public static SpotlightView getSpotlightView() {
        return spotlightViewWeakReference.get();
    }

    /**
     * sets {@link Target}s to Spotlight
     *
     * @param targets targets to show
     * @return the SpotlightView
     */
    public <T extends Target> Spotlight setTargets(T... targets) {
        this.targets = new ArrayList<Target>(Arrays.asList(targets));
        return this;
    }

    /**
     * sets duration to {@link Target} Animation
     *
     * @param duration duration of Target Animation
     * @return the SpotlightView
     */
    public Spotlight setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    /**
     * sets duration to {@link Target} Animation
     *
     * @param animation type of Target Animation
     * @return the SpotlightView
     */
    public Spotlight setAnimation(TimeInterpolator animation) {
        this.animation = animation;
        return this;
    }

    /**
     * Sets Spotlight start Listener to Spotlight
     *
     * @param listener OnSpotlightStartedListener of Spotlight
     * @return This Spotlight
     */
    public Spotlight setOnSpotlightStartedListener(
            final OnSpotlightStartedListener listener) {
        startedListener = listener;
        return this;
    }

    /**
     * Sets Spotlight end Listener to Spotlight
     *
     * @param listener OnSpotlightEndedListener of Spotlight
     * @return This Spotlight
     */
    public Spotlight setOnSpotlightEndedListener(final OnSpotlightEndedListener listener) {
        endedListener = listener;
        return this;
    }

    public Spotlight setOnTargetClosedListener(final OnTargetClosedListener listener) {
        targetClosedListener = listener;
        return this;
    }

    public Spotlight setMaskColor(int argb) {
        maskColor = argb;
        return this;
    }

    /**
     * Shows {@link SpotlightView}
     */
    public static void start() {
        spotlightView();
    }

    public static Target getCurrentTarget() {
        if (targets != null && targets.size() > 0) {
          return targets.get(0);
        } else {
            return null;
        }
    }

    /**
     * Creates the spotlight view and starts
     */
    private static void spotlightView() {
        if (getContext() == null) {
            throw new RuntimeException("context is null");
        }
        final View decorView = ((Activity) getContext()).getWindow().getDecorView();
        SpotlightView spotlightView = new SpotlightView(getContext());
        spotlightView.setMaskColor(maskColor);
        spotlightViewWeakReference = new WeakReference<>(spotlightView);
        spotlightView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup) decorView).addView(spotlightView);
        spotlightView.setOnSpotlightStateChangedListener(new SpotlightView.OnSpotlightStateChangedListener() {
            @Override
            public void onTargetClosed() {
                if (lastTarget != null && targetClosedListener != null) {
                    targetClosedListener.onTargetClosed(lastTarget);
                }
                if (targets != null && targets.size() > 0) {
                    startTarget();
                } else {
                    finishSpotlight();
                }
            }

            @Override
            public void onTargetClicked() {
                finishTarget();
            }
        });
        startSpotlight();
    }

    /**
     * show Target
     */
    private static void startTarget() {
        if (targets != null && targets.size() > 0) {
            Target target = targets.get(0);
            getSpotlightView().removeAllViews();
            if (target.getView().getParent() != null) {
                ((ViewGroup) target.getView().getParent()).removeView(target.getView());
            }
            getSpotlightView().addView(target.getView());
            getSpotlightView().turnUp(target.getPoint().x, target.getPoint().y, target.getRadius(),
                    duration, animation);
        }
    }

    /**
     * show Spotlight
     */
    private static void startSpotlight() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(getSpotlightView(), "alpha", 0f, 1f);
        objectAnimator.setDuration(START_SPOTLIGHT_DURATION);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (startedListener != null) startedListener.onStarted();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startTarget();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    /**
     * hide Target
     */
    public static void finishTarget() {
        if (targets != null && targets.size() > 0) {
            lastTarget = targets.get(0);
            Target target = targets.remove(0);
            getSpotlightView().turnDown(target.getRadius(), duration, animation);
        }
    }

    /**
     * hide Spotlight
     */
    public static void finishSpotlight() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(getSpotlightView(), "alpha", 1f, 0f);
        objectAnimator.setDuration(FINISH_SPOTLIGHT_DURATION);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                final View decorView = ((Activity) getContext()).getWindow().getDecorView();
                ((ViewGroup) decorView).removeView(getSpotlightView());
                if (startedListener != null) endedListener.onEnded();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }
}
