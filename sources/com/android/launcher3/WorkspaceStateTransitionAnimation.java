package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.Workspace.State;
import com.android.launcher3.anim.AnimationLayerSet;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.dragndrop.DragLayer;

public class WorkspaceStateTransitionAnimation {
    static final int BACKGROUND_FADE_OUT_DURATION = 350;
    public static final String TAG = "WorkspaceStateTransitionAnimation";
    int mAllAppsTransitionTime;
    float mCurrentScale;
    final Launcher mLauncher;
    float mNewScale;
    int mOverlayTransitionTime;
    float mOverviewModeShrinkFactor;
    int mOverviewTransitionTime;
    float mSpringLoadedShrinkFactor;
    int mSpringLoadedTransitionTime;
    AnimatorSet mStateAnimator;
    final Workspace mWorkspace;
    boolean mWorkspaceFadeInAdjacentScreens;
    float mWorkspaceScrimAlpha;
    final ZoomInInterpolator mZoomInInterpolator = new ZoomInInterpolator();

    public WorkspaceStateTransitionAnimation(Launcher launcher, Workspace workspace) {
        this.mLauncher = launcher;
        this.mWorkspace = workspace;
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        Resources resources = launcher.getResources();
        this.mAllAppsTransitionTime = resources.getInteger(C0622R.integer.config_allAppsTransitionTime);
        this.mOverviewTransitionTime = resources.getInteger(C0622R.integer.config_overviewTransitionTime);
        this.mOverlayTransitionTime = resources.getInteger(C0622R.integer.config_overlayTransitionTime);
        this.mSpringLoadedTransitionTime = this.mOverlayTransitionTime / 2;
        this.mSpringLoadedShrinkFactor = this.mLauncher.getDeviceProfile().workspaceSpringLoadShrinkFactor;
        this.mOverviewModeShrinkFactor = ((float) resources.getInteger(C0622R.integer.config_workspaceOverviewShrinkPercentage)) / 100.0f;
        this.mWorkspaceScrimAlpha = ((float) resources.getInteger(C0622R.integer.config_workspaceScrimAlpha)) / 100.0f;
        this.mWorkspaceFadeInAdjacentScreens = deviceProfile.shouldFadeAdjacentWorkspaceScreens();
    }

    public void snapToPageFromOverView(int i) {
        this.mWorkspace.snapToPage(i, this.mOverviewTransitionTime, (TimeInterpolator) this.mZoomInInterpolator);
    }

    public AnimatorSet getAnimationToState(State state, State state2, boolean z, AnimationLayerSet animationLayerSet) {
        boolean isEnabled = ((AccessibilityManager) this.mLauncher.getSystemService("accessibility")).isEnabled();
        TransitionStates transitionStates = new TransitionStates(state, state2);
        animateWorkspace(transitionStates, z, getAnimationDuration(transitionStates), animationLayerSet, isEnabled);
        animateBackgroundGradient(transitionStates, z, 350);
        return this.mStateAnimator;
    }

    public float getFinalScale() {
        return this.mNewScale;
    }

    private int getAnimationDuration(TransitionStates transitionStates) {
        if (transitionStates.workspaceToAllApps || transitionStates.overviewToAllApps) {
            return this.mAllAppsTransitionTime;
        }
        if (transitionStates.workspaceToOverview || transitionStates.overviewToWorkspace) {
            return this.mOverviewTransitionTime;
        }
        if (this.mLauncher.mState == State.WORKSPACE_SPRING_LOADED || (transitionStates.oldStateIsNormal && transitionStates.stateIsSpringLoaded)) {
            return this.mSpringLoadedTransitionTime;
        }
        return this.mOverlayTransitionTime;
    }

    private void animateWorkspace(TransitionStates transitionStates, boolean z, int i, AnimationLayerSet animationLayerSet, boolean z2) {
        float f;
        int i2;
        int i3;
        int i4;
        final TransitionStates transitionStates2 = transitionStates;
        int i5 = i;
        AnimationLayerSet animationLayerSet2 = animationLayerSet;
        final boolean z3 = z2;
        cancelAnimation();
        if (z) {
            this.mStateAnimator = LauncherAnimUtils.createAnimatorSet();
        }
        float f2 = (transitionStates2.stateIsSpringLoaded || transitionStates2.stateIsOverview) ? 1.0f : 0.0f;
        float f3 = (transitionStates2.stateIsNormal || transitionStates2.stateIsSpringLoaded || transitionStates2.stateIsNormalHidden) ? 1.0f : 0.0f;
        if (!transitionStates2.stateIsNormal) {
            boolean z4 = transitionStates2.stateIsNormalHidden;
        }
        float f4 = (transitionStates2.stateIsOverview || transitionStates2.stateIsOverviewHidden) ? (float) this.mWorkspace.getOverviewModeTranslationY() : transitionStates2.stateIsSpringLoaded ? this.mWorkspace.getSpringLoadedTranslationY() : 0.0f;
        int childCount = this.mWorkspace.getChildCount();
        int numCustomPages = this.mWorkspace.numCustomPages();
        this.mNewScale = 1.0f;
        if (transitionStates2.stateIsOverview) {
            this.mWorkspace.enableFreeScroll();
        } else if (transitionStates2.oldStateIsOverview) {
            this.mWorkspace.disableFreeScroll();
        }
        if (!transitionStates2.stateIsNormal) {
            if (transitionStates2.stateIsSpringLoaded) {
                this.mNewScale = this.mSpringLoadedShrinkFactor;
            } else if (transitionStates2.stateIsOverview || transitionStates2.stateIsOverviewHidden) {
                this.mNewScale = this.mOverviewModeShrinkFactor;
            }
        }
        int pageNearestToCenterOfScreen = this.mWorkspace.getPageNearestToCenterOfScreen();
        int i6 = 0;
        while (i6 < childCount) {
            CellLayout cellLayout = (CellLayout) this.mWorkspace.getChildAt(i6);
            float alpha = cellLayout.getShortcutsAndWidgets().getAlpha();
            if (!transitionStates2.stateIsOverviewHidden && (!transitionStates2.stateIsNormalHidden ? !transitionStates2.stateIsNormal || !this.mWorkspaceFadeInAdjacentScreens || i6 == pageNearestToCenterOfScreen || i6 < numCustomPages : i6 == this.mWorkspace.getNextPage())) {
                f = 1.0f;
            } else {
                f = 0.0f;
            }
            if (z) {
                float backgroundAlpha = cellLayout.getBackgroundAlpha();
                if (alpha != f) {
                    i4 = childCount;
                    i3 = numCustomPages;
                    i2 = pageNearestToCenterOfScreen;
                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(cellLayout.getShortcutsAndWidgets(), View.ALPHA, new float[]{f});
                    ofFloat.setDuration((long) i5).setInterpolator(this.mZoomInInterpolator);
                    this.mStateAnimator.play(ofFloat);
                } else {
                    i4 = childCount;
                    i3 = numCustomPages;
                    i2 = pageNearestToCenterOfScreen;
                }
                if (backgroundAlpha != 0.0f || f2 != 0.0f) {
                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(cellLayout, "backgroundAlpha", new float[]{backgroundAlpha, f2});
                    ofFloat2.setInterpolator(this.mZoomInInterpolator);
                    ofFloat2.setDuration((long) i5);
                    this.mStateAnimator.play(ofFloat2);
                }
            } else {
                i4 = childCount;
                i3 = numCustomPages;
                i2 = pageNearestToCenterOfScreen;
                cellLayout.setBackgroundAlpha(f2);
                cellLayout.setShortcutAndWidgetAlpha(f);
            }
            i6++;
            childCount = i4;
            numCustomPages = i3;
            pageNearestToCenterOfScreen = i2;
        }
        float f5 = 0.0f;
        final ViewGroup overviewPanel = this.mLauncher.getOverviewPanel();
        if (transitionStates2.stateIsOverview) {
            f5 = 1.0f;
        }
        if (z) {
            if (f5 != overviewPanel.getAlpha()) {
                ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(overviewPanel, View.ALPHA, new float[]{f5});
                ofFloat3.addListener(new AlphaUpdateListener(overviewPanel, z3));
                animationLayerSet2.addView(overviewPanel);
                if (transitionStates2.overviewToWorkspace) {
                    ofFloat3.setInterpolator(new DecelerateInterpolator(2.0f));
                } else if (transitionStates2.workspaceToOverview) {
                    ofFloat3.setInterpolator(null);
                }
                ofFloat3.setDuration((long) i5);
                this.mStateAnimator.play(ofFloat3);
            }
            long j = (long) i5;
            ObjectAnimator duration = LauncherAnimUtils.ofPropertyValuesHolder(this.mWorkspace, new PropertyListBuilder().scale(this.mNewScale).translationY(f4).build()).setDuration(j);
            duration.setInterpolator(this.mZoomInInterpolator);
            this.mStateAnimator.play(duration);
            animationLayerSet2.addView(this.mLauncher.getHotseat());
            animationLayerSet2.addView(this.mWorkspace.getPageIndicator());
            ValueAnimator createHotseatAlphaAnimator = this.mWorkspace.createHotseatAlphaAnimator(f3);
            if (transitionStates2.workspaceToOverview) {
                createHotseatAlphaAnimator.setInterpolator(new DecelerateInterpolator(2.0f));
            } else if (transitionStates2.overviewToWorkspace) {
                createHotseatAlphaAnimator.setInterpolator(null);
            }
            createHotseatAlphaAnimator.setDuration(j);
            this.mStateAnimator.play(createHotseatAlphaAnimator);
            this.mStateAnimator.addListener(new AnimatorListenerAdapter() {
                boolean canceled = false;

                public void onAnimationCancel(Animator animator) {
                    this.canceled = true;
                }

                public void onAnimationStart(Animator animator) {
                    WorkspaceStateTransitionAnimation.this.mWorkspace.getPageIndicator().setShouldAutoHide(!transitionStates2.stateIsSpringLoaded);
                }

                public void onAnimationEnd(Animator animator) {
                    WorkspaceStateTransitionAnimation.this.mStateAnimator = null;
                    if (!this.canceled && z3 && overviewPanel.getVisibility() == 0) {
                        overviewPanel.getChildAt(0).performAccessibilityAction(64, null);
                    }
                }
            });
            return;
        }
        overviewPanel.setAlpha(f5);
        AlphaUpdateListener.updateVisibility(overviewPanel, z3);
        this.mWorkspace.getPageIndicator().setShouldAutoHide(!transitionStates2.stateIsSpringLoaded);
        this.mWorkspace.createHotseatAlphaAnimator(f3).end();
        this.mWorkspace.updateCustomContentVisibility();
        this.mWorkspace.setScaleX(this.mNewScale);
        this.mWorkspace.setScaleY(this.mNewScale);
        this.mWorkspace.setTranslationY(f4);
        if (z3 && overviewPanel.getVisibility() == 0) {
            overviewPanel.getChildAt(0).performAccessibilityAction(64, null);
        }
    }

    private void animateBackgroundGradient(TransitionStates transitionStates, boolean z, int i) {
        final DragLayer dragLayer = this.mLauncher.getDragLayer();
        float backgroundAlpha = dragLayer.getBackgroundAlpha();
        float f = (transitionStates.stateIsNormal || transitionStates.stateIsNormalHidden) ? 0.0f : this.mWorkspaceScrimAlpha;
        if (f == backgroundAlpha) {
            return;
        }
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{backgroundAlpha, f});
            ofFloat.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    dragLayer.setBackgroundAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                }
            });
            ofFloat.setInterpolator(new DecelerateInterpolator(1.5f));
            ofFloat.setDuration((long) i);
            this.mStateAnimator.play(ofFloat);
            return;
        }
        dragLayer.setBackgroundAlpha(f);
    }

    private void cancelAnimation() {
        if (this.mStateAnimator != null) {
            this.mStateAnimator.setDuration(0);
            this.mStateAnimator.cancel();
        }
        this.mStateAnimator = null;
    }
}
