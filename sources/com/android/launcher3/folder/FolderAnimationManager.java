package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.p001v4.graphics.ColorUtils;
import android.util.Property;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.PropertyResetListener;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.dragndrop.DragLayer.LayoutParams;
import com.android.launcher3.util.Themes;
import java.util.Iterator;
import java.util.List;

public class FolderAnimationManager {
    private static final Property<View, Float> SCALE_PROPERTY = new Property<View, Float>(Float.class, "scale") {
        public Float get(View view) {
            return Float.valueOf(view.getScaleX());
        }

        public void set(View view, Float f) {
            view.setScaleX(f.floatValue());
            view.setScaleY(f.floatValue());
        }
    };
    private FolderPagedView mContent;
    private Context mContext;
    private final int mDelay;
    private final int mDuration;
    /* access modifiers changed from: private */
    public Folder mFolder;
    private GradientDrawable mFolderBackground;
    private FolderIcon mFolderIcon;
    private final TimeInterpolator mFolderInterpolator;
    /* access modifiers changed from: private */
    public final boolean mIsOpening;
    private final TimeInterpolator mLargeFolderPreviewItemCloseInterpolator;
    private final TimeInterpolator mLargeFolderPreviewItemOpenInterpolator;
    private Launcher mLauncher;
    private PreviewBackground mPreviewBackground;
    private final PreviewItemDrawingParams mTmpParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0.0f);

    public FolderAnimationManager(Folder folder, boolean z) {
        this.mFolder = folder;
        this.mContent = folder.mContent;
        this.mFolderBackground = (GradientDrawable) this.mFolder.getBackground();
        this.mFolderIcon = folder.mFolderIcon;
        this.mPreviewBackground = this.mFolderIcon.mBackground;
        this.mContext = folder.getContext();
        this.mLauncher = folder.mLauncher;
        this.mIsOpening = z;
        this.mDuration = this.mFolder.mMaterialExpandDuration;
        this.mDelay = this.mContext.getResources().getInteger(C0622R.integer.config_folderDelay);
        this.mFolderInterpolator = AnimationUtils.loadInterpolator(this.mContext, C0622R.interpolator.folder_interpolator);
        this.mLargeFolderPreviewItemOpenInterpolator = AnimationUtils.loadInterpolator(this.mContext, C0622R.interpolator.large_folder_preview_item_open_interpolator);
        this.mLargeFolderPreviewItemCloseInterpolator = AnimationUtils.loadInterpolator(this.mContext, C0622R.interpolator.large_folder_preview_item_close_interpolator);
    }

    public AnimatorSet getAnimator() {
        LayoutParams layoutParams = (LayoutParams) this.mFolder.getLayoutParams();
        PreviewLayoutRule layoutRule = this.mFolderIcon.getLayoutRule();
        List previewItems = this.mFolderIcon.getPreviewItems();
        Rect rect = new Rect();
        float descendantRectRelativeToSelf = this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(this.mFolderIcon, rect);
        int scaledRadius = this.mPreviewBackground.getScaledRadius();
        float f = ((float) (scaledRadius * 2)) * descendantRectRelativeToSelf;
        float iconSize = layoutRule.getIconSize() * layoutRule.scaleForItem(0, previewItems.size());
        float iconSize2 = (iconSize / ((float) ((BubbleTextView) previewItems.get(0)).getIconSize())) * descendantRectRelativeToSelf;
        float f2 = this.mIsOpening ? iconSize2 : 1.0f;
        this.mFolder.setScaleX(f2);
        this.mFolder.setScaleY(f2);
        this.mFolder.setPivotX(0.0f);
        this.mFolder.setPivotY(0.0f);
        int i = (int) (iconSize / 2.0f);
        if (Utilities.isRtl(this.mContext.getResources())) {
            i = (int) (((((float) layoutParams.width) * iconSize2) - f) - ((float) i));
        }
        int i2 = i;
        int paddingLeft = (int) (((float) (this.mFolder.getPaddingLeft() + this.mContent.getPaddingLeft())) * iconSize2);
        int paddingTop = (int) (((float) (this.mFolder.getPaddingTop() + this.mContent.getPaddingTop())) * iconSize2);
        float offsetX = (float) ((((rect.left + this.mPreviewBackground.getOffsetX()) - paddingLeft) - i2) - layoutParams.f61x);
        float offsetY = (float) (((rect.top + this.mPreviewBackground.getOffsetY()) - paddingTop) - layoutParams.f62y);
        int attrColor = Themes.getAttrColor(this.mContext, 16843827);
        int alphaComponent = ColorUtils.setAlphaComponent(attrColor, this.mPreviewBackground.getBackgroundAlpha());
        this.mFolderBackground.setColor(this.mIsOpening ? alphaComponent : attrColor);
        float f3 = (float) (paddingLeft + i2);
        float f4 = (float) paddingTop;
        Rect rect2 = new Rect(Math.round(f3 / iconSize2), Math.round(f4 / iconSize2), Math.round((f3 + f) / iconSize2), Math.round((f4 + f) / iconSize2));
        Rect rect3 = new Rect(0, 0, layoutParams.width, layoutParams.height);
        float f5 = (f / iconSize2) / 2.0f;
        float pxFromDp = (float) Utilities.pxFromDp(2.0f, this.mContext.getResources().getDisplayMetrics());
        AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        int i3 = i2;
        float f6 = descendantRectRelativeToSelf;
        PropertyResetListener propertyResetListener = new PropertyResetListener(BubbleTextView.TEXT_ALPHA_PROPERTY, Integer.valueOf(Color.alpha(Themes.getAttrColor(this.mContext, 16842808))));
        for (BubbleTextView bubbleTextView : this.mFolder.getItemsOnPage(this.mFolder.mContent.getCurrentPage())) {
            if (this.mIsOpening) {
                bubbleTextView.setTextVisibility(false);
            }
            ObjectAnimator createTextAlphaAnimator = bubbleTextView.createTextAlphaAnimator(this.mIsOpening);
            createTextAlphaAnimator.addListener(propertyResetListener);
            play(createAnimatorSet, createTextAlphaAnimator);
        }
        play(createAnimatorSet, getAnimator((View) this.mFolder, View.TRANSLATION_X, offsetX, 0.0f));
        play(createAnimatorSet, getAnimator((View) this.mFolder, View.TRANSLATION_Y, offsetY, 0.0f));
        play(createAnimatorSet, getAnimator((View) this.mFolder, (Property) SCALE_PROPERTY, iconSize2, 1.0f));
        play(createAnimatorSet, getAnimator(this.mFolderBackground, "color", alphaComponent, attrColor));
        play(createAnimatorSet, this.mFolderIcon.mFolderName.createTextAlphaAnimator(!this.mIsOpening));
        C07292 r0 = new RoundedRectRevealOutlineProvider(f5, pxFromDp, rect2, rect3) {
            public boolean shouldRemoveElevationDuringAnimation() {
                return true;
            }
        };
        play(createAnimatorSet, r0.createRevealAnimator(this.mFolder, !this.mIsOpening));
        int i4 = this.mDuration / 2;
        play(createAnimatorSet, getAnimator((View) this.mFolder, View.TRANSLATION_Z, -this.mFolder.getElevation(), 0.0f), this.mIsOpening ? (long) i4 : 0, i4);
        createAnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                FolderAnimationManager.this.mFolder.setTranslationX(0.0f);
                FolderAnimationManager.this.mFolder.setTranslationY(0.0f);
                FolderAnimationManager.this.mFolder.setTranslationZ(0.0f);
                FolderAnimationManager.this.mFolder.setScaleX(1.0f);
                FolderAnimationManager.this.mFolder.setScaleY(1.0f);
            }
        });
        Iterator it = createAnimatorSet.getChildAnimations().iterator();
        while (it.hasNext()) {
            ((Animator) it.next()).setInterpolator(this.mFolderInterpolator);
        }
        int radius = scaledRadius - this.mPreviewBackground.getRadius();
        addPreviewItemAnimators(createAnimatorSet, iconSize2 / f6, i3 + radius, radius);
        return createAnimatorSet;
    }

    private void addPreviewItemAnimators(AnimatorSet animatorSet, float f, int i, int i2) {
        List previewItemsOnPage;
        int i3;
        int i4;
        List list;
        AnimatorSet animatorSet2 = animatorSet;
        PreviewLayoutRule layoutRule = this.mFolderIcon.getLayoutRule();
        boolean z = true;
        boolean z2 = this.mFolder.mContent.getCurrentPage() == 0;
        if (z2) {
            previewItemsOnPage = this.mFolderIcon.getPreviewItems();
        } else {
            previewItemsOnPage = this.mFolderIcon.getPreviewItemsOnPage(this.mFolder.mContent.getCurrentPage());
        }
        List list2 = previewItemsOnPage;
        int size = list2.size();
        if (z2) {
            i3 = size;
        } else {
            i3 = FolderIcon.NUM_ITEMS_IN_PREVIEW;
        }
        TimeInterpolator previewItemInterpolator = getPreviewItemInterpolator();
        ShortcutAndWidgetContainer shortcutsAndWidgets = this.mContent.getPageAt(0).getShortcutsAndWidgets();
        int i5 = 0;
        while (i5 < size) {
            final BubbleTextView bubbleTextView = (BubbleTextView) list2.get(i5);
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) bubbleTextView.getLayoutParams();
            layoutParams.isLockedToGrid = z;
            shortcutsAndWidgets.setupLp(bubbleTextView);
            float iconSize = (layoutRule.getIconSize() * layoutRule.scaleForItem(i5, i3)) / ((float) ((BubbleTextView) list2.get(i5)).getIconSize());
            float f2 = iconSize / f;
            float f3 = this.mIsOpening ? f2 : 1.0f;
            bubbleTextView.setScaleX(f3);
            bubbleTextView.setScaleY(f3);
            layoutRule.computePreviewItemDrawingParams(i5, i3, this.mTmpParams);
            PreviewLayoutRule previewLayoutRule = layoutRule;
            final float iconSize2 = (float) (((int) (((this.mTmpParams.transX - ((float) (((int) (((float) (layoutParams.width - bubbleTextView.getIconSize())) * iconSize)) / 2))) + ((float) i)) / f)) - layoutParams.f46x);
            float f4 = (float) (((int) ((this.mTmpParams.transY + ((float) i2)) / f)) - layoutParams.f47y);
            Animator animator = getAnimator((View) bubbleTextView, View.TRANSLATION_X, iconSize2, 0.0f);
            animator.setInterpolator(previewItemInterpolator);
            play(animatorSet2, animator);
            int i6 = i5;
            Animator animator2 = getAnimator((View) bubbleTextView, View.TRANSLATION_Y, f4, 0.0f);
            animator2.setInterpolator(previewItemInterpolator);
            play(animatorSet2, animator2);
            Animator animator3 = getAnimator((View) bubbleTextView, (Property) SCALE_PROPERTY, f2, 1.0f);
            animator3.setInterpolator(previewItemInterpolator);
            play(animatorSet2, animator3);
            float f5 = f2;
            if (this.mFolder.getItemCount() > FolderIcon.NUM_ITEMS_IN_PREVIEW) {
                int i7 = this.mIsOpening ? this.mDelay : this.mDelay * 2;
                if (this.mIsOpening) {
                    long j = (long) i7;
                    animator.setStartDelay(j);
                    animator2.setStartDelay(j);
                    animator3.setStartDelay(j);
                }
                list = list2;
                i4 = size;
                long j2 = (long) i7;
                animator.setDuration(animator.getDuration() - j2);
                animator2.setDuration(animator2.getDuration() - j2);
                animator3.setDuration(animator3.getDuration() - j2);
            } else {
                list = list2;
                i4 = size;
            }
            final float f6 = f5;
            final float f7 = f4;
            int i8 = i6;
            C07314 r0 = new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    if (FolderAnimationManager.this.mIsOpening) {
                        bubbleTextView.setTranslationX(iconSize2);
                        bubbleTextView.setTranslationY(f7);
                        bubbleTextView.setScaleX(f6);
                        bubbleTextView.setScaleY(f6);
                    }
                }

                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    bubbleTextView.setTranslationX(0.0f);
                    bubbleTextView.setTranslationY(0.0f);
                    bubbleTextView.setScaleX(1.0f);
                    bubbleTextView.setScaleY(1.0f);
                }
            };
            animatorSet2.addListener(r0);
            i5 = i8 + 1;
            layoutRule = previewLayoutRule;
            list2 = list;
            size = i4;
            z = true;
        }
    }

    private void play(AnimatorSet animatorSet, Animator animator) {
        play(animatorSet, animator, animator.getStartDelay(), this.mDuration);
    }

    private void play(AnimatorSet animatorSet, Animator animator, long j, int i) {
        animator.setStartDelay(j);
        animator.setDuration((long) i);
        animatorSet.play(animator);
    }

    private TimeInterpolator getPreviewItemInterpolator() {
        if (this.mFolder.getItemCount() <= FolderIcon.NUM_ITEMS_IN_PREVIEW) {
            return this.mFolderInterpolator;
        }
        return this.mIsOpening ? this.mLargeFolderPreviewItemOpenInterpolator : this.mLargeFolderPreviewItemCloseInterpolator;
    }

    private Animator getAnimator(View view, Property property, float f, float f2) {
        if (this.mIsOpening) {
            return ObjectAnimator.ofFloat(view, property, new float[]{f, f2});
        }
        return ObjectAnimator.ofFloat(view, property, new float[]{f2, f});
    }

    private Animator getAnimator(GradientDrawable gradientDrawable, String str, int i, int i2) {
        if (this.mIsOpening) {
            return ObjectAnimator.ofArgb(gradientDrawable, str, new int[]{i, i2});
        }
        return ObjectAnimator.ofArgb(gradientDrawable, str, new int[]{i2, i});
    }
}
