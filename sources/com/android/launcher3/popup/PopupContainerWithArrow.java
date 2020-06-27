package com.android.launcher3.popup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.CornerPathEffect;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout.LayoutParams;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.Utilities;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.accessibility.ShortcutMenuAccessibilityDelegate;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.anim.PropertyResetListener;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.dragndrop.DragController.DragListener;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragOptions.PreDragCondition;
import com.android.launcher3.graphics.TriangleShape;
import com.android.launcher3.notification.NotificationItemView;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.popup.PopupPopulator.Item;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutsItemView;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Themes;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@TargetApi(24)
public class PopupContainerWithArrow extends AbstractFloatingView implements DragSource, DragListener {
    public static final int ROUNDED_BOTTOM_CORNERS = 2;
    public static final int ROUNDED_TOP_CORNERS = 1;
    private LauncherAccessibilityDelegate mAccessibilityDelegate;
    /* access modifiers changed from: private */
    public View mArrow;
    /* access modifiers changed from: private */
    public boolean mDeferContainerRemoval;
    private final Rect mEndRect;
    private int mGravity;
    private final Handler mHandler;
    private PointF mInterceptTouchDown;
    protected boolean mIsAboveIcon;
    private boolean mIsLeftAligned;
    private final boolean mIsRtl;
    protected final Launcher mLauncher;
    /* access modifiers changed from: private */
    public NotificationItemView mNotificationItemView;
    protected Animator mOpenCloseAnimator;
    protected BubbleTextView mOriginalIcon;
    /* access modifiers changed from: private */
    public AnimatorSet mReduceHeightAnimatorSet;
    public ShortcutsItemView mShortcutsItemView;
    private boolean mShouldAnimate;
    /* access modifiers changed from: private */
    public final int mStartDragThreshold;
    private final Rect mStartRect;
    private final Rect mTempRect;

    @Retention(RetentionPolicy.SOURCE)
    public @interface RoundedCornerFlags {
    }

    public float getIntrinsicIconScaleFactor() {
        return 1.0f;
    }

    public int getLogContainerType() {
        return 9;
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 2) != 0;
    }

    public boolean supportsAppInfoDropTarget() {
        return true;
    }

    public boolean supportsDeleteDropTarget() {
        return false;
    }

    public PopupContainerWithArrow(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTempRect = new Rect();
        this.mInterceptTouchDown = new PointF();
        this.mStartRect = new Rect();
        this.mEndRect = new Rect();
        this.mShouldAnimate = false;
        this.mHandler = new Handler();
        this.mLauncher = Launcher.getLauncher(context);
        this.mStartDragThreshold = getResources().getDimensionPixelSize(C0622R.dimen.deep_shortcuts_start_drag_threshold);
        this.mAccessibilityDelegate = new ShortcutMenuAccessibilityDelegate(this.mLauncher);
        this.mIsRtl = Utilities.isRtl(getResources());
    }

    public PopupContainerWithArrow(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PopupContainerWithArrow(Context context) {
        this(context, null, 0);
    }

    public LauncherAccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public static PopupContainerWithArrow showForIcon(BubbleTextView bubbleTextView) {
        Launcher launcher = Launcher.getLauncher(bubbleTextView.getContext());
        if (getOpen(launcher) != null) {
            bubbleTextView.clearFocus();
            return null;
        }
        ItemInfo itemInfo = (ItemInfo) bubbleTextView.getTag();
        if (!DeepShortcutManager.supportsShortcuts(itemInfo)) {
            return null;
        }
        PopupDataProvider popupDataProvider = launcher.getPopupDataProvider();
        List shortcutIdsForItem = popupDataProvider.getShortcutIdsForItem(itemInfo);
        List notificationKeysForItem = popupDataProvider.getNotificationKeysForItem(itemInfo);
        List enabledSystemShortcutsForItem = popupDataProvider.getEnabledSystemShortcutsForItem(itemInfo);
        PopupContainerWithArrow popupContainerWithArrow = (PopupContainerWithArrow) launcher.getLayoutInflater().inflate(C0622R.layout.popup_container, launcher.getDragLayer(), false);
        popupContainerWithArrow.setVisibility(4);
        launcher.getDragLayer().addView(popupContainerWithArrow);
        popupContainerWithArrow.populateAndShow(bubbleTextView, shortcutIdsForItem, notificationKeysForItem, enabledSystemShortcutsForItem);
        return popupContainerWithArrow;
    }

    public void populateAndShow(BubbleTextView bubbleTextView, List<String> list, List<NotificationKeyData> list2, List<SystemShortcut> list3) {
        List list4;
        List systemShortcutViews;
        BubbleTextView bubbleTextView2 = bubbleTextView;
        Resources resources = getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(C0622R.dimen.popup_arrow_width);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(C0622R.dimen.popup_arrow_height);
        int dimensionPixelSize3 = resources.getDimensionPixelSize(C0622R.dimen.popup_arrow_vertical_offset);
        this.mOriginalIcon = bubbleTextView2;
        Item[] itemsToPopulate = PopupPopulator.getItemsToPopulate(list, list2, list3);
        addDummyViews(itemsToPopulate, list2.size());
        measure(0, 0);
        int i = dimensionPixelSize2 + dimensionPixelSize3;
        orientAboutIcon(bubbleTextView2, i);
        boolean z = this.mIsAboveIcon;
        if (z) {
            removeAllViews();
            this.mNotificationItemView = null;
            this.mShortcutsItemView = null;
            addDummyViews(PopupPopulator.reverseItems(itemsToPopulate), list2.size());
            measure(0, 0);
            orientAboutIcon(bubbleTextView2, i);
        }
        ItemInfo itemInfo = (ItemInfo) bubbleTextView.getTag();
        if (this.mShortcutsItemView == null) {
            list4 = Collections.EMPTY_LIST;
        } else {
            list4 = this.mShortcutsItemView.getDeepShortcutViews(z);
        }
        if (this.mShortcutsItemView == null) {
            systemShortcutViews = Collections.EMPTY_LIST;
        } else {
            systemShortcutViews = this.mShortcutsItemView.getSystemShortcutViews(z);
        }
        List list5 = systemShortcutViews;
        if (this.mNotificationItemView != null) {
            updateNotificationHeader();
        }
        int size = list4.size() + list5.size();
        int size2 = list2.size();
        if (size2 == 0) {
            setContentDescription(getContext().getString(C0622R.string.shortcuts_menu_description, new Object[]{Integer.valueOf(size), bubbleTextView.getContentDescription().toString()}));
        } else {
            setContentDescription(getContext().getString(C0622R.string.shortcuts_menu_with_notifications_description, new Object[]{Integer.valueOf(size), Integer.valueOf(size2), bubbleTextView.getContentDescription().toString()}));
        }
        this.mArrow = addArrowView(resources.getDimensionPixelSize(isAlignedWithStart() ? C0622R.dimen.popup_arrow_horizontal_offset_start : C0622R.dimen.popup_arrow_horizontal_offset_end), dimensionPixelSize3, dimensionPixelSize, dimensionPixelSize2);
        this.mArrow.setPivotX((float) (dimensionPixelSize / 2));
        this.mArrow.setPivotY(this.mIsAboveIcon ? 0.0f : (float) dimensionPixelSize2);
        measure(0, 0);
        this.mShouldAnimate = true;
        this.mLauncher.getDragController().addDragListener(this);
        this.mOriginalIcon.forceHideBadge(true);
        new Handler(LauncherModel.getWorkerLooper()).postAtFrontOfQueue(PopupPopulator.createUpdateRunnable(this.mLauncher, itemInfo, new Handler(Looper.getMainLooper()), this, list, list4, list2, this.mNotificationItemView, list3, list5));
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x011a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addDummyViews(com.android.launcher3.popup.PopupPopulator.Item[] r18, int r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            android.content.res.Resources r3 = r17.getResources()
            com.android.launcher3.Launcher r4 = r0.mLauncher
            android.view.LayoutInflater r4 = r4.getLayoutInflater()
            int r5 = r1.length
            r7 = 0
            r8 = 0
            r9 = 3
        L_0x0014:
            if (r8 >= r5) goto L_0x0121
            r11 = r1[r8]
            r12 = 0
            if (r8 <= 0) goto L_0x0020
            int r13 = r8 + -1
            r13 = r1[r13]
            goto L_0x0021
        L_0x0020:
            r13 = r12
        L_0x0021:
            int r14 = r5 + -1
            if (r8 >= r14) goto L_0x0029
            int r12 = r8 + 1
            r12 = r1[r12]
        L_0x0029:
            int r14 = r11.layoutId
            android.view.View r14 = r4.inflate(r14, r0, r7)
            r15 = 1
            if (r13 == 0) goto L_0x003b
            boolean r6 = r11.isShortcut
            boolean r13 = r13.isShortcut
            r6 = r6 ^ r13
            if (r6 == 0) goto L_0x003b
            r6 = 1
            goto L_0x003c
        L_0x003b:
            r6 = 0
        L_0x003c:
            if (r12 == 0) goto L_0x0047
            boolean r13 = r11.isShortcut
            boolean r12 = r12.isShortcut
            r12 = r12 ^ r13
            if (r12 == 0) goto L_0x0047
            r12 = 1
            goto L_0x0048
        L_0x0047:
            r12 = 0
        L_0x0048:
            com.android.launcher3.popup.PopupPopulator$Item r13 = com.android.launcher3.popup.PopupPopulator.Item.NOTIFICATION
            if (r11 != r13) goto L_0x00b5
            r13 = r14
            com.android.launcher3.notification.NotificationItemView r13 = (com.android.launcher3.notification.NotificationItemView) r13
            r0.mNotificationItemView = r13
            if (r2 <= r15) goto L_0x0054
            goto L_0x0055
        L_0x0054:
            r15 = 0
        L_0x0055:
            if (r15 == 0) goto L_0x005a
            int r13 = com.android.launcher3.C0622R.dimen.notification_footer_height
            goto L_0x005c
        L_0x005a:
            int r13 = com.android.launcher3.C0622R.dimen.notification_empty_footer_height
        L_0x005c:
            int r13 = r3.getDimensionPixelSize(r13)
            int r10 = com.android.launcher3.C0622R.C0625id.footer
            android.view.View r10 = r14.findViewById(r10)
            android.view.ViewGroup$LayoutParams r10 = r10.getLayoutParams()
            r10.height = r13
            if (r15 == 0) goto L_0x0079
            com.android.launcher3.notification.NotificationItemView r10 = r0.mNotificationItemView
            int r13 = com.android.launcher3.C0622R.C0625id.divider
            android.view.View r10 = r10.findViewById(r13)
            r10.setVisibility(r7)
        L_0x0079:
            if (r6 == 0) goto L_0x0089
            com.android.launcher3.notification.NotificationItemView r10 = r0.mNotificationItemView
            int r13 = com.android.launcher3.C0622R.C0625id.gutter_top
            android.view.View r10 = r10.findViewById(r13)
            r10.setVisibility(r7)
            r16 = 2
            goto L_0x008b
        L_0x0089:
            r16 = 3
        L_0x008b:
            if (r12 == 0) goto L_0x009a
            r16 = r16 & -3
            com.android.launcher3.notification.NotificationItemView r10 = r0.mNotificationItemView
            int r13 = com.android.launcher3.C0622R.C0625id.gutter_bottom
            android.view.View r10 = r10.findViewById(r13)
            r10.setVisibility(r7)
        L_0x009a:
            r10 = r16
            com.android.launcher3.Launcher r13 = r0.mLauncher
            int r15 = com.android.launcher3.C0622R.attr.popupColorTertiary
            int r13 = com.android.launcher3.util.Themes.getAttrColor(r13, r15)
            com.android.launcher3.notification.NotificationItemView r15 = r0.mNotificationItemView
            r15.setBackgroundWithCorners(r13, r10)
            com.android.launcher3.notification.NotificationItemView r10 = r0.mNotificationItemView
            com.android.launcher3.notification.NotificationMainView r10 = r10.getMainView()
            com.android.launcher3.accessibility.LauncherAccessibilityDelegate r13 = r0.mAccessibilityDelegate
            r10.setAccessibilityDelegate(r13)
            goto L_0x00be
        L_0x00b5:
            com.android.launcher3.popup.PopupPopulator$Item r10 = com.android.launcher3.popup.PopupPopulator.Item.SHORTCUT
            if (r11 != r10) goto L_0x00be
            com.android.launcher3.accessibility.LauncherAccessibilityDelegate r10 = r0.mAccessibilityDelegate
            r14.setAccessibilityDelegate(r10)
        L_0x00be:
            boolean r10 = r11.isShortcut
            if (r10 == 0) goto L_0x011a
            com.android.launcher3.shortcuts.ShortcutsItemView r10 = r0.mShortcutsItemView
            if (r10 != 0) goto L_0x00d9
            int r10 = com.android.launcher3.C0622R.layout.shortcuts_item
            android.view.View r10 = r4.inflate(r10, r0, r7)
            com.android.launcher3.shortcuts.ShortcutsItemView r10 = (com.android.launcher3.shortcuts.ShortcutsItemView) r10
            r0.mShortcutsItemView = r10
            com.android.launcher3.shortcuts.ShortcutsItemView r10 = r0.mShortcutsItemView
            r0.addView(r10)
            if (r6 == 0) goto L_0x00d9
            r9 = r9 & -2
        L_0x00d9:
            com.android.launcher3.popup.PopupPopulator$Item r6 = com.android.launcher3.popup.PopupPopulator.Item.SYSTEM_SHORTCUT_ICON
            if (r11 == r6) goto L_0x010f
            if (r2 <= 0) goto L_0x010f
            android.view.ViewGroup$LayoutParams r6 = r14.getLayoutParams()
            int r6 = r6.height
            android.view.ViewGroup$LayoutParams r10 = r14.getLayoutParams()
            int r13 = com.android.launcher3.C0622R.dimen.bg_popup_item_condensed_height
            int r13 = r3.getDimensionPixelSize(r13)
            r10.height = r13
            boolean r10 = r14 instanceof com.android.launcher3.shortcuts.DeepShortcutView
            if (r10 == 0) goto L_0x010f
            android.view.ViewGroup$LayoutParams r10 = r14.getLayoutParams()
            int r10 = r10.height
            float r10 = (float) r10
            float r6 = (float) r6
            float r10 = r10 / r6
            r6 = r14
            com.android.launcher3.shortcuts.DeepShortcutView r6 = (com.android.launcher3.shortcuts.DeepShortcutView) r6
            android.view.View r13 = r6.getIconView()
            r13.setScaleX(r10)
            android.view.View r6 = r6.getIconView()
            r6.setScaleY(r10)
        L_0x010f:
            com.android.launcher3.shortcuts.ShortcutsItemView r6 = r0.mShortcutsItemView
            r6.addShortcutView(r14, r11)
            if (r12 == 0) goto L_0x011d
            r6 = r9 & -3
            r9 = r6
            goto L_0x011d
        L_0x011a:
            r0.addView(r14)
        L_0x011d:
            int r8 = r8 + 1
            goto L_0x0014
        L_0x0121:
            com.android.launcher3.Launcher r1 = r0.mLauncher
            int r3 = com.android.launcher3.C0622R.attr.popupColorPrimary
            int r1 = com.android.launcher3.util.Themes.getAttrColor(r1, r3)
            com.android.launcher3.shortcuts.ShortcutsItemView r3 = r0.mShortcutsItemView
            r3.setBackgroundWithCorners(r1, r9)
            if (r2 <= 0) goto L_0x0138
            com.android.launcher3.shortcuts.ShortcutsItemView r1 = r0.mShortcutsItemView
            boolean r2 = r0.mIsAboveIcon
            r3 = 2
            r1.hideShortcuts(r2, r3)
        L_0x0138:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.popup.PopupContainerWithArrow.addDummyViews(com.android.launcher3.popup.PopupPopulator$Item[], int):void");
    }

    /* access modifiers changed from: protected */
    public PopupItemView getItemViewAt(int i) {
        if (!this.mIsAboveIcon) {
            i++;
        }
        return (PopupItemView) getChildAt(i);
    }

    /* access modifiers changed from: protected */
    public int getItemCount() {
        return getChildCount() - 1;
    }

    private void animateOpen() {
        setVisibility(0);
        this.mIsOpen = true;
        AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        Resources resources = getResources();
        long integer = (long) resources.getInteger(C0622R.integer.config_popupOpenCloseDuration);
        AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
        int i = 0;
        for (int i2 = 0; i2 < getItemCount(); i2++) {
            i += getItemViewAt(i2).getMeasuredHeight();
        }
        Point computeAnimStartPoint = computeAnimStartPoint(i);
        int paddingTop = this.mIsAboveIcon ? getPaddingTop() : computeAnimStartPoint.y;
        float backgroundRadius = getItemViewAt(0).getBackgroundRadius();
        if (Gravity.isHorizontal(this.mGravity)) {
            computeAnimStartPoint.x = getMeasuredWidth() / 2;
        }
        this.mStartRect.set(computeAnimStartPoint.x, computeAnimStartPoint.y, computeAnimStartPoint.x, computeAnimStartPoint.y);
        this.mEndRect.set(0, paddingTop, getMeasuredWidth(), i + paddingTop);
        ValueAnimator createRevealAnimator = new RoundedRectRevealOutlineProvider(backgroundRadius, backgroundRadius, this.mStartRect, this.mEndRect).createRevealAnimator(this, false);
        createRevealAnimator.setDuration(integer);
        createRevealAnimator.setInterpolator(accelerateDecelerateInterpolator);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, ALPHA, new float[]{0.0f, 1.0f});
        ofFloat.setDuration(integer);
        ofFloat.setInterpolator(accelerateDecelerateInterpolator);
        createAnimatorSet.play(ofFloat);
        this.mArrow.setScaleX(0.0f);
        this.mArrow.setScaleY(0.0f);
        ObjectAnimator duration = createArrowScaleAnim(1.0f).setDuration((long) resources.getInteger(C0622R.integer.config_popupArrowOpenDuration));
        createAnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PopupContainerWithArrow.this.mOpenCloseAnimator = null;
                Utilities.sendCustomAccessibilityEvent(PopupContainerWithArrow.this, 32, PopupContainerWithArrow.this.getContext().getString(C0622R.string.action_deep_shortcut));
            }
        });
        this.mOpenCloseAnimator = createAnimatorSet;
        createAnimatorSet.playSequentially(new Animator[]{createRevealAnimator, duration});
        createAnimatorSet.start();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        enforceContainedWithinScreen(i, i3);
        if (this.mShouldAnimate) {
            this.mShouldAnimate = false;
            if (Gravity.isHorizontal(this.mGravity)) {
                if (Gravity.isVertical(this.mGravity) || this.mLauncher.getDeviceProfile().isVerticalBarLayout()) {
                    this.mArrow.setVisibility(4);
                } else {
                    LayoutParams layoutParams = (LayoutParams) this.mArrow.getLayoutParams();
                    layoutParams.gravity = 1;
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = 0;
                }
            }
            animateOpen();
        }
    }

    private void enforceContainedWithinScreen(int i, int i2) {
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        if (getTranslationX() + ((float) i) < 0.0f || getTranslationX() + ((float) i2) > ((float) dragLayer.getWidth())) {
            this.mGravity |= 1;
        }
        if (Gravity.isHorizontal(this.mGravity)) {
            setX((float) ((dragLayer.getWidth() / 2) - (getMeasuredWidth() / 2)));
        }
        if (Gravity.isVertical(this.mGravity)) {
            setY((float) ((dragLayer.getHeight() / 2) - (getMeasuredHeight() / 2)));
        }
    }

    private Point computeAnimStartPoint(int i) {
        int dimensionPixelSize = getResources().getDimensionPixelSize(this.mIsLeftAligned ^ this.mIsRtl ? C0622R.dimen.popup_arrow_horizontal_center_start : C0622R.dimen.popup_arrow_horizontal_center_end);
        if (!this.mIsLeftAligned) {
            dimensionPixelSize = getMeasuredWidth() - dimensionPixelSize;
        }
        int measuredHeight = ((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom()) - i;
        int paddingTop = getPaddingTop();
        if (!this.mIsAboveIcon) {
            i = measuredHeight;
        }
        return new Point(dimensionPixelSize, paddingTop + i);
    }

    private void orientAboutIcon(BubbleTextView bubbleTextView, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight() + i;
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        dragLayer.getDescendantRectRelativeToSelf(bubbleTextView, this.mTempRect);
        Rect insets = dragLayer.getInsets();
        int paddingLeft = this.mTempRect.left + bubbleTextView.getPaddingLeft();
        int paddingRight = (this.mTempRect.right - measuredWidth) - bubbleTextView.getPaddingRight();
        int i6 = (!((paddingLeft + measuredWidth) + insets.left < dragLayer.getRight() - insets.right) || (this.mIsRtl && (paddingRight > dragLayer.getLeft() + insets.left))) ? paddingRight : paddingLeft;
        this.mIsLeftAligned = i6 == paddingLeft;
        if (this.mIsRtl) {
            i6 -= dragLayer.getWidth() - measuredWidth;
        }
        int width = (int) (((float) ((bubbleTextView.getWidth() - bubbleTextView.getTotalPaddingLeft()) - bubbleTextView.getTotalPaddingRight())) * bubbleTextView.getScaleX());
        Resources resources = getResources();
        if (isAlignedWithStart()) {
            i2 = ((width / 2) - (resources.getDimensionPixelSize(C0622R.dimen.deep_shortcut_icon_size) / 2)) - resources.getDimensionPixelSize(C0622R.dimen.popup_padding_start);
        } else {
            i2 = ((width / 2) - (resources.getDimensionPixelSize(C0622R.dimen.deep_shortcut_drag_handle_size) / 2)) - resources.getDimensionPixelSize(C0622R.dimen.popup_padding_end);
        }
        if (!this.mIsLeftAligned) {
            i2 = -i2;
        }
        int i7 = i6 + i2;
        if (bubbleTextView.getIcon() != null) {
            i3 = bubbleTextView.getIcon().getBounds().height();
        } else {
            i3 = bubbleTextView.getHeight();
        }
        int paddingTop = (this.mTempRect.top + bubbleTextView.getPaddingTop()) - measuredHeight;
        this.mIsAboveIcon = paddingTop > dragLayer.getTop() + insets.top;
        if (!this.mIsAboveIcon) {
            paddingTop = this.mTempRect.top + bubbleTextView.getPaddingTop() + i3;
        }
        if (this.mIsRtl) {
            i4 = i7 + insets.right;
        } else {
            i4 = i7 - insets.left;
        }
        int i8 = paddingTop - insets.top;
        this.mGravity = 0;
        if (measuredHeight + i8 > dragLayer.getBottom() - insets.bottom) {
            this.mGravity = 16;
            int i9 = (paddingLeft + width) - insets.left;
            int i10 = (paddingRight - width) - insets.left;
            if (!this.mIsRtl) {
                if (measuredWidth + i9 < dragLayer.getRight()) {
                    this.mIsLeftAligned = true;
                    i5 = i9;
                    this.mIsAboveIcon = true;
                } else {
                    this.mIsLeftAligned = false;
                }
            } else if (i10 > dragLayer.getLeft()) {
                this.mIsLeftAligned = false;
            } else {
                this.mIsLeftAligned = true;
                i5 = i9;
                this.mIsAboveIcon = true;
            }
            i5 = i10;
            this.mIsAboveIcon = true;
        }
        setX((float) i4);
        setY((float) i8);
    }

    private boolean isAlignedWithStart() {
        return (this.mIsLeftAligned && !this.mIsRtl) || (!this.mIsLeftAligned && this.mIsRtl);
    }

    private View addArrowView(int i, int i2, int i3, int i4) {
        LayoutParams layoutParams = new LayoutParams(i3, i4);
        if (this.mIsLeftAligned) {
            layoutParams.gravity = 3;
            layoutParams.leftMargin = i;
        } else {
            layoutParams.gravity = 5;
            layoutParams.rightMargin = i;
        }
        if (this.mIsAboveIcon) {
            layoutParams.topMargin = i2;
        } else {
            layoutParams.bottomMargin = i2;
        }
        View view = new View(getContext());
        int i5 = 0;
        if (Gravity.isVertical(this.mGravity)) {
            view.setVisibility(4);
        } else {
            ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create((float) i3, (float) i4, !this.mIsAboveIcon));
            Paint paint = shapeDrawable.getPaint();
            PopupItemView popupItemView = (PopupItemView) getChildAt(this.mIsAboveIcon ? getChildCount() - 1 : 0);
            paint.setColor(Themes.getAttrColor(this.mLauncher, C0622R.attr.popupColorPrimary));
            paint.setPathEffect(new CornerPathEffect((float) getResources().getDimensionPixelSize(C0622R.dimen.popup_arrow_corner_radius)));
            view.setBackground(shapeDrawable);
            view.setElevation(getElevation());
        }
        if (this.mIsAboveIcon) {
            i5 = getChildCount();
        }
        addView(view, i5, layoutParams);
        return view;
    }

    public View getExtendedTouchView() {
        return this.mOriginalIcon;
    }

    public PreDragCondition createPreDragCondition() {
        return new PreDragCondition() {
            public boolean shouldStartDrag(double d) {
                return d > ((double) PopupContainerWithArrow.this.mStartDragThreshold);
            }

            public void onPreDragStart(DragObject dragObject) {
                if (PopupContainerWithArrow.this.mIsAboveIcon) {
                    PopupContainerWithArrow.this.mOriginalIcon.setIconVisible(false);
                    PopupContainerWithArrow.this.mOriginalIcon.setVisibility(0);
                    return;
                }
                PopupContainerWithArrow.this.mOriginalIcon.setVisibility(4);
            }

            public void onPreDragEnd(DragObject dragObject, boolean z) {
                PopupContainerWithArrow.this.mOriginalIcon.setIconVisible(true);
                if (z) {
                    PopupContainerWithArrow.this.mOriginalIcon.setVisibility(4);
                    return;
                }
                PopupContainerWithArrow.this.mLauncher.getUserEventDispatcher().logDeepShortcutsOpen(PopupContainerWithArrow.this.mOriginalIcon);
                if (!PopupContainerWithArrow.this.mIsAboveIcon) {
                    PopupContainerWithArrow.this.mOriginalIcon.setVisibility(0);
                    PopupContainerWithArrow.this.mOriginalIcon.setTextVisibility(false);
                }
            }
        };
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (motionEvent.getAction() == 0) {
            this.mInterceptTouchDown.set(motionEvent.getX(), motionEvent.getY());
            return false;
        }
        if (Math.hypot((double) (this.mInterceptTouchDown.x - motionEvent.getX()), (double) (this.mInterceptTouchDown.y - motionEvent.getY())) > ((double) ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
            z = true;
        }
        return z;
    }

    public void updateNotificationHeader(Set<PackageUserKey> set) {
        if (set.contains(PackageUserKey.fromItemInfo((ItemInfo) this.mOriginalIcon.getTag()))) {
            updateNotificationHeader();
        }
    }

    private void updateNotificationHeader() {
        BadgeInfo badgeInfoForItem = this.mLauncher.getPopupDataProvider().getBadgeInfoForItem((ItemInfo) this.mOriginalIcon.getTag());
        if (this.mNotificationItemView != null && badgeInfoForItem != null) {
            this.mNotificationItemView.updateHeader(badgeInfoForItem.getNotificationCount(), this.mOriginalIcon.getBadgePalette());
        }
    }

    public void trimNotifications(Map<PackageUserKey, BadgeInfo> map) {
        int i;
        if (this.mNotificationItemView != null) {
            BadgeInfo badgeInfo = (BadgeInfo) map.get(PackageUserKey.fromItemInfo((ItemInfo) this.mOriginalIcon.getTag()));
            if (badgeInfo == null || badgeInfo.getNotificationKeys().size() == 0) {
                AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
                if (this.mShortcutsItemView != null) {
                    i = this.mShortcutsItemView.getHiddenShortcutsHeight();
                    this.mShortcutsItemView.setBackgroundWithCorners(Themes.getAttrColor(this.mLauncher, C0622R.attr.popupColorPrimary), 3);
                    createAnimatorSet.play(this.mShortcutsItemView.showAllShortcuts(this.mIsAboveIcon));
                } else {
                    i = 0;
                }
                int integer = getResources().getInteger(C0622R.integer.config_removeNotificationViewDuration);
                createAnimatorSet.play(adjustItemHeights(this.mNotificationItemView.getHeightMinusFooter(), i, integer));
                ObjectAnimator duration = ObjectAnimator.ofFloat(this.mNotificationItemView, ALPHA, new float[]{0.0f}).setDuration((long) integer);
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        PopupContainerWithArrow.this.removeView(PopupContainerWithArrow.this.mNotificationItemView);
                        PopupContainerWithArrow.this.mNotificationItemView = null;
                        if (PopupContainerWithArrow.this.getItemCount() == 0) {
                            PopupContainerWithArrow.this.close(false);
                        }
                    }
                });
                createAnimatorSet.play(duration);
                long integer2 = (long) getResources().getInteger(C0622R.integer.config_popupArrowOpenDuration);
                ObjectAnimator duration2 = createArrowScaleAnim(0.0f).setDuration(integer2);
                duration2.setStartDelay(0);
                ObjectAnimator duration3 = createArrowScaleAnim(1.0f).setDuration(integer2);
                duration3.setStartDelay((long) (((double) integer) - (((double) integer2) * 1.5d)));
                createAnimatorSet.playSequentially(new Animator[]{duration2, duration3});
                createAnimatorSet.start();
                return;
            }
            this.mNotificationItemView.trimNotifications(NotificationKeyData.extractKeysOnly(badgeInfo.getNotificationKeys()));
        }
    }

    /* access modifiers changed from: protected */
    public void onWidgetsBound() {
        if (this.mShortcutsItemView != null) {
            this.mShortcutsItemView.enableWidgetsIfExist(this.mOriginalIcon);
        }
    }

    private ObjectAnimator createArrowScaleAnim(float f) {
        return LauncherAnimUtils.ofPropertyValuesHolder(this.mArrow, new PropertyListBuilder().scale(f).build());
    }

    public Animator reduceNotificationViewHeight(int i, int i2) {
        return adjustItemHeights(i, 0, i2);
    }

    public Animator adjustItemHeights(int i, int i2, int i3) {
        if (this.mReduceHeightAnimatorSet != null) {
            this.mReduceHeightAnimatorSet.cancel();
        }
        final int i4 = this.mIsAboveIcon ? i - i2 : -i;
        this.mReduceHeightAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        boolean z = i == this.mNotificationItemView.getHeightMinusFooter();
        this.mReduceHeightAnimatorSet.play(this.mNotificationItemView.animateHeightRemoval(i, this.mIsAboveIcon && z));
        PropertyResetListener propertyResetListener = new PropertyResetListener(TRANSLATION_Y, Float.valueOf(0.0f));
        boolean z2 = false;
        for (int i5 = 0; i5 < getItemCount(); i5++) {
            PopupItemView itemViewAt = getItemViewAt(i5);
            if (z2) {
                itemViewAt.setTranslationY(itemViewAt.getTranslationY() - ((float) i2));
            }
            if (itemViewAt != this.mNotificationItemView || (this.mIsAboveIcon && !z)) {
                ObjectAnimator duration = ObjectAnimator.ofFloat(itemViewAt, TRANSLATION_Y, new float[]{itemViewAt.getTranslationY() + ((float) i4)}).setDuration((long) i3);
                duration.addListener(propertyResetListener);
                this.mReduceHeightAnimatorSet.play(duration);
                if (itemViewAt == this.mShortcutsItemView) {
                    z2 = true;
                }
            }
        }
        if (this.mIsAboveIcon) {
            this.mArrow.setTranslationY(this.mArrow.getTranslationY() - ((float) i2));
        }
        this.mReduceHeightAnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (PopupContainerWithArrow.this.mIsAboveIcon) {
                    PopupContainerWithArrow.this.setTranslationY(PopupContainerWithArrow.this.getTranslationY() + ((float) i4));
                    PopupContainerWithArrow.this.mArrow.setTranslationY(0.0f);
                }
                PopupContainerWithArrow.this.mReduceHeightAnimatorSet = null;
            }
        });
        return this.mReduceHeightAnimatorSet;
    }

    public void onDropCompleted(View view, DragObject dragObject, boolean z, boolean z2) {
        if (!z2) {
            dragObject.dragView.remove();
            this.mLauncher.getDropTargetBar().onDragEnd();
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    PopupContainerWithArrow.this.mLauncher.showWorkspace(true);
                    PopupContainerWithArrow.this.mLauncher.getWorkspace().removeExtraEmptyScreen(true, true);
                }
            }, 500);
        }
    }

    public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
        this.mDeferContainerRemoval = true;
        animateClose();
    }

    public void onDragEnd() {
        if (this.mIsOpen) {
            return;
        }
        if (this.mOpenCloseAnimator != null) {
            this.mDeferContainerRemoval = false;
        } else if (this.mDeferContainerRemoval) {
            closeComplete();
        }
    }

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
        target.itemType = 5;
        target2.containerType = 9;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        this.mShouldAnimate = false;
        if (z) {
            animateClose();
        } else {
            closeComplete();
        }
    }

    /* access modifiers changed from: protected */
    public void animateClose() {
        if (this.mIsOpen) {
            this.mEndRect.setEmpty();
            if (this.mOpenCloseAnimator != null) {
                Outline outline = new Outline();
                getOutlineProvider().getOutline(this, outline);
                if (Utilities.ATLEAST_NOUGAT) {
                    outline.getRect(this.mEndRect);
                }
                this.mOpenCloseAnimator.cancel();
            }
            this.mIsOpen = false;
            AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
            long integer = (long) getResources().getInteger(C0622R.integer.config_popupOpenCloseDuration);
            AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
            int i = 0;
            for (int i2 = 0; i2 < getItemCount(); i2++) {
                i += getItemViewAt(i2).getMeasuredHeight();
            }
            Point computeAnimStartPoint = computeAnimStartPoint(i);
            int paddingTop = this.mIsAboveIcon ? getPaddingTop() : computeAnimStartPoint.y;
            float backgroundRadius = getItemViewAt(0).getBackgroundRadius();
            if (Gravity.isHorizontal(this.mGravity)) {
                computeAnimStartPoint.x = getMeasuredWidth() / 2;
            }
            this.mStartRect.set(computeAnimStartPoint.x, computeAnimStartPoint.y, computeAnimStartPoint.x, computeAnimStartPoint.y);
            if (this.mEndRect.isEmpty()) {
                this.mEndRect.set(0, paddingTop, getMeasuredWidth(), i + paddingTop);
            }
            ValueAnimator createRevealAnimator = new RoundedRectRevealOutlineProvider(backgroundRadius, backgroundRadius, this.mStartRect, this.mEndRect).createRevealAnimator(this, true);
            createRevealAnimator.setDuration(integer);
            createRevealAnimator.setInterpolator(accelerateDecelerateInterpolator);
            createAnimatorSet.play(createRevealAnimator);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, ALPHA, new float[]{0.0f});
            ofFloat.setDuration(integer);
            ofFloat.setInterpolator(accelerateDecelerateInterpolator);
            createAnimatorSet.play(ofFloat);
            ObjectAnimator createTextAlphaAnimator = this.mOriginalIcon.createTextAlphaAnimator(true);
            createTextAlphaAnimator.setDuration(integer);
            createAnimatorSet.play(createTextAlphaAnimator);
            createAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PopupContainerWithArrow.this.mOpenCloseAnimator = null;
                    if (PopupContainerWithArrow.this.mDeferContainerRemoval) {
                        PopupContainerWithArrow.this.setVisibility(4);
                    } else {
                        PopupContainerWithArrow.this.closeComplete();
                    }
                }
            });
            this.mOpenCloseAnimator = createAnimatorSet;
            createAnimatorSet.start();
            this.mOriginalIcon.forceHideBadge(false);
        }
    }

    /* access modifiers changed from: protected */
    public void closeComplete() {
        if (this.mOpenCloseAnimator != null) {
            this.mOpenCloseAnimator.cancel();
            this.mOpenCloseAnimator = null;
        }
        this.mIsOpen = false;
        this.mDeferContainerRemoval = false;
        this.mOriginalIcon.setTextVisibility(this.mOriginalIcon.shouldTextBeVisible());
        this.mOriginalIcon.forceHideBadge(false);
        this.mLauncher.getDragController().removeDragListener(this);
        this.mLauncher.getDragLayer().removeView(this);
    }

    public static PopupContainerWithArrow getOpen(Launcher launcher) {
        return (PopupContainerWithArrow) getOpenView(launcher, 2);
    }
}
