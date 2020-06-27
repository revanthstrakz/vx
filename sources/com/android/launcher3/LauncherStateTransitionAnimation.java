package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import com.android.launcher3.Workspace.State;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.anim.AnimationLayerSet;
import com.android.launcher3.anim.CircleRevealOutlineProvider;
import com.android.launcher3.widget.WidgetsContainerView;

public class LauncherStateTransitionAnimation {
    public static final int CIRCULAR_REVEAL = 0;
    private static final float FINAL_REVEAL_ALPHA_FOR_WIDGETS = 0.3f;
    public static final int PULLUP = 1;
    public static final int SINGLE_FRAME_DELAY = 16;
    public static final String TAG = "LSTAnimation";
    AllAppsTransitionController mAllAppsController;
    AnimatorSet mCurrentAnimation;
    Launcher mLauncher;

    private static class PrivateTransitionCallbacks {
        /* access modifiers changed from: private */
        public final float materialRevealViewFinalAlpha;

        /* access modifiers changed from: 0000 */
        public AnimatorListenerAdapter getMaterialRevealViewAnimatorListener(View view, View view2) {
            return null;
        }

        /* access modifiers changed from: 0000 */
        public float getMaterialRevealViewStartFinalRadius() {
            return 0.0f;
        }

        /* access modifiers changed from: 0000 */
        public void onTransitionComplete() {
        }

        PrivateTransitionCallbacks(float f) {
            this.materialRevealViewFinalAlpha = f;
        }
    }

    private class StartAnimRunnable implements Runnable {
        private final AnimatorSet mAnim;
        private final View mViewToFocus;

        public StartAnimRunnable(AnimatorSet animatorSet, View view) {
            this.mAnim = animatorSet;
            this.mViewToFocus = view;
        }

        public void run() {
            if (LauncherStateTransitionAnimation.this.mCurrentAnimation == this.mAnim) {
                if (this.mViewToFocus != null) {
                    this.mViewToFocus.requestFocus();
                }
                this.mAnim.start();
            }
        }
    }

    public LauncherStateTransitionAnimation(Launcher launcher, AllAppsTransitionController allAppsTransitionController) {
        this.mLauncher = launcher;
        this.mAllAppsController = allAppsTransitionController;
    }

    public void startAnimationToAllApps(boolean z, final boolean z2) {
        final AllAppsContainerView appsView = this.mLauncher.getAppsView();
        startAnimationToOverlay(State.NORMAL_HIDDEN, this.mLauncher.getStartViewForAllAppsRevealAnimation(), appsView, z, 1, new PrivateTransitionCallbacks(1.0f) {
            public float getMaterialRevealViewStartFinalRadius() {
                return (float) (LauncherStateTransitionAnimation.this.mLauncher.getDeviceProfile().allAppsButtonVisualSize / 2);
            }

            public AnimatorListenerAdapter getMaterialRevealViewAnimatorListener(View view, final View view2) {
                return new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        view2.setVisibility(4);
                    }

                    public void onAnimationEnd(Animator animator) {
                        view2.setVisibility(0);
                    }
                };
            }

            /* access modifiers changed from: 0000 */
            public void onTransitionComplete() {
                LauncherStateTransitionAnimation.this.mLauncher.getUserEventDispatcher().resetElapsedContainerMillis();
                if (z2) {
                    appsView.startAppsSearch();
                }
            }
        });
    }

    public void startAnimationToWidgets(boolean z) {
        WidgetsContainerView widgetsView = this.mLauncher.getWidgetsView();
        startAnimationToOverlay(State.OVERVIEW_HIDDEN, this.mLauncher.getWidgetsButton(), widgetsView, z, 0, new PrivateTransitionCallbacks(FINAL_REVEAL_ALPHA_FOR_WIDGETS) {
            /* access modifiers changed from: 0000 */
            public void onTransitionComplete() {
                LauncherStateTransitionAnimation.this.mLauncher.getUserEventDispatcher().resetElapsedContainerMillis();
            }
        });
    }

    public void startAnimationToWorkspace(State state, State state2, State state3, boolean z, Runnable runnable) {
        if (!(state3 == State.NORMAL || state3 == State.SPRING_LOADED || state3 == State.OVERVIEW)) {
            Log.e(TAG, "Unexpected call to startAnimationToWorkspace");
        }
        if (state == State.APPS || state == State.APPS_SPRING_LOADED || this.mAllAppsController.isTransitioning()) {
            startAnimationToWorkspaceFromAllApps(state2, state3, z, 1, runnable);
        } else if (state == State.WIDGETS || state == State.WIDGETS_SPRING_LOADED) {
            startAnimationToWorkspaceFromWidgets(state2, state3, z, runnable);
        } else {
            startAnimationToNewWorkspaceState(state2, state3, z, runnable);
        }
    }

    private void startAnimationToOverlay(State state, View view, BaseContainerView baseContainerView, boolean z, int i, PrivateTransitionCallbacks privateTransitionCallbacks) {
        View view2 = view;
        BaseContainerView baseContainerView2 = baseContainerView;
        int i2 = i;
        final PrivateTransitionCallbacks privateTransitionCallbacks2 = privateTransitionCallbacks;
        AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        Resources resources = this.mLauncher.getResources();
        int integer = resources.getInteger(C0622R.integer.config_overlayRevealTime);
        int integer2 = resources.getInteger(C0622R.integer.config_overlaySlideRevealTime);
        int integer3 = resources.getInteger(C0622R.integer.config_overlayItemsAlphaStagger);
        AnimationLayerSet animationLayerSet = new AnimationLayerSet();
        boolean z2 = view2 != null;
        cancelAnimation();
        int i3 = integer2;
        View contentView = baseContainerView.getContentView();
        playCommonTransitionAnimations(state, z, z2, createAnimatorSet, animationLayerSet);
        if (!z || !z2) {
            BaseContainerView baseContainerView3 = baseContainerView;
            if (state == State.NORMAL_HIDDEN) {
                this.mAllAppsController.finishPullUp();
            }
            baseContainerView3.setTranslationX(0.0f);
            baseContainerView3.setTranslationY(0.0f);
            baseContainerView3.setScaleX(1.0f);
            baseContainerView3.setScaleY(1.0f);
            baseContainerView3.setAlpha(1.0f);
            baseContainerView3.setVisibility(0);
            contentView.setVisibility(0);
            privateTransitionCallbacks.onTransitionComplete();
            return;
        }
        if (i2 == 0) {
            final View revealView = baseContainerView.getRevealView();
            int measuredWidth = revealView.getMeasuredWidth() / 2;
            int measuredHeight = revealView.getMeasuredHeight() / 2;
            float hypot = (float) Math.hypot((double) measuredWidth, (double) measuredHeight);
            revealView.setVisibility(0);
            revealView.setAlpha(0.0f);
            revealView.setTranslationY(0.0f);
            revealView.setTranslationX(0.0f);
            int[] centerDeltaInScreenSpace = Utilities.getCenterDeltaInScreenSpace(revealView, view2);
            float f = (float) centerDeltaInScreenSpace[0];
            float f2 = (float) centerDeltaInScreenSpace[1];
            float f3 = hypot;
            int i4 = measuredWidth;
            int i5 = measuredHeight;
            ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(revealView, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(View.ALPHA, new float[]{privateTransitionCallbacks.materialRevealViewFinalAlpha, 1.0f}), PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[]{f2, 0.0f}), PropertyValuesHolder.ofFloat(View.TRANSLATION_X, new float[]{f, 0.0f})});
            long j = (long) integer;
            ofPropertyValuesHolder.setDuration(j);
            ofPropertyValuesHolder.setInterpolator(new LogDecelerateInterpolator(100, 0));
            animationLayerSet.addView(revealView);
            createAnimatorSet.play(ofPropertyValuesHolder);
            contentView.setVisibility(0);
            contentView.setAlpha(0.0f);
            contentView.setTranslationY(f2);
            animationLayerSet.addView(contentView);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(contentView, "translationY", new float[]{f2, 0.0f});
            ofFloat.setDuration(j);
            ofFloat.setInterpolator(new LogDecelerateInterpolator(100, 0));
            long j2 = (long) integer3;
            ofFloat.setStartDelay(j2);
            createAnimatorSet.play(ofFloat);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(contentView, "alpha", new float[]{0.0f, 1.0f});
            ofFloat2.setDuration(j);
            ofFloat2.setInterpolator(new AccelerateInterpolator(1.5f));
            ofFloat2.setStartDelay(j2);
            createAnimatorSet.play(ofFloat2);
            float materialRevealViewStartFinalRadius = privateTransitionCallbacks.getMaterialRevealViewStartFinalRadius();
            AnimatorListenerAdapter materialRevealViewAnimatorListener = privateTransitionCallbacks2.getMaterialRevealViewAnimatorListener(revealView, view);
            ValueAnimator createRevealAnimator = new CircleRevealOutlineProvider(i4, i5, materialRevealViewStartFinalRadius, f3).createRevealAnimator(revealView);
            createRevealAnimator.setDuration(j);
            createRevealAnimator.setInterpolator(new LogDecelerateInterpolator(100, 0));
            if (materialRevealViewAnimatorListener != null) {
                createRevealAnimator.addListener(materialRevealViewAnimatorListener);
            }
            createAnimatorSet.play(createRevealAnimator);
            createAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    revealView.setVisibility(4);
                    LauncherStateTransitionAnimation.this.cleanupAnimation();
                    privateTransitionCallbacks2.onTransitionComplete();
                }
            });
            baseContainerView.bringToFront();
            BaseContainerView baseContainerView4 = baseContainerView;
            baseContainerView4.setVisibility(0);
            createAnimatorSet.addListener(animationLayerSet);
            baseContainerView4.post(new StartAnimRunnable(createAnimatorSet, baseContainerView4));
            this.mCurrentAnimation = createAnimatorSet;
        } else {
            BaseContainerView baseContainerView5 = baseContainerView;
            if (i2 == 1) {
                createAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        LauncherStateTransitionAnimation.this.cleanupAnimation();
                        privateTransitionCallbacks2.onTransitionComplete();
                    }
                });
                boolean animateToAllApps = this.mAllAppsController.animateToAllApps(createAnimatorSet, (long) i3);
                StartAnimRunnable startAnimRunnable = new StartAnimRunnable(createAnimatorSet, baseContainerView5);
                this.mCurrentAnimation = createAnimatorSet;
                this.mCurrentAnimation.addListener(animationLayerSet);
                if (animateToAllApps) {
                    baseContainerView5.post(startAnimRunnable);
                } else {
                    startAnimRunnable.run();
                }
            }
        }
    }

    private void playCommonTransitionAnimations(State state, boolean z, boolean z2, AnimatorSet animatorSet, AnimationLayerSet animationLayerSet) {
        Animator startWorkspaceStateChangeAnimation = this.mLauncher.startWorkspaceStateChangeAnimation(state, z, animationLayerSet);
        if (z && z2 && startWorkspaceStateChangeAnimation != null) {
            animatorSet.play(startWorkspaceStateChangeAnimation);
        }
    }

    private void startAnimationToWorkspaceFromAllApps(State state, State state2, boolean z, int i, Runnable runnable) {
        C06075 r8 = new PrivateTransitionCallbacks(1.0f) {
            /* access modifiers changed from: 0000 */
            public float getMaterialRevealViewStartFinalRadius() {
                return (float) (LauncherStateTransitionAnimation.this.mLauncher.getDeviceProfile().allAppsButtonVisualSize / 2);
            }

            public AnimatorListenerAdapter getMaterialRevealViewAnimatorListener(final View view, final View view2) {
                return new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        view2.setVisibility(0);
                        view2.setAlpha(0.0f);
                    }

                    public void onAnimationEnd(Animator animator) {
                        view.setVisibility(4);
                        view2.setAlpha(1.0f);
                    }
                };
            }

            /* access modifiers changed from: 0000 */
            public void onTransitionComplete() {
                LauncherStateTransitionAnimation.this.mLauncher.getUserEventDispatcher().resetElapsedContainerMillis();
            }
        };
        startAnimationToWorkspaceFromOverlay(state, state2, this.mLauncher.getStartViewForAllAppsRevealAnimation(), this.mLauncher.getAppsView(), z, i, runnable, r8);
    }

    private void startAnimationToWorkspaceFromWidgets(State state, State state2, boolean z, Runnable runnable) {
        State state3 = state;
        State state4 = state2;
        startAnimationToWorkspaceFromOverlay(state3, state4, this.mLauncher.getWidgetsButton(), this.mLauncher.getWidgetsView(), z, 0, runnable, new PrivateTransitionCallbacks(FINAL_REVEAL_ALPHA_FOR_WIDGETS) {
            public AnimatorListenerAdapter getMaterialRevealViewAnimatorListener(final View view, View view2) {
                return new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        view.setVisibility(4);
                    }
                };
            }

            /* access modifiers changed from: 0000 */
            public void onTransitionComplete() {
                LauncherStateTransitionAnimation.this.mLauncher.getUserEventDispatcher().resetElapsedContainerMillis();
            }
        });
    }

    private void startAnimationToNewWorkspaceState(State state, State state2, boolean z, final Runnable runnable) {
        Workspace workspace = this.mLauncher.getWorkspace();
        AnimationLayerSet animationLayerSet = new AnimationLayerSet();
        AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        cancelAnimation();
        playCommonTransitionAnimations(state2, z, z, createAnimatorSet, animationLayerSet);
        this.mLauncher.getUserEventDispatcher().resetElapsedContainerMillis();
        if (z) {
            createAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (runnable != null) {
                        runnable.run();
                    }
                    LauncherStateTransitionAnimation.this.cleanupAnimation();
                }
            });
            createAnimatorSet.addListener(animationLayerSet);
            workspace.post(new StartAnimRunnable(createAnimatorSet, null));
            this.mCurrentAnimation = createAnimatorSet;
            return;
        }
        if (runnable != null) {
            runnable.run();
        }
        this.mCurrentAnimation = null;
    }

    private void startAnimationToWorkspaceFromOverlay(State state, State state2, View view, BaseContainerView baseContainerView, boolean z, int i, Runnable runnable, PrivateTransitionCallbacks privateTransitionCallbacks) {
        AnimationLayerSet animationLayerSet;
        final View view2;
        View view3;
        float f;
        View view4 = view;
        BaseContainerView baseContainerView2 = baseContainerView;
        int i2 = i;
        Runnable runnable2 = runnable;
        PrivateTransitionCallbacks privateTransitionCallbacks2 = privateTransitionCallbacks;
        AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        Resources resources = this.mLauncher.getResources();
        int integer = resources.getInteger(C0622R.integer.config_overlayRevealTime);
        int integer2 = resources.getInteger(C0622R.integer.config_overlaySlideRevealTime);
        int integer3 = resources.getInteger(C0622R.integer.config_overlayItemsAlphaStagger);
        Workspace workspace = this.mLauncher.getWorkspace();
        View revealView = baseContainerView.getRevealView();
        View contentView = baseContainerView.getContentView();
        AnimationLayerSet animationLayerSet2 = new AnimationLayerSet();
        boolean z2 = view4 != null;
        cancelAnimation();
        int i3 = integer2;
        AnimationLayerSet animationLayerSet3 = animationLayerSet2;
        View view5 = contentView;
        View view6 = revealView;
        Workspace workspace2 = workspace;
        playCommonTransitionAnimations(state2, z, z2, createAnimatorSet, animationLayerSet3);
        if (!z || !z2) {
            BaseContainerView baseContainerView3 = baseContainerView2;
            Runnable runnable3 = runnable2;
            PrivateTransitionCallbacks privateTransitionCallbacks3 = privateTransitionCallbacks2;
            if (state == State.NORMAL_HIDDEN) {
                this.mAllAppsController.finishPullDown();
            }
            baseContainerView3.setVisibility(8);
            privateTransitionCallbacks.onTransitionComplete();
            if (runnable3 != null) {
                runnable.run();
            }
            return;
        }
        if (i2 == 0) {
            if (baseContainerView.getVisibility() == 0) {
                int measuredWidth = view6.getMeasuredWidth() / 2;
                int measuredHeight = view6.getMeasuredHeight() / 2;
                int i4 = integer3;
                float hypot = (float) Math.hypot((double) measuredWidth, (double) measuredHeight);
                View view7 = view6;
                view7.setVisibility(0);
                view7.setAlpha(1.0f);
                view7.setTranslationY(0.0f);
                animationLayerSet = animationLayerSet3;
                animationLayerSet.addView(view7);
                int[] centerDeltaInScreenSpace = Utilities.getCenterDeltaInScreenSpace(view7, view4);
                float f2 = (float) centerDeltaInScreenSpace[0];
                float f3 = (float) centerDeltaInScreenSpace[1];
                LogDecelerateInterpolator logDecelerateInterpolator = new LogDecelerateInterpolator(100, 0);
                int i5 = measuredWidth;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view7, "translationY", new float[]{0.0f, f3});
                long j = (long) (integer - 16);
                ofFloat.setDuration(j);
                long j2 = (long) (i4 + 16);
                ofFloat.setStartDelay(j2);
                ofFloat.setInterpolator(logDecelerateInterpolator);
                createAnimatorSet.play(ofFloat);
                int i6 = measuredHeight;
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view7, "translationX", new float[]{0.0f, f2});
                ofFloat2.setDuration(j);
                ofFloat2.setStartDelay(j2);
                ofFloat2.setInterpolator(logDecelerateInterpolator);
                createAnimatorSet.play(ofFloat2);
                if (privateTransitionCallbacks.materialRevealViewFinalAlpha != 1.0f) {
                    ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(view7, "alpha", new float[]{1.0f, privateTransitionCallbacks.materialRevealViewFinalAlpha});
                    f = hypot;
                    view3 = view7;
                    ofFloat3.setDuration((long) integer);
                    ofFloat3.setInterpolator(logDecelerateInterpolator);
                    createAnimatorSet.play(ofFloat3);
                } else {
                    f = hypot;
                    view3 = view7;
                }
                view2 = view5;
                animationLayerSet.addView(view2);
                ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(view2, "translationY", new float[]{0.0f, f3});
                view2.setTranslationY(0.0f);
                ofFloat4.setDuration(j);
                ofFloat4.setInterpolator(logDecelerateInterpolator);
                ofFloat4.setStartDelay(j2);
                createAnimatorSet.play(ofFloat4);
                view2.setAlpha(1.0f);
                ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(view2, "alpha", new float[]{1.0f, 0.0f});
                ofFloat5.setDuration(100);
                ofFloat5.setInterpolator(logDecelerateInterpolator);
                createAnimatorSet.play(ofFloat5);
                ValueAnimator ofFloat6 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                ofFloat6.addUpdateListener(new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LauncherStateTransitionAnimation.this.mLauncher.getDragLayer().invalidateScrim();
                    }
                });
                createAnimatorSet.play(ofFloat6);
                float materialRevealViewStartFinalRadius = privateTransitionCallbacks.getMaterialRevealViewStartFinalRadius();
                View view8 = view3;
                AnimatorListenerAdapter materialRevealViewAnimatorListener = privateTransitionCallbacks.getMaterialRevealViewAnimatorListener(view8, view);
                ValueAnimator createRevealAnimator = new CircleRevealOutlineProvider(i5, i6, f, materialRevealViewStartFinalRadius).createRevealAnimator(view8);
                createRevealAnimator.setInterpolator(new LogDecelerateInterpolator(100, 0));
                createRevealAnimator.setDuration((long) integer);
                createRevealAnimator.setStartDelay((long) i4);
                if (materialRevealViewAnimatorListener != null) {
                    createRevealAnimator.addListener(materialRevealViewAnimatorListener);
                }
                createAnimatorSet.play(createRevealAnimator);
            } else {
                PrivateTransitionCallbacks privateTransitionCallbacks4 = privateTransitionCallbacks2;
                animationLayerSet = animationLayerSet3;
                view2 = view5;
            }
            final BaseContainerView baseContainerView4 = baseContainerView;
            final Runnable runnable4 = runnable;
            final PrivateTransitionCallbacks privateTransitionCallbacks5 = privateTransitionCallbacks;
            C06139 r0 = new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    baseContainerView4.setVisibility(8);
                    if (runnable4 != null) {
                        runnable4.run();
                    }
                    if (view2 != null) {
                        view2.setTranslationX(0.0f);
                        view2.setTranslationY(0.0f);
                        view2.setAlpha(1.0f);
                    }
                    LauncherStateTransitionAnimation.this.cleanupAnimation();
                    privateTransitionCallbacks5.onTransitionComplete();
                }
            };
            createAnimatorSet.addListener(r0);
            this.mCurrentAnimation = createAnimatorSet;
            this.mCurrentAnimation.addListener(animationLayerSet);
            baseContainerView.post(new StartAnimRunnable(createAnimatorSet, null));
        } else {
            BaseContainerView baseContainerView5 = baseContainerView2;
            final PrivateTransitionCallbacks privateTransitionCallbacks6 = privateTransitionCallbacks2;
            AnimationLayerSet animationLayerSet4 = animationLayerSet3;
            View view9 = view5;
            if (i2 == 1) {
                animationLayerSet4.addView(view9);
                final Runnable runnable5 = runnable;
                createAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    boolean canceled = false;

                    public void onAnimationCancel(Animator animator) {
                        this.canceled = true;
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (!this.canceled) {
                            if (runnable5 != null) {
                                runnable5.run();
                            }
                            LauncherStateTransitionAnimation.this.cleanupAnimation();
                            privateTransitionCallbacks6.onTransitionComplete();
                        }
                    }
                });
                boolean animateToWorkspace = this.mAllAppsController.animateToWorkspace(createAnimatorSet, (long) i3);
                StartAnimRunnable startAnimRunnable = new StartAnimRunnable(createAnimatorSet, workspace2);
                this.mCurrentAnimation = createAnimatorSet;
                this.mCurrentAnimation.addListener(animationLayerSet4);
                if (animateToWorkspace) {
                    baseContainerView5.post(startAnimRunnable);
                } else {
                    startAnimRunnable.run();
                }
            }
        }
    }

    private void cancelAnimation() {
        if (this.mCurrentAnimation != null) {
            this.mCurrentAnimation.setDuration(0);
            this.mCurrentAnimation.cancel();
            this.mCurrentAnimation = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void cleanupAnimation() {
        this.mCurrentAnimation = null;
    }
}
