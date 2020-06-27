package com.android.launcher3.allapps;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.animation.SpringAnimation;
import android.support.p001v4.app.NotificationCompat;
import android.support.p001v4.view.animation.FastOutSlowInInterpolator;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import com.android.launcher3.C0622R;
import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.Workspace.Direction;
import com.android.launcher3.allapps.SearchUiManager.OnScrollRangeChangeListener;
import com.android.launcher3.anim.SpringAnimationHandler;
import com.android.launcher3.graphics.GradientView;
import com.android.launcher3.touch.SwipeDetector;
import com.android.launcher3.touch.SwipeDetector.Listener;
import com.android.launcher3.touch.SwipeDetector.ScrollInterpolator;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.TouchController;
import java.lang.reflect.InvocationTargetException;

public class AllAppsTransitionController implements TouchController, Listener, OnScrollRangeChangeListener {
    private static final boolean DBG = false;
    private static final float DEFAULT_SHIFT_RANGE = 10.0f;
    private static final float NOTIFICATION_CLOSE_VELOCITY = -0.35f;
    private static final float NOTIFICATION_OPEN_VELOCITY = 2.25f;
    private static final float PARALLAX_COEFFICIENT = 0.125f;
    private static final float RECATCH_REJECTION_FRACTION = 0.0875f;
    private static final int SINGLE_FRAME_MS = 16;
    private static final String TAG = "AllAppsTrans";
    private int mAllAppsBackgroundColor;
    private long mAnimationDuration;
    private AllAppsContainerView mAppsView;
    private AllAppsCaretController mCaretController;
    private float mContainerVelocity;
    private AnimatorSet mCurrentAnimation;
    private final Interpolator mDecelInterpolator = new DecelerateInterpolator(3.0f);
    /* access modifiers changed from: private */
    public final SwipeDetector mDetector;
    /* access modifiers changed from: private */
    public Animator mDiscoBounceAnimation;
    private final ArgbEvaluator mEvaluator;
    private final Interpolator mFastOutSlowInInterpolator = new FastOutSlowInInterpolator();
    private GradientView mGradientView;
    private Hotseat mHotseat;
    private final Interpolator mHotseatAccelInterpolator = new AccelerateInterpolator(1.5f);
    private int mHotseatBackgroundColor;
    private final boolean mIsDarkTheme;
    /* access modifiers changed from: private */
    public boolean mIsTranslateWithoutWorkspace = false;
    private final Launcher mLauncher;
    private boolean mNoIntercept;
    private NotificationState mNotificationState;
    private float mProgress;
    private final ScrollInterpolator mScrollInterpolator = new ScrollInterpolator();
    private SpringAnimation mSearchSpring;
    private float mShiftRange;
    private float mShiftStart;
    private SpringAnimationHandler mSpringAnimationHandler;
    private float mStatusBarHeight;
    private boolean mTouchEventStartedOnHotseat;
    private Workspace mWorkspace;
    private final Interpolator mWorkspaceAccelnterpolator = new AccelerateInterpolator(2.0f);

    enum NotificationState {
        Locked,
        Free,
        Opened,
        Closed
    }

    public AllAppsTransitionController(Launcher launcher) {
        this.mLauncher = launcher;
        this.mDetector = new SwipeDetector((Context) launcher, (Listener) this, SwipeDetector.VERTICAL);
        this.mShiftRange = DEFAULT_SHIFT_RANGE;
        this.mProgress = 1.0f;
        this.mEvaluator = new ArgbEvaluator();
        this.mAllAppsBackgroundColor = Themes.getAttrColor(launcher, 16843827);
        this.mIsDarkTheme = Themes.getAttrBoolean(this.mLauncher, C0622R.attr.isMainColorDark);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006f, code lost:
        if (isInDisallowRecatchTopZone() != false) goto L_0x005d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onControllerInterceptTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            int r0 = r6.getAction()
            r1 = 0
            if (r0 != 0) goto L_0x0077
            r5.mNoIntercept = r1
            com.android.launcher3.Launcher r0 = r5.mLauncher
            com.android.launcher3.dragndrop.DragLayer r0 = r0.getDragLayer()
            boolean r0 = r0.isEventOverHotseat(r6)
            r5.mTouchEventStartedOnHotseat = r0
            com.android.launcher3.Launcher r0 = r5.mLauncher
            boolean r0 = r0.isAllAppsVisible()
            r2 = 1
            if (r0 != 0) goto L_0x002d
            com.android.launcher3.Launcher r0 = r5.mLauncher
            com.android.launcher3.Workspace r0 = r0.getWorkspace()
            boolean r0 = r0.workspaceInModalState()
            if (r0 == 0) goto L_0x002d
            r5.mNoIntercept = r2
            goto L_0x0077
        L_0x002d:
            com.android.launcher3.Launcher r0 = r5.mLauncher
            boolean r0 = r0.isAllAppsVisible()
            if (r0 == 0) goto L_0x0040
            com.android.launcher3.allapps.AllAppsContainerView r0 = r5.mAppsView
            boolean r0 = r0.shouldContainerScroll(r6)
            if (r0 != 0) goto L_0x0040
            r5.mNoIntercept = r2
            goto L_0x0077
        L_0x0040:
            com.android.launcher3.Launcher r0 = r5.mLauncher
            com.android.launcher3.AbstractFloatingView r0 = com.android.launcher3.AbstractFloatingView.getTopOpenView(r0)
            if (r0 == 0) goto L_0x004b
            r5.mNoIntercept = r2
            goto L_0x0077
        L_0x004b:
            com.android.launcher3.touch.SwipeDetector r0 = r5.mDetector
            boolean r0 = r0.isIdleState()
            r3 = 3
            r4 = 2
            if (r0 == 0) goto L_0x0062
            com.android.launcher3.Launcher r0 = r5.mLauncher
            boolean r0 = r0.isAllAppsVisible()
            if (r0 == 0) goto L_0x0060
        L_0x005d:
            r2 = 0
            r3 = 2
            goto L_0x0072
        L_0x0060:
            r2 = 0
            goto L_0x0072
        L_0x0062:
            boolean r0 = r5.isInDisallowRecatchBottomZone()
            if (r0 == 0) goto L_0x006b
            r2 = 0
            r3 = 1
            goto L_0x0072
        L_0x006b:
            boolean r0 = r5.isInDisallowRecatchTopZone()
            if (r0 == 0) goto L_0x0072
            goto L_0x005d
        L_0x0072:
            com.android.launcher3.touch.SwipeDetector r0 = r5.mDetector
            r0.setDetectableScrollConditions(r3, r2)
        L_0x0077:
            boolean r0 = r5.mNoIntercept
            if (r0 == 0) goto L_0x007c
            return r1
        L_0x007c:
            com.android.launcher3.touch.SwipeDetector r0 = r5.mDetector
            r0.onTouchEvent(r6)
            com.android.launcher3.touch.SwipeDetector r6 = r5.mDetector
            boolean r6 = r6.isSettlingState()
            if (r6 == 0) goto L_0x0096
            boolean r6 = r5.isInDisallowRecatchBottomZone()
            if (r6 != 0) goto L_0x0095
            boolean r6 = r5.isInDisallowRecatchTopZone()
            if (r6 == 0) goto L_0x0096
        L_0x0095:
            return r1
        L_0x0096:
            com.android.launcher3.touch.SwipeDetector r6 = r5.mDetector
            boolean r6 = r6.isDraggingOrSettling()
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.AllAppsTransitionController.onControllerInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        if (hasSpringAnimationHandler()) {
            this.mSpringAnimationHandler.addMovement(motionEvent);
        }
        return this.mDetector.onTouchEvent(motionEvent);
    }

    private boolean isInDisallowRecatchTopZone() {
        return this.mProgress < RECATCH_REJECTION_FRACTION;
    }

    private boolean isInDisallowRecatchBottomZone() {
        return this.mProgress > 0.9125f;
    }

    public void onDragStart(boolean z) {
        this.mCaretController.onDragStart();
        cancelAnimation();
        this.mCurrentAnimation = LauncherAnimUtils.createAnimatorSet();
        this.mShiftStart = this.mAppsView.getTranslationY();
        preparePull(z);
        if (hasSpringAnimationHandler()) {
            this.mSpringAnimationHandler.skipToEnd();
        }
        this.mNotificationState = NotificationState.Free;
    }

    public boolean onDrag(float f, float f2) {
        if (this.mAppsView == null) {
            return false;
        }
        if (this.mNotificationState != NotificationState.Locked) {
            if (this.mProgress < 1.0f) {
                this.mNotificationState = NotificationState.Locked;
            } else {
                if (f2 > NOTIFICATION_OPEN_VELOCITY && (this.mNotificationState == NotificationState.Free || this.mNotificationState == NotificationState.Closed)) {
                    this.mNotificationState = openNotifications() ? NotificationState.Opened : NotificationState.Locked;
                } else if (f2 < NOTIFICATION_CLOSE_VELOCITY && this.mNotificationState == NotificationState.Opened) {
                    this.mNotificationState = closeNotifications() ? NotificationState.Closed : NotificationState.Locked;
                }
                if (this.mNotificationState == NotificationState.Opened || this.mNotificationState == NotificationState.Closed) {
                    return true;
                }
            }
        }
        this.mContainerVelocity = f2;
        setProgress(Math.min(Math.max(0.0f, this.mShiftStart + f), this.mShiftRange) / this.mShiftRange);
        return true;
    }

    @SuppressLint({"WrongConstant", "PrivateApi"})
    private boolean openNotifications() {
        try {
            Class.forName("android.app.StatusBarManager").getMethod("expandNotificationsPanel", new Class[0]).invoke(this.mLauncher.getSystemService("statusbar"), new Object[0]);
            return true;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
            return false;
        }
    }

    @SuppressLint({"WrongConstant", "PrivateApi"})
    private boolean closeNotifications() {
        try {
            Class.forName("android.app.StatusBarManager").getMethod("collapsePanels", new Class[0]).invoke(this.mLauncher.getSystemService("statusbar"), new Object[0]);
            return true;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
            return false;
        }
    }

    public void onDragEnd(float f, boolean z) {
        if (this.mAppsView != null) {
            int i = this.mTouchEventStartedOnHotseat ? 2 : 1;
            if (!z || this.mNotificationState == NotificationState.Opened || this.mNotificationState == NotificationState.Closed) {
                if (this.mAppsView.getTranslationY() > this.mShiftRange / 2.0f) {
                    calculateDuration(f, Math.abs(this.mShiftRange - this.mAppsView.getTranslationY()));
                    this.mLauncher.showWorkspace(true);
                } else {
                    calculateDuration(f, Math.abs(this.mAppsView.getTranslationY()));
                    if (!this.mLauncher.isAllAppsVisible()) {
                        this.mLauncher.getUserEventDispatcher().logActionOnContainer(3, 1, i);
                    }
                    this.mLauncher.showAppsView(true, false, false);
                }
            } else if (f < 0.0f) {
                calculateDuration(f, this.mAppsView.getTranslationY());
                if (!this.mLauncher.isAllAppsVisible()) {
                    this.mLauncher.getUserEventDispatcher().logActionOnContainer(4, 1, i);
                }
                this.mLauncher.showAppsView(true, false, false);
                if (hasSpringAnimationHandler()) {
                    this.mSpringAnimationHandler.add(this.mSearchSpring, true);
                    this.mSpringAnimationHandler.animateToFinalPosition(0.0f, 1);
                }
            } else {
                calculateDuration(f, Math.abs(this.mShiftRange - this.mAppsView.getTranslationY()));
                this.mLauncher.showWorkspace(true);
            }
        }
    }

    public boolean isTransitioning() {
        return this.mDetector.isDraggingOrSettling();
    }

    public boolean isDragging() {
        return this.mDetector.isDraggingState();
    }

    public void preparePull(boolean z) {
        if (z) {
            ((InputMethodManager) this.mLauncher.getSystemService("input_method")).hideSoftInputFromWindow(this.mLauncher.getAppsView().getWindowToken(), 0);
            this.mStatusBarHeight = (float) this.mLauncher.getDragLayer().getInsets().top;
            this.mHotseat.setVisibility(0);
            this.mHotseatBackgroundColor = this.mHotseat.getBackgroundDrawableColor();
            this.mHotseat.setBackgroundTransparent(true);
            if (!this.mLauncher.isAllAppsVisible()) {
                this.mLauncher.tryAndUpdatePredictedApps();
                this.mAppsView.reset();
                this.mAppsView.setVisibility(0);
            }
        }
    }

    private void updateLightStatusBar(float f) {
        if (f <= this.mShiftRange / 4.0f) {
            this.mLauncher.getSystemUiController().updateUiState(1, !this.mIsDarkTheme);
        } else {
            this.mLauncher.getSystemUiController().updateUiState(1, 0);
        }
    }

    private void updateAllAppsBg(float f) {
        if (this.mGradientView == null) {
            this.mGradientView = (GradientView) this.mLauncher.findViewById(C0622R.C0625id.gradient_bg);
            this.mGradientView.setVisibility(0);
            this.mGradientView.setShiftScrim(!Utilities.ATLEAST_MARSHMALLOW);
        }
        this.mGradientView.setProgress(f);
    }

    public void setProgress(float f) {
        float f2 = this.mProgress * this.mShiftRange;
        this.mProgress = f;
        float f3 = this.mShiftRange * f;
        float boundToRange = Utilities.boundToRange(f, 0.0f, 1.0f);
        float f4 = 1.0f - boundToRange;
        float interpolation = this.mWorkspaceAccelnterpolator.getInterpolation(boundToRange);
        float interpolation2 = this.mHotseatAccelInterpolator.getInterpolation(boundToRange);
        ((Integer) this.mEvaluator.evaluate(this.mDecelInterpolator.getInterpolation(f4), Integer.valueOf(this.mHotseatBackgroundColor), Integer.valueOf(this.mAllAppsBackgroundColor))).intValue();
        Color.alpha(((Integer) this.mEvaluator.evaluate(f4, Integer.valueOf(this.mHotseatBackgroundColor), Integer.valueOf(this.mAllAppsBackgroundColor))).intValue());
        updateAllAppsBg(f4);
        this.mAppsView.getContentView().setAlpha(f4);
        this.mAppsView.setTranslationY(f3);
        if (!this.mLauncher.getDeviceProfile().isVerticalBarLayout()) {
            this.mWorkspace.setHotseatTranslationAndAlpha(Direction.Y, (-this.mShiftRange) + f3, interpolation2);
        } else {
            this.mWorkspace.setHotseatTranslationAndAlpha(Direction.Y, ((-this.mShiftRange) + f3) * PARALLAX_COEFFICIENT, interpolation2);
        }
        if (!this.mIsTranslateWithoutWorkspace) {
            this.mWorkspace.setWorkspaceYTranslationAndAlpha(((-this.mShiftRange) + f3) * PARALLAX_COEFFICIENT, interpolation);
            if (!this.mDetector.isDraggingState()) {
                this.mContainerVelocity = this.mDetector.computeVelocity(f3 - f2, System.currentTimeMillis());
            }
            this.mCaretController.updateCaret(f, this.mContainerVelocity, this.mDetector.isDraggingState());
            updateLightStatusBar(f3);
        }
    }

    public float getProgress() {
        return this.mProgress;
    }

    private void calculateDuration(float f, float f2) {
        this.mAnimationDuration = SwipeDetector.calculateDuration(f, f2 / this.mShiftRange);
    }

    public boolean animateToAllApps(AnimatorSet animatorSet, long j) {
        boolean z;
        TimeInterpolator timeInterpolator;
        if (animatorSet == null) {
            return true;
        }
        if (this.mDetector.isIdleState()) {
            preparePull(true);
            this.mAnimationDuration = j;
            this.mShiftStart = this.mAppsView.getTranslationY();
            timeInterpolator = this.mFastOutSlowInInterpolator;
            z = true;
        } else {
            this.mScrollInterpolator.setVelocityAtZero(Math.abs(this.mContainerVelocity));
            timeInterpolator = this.mScrollInterpolator;
            float f = this.mProgress + ((this.mContainerVelocity * 16.0f) / this.mShiftRange);
            if (f >= 0.0f) {
                this.mProgress = f;
            }
            z = false;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, NotificationCompat.CATEGORY_PROGRESS, new float[]{this.mProgress, 0.0f});
        ofFloat.setDuration(this.mAnimationDuration);
        ofFloat.setInterpolator(timeInterpolator);
        animatorSet.play(ofFloat);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            boolean canceled = false;

            public void onAnimationCancel(Animator animator) {
                this.canceled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (!this.canceled) {
                    AllAppsTransitionController.this.finishPullUp();
                    AllAppsTransitionController.this.cleanUpAnimation();
                    AllAppsTransitionController.this.mDetector.finishedScrolling();
                }
            }
        });
        this.mCurrentAnimation = animatorSet;
        return z;
    }

    public void showDiscoveryBounce() {
        cancelDiscoveryAnimation();
        this.mDiscoBounceAnimation = AnimatorInflater.loadAnimator(this.mLauncher, C0622R.animator.discovery_bounce);
        this.mDiscoBounceAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                AllAppsTransitionController.this.mIsTranslateWithoutWorkspace = true;
                AllAppsTransitionController.this.preparePull(true);
            }

            public void onAnimationEnd(Animator animator) {
                AllAppsTransitionController.this.finishPullDown();
                AllAppsTransitionController.this.mDiscoBounceAnimation = null;
                AllAppsTransitionController.this.mIsTranslateWithoutWorkspace = false;
            }
        });
        this.mDiscoBounceAnimation.setTarget(this);
        this.mAppsView.post(new Runnable() {
            public void run() {
                if (AllAppsTransitionController.this.mDiscoBounceAnimation != null) {
                    AllAppsTransitionController.this.mDiscoBounceAnimation.start();
                }
            }
        });
    }

    public boolean animateToWorkspace(AnimatorSet animatorSet, long j) {
        boolean z;
        TimeInterpolator timeInterpolator;
        if (animatorSet == null) {
            return true;
        }
        if (this.mDetector.isIdleState()) {
            preparePull(true);
            this.mAnimationDuration = j;
            this.mShiftStart = this.mAppsView.getTranslationY();
            timeInterpolator = this.mFastOutSlowInInterpolator;
            z = true;
        } else {
            this.mScrollInterpolator.setVelocityAtZero(Math.abs(this.mContainerVelocity));
            timeInterpolator = this.mScrollInterpolator;
            float f = this.mProgress + ((this.mContainerVelocity * 16.0f) / this.mShiftRange);
            if (f <= 1.0f) {
                this.mProgress = f;
            }
            z = false;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, NotificationCompat.CATEGORY_PROGRESS, new float[]{this.mProgress, 1.0f});
        ofFloat.setDuration(this.mAnimationDuration);
        ofFloat.setInterpolator(timeInterpolator);
        animatorSet.play(ofFloat);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            boolean canceled = false;

            public void onAnimationCancel(Animator animator) {
                this.canceled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (!this.canceled) {
                    AllAppsTransitionController.this.finishPullDown();
                    AllAppsTransitionController.this.cleanUpAnimation();
                    AllAppsTransitionController.this.mDetector.finishedScrolling();
                }
            }
        });
        this.mCurrentAnimation = animatorSet;
        return z;
    }

    public void finishPullUp() {
        this.mHotseat.setVisibility(4);
        if (hasSpringAnimationHandler()) {
            this.mSpringAnimationHandler.remove(this.mSearchSpring);
            this.mSpringAnimationHandler.reset();
        }
        setProgress(0.0f);
    }

    public void finishPullDown() {
        this.mAppsView.setVisibility(4);
        this.mHotseat.setBackgroundTransparent(false);
        this.mHotseat.setVisibility(0);
        this.mAppsView.reset();
        if (hasSpringAnimationHandler()) {
            this.mSpringAnimationHandler.reset();
        }
        setProgress(1.0f);
    }

    private void cancelAnimation() {
        if (this.mCurrentAnimation != null) {
            this.mCurrentAnimation.cancel();
            this.mCurrentAnimation = null;
        }
        cancelDiscoveryAnimation();
    }

    public void cancelDiscoveryAnimation() {
        if (this.mDiscoBounceAnimation != null) {
            this.mDiscoBounceAnimation.cancel();
            this.mDiscoBounceAnimation = null;
        }
    }

    /* access modifiers changed from: private */
    public void cleanUpAnimation() {
        this.mCurrentAnimation = null;
    }

    public void setupViews(AllAppsContainerView allAppsContainerView, Hotseat hotseat, Workspace workspace) {
        this.mAppsView = allAppsContainerView;
        this.mHotseat = hotseat;
        this.mWorkspace = workspace;
        this.mHotseat.bringToFront();
        this.mCaretController = new AllAppsCaretController(this.mWorkspace.getPageIndicator().getCaretDrawable(), this.mLauncher);
        this.mAppsView.getSearchUiManager().addOnScrollRangeChangeListener(this);
        this.mSpringAnimationHandler = this.mAppsView.getSpringAnimationHandler();
        this.mSearchSpring = this.mAppsView.getSearchUiManager().getSpringForFling();
    }

    private boolean hasSpringAnimationHandler() {
        return this.mSpringAnimationHandler != null;
    }

    public void onScrollRangeChanged(int i) {
        this.mShiftRange = (float) i;
        setProgress(this.mProgress);
    }
}
