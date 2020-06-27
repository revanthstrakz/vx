package com.android.launcher3.util;

import android.app.WallpaperManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;

public class WallpaperOffsetInterpolator implements FrameCallback {
    private static final int ANIMATION_DURATION = 250;
    private static final int MIN_PARALLAX_PAGE_SPAN = 4;
    private static final String TAG = "WPOffsetInterpolator";
    private boolean mAnimating;
    private float mAnimationStartOffset;
    private long mAnimationStartTime;
    private final Choreographer mChoreographer = Choreographer.getInstance();
    private float mCurrentOffset = 0.5f;
    private float mFinalOffset = 0.0f;
    private final Interpolator mInterpolator = new DecelerateInterpolator(1.5f);
    private final boolean mIsRtl;
    private float mLastSetWallpaperOffsetSteps = 0.0f;
    private boolean mLockedToDefaultPage;
    int mNumPagesForWallpaperParallax;
    int mNumScreens;
    private boolean mWaitingForUpdate;
    private boolean mWallpaperIsLiveWallpaper;
    private final WallpaperManager mWallpaperManager;
    private IBinder mWindowToken;
    private final Workspace mWorkspace;

    public WallpaperOffsetInterpolator(Workspace workspace) {
        this.mWorkspace = workspace;
        this.mWallpaperManager = WallpaperManager.getInstance(workspace.getContext());
        this.mIsRtl = Utilities.isRtl(workspace.getResources());
    }

    public void doFrame(long j) {
        updateOffset(false);
    }

    private void updateOffset(boolean z) {
        if (this.mWaitingForUpdate || z) {
            this.mWaitingForUpdate = false;
            if (computeScrollOffset() && this.mWindowToken != null) {
                try {
                    this.mWallpaperManager.setWallpaperOffsets(this.mWindowToken, getCurrX(), 0.5f);
                    setWallpaperOffsetSteps();
                } catch (IllegalArgumentException e) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Error updating wallpaper offset: ");
                    sb.append(e);
                    Log.e(str, sb.toString());
                }
            }
        }
    }

    public void setLockToDefaultPage(boolean z) {
        this.mLockedToDefaultPage = z;
    }

    public boolean isLockedToDefaultPage() {
        return this.mLockedToDefaultPage;
    }

    public boolean computeScrollOffset() {
        float f = this.mCurrentOffset;
        if (this.mAnimating) {
            long currentTimeMillis = System.currentTimeMillis() - this.mAnimationStartTime;
            this.mCurrentOffset = this.mAnimationStartOffset + ((this.mFinalOffset - this.mAnimationStartOffset) * this.mInterpolator.getInterpolation(((float) currentTimeMillis) / 250.0f));
            this.mAnimating = currentTimeMillis < 250;
        } else {
            this.mCurrentOffset = this.mFinalOffset;
        }
        if (Math.abs(this.mCurrentOffset - this.mFinalOffset) > 1.0E-7f) {
            scheduleUpdate();
        }
        return Math.abs(f - this.mCurrentOffset) > 1.0E-7f;
    }

    public float wallpaperOffsetForScroll(int i) {
        int i2;
        int i3;
        int numScreensExcludingEmptyAndCustom = getNumScreensExcludingEmptyAndCustom();
        float f = 1.0f;
        float f2 = 0.0f;
        if (this.mLockedToDefaultPage || numScreensExcludingEmptyAndCustom <= 1) {
            if (!this.mIsRtl) {
                f = 0.0f;
            }
            return f;
        }
        if (this.mWallpaperIsLiveWallpaper) {
            this.mNumPagesForWallpaperParallax = numScreensExcludingEmptyAndCustom;
        } else {
            this.mNumPagesForWallpaperParallax = Math.max(4, numScreensExcludingEmptyAndCustom);
        }
        if (this.mIsRtl) {
            i3 = this.mWorkspace.numCustomPages();
            i2 = (i3 + numScreensExcludingEmptyAndCustom) - 1;
        } else {
            i2 = this.mWorkspace.numCustomPages();
            i3 = (i2 + numScreensExcludingEmptyAndCustom) - 1;
        }
        int scrollForPage = this.mWorkspace.getScrollForPage(i2);
        int scrollForPage2 = this.mWorkspace.getScrollForPage(i3) - scrollForPage;
        if (scrollForPage2 == 0) {
            return 0.0f;
        }
        float boundToRange = Utilities.boundToRange(((float) ((i - scrollForPage) - this.mWorkspace.getLayoutTransitionOffsetForPage(0))) / ((float) scrollForPage2), 0.0f, 1.0f);
        if (this.mIsRtl) {
            f2 = ((float) ((this.mNumPagesForWallpaperParallax - 1) - (numScreensExcludingEmptyAndCustom - 1))) / ((float) (this.mNumPagesForWallpaperParallax - 1));
        }
        return f2 + (boundToRange * (((float) (numScreensExcludingEmptyAndCustom - 1)) / ((float) (this.mNumPagesForWallpaperParallax - 1))));
    }

    private float wallpaperOffsetForCurrentScroll() {
        return wallpaperOffsetForScroll(this.mWorkspace.getScrollX());
    }

    private int numEmptyScreensToIgnore() {
        return (this.mWorkspace.getChildCount() - this.mWorkspace.numCustomPages() < 4 || !this.mWorkspace.hasExtraEmptyScreen()) ? 0 : 1;
    }

    private int getNumScreensExcludingEmptyAndCustom() {
        return (this.mWorkspace.getChildCount() - numEmptyScreensToIgnore()) - this.mWorkspace.numCustomPages();
    }

    public void syncWithScroll() {
        setFinalX(wallpaperOffsetForCurrentScroll());
        updateOffset(true);
    }

    public float getCurrX() {
        return this.mCurrentOffset;
    }

    public float getFinalX() {
        return this.mFinalOffset;
    }

    private void animateToFinal() {
        this.mAnimating = true;
        this.mAnimationStartOffset = this.mCurrentOffset;
        this.mAnimationStartTime = System.currentTimeMillis();
    }

    private void setWallpaperOffsetSteps() {
        float f = 1.0f / ((float) (this.mNumPagesForWallpaperParallax - 1));
        if (f != this.mLastSetWallpaperOffsetSteps) {
            this.mWallpaperManager.setWallpaperOffsetSteps(f, 1.0f);
            this.mLastSetWallpaperOffsetSteps = f;
        }
    }

    public void setFinalX(float f) {
        scheduleUpdate();
        this.mFinalOffset = Math.max(0.0f, Math.min(f, 1.0f));
        if (getNumScreensExcludingEmptyAndCustom() != this.mNumScreens) {
            if (this.mNumScreens > 0 && Float.compare(this.mCurrentOffset, this.mFinalOffset) != 0) {
                animateToFinal();
            }
            this.mNumScreens = getNumScreensExcludingEmptyAndCustom();
        }
    }

    private void scheduleUpdate() {
        if (!this.mWaitingForUpdate) {
            this.mChoreographer.postFrameCallback(this);
            this.mWaitingForUpdate = true;
        }
    }

    public void jumpToFinal() {
        this.mCurrentOffset = this.mFinalOffset;
    }

    public void onResume() {
        this.mWallpaperIsLiveWallpaper = this.mWallpaperManager.getWallpaperInfo() != null;
        this.mLastSetWallpaperOffsetSteps = 0.0f;
    }

    public void setWindowToken(IBinder iBinder) {
        this.mWindowToken = iBinder;
    }
}
