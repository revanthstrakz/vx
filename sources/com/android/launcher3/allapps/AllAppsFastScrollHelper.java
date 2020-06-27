package com.android.launcher3.allapps;

import android.support.p004v7.widget.RecyclerView.ViewHolder;
import com.android.launcher3.allapps.AllAppsGridAdapter.BindViewCallback;
import com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem;
import com.android.launcher3.allapps.AlphabeticalAppsList.FastScrollSectionInfo;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class AllAppsFastScrollHelper implements BindViewCallback {
    private static final int INITIAL_TOUCH_SETTLING_DURATION = 100;
    private static final int REPEAT_TOUCH_SETTLING_DURATION = 200;
    private AlphabeticalAppsList mApps;
    String mCurrentFastScrollSection;
    int mFastScrollFrameIndex;
    final int[] mFastScrollFrames = new int[10];
    Runnable mFastScrollToTargetSectionRunnable = new Runnable() {
        public void run() {
            AllAppsFastScrollHelper.this.mCurrentFastScrollSection = AllAppsFastScrollHelper.this.mTargetFastScrollSection;
            AllAppsFastScrollHelper.this.mHasFastScrollTouchSettled = true;
            AllAppsFastScrollHelper.this.mHasFastScrollTouchSettledAtLeastOnce = true;
            AllAppsFastScrollHelper.this.updateTrackedViewsFastScrollFocusState();
        }
    };
    /* access modifiers changed from: private */
    public boolean mHasFastScrollTouchSettled;
    /* access modifiers changed from: private */
    public boolean mHasFastScrollTouchSettledAtLeastOnce;
    /* access modifiers changed from: private */
    public AllAppsRecyclerView mRv;
    Runnable mSmoothSnapNextFrameRunnable = new Runnable() {
        public void run() {
            if (AllAppsFastScrollHelper.this.mFastScrollFrameIndex < AllAppsFastScrollHelper.this.mFastScrollFrames.length) {
                AllAppsFastScrollHelper.this.mRv.scrollBy(0, AllAppsFastScrollHelper.this.mFastScrollFrames[AllAppsFastScrollHelper.this.mFastScrollFrameIndex]);
                AllAppsFastScrollHelper.this.mFastScrollFrameIndex++;
                AllAppsFastScrollHelper.this.mRv.postOnAnimation(AllAppsFastScrollHelper.this.mSmoothSnapNextFrameRunnable);
            }
        }
    };
    int mTargetFastScrollPosition = -1;
    String mTargetFastScrollSection;
    private HashSet<ViewHolder> mTrackedFastScrollViews = new HashSet<>();

    public AllAppsFastScrollHelper(AllAppsRecyclerView allAppsRecyclerView, AlphabeticalAppsList alphabeticalAppsList) {
        this.mRv = allAppsRecyclerView;
        this.mApps = alphabeticalAppsList;
    }

    public void onSetAdapter(AllAppsGridAdapter allAppsGridAdapter) {
        allAppsGridAdapter.setBindViewCallback(this);
    }

    public boolean smoothScrollToSection(int i, int i2, FastScrollSectionInfo fastScrollSectionInfo) {
        if (this.mTargetFastScrollPosition == fastScrollSectionInfo.fastScrollToItem.position) {
            return false;
        }
        this.mTargetFastScrollPosition = fastScrollSectionInfo.fastScrollToItem.position;
        smoothSnapToPosition(i, i2, fastScrollSectionInfo);
        return true;
    }

    private void smoothSnapToPosition(int i, int i2, FastScrollSectionInfo fastScrollSectionInfo) {
        int i3;
        this.mRv.removeCallbacks(this.mSmoothSnapNextFrameRunnable);
        this.mRv.removeCallbacks(this.mFastScrollToTargetSectionRunnable);
        trackAllChildViews();
        if (this.mHasFastScrollTouchSettled) {
            this.mCurrentFastScrollSection = fastScrollSectionInfo.sectionName;
            this.mTargetFastScrollSection = null;
            updateTrackedViewsFastScrollFocusState();
        } else {
            this.mCurrentFastScrollSection = null;
            this.mTargetFastScrollSection = fastScrollSectionInfo.sectionName;
            this.mHasFastScrollTouchSettled = false;
            updateTrackedViewsFastScrollFocusState();
            this.mRv.postDelayed(this.mFastScrollToTargetSectionRunnable, this.mHasFastScrollTouchSettledAtLeastOnce ? 200 : 100);
        }
        List fastScrollerSections = this.mApps.getFastScrollerSections();
        int i4 = fastScrollSectionInfo.fastScrollToItem.position;
        if (fastScrollerSections.size() <= 0 || fastScrollerSections.get(0) != fastScrollSectionInfo) {
            i3 = Math.min(i2, this.mRv.getCurrentScrollY(i4, 0));
        } else {
            i3 = 0;
        }
        int length = this.mFastScrollFrames.length;
        int i5 = i3 - i;
        float signum = Math.signum((float) i5);
        int ceil = (int) (((double) signum) * Math.ceil((double) (((float) Math.abs(i5)) / ((float) length))));
        int i6 = i5;
        for (int i7 = 0; i7 < length; i7++) {
            this.mFastScrollFrames[i7] = (int) (((float) Math.min(Math.abs(ceil), Math.abs(i6))) * signum);
            i6 -= ceil;
        }
        this.mFastScrollFrameIndex = 0;
        this.mRv.postOnAnimation(this.mSmoothSnapNextFrameRunnable);
    }

    public void onFastScrollCompleted() {
        this.mRv.removeCallbacks(this.mSmoothSnapNextFrameRunnable);
        this.mRv.removeCallbacks(this.mFastScrollToTargetSectionRunnable);
        this.mHasFastScrollTouchSettled = false;
        this.mHasFastScrollTouchSettledAtLeastOnce = false;
        this.mCurrentFastScrollSection = null;
        this.mTargetFastScrollSection = null;
        this.mTargetFastScrollPosition = -1;
        updateTrackedViewsFastScrollFocusState();
        this.mTrackedFastScrollViews.clear();
    }

    public void onBindView(AllAppsGridAdapter.ViewHolder viewHolder) {
        if (this.mCurrentFastScrollSection != null || this.mTargetFastScrollSection != null) {
            this.mTrackedFastScrollViews.add(viewHolder);
        }
    }

    private void trackAllChildViews() {
        int childCount = this.mRv.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ViewHolder childViewHolder = this.mRv.getChildViewHolder(this.mRv.getChildAt(i));
            if (childViewHolder != null) {
                this.mTrackedFastScrollViews.add(childViewHolder);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateTrackedViewsFastScrollFocusState() {
        Iterator it = this.mTrackedFastScrollViews.iterator();
        while (it.hasNext()) {
            ViewHolder viewHolder = (ViewHolder) it.next();
            int adapterPosition = viewHolder.getAdapterPosition();
            boolean z = false;
            if (this.mCurrentFastScrollSection != null && adapterPosition > -1 && adapterPosition < this.mApps.getAdapterItems().size()) {
                AdapterItem adapterItem = (AdapterItem) this.mApps.getAdapterItems().get(adapterPosition);
                if (adapterItem != null && this.mCurrentFastScrollSection.equals(adapterItem.sectionName) && adapterItem.position == this.mTargetFastScrollPosition) {
                    z = true;
                }
            }
            viewHolder.itemView.setActivated(z);
        }
    }
}
