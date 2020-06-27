package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.android.launcher3.Workspace.State;
import com.android.launcher3.anim.AnimationLayerSet;

public class PinchAnimationManager {
    private static final int INDEX_HOTSEAT = 0;
    private static final int INDEX_OVERVIEW_PANEL_BUTTONS = 1;
    private static final int INDEX_SCRIM = 2;
    private static final LinearInterpolator INTERPOLATOR = new LinearInterpolator();
    private static final String TAG = "PinchAnimationManager";
    private static final int THRESHOLD_ANIM_DURATION = 150;
    private final Animator[] mAnimators = new Animator[3];
    /* access modifiers changed from: private */
    public boolean mIsAnimating;
    private Launcher mLauncher;
    private int mNormalOverviewTransitionDuration;
    private float mOverviewScale;
    private float mOverviewTranslationY;
    /* access modifiers changed from: private */
    public Workspace mWorkspace;

    public PinchAnimationManager(Launcher launcher) {
        this.mLauncher = launcher;
        this.mWorkspace = launcher.mWorkspace;
        this.mOverviewScale = this.mWorkspace.getOverviewModeShrinkFactor();
        this.mOverviewTranslationY = (float) this.mWorkspace.getOverviewModeTranslationY();
        this.mNormalOverviewTransitionDuration = this.mWorkspace.getStateTransitionAnimation().mOverviewTransitionTime;
    }

    public int getNormalOverviewTransitionDuration() {
        return this.mNormalOverviewTransitionDuration;
    }

    public void animateToProgress(float f, float f2, int i, final PinchThresholdManager pinchThresholdManager) {
        if (i == -1) {
            i = this.mNormalOverviewTransitionDuration;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f, f2});
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                PinchAnimationManager.this.setAnimationProgress(floatValue);
                pinchThresholdManager.updateAndAnimatePassedThreshold(floatValue, PinchAnimationManager.this);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PinchAnimationManager.this.mIsAnimating = false;
                pinchThresholdManager.reset();
                PinchAnimationManager.this.mWorkspace.onEndStateTransition();
            }
        });
        ofFloat.setDuration((long) i).start();
        this.mIsAnimating = true;
    }

    public boolean isAnimating() {
        return this.mIsAnimating;
    }

    public void setAnimationProgress(float f) {
        float f2 = ((1.0f - this.mOverviewScale) * f) + this.mOverviewScale;
        float f3 = 1.0f - f;
        float f4 = this.mOverviewTranslationY * f3;
        this.mWorkspace.setScaleX(f2);
        this.mWorkspace.setScaleY(f2);
        this.mWorkspace.setTranslationY(f4);
        setOverviewPanelsAlpha(f3, 0);
    }

    public void animateThreshold(float f, State state, State state2) {
        boolean z = false;
        if (f == 0.4f) {
            if (state == State.OVERVIEW) {
                if (state2 == State.OVERVIEW) {
                    z = true;
                }
                animateOverviewPanelButtons(z);
            } else if (state == State.NORMAL) {
                if (state2 == State.NORMAL) {
                    z = true;
                }
                animateHotseatAndQsb(z);
            }
        } else if (f == 0.7f) {
            if (state == State.OVERVIEW) {
                animateHotseatAndQsb(state2 == State.NORMAL);
                if (state2 == State.OVERVIEW) {
                    z = true;
                }
                animateScrim(z);
            } else if (state == State.NORMAL) {
                animateOverviewPanelButtons(state2 == State.OVERVIEW);
                if (state2 == State.OVERVIEW) {
                    z = true;
                }
                animateScrim(z);
            }
        } else if (f != 0.95f) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Received unknown threshold to animate: ");
            sb.append(f);
            Log.e(str, sb.toString());
        } else if (state == State.OVERVIEW && state2 == State.NORMAL) {
            this.mLauncher.getUserEventDispatcher().logActionOnContainer(5, 0, 6, this.mWorkspace.getCurrentPage());
            this.mLauncher.showWorkspace(true);
            this.mWorkspace.snapToPage(this.mWorkspace.getCurrentPage());
        } else if (state == State.NORMAL && state2 == State.OVERVIEW) {
            this.mLauncher.getUserEventDispatcher().logActionOnContainer(5, 0, 1, this.mWorkspace.getCurrentPage());
            this.mLauncher.showOverviewMode(true);
        }
    }

    private void setOverviewPanelsAlpha(float f, int i) {
        int childCount = this.mWorkspace.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            CellLayout cellLayout = (CellLayout) this.mWorkspace.getChildAt(i2);
            if (i == 0) {
                cellLayout.setBackgroundAlpha(f);
            } else {
                ObjectAnimator.ofFloat(cellLayout, "backgroundAlpha", new float[]{f}).setDuration((long) i).start();
            }
        }
    }

    private void animateHotseatAndQsb(boolean z) {
        startAnimator(0, this.mWorkspace.createHotseatAlphaAnimator(z ? 1.0f : 0.0f), 150);
    }

    private void animateOverviewPanelButtons(boolean z) {
        animateShowHideView(1, this.mLauncher.getOverviewPanel(), z);
    }

    private void animateScrim(boolean z) {
        startAnimator(2, ObjectAnimator.ofFloat(this.mLauncher.getDragLayer(), "backgroundAlpha", new float[]{z ? this.mWorkspace.getStateTransitionAnimation().mWorkspaceScrimAlpha : 0.0f}), (long) this.mNormalOverviewTransitionDuration);
    }

    private void animateShowHideView(int i, final View view, boolean z) {
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        fArr[0] = z ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, property, fArr);
        ofFloat.addListener(new AnimationLayerSet(view));
        if (z) {
            view.setVisibility(0);
        } else {
            ofFloat.addListener(new AnimatorListenerAdapter() {
                private boolean mCancelled = false;

                public void onAnimationCancel(Animator animator) {
                    this.mCancelled = true;
                }

                public void onAnimationEnd(Animator animator) {
                    if (!this.mCancelled) {
                        view.setVisibility(4);
                    }
                }
            });
        }
        startAnimator(i, ofFloat, 150);
    }

    private void startAnimator(int i, Animator animator, long j) {
        if (this.mAnimators[i] != null) {
            this.mAnimators[i].cancel();
        }
        this.mAnimators[i] = animator;
        this.mAnimators[i].setInterpolator(INTERPOLATOR);
        this.mAnimators[i].setDuration(j).start();
    }
}
