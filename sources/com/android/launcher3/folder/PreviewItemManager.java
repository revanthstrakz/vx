package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.p004v7.widget.helper.ItemTouchHelper.Callback;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import java.util.ArrayList;
import java.util.List;

public class PreviewItemManager {
    private static final int FINAL_ITEM_ANIMATION_DURATION = 200;
    static final int INITIAL_ITEM_ANIMATION_DURATION = 350;
    private static final int ITEM_SLIDE_IN_OUT_DISTANCE_PX = 200;
    private static final int SLIDE_IN_FIRST_PAGE_ANIMATION_DURATION = 300;
    private static final int SLIDE_IN_FIRST_PAGE_ANIMATION_DURATION_DELAY = 100;
    /* access modifiers changed from: private */
    public float mCurrentPageItemsTransX = 0.0f;
    /* access modifiers changed from: private */
    public ArrayList<PreviewItemDrawingParams> mCurrentPageParams = new ArrayList<>();
    private ArrayList<PreviewItemDrawingParams> mFirstPageParams = new ArrayList<>();
    private FolderIcon mIcon;
    private float mIntrinsicIconSize = -1.0f;
    private int mPrevTopPadding = -1;
    private Drawable mReferenceDrawable = null;
    private boolean mShouldSlideInFirstPage;
    private int mTotalWidth = -1;

    public PreviewItemManager(FolderIcon folderIcon) {
        this.mIcon = folderIcon;
    }

    public FolderPreviewItemAnim createFirstItemAnimation(boolean z, Runnable runnable) {
        if (z) {
            FolderPreviewItemAnim folderPreviewItemAnim = new FolderPreviewItemAnim(this, (PreviewItemDrawingParams) this.mFirstPageParams.get(0), 0, 2, -1, -1, Callback.DEFAULT_DRAG_ANIMATION_DURATION, runnable);
            return folderPreviewItemAnim;
        }
        FolderPreviewItemAnim folderPreviewItemAnim2 = new FolderPreviewItemAnim(this, (PreviewItemDrawingParams) this.mFirstPageParams.get(0), -1, -1, 0, 2, 350, runnable);
        return folderPreviewItemAnim2;
    }

    /* access modifiers changed from: 0000 */
    public Drawable prepareCreateAnimation(View view) {
        Drawable drawable = ((TextView) view).getCompoundDrawables()[1];
        computePreviewDrawingParams(drawable.getIntrinsicWidth(), view.getMeasuredWidth());
        this.mReferenceDrawable = drawable;
        return drawable;
    }

    public void recomputePreviewDrawingParams() {
        if (this.mReferenceDrawable != null) {
            computePreviewDrawingParams(this.mReferenceDrawable.getIntrinsicWidth(), this.mIcon.getMeasuredWidth());
        }
    }

    private void computePreviewDrawingParams(int i, int i2) {
        float f = (float) i;
        if (this.mIntrinsicIconSize != f || this.mTotalWidth != i2 || this.mPrevTopPadding != this.mIcon.getPaddingTop()) {
            this.mIntrinsicIconSize = f;
            this.mTotalWidth = i2;
            this.mPrevTopPadding = this.mIcon.getPaddingTop();
            this.mIcon.mBackground.setup(this.mIcon.mLauncher, this.mIcon, this.mTotalWidth, this.mIcon.getPaddingTop());
            this.mIcon.mPreviewLayoutRule.init(this.mIcon.mBackground.previewSize, this.mIntrinsicIconSize, Utilities.isRtl(this.mIcon.getResources()));
            updateItemDrawingParams(false);
        }
    }

    /* access modifiers changed from: 0000 */
    public PreviewItemDrawingParams computePreviewItemDrawingParams(int i, int i2, PreviewItemDrawingParams previewItemDrawingParams) {
        if (i == -1) {
            return getFinalIconParams(previewItemDrawingParams);
        }
        return this.mIcon.mPreviewLayoutRule.computePreviewItemDrawingParams(i, i2, previewItemDrawingParams);
    }

    private PreviewItemDrawingParams getFinalIconParams(PreviewItemDrawingParams previewItemDrawingParams) {
        float f = (float) this.mIcon.mLauncher.getDeviceProfile().iconSizePx;
        float f2 = (((float) this.mIcon.mBackground.previewSize) - f) / 2.0f;
        previewItemDrawingParams.update(f2, f2, f / ((float) this.mReferenceDrawable.getIntrinsicWidth()));
        return previewItemDrawingParams;
    }

    public void drawParams(Canvas canvas, ArrayList<PreviewItemDrawingParams> arrayList, float f) {
        canvas.translate(f, 0.0f);
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            PreviewItemDrawingParams previewItemDrawingParams = (PreviewItemDrawingParams) arrayList.get(size);
            if (!previewItemDrawingParams.hidden) {
                drawPreviewItem(canvas, previewItemDrawingParams);
            }
        }
        canvas.translate(-f, 0.0f);
    }

    public void draw(Canvas canvas) {
        float f;
        PreviewBackground folderBackground = this.mIcon.getFolderBackground();
        canvas.translate((float) folderBackground.basePreviewOffsetX, (float) folderBackground.basePreviewOffsetY);
        if (this.mShouldSlideInFirstPage) {
            drawParams(canvas, this.mCurrentPageParams, this.mCurrentPageItemsTransX);
            f = this.mCurrentPageItemsTransX - 0.022460938f;
        } else {
            f = 0.0f;
        }
        drawParams(canvas, this.mFirstPageParams, f);
        canvas.translate((float) (-folderBackground.basePreviewOffsetX), (float) (-folderBackground.basePreviewOffsetY));
    }

    public void onParamsChanged() {
        this.mIcon.invalidate();
    }

    private void drawPreviewItem(Canvas canvas, PreviewItemDrawingParams previewItemDrawingParams) {
        canvas.save(1);
        canvas.translate(previewItemDrawingParams.transX, previewItemDrawingParams.transY);
        canvas.scale(previewItemDrawingParams.scale, previewItemDrawingParams.scale);
        Drawable drawable = previewItemDrawingParams.drawable;
        if (drawable != null) {
            Rect bounds = drawable.getBounds();
            canvas.save();
            canvas.translate((float) (-bounds.left), (float) (-bounds.top));
            canvas.scale(this.mIntrinsicIconSize / ((float) bounds.width()), this.mIntrinsicIconSize / ((float) bounds.height()));
            drawable.draw(canvas);
            canvas.restore();
        }
        canvas.restore();
    }

    public void hidePreviewItem(int i, boolean z) {
        PreviewItemDrawingParams previewItemDrawingParams = i < this.mFirstPageParams.size() ? (PreviewItemDrawingParams) this.mFirstPageParams.get(i) : null;
        if (previewItemDrawingParams != null) {
            previewItemDrawingParams.hidden = z;
        }
    }

    /* access modifiers changed from: 0000 */
    public void buildParamsForPage(int i, ArrayList<PreviewItemDrawingParams> arrayList, boolean z) {
        char c;
        int i2 = i;
        ArrayList<PreviewItemDrawingParams> arrayList2 = arrayList;
        List previewItemsOnPage = this.mIcon.getPreviewItemsOnPage(i2);
        int size = arrayList.size();
        while (true) {
            c = 1;
            if (previewItemsOnPage.size() >= arrayList.size()) {
                break;
            }
            arrayList2.remove(arrayList.size() - 1);
        }
        while (previewItemsOnPage.size() > arrayList.size()) {
            arrayList2.add(new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0.0f));
        }
        int size2 = i2 == 0 ? previewItemsOnPage.size() : FolderIcon.NUM_ITEMS_IN_PREVIEW;
        int i3 = 0;
        while (i3 < arrayList.size()) {
            PreviewItemDrawingParams previewItemDrawingParams = (PreviewItemDrawingParams) arrayList2.get(i3);
            previewItemDrawingParams.drawable = ((BubbleTextView) previewItemsOnPage.get(i3)).getCompoundDrawables()[c];
            if (previewItemDrawingParams.drawable != null && !this.mIcon.mFolder.isOpen()) {
                previewItemDrawingParams.drawable.setCallback(this.mIcon);
            }
            if (!z || FeatureFlags.LAUNCHER3_LEGACY_FOLDER_ICON) {
                PreviewItemDrawingParams previewItemDrawingParams2 = previewItemDrawingParams;
                computePreviewItemDrawingParams(i3, size2, previewItemDrawingParams2);
                if (this.mReferenceDrawable == null) {
                    this.mReferenceDrawable = previewItemDrawingParams2.drawable;
                }
            } else {
                FolderPreviewItemAnim folderPreviewItemAnim = r0;
                PreviewItemDrawingParams previewItemDrawingParams3 = previewItemDrawingParams;
                FolderPreviewItemAnim folderPreviewItemAnim2 = new FolderPreviewItemAnim(this, previewItemDrawingParams, i3, size, i3, size2, 400, null);
                if (previewItemDrawingParams3.anim != null) {
                    if (!previewItemDrawingParams3.anim.hasEqualFinalState(folderPreviewItemAnim)) {
                        previewItemDrawingParams3.anim.cancel();
                    }
                }
                previewItemDrawingParams3.anim = folderPreviewItemAnim;
                previewItemDrawingParams3.anim.start();
            }
            i3++;
            arrayList2 = arrayList;
            c = 1;
        }
    }

    /* access modifiers changed from: 0000 */
    public void onFolderClose(int i) {
        this.mShouldSlideInFirstPage = i != 0;
        if (this.mShouldSlideInFirstPage) {
            this.mCurrentPageItemsTransX = 0.0f;
            buildParamsForPage(i, this.mCurrentPageParams, false);
            onParamsChanged();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 200.0f});
            ofFloat.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PreviewItemManager.this.mCurrentPageItemsTransX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    PreviewItemManager.this.onParamsChanged();
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PreviewItemManager.this.mCurrentPageParams.clear();
                }
            });
            ofFloat.setStartDelay(100);
            ofFloat.setDuration(300);
            ofFloat.start();
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateItemDrawingParams(boolean z) {
        buildParamsForPage(0, this.mFirstPageParams, z);
    }

    /* access modifiers changed from: 0000 */
    public boolean verifyDrawable(@NonNull Drawable drawable) {
        for (int i = 0; i < this.mFirstPageParams.size(); i++) {
            if (((PreviewItemDrawingParams) this.mFirstPageParams.get(i)).drawable == drawable) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public float getIntrinsicIconSize() {
        return this.mIntrinsicIconSize;
    }

    public void onDrop(List<BubbleTextView> list, List<BubbleTextView> list2, ShortcutInfo shortcutInfo) {
        int size = list2.size();
        ArrayList<PreviewItemDrawingParams> arrayList = this.mFirstPageParams;
        buildParamsForPage(0, arrayList, false);
        ArrayList arrayList2 = new ArrayList();
        for (BubbleTextView bubbleTextView : list2) {
            if (!list.contains(bubbleTextView) && !bubbleTextView.getTag().equals(shortcutInfo)) {
                arrayList2.add(bubbleTextView);
            }
        }
        for (int i = 0; i < arrayList2.size(); i++) {
            int indexOf = list2.indexOf(arrayList2.get(i));
            PreviewItemDrawingParams previewItemDrawingParams = (PreviewItemDrawingParams) arrayList.get(indexOf);
            computePreviewItemDrawingParams(indexOf, size, previewItemDrawingParams);
            updateTransitionParam(previewItemDrawingParams, (BubbleTextView) arrayList2.get(i), this.mIcon.mPreviewLayoutRule.getEnterIndex(), list2.indexOf(arrayList2.get(i)));
        }
        for (int i2 = 0; i2 < list2.size(); i2++) {
            int indexOf2 = list.indexOf(list2.get(i2));
            if (indexOf2 >= 0 && i2 != indexOf2) {
                updateTransitionParam((PreviewItemDrawingParams) arrayList.get(i2), (BubbleTextView) list2.get(i2), indexOf2, i2);
            }
        }
        ArrayList arrayList3 = new ArrayList(list);
        arrayList3.removeAll(list2);
        for (int i3 = 0; i3 < arrayList3.size(); i3++) {
            BubbleTextView bubbleTextView2 = (BubbleTextView) arrayList3.get(i3);
            int indexOf3 = list.indexOf(bubbleTextView2);
            PreviewItemDrawingParams computePreviewItemDrawingParams = computePreviewItemDrawingParams(indexOf3, size, null);
            updateTransitionParam(computePreviewItemDrawingParams, bubbleTextView2, indexOf3, this.mIcon.mPreviewLayoutRule.getExitIndex());
            arrayList.add(0, computePreviewItemDrawingParams);
        }
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            if (((PreviewItemDrawingParams) arrayList.get(i4)).anim != null) {
                ((PreviewItemDrawingParams) arrayList.get(i4)).anim.start();
            }
        }
    }

    private void updateTransitionParam(PreviewItemDrawingParams previewItemDrawingParams, BubbleTextView bubbleTextView, int i, int i2) {
        previewItemDrawingParams.drawable = bubbleTextView.getCompoundDrawables()[1];
        if (!this.mIcon.mFolder.isOpen()) {
            previewItemDrawingParams.drawable.setCallback(this.mIcon);
        }
        FolderPreviewItemAnim folderPreviewItemAnim = new FolderPreviewItemAnim(this, previewItemDrawingParams, i, FolderIcon.NUM_ITEMS_IN_PREVIEW, i2, FolderIcon.NUM_ITEMS_IN_PREVIEW, 400, null);
        if (previewItemDrawingParams.anim != null && !previewItemDrawingParams.anim.hasEqualFinalState(folderPreviewItemAnim)) {
            previewItemDrawingParams.anim.cancel();
        }
        previewItemDrawingParams.anim = folderPreviewItemAnim;
    }
}
