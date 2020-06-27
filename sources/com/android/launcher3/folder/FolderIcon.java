package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.launcher3.Alarm;
import com.android.launcher3.AppInfo;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.CellLayout;
import com.android.launcher3.CheckLongPressHelper;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.FolderInfo.FolderListener;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.SimpleOnStylusPressListener;
import com.android.launcher3.StylusEventHelper;
import com.android.launcher3.Utilities;
import com.android.launcher3.badge.BadgeRenderer;
import com.android.launcher3.badge.FolderBadgeInfo;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.BaseItemDragListener;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import java.util.ArrayList;
import java.util.List;

public class FolderIcon extends FrameLayout implements FolderListener {
    private static final Property<FolderIcon, Float> BADGE_SCALE_PROPERTY = new Property<FolderIcon, Float>(Float.TYPE, "badgeScale") {
        public Float get(FolderIcon folderIcon) {
            return Float.valueOf(folderIcon.mBadgeScale);
        }

        public void set(FolderIcon folderIcon, Float f) {
            folderIcon.mBadgeScale = f.floatValue();
            folderIcon.invalidate();
        }
    };
    static final int DROP_IN_ANIMATION_DURATION = 400;
    public static final int NUM_ITEMS_IN_PREVIEW = (FeatureFlags.LAUNCHER3_LEGACY_FOLDER_ICON ? 3 : 4);
    private static final int ON_OPEN_DELAY = 800;
    public static final boolean SPRING_LOADING_ENABLED = true;
    static boolean sStaticValuesDirty = true;
    boolean mAnimating = false;
    PreviewBackground mBackground = new PreviewBackground();
    private boolean mBackgroundIsVisible = true;
    private FolderBadgeInfo mBadgeInfo = new FolderBadgeInfo();
    private BadgeRenderer mBadgeRenderer;
    /* access modifiers changed from: private */
    public float mBadgeScale;
    Folder mFolder;
    BubbleTextView mFolderName;
    private FolderInfo mInfo;
    Launcher mLauncher;
    private CheckLongPressHelper mLongPressHelper;
    OnAlarmListener mOnOpenListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            FolderIcon.this.mFolder.beginExternalDrag();
            FolderIcon.this.mFolder.animateOpen();
        }
    };
    private Alarm mOpenAlarm = new Alarm();
    /* access modifiers changed from: private */
    public PreviewItemManager mPreviewItemManager;
    PreviewLayoutRule mPreviewLayoutRule;
    FolderIconPreviewVerifier mPreviewVerifier;
    private float mSlop;
    private StylusEventHelper mStylusEventHelper;
    private Rect mTempBounds = new Rect();
    private Point mTempSpaceForBadgeOffset = new Point();
    private PreviewItemDrawingParams mTmpParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0.0f);

    interface PreviewLayoutRule {
        boolean clipToBackground();

        PreviewItemDrawingParams computePreviewItemDrawingParams(int i, int i2, PreviewItemDrawingParams previewItemDrawingParams);

        int getEnterIndex();

        int getExitIndex();

        float getIconSize();

        boolean hasEnterExitIndices();

        void init(int i, float f, boolean z);

        int maxNumItems();

        float scaleForItem(int i, int i2);
    }

    public void prepareAutoUpdate() {
    }

    public FolderIcon(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public FolderIcon(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.mLongPressHelper = new CheckLongPressHelper(this);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        this.mPreviewLayoutRule = FeatureFlags.LAUNCHER3_LEGACY_FOLDER_ICON ? new StackFolderIconLayoutRule() : new ClippedFolderIconLayoutRule();
        this.mSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.mPreviewItemManager = new PreviewItemManager(this);
    }

    public static FolderIcon fromXml(int i, Launcher launcher, ViewGroup viewGroup, FolderInfo folderInfo) {
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        FolderIcon folderIcon = (FolderIcon) LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false);
        folderIcon.setClipToPadding(false);
        folderIcon.mFolderName = (BubbleTextView) folderIcon.findViewById(C0622R.C0625id.folder_icon_name);
        folderIcon.mFolderName.setText(folderInfo.title);
        folderIcon.mFolderName.setCompoundDrawablePadding(0);
        ((LayoutParams) folderIcon.mFolderName.getLayoutParams()).topMargin = deviceProfile.iconSizePx + deviceProfile.iconDrawablePaddingPx;
        folderIcon.setTag(folderInfo);
        folderIcon.setOnClickListener(launcher);
        folderIcon.mInfo = folderInfo;
        folderIcon.mLauncher = launcher;
        folderIcon.mBadgeRenderer = launcher.getDeviceProfile().mBadgeRenderer;
        folderIcon.setContentDescription(launcher.getString(C0622R.string.folder_name_format, new Object[]{folderInfo.title}));
        Folder fromXml = Folder.fromXml(launcher);
        fromXml.setDragController(launcher.getDragController());
        fromXml.setFolderIcon(folderIcon);
        fromXml.bind(folderInfo);
        folderIcon.setFolder(fromXml);
        folderIcon.setAccessibilityDelegate(launcher.getAccessibilityDelegate());
        folderInfo.addListener(folderIcon);
        folderIcon.setOnFocusChangeListener(launcher.mFocusHandler);
        return folderIcon;
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        sStaticValuesDirty = true;
        return super.onSaveInstanceState();
    }

    public Folder getFolder() {
        return this.mFolder;
    }

    private void setFolder(Folder folder) {
        this.mFolder = folder;
        this.mPreviewVerifier = new FolderIconPreviewVerifier(this.mLauncher.getDeviceProfile().inv);
        this.mPreviewItemManager.updateItemDrawingParams(false);
    }

    private boolean willAcceptItem(ItemInfo itemInfo) {
        int i = itemInfo.itemType;
        if ((i == 0 || i == 1 || i == 6) && !this.mFolder.isFull() && itemInfo != this.mInfo && !this.mFolder.isOpen()) {
            return true;
        }
        return false;
    }

    public boolean acceptDrop(ItemInfo itemInfo) {
        return !this.mFolder.isDestroyed() && willAcceptItem(itemInfo);
    }

    public void addItem(ShortcutInfo shortcutInfo) {
        addItem(shortcutInfo, true);
    }

    public void addItem(ShortcutInfo shortcutInfo, boolean z) {
        this.mInfo.add(shortcutInfo, z);
    }

    public void removeItem(ShortcutInfo shortcutInfo, boolean z) {
        this.mInfo.remove(shortcutInfo, z);
    }

    public void onDragEnter(ItemInfo itemInfo) {
        if (!this.mFolder.isDestroyed() && willAcceptItem(itemInfo)) {
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) getLayoutParams();
            this.mBackground.animateToAccept((CellLayout) getParent().getParent(), layoutParams.cellX, layoutParams.cellY);
            this.mOpenAlarm.setOnAlarmListener(this.mOnOpenListener);
            if ((itemInfo instanceof AppInfo) || (itemInfo instanceof ShortcutInfo) || (itemInfo instanceof PendingAddShortcutInfo)) {
                this.mOpenAlarm.setAlarm(800);
            }
        }
    }

    public Drawable prepareCreateAnimation(View view) {
        return this.mPreviewItemManager.prepareCreateAnimation(view);
    }

    public void performCreateAnimation(ShortcutInfo shortcutInfo, View view, ShortcutInfo shortcutInfo2, DragView dragView, Rect rect, float f, Runnable runnable) {
        prepareCreateAnimation(view);
        addItem(shortcutInfo);
        this.mPreviewItemManager.createFirstItemAnimation(false, null).start();
        onDrop(shortcutInfo2, dragView, rect, f, 1, runnable);
    }

    public void performDestroyAnimation(Runnable runnable) {
        this.mPreviewItemManager.createFirstItemAnimation(true, runnable).start();
    }

    public void onDragExit() {
        this.mBackground.animateToRest();
        this.mOpenAlarm.cancelAlarm();
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x00b3  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x012a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onDrop(com.android.launcher3.ShortcutInfo r21, com.android.launcher3.dragndrop.DragView r22, android.graphics.Rect r23, float r24, int r25, java.lang.Runnable r26) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r3 = r22
            r2 = -1
            r1.cellX = r2
            r1.cellY = r2
            if (r3 == 0) goto L_0x0137
            com.android.launcher3.Launcher r2 = r0.mLauncher
            com.android.launcher3.dragndrop.DragLayer r2 = r2.getDragLayer()
            android.graphics.Rect r4 = new android.graphics.Rect
            r4.<init>()
            r2.getViewRectRelativeToSelf(r3, r4)
            if (r23 != 0) goto L_0x005d
            android.graphics.Rect r5 = new android.graphics.Rect
            r5.<init>()
            com.android.launcher3.Launcher r6 = r0.mLauncher
            com.android.launcher3.Workspace r6 = r6.getWorkspace()
            android.view.ViewParent r7 = r20.getParent()
            android.view.ViewParent r7 = r7.getParent()
            com.android.launcher3.CellLayout r7 = (com.android.launcher3.CellLayout) r7
            r6.setFinalTransitionTransform(r7)
            float r7 = r20.getScaleX()
            float r8 = r20.getScaleY()
            r9 = 1065353216(0x3f800000, float:1.0)
            r0.setScaleX(r9)
            r0.setScaleY(r9)
            float r9 = r2.getDescendantRectRelativeToSelf(r0, r5)
            r0.setScaleX(r7)
            r0.setScaleY(r8)
            android.view.ViewParent r7 = r20.getParent()
            android.view.ViewParent r7 = r7.getParent()
            com.android.launcher3.CellLayout r7 = (com.android.launcher3.CellLayout) r7
            r6.resetTransitionTransform(r7)
            goto L_0x0061
        L_0x005d:
            r5 = r23
            r9 = r24
        L_0x0061:
            com.android.launcher3.folder.FolderIcon$PreviewLayoutRule r6 = r0.mPreviewLayoutRule
            int r6 = r6.maxNumItems()
            r15 = 1
            r7 = 0
            r8 = r25
            if (r8 < r6) goto L_0x00ae
            com.android.launcher3.folder.FolderIcon$PreviewLayoutRule r6 = r0.mPreviewLayoutRule
            boolean r6 = r6.hasEnterExitIndices()
            if (r6 == 0) goto L_0x00ae
            java.util.List r6 = r0.getPreviewItemsOnPage(r7)
            r0.addItem(r1, r7)
            java.util.List r10 = r0.getPreviewItemsOnPage(r7)
            boolean r11 = r6.containsAll(r10)
            if (r11 != 0) goto L_0x00ab
            r11 = r8
            r8 = 0
        L_0x0088:
            int r12 = r10.size()
            if (r8 >= r12) goto L_0x00a2
            java.lang.Object r12 = r10.get(r8)
            com.android.launcher3.BubbleTextView r12 = (com.android.launcher3.BubbleTextView) r12
            java.lang.Object r12 = r12.getTag()
            boolean r12 = r12.equals(r1)
            if (r12 == 0) goto L_0x009f
            r11 = r8
        L_0x009f:
            int r8 = r8 + 1
            goto L_0x0088
        L_0x00a2:
            com.android.launcher3.folder.PreviewItemManager r8 = r0.mPreviewItemManager
            r8.onDrop(r6, r10, r1)
            r14 = r11
            r17 = 1
            goto L_0x00b1
        L_0x00ab:
            r0.removeItem(r1, r7)
        L_0x00ae:
            r14 = r8
            r17 = 0
        L_0x00b1:
            if (r17 != 0) goto L_0x00b6
            r20.addItem(r21)
        L_0x00b6:
            r6 = 2
            int[] r8 = new int[r6]
            int r10 = r14 + 1
            float r10 = r0.getLocalCenterForIndex(r14, r10, r8)
            r11 = r8[r7]
            float r11 = (float) r11
            float r11 = r11 * r9
            int r11 = java.lang.Math.round(r11)
            r8[r7] = r11
            r11 = r8[r15]
            float r11 = (float) r11
            float r11 = r11 * r9
            int r11 = java.lang.Math.round(r11)
            r8[r15] = r11
            r7 = r8[r7]
            int r11 = r22.getMeasuredWidth()
            int r11 = r11 / r6
            int r7 = r7 - r11
            r8 = r8[r15]
            int r11 = r22.getMeasuredHeight()
            int r11 = r11 / r6
            int r8 = r8 - r11
            r5.offset(r7, r8)
            com.android.launcher3.folder.FolderIcon$PreviewLayoutRule r6 = r0.mPreviewLayoutRule
            int r6 = r6.maxNumItems()
            if (r14 >= r6) goto L_0x00f3
            r6 = 1056964608(0x3f000000, float:0.5)
            goto L_0x00f4
        L_0x00f3:
            r6 = 0
        L_0x00f4:
            float r10 = r10 * r9
            r7 = 1065353216(0x3f800000, float:1.0)
            r8 = 1065353216(0x3f800000, float:1.0)
            r11 = 400(0x190, float:5.6E-43)
            android.view.animation.DecelerateInterpolator r12 = new android.view.animation.DecelerateInterpolator
            r9 = 1073741824(0x40000000, float:2.0)
            r12.<init>(r9)
            android.view.animation.AccelerateInterpolator r13 = new android.view.animation.AccelerateInterpolator
            r13.<init>(r9)
            r16 = 0
            r18 = 0
            r3 = r22
            r9 = r10
            r19 = r14
            r14 = r26
            r15 = r16
            r16 = r18
            r2.animateView(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)
            com.android.launcher3.folder.Folder r2 = r0.mFolder
            r2.hideItem(r1)
            if (r17 != 0) goto L_0x012a
            com.android.launcher3.folder.PreviewItemManager r2 = r0.mPreviewItemManager
            r11 = r19
            r3 = 1
            r2.hidePreviewItem(r11, r3)
            goto L_0x012c
        L_0x012a:
            r11 = r19
        L_0x012c:
            com.android.launcher3.folder.FolderIcon$3 r2 = new com.android.launcher3.folder.FolderIcon$3
            r2.<init>(r11, r1)
            r3 = 400(0x190, double:1.976E-321)
            r0.postDelayed(r2, r3)
            goto L_0x013a
        L_0x0137:
            r20.addItem(r21)
        L_0x013a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.FolderIcon.onDrop(com.android.launcher3.ShortcutInfo, com.android.launcher3.dragndrop.DragView, android.graphics.Rect, float, int, java.lang.Runnable):void");
    }

    public void onDrop(DragObject dragObject) {
        ShortcutInfo shortcutInfo;
        if (dragObject.dragInfo instanceof AppInfo) {
            shortcutInfo = ((AppInfo) dragObject.dragInfo).makeShortcut();
        } else if (dragObject.dragSource instanceof BaseItemDragListener) {
            shortcutInfo = new ShortcutInfo((ShortcutInfo) dragObject.dragInfo);
        } else {
            shortcutInfo = (ShortcutInfo) dragObject.dragInfo;
        }
        ShortcutInfo shortcutInfo2 = shortcutInfo;
        this.mFolder.notifyDrop();
        onDrop(shortcutInfo2, dragObject.dragView, null, 1.0f, this.mInfo.contents.size(), dragObject.postAnimationRunnable);
    }

    public void setBadgeInfo(FolderBadgeInfo folderBadgeInfo) {
        updateBadgeScale(this.mBadgeInfo.hasBadge(), folderBadgeInfo.hasBadge());
        this.mBadgeInfo = folderBadgeInfo;
    }

    public PreviewLayoutRule getLayoutRule() {
        return this.mPreviewLayoutRule;
    }

    private void updateBadgeScale(boolean z, boolean z2) {
        float f = z2 ? 1.0f : 0.0f;
        if (!(z ^ z2) || !isShown()) {
            this.mBadgeScale = f;
            invalidate();
            return;
        }
        createBadgeScaleAnimator(f).start();
    }

    public Animator createBadgeScaleAnimator(float... fArr) {
        return ObjectAnimator.ofFloat(this, BADGE_SCALE_PROPERTY, fArr);
    }

    public boolean hasBadge() {
        return this.mBadgeInfo != null && this.mBadgeInfo.hasBadge();
    }

    private float getLocalCenterForIndex(int i, int i2, int[] iArr) {
        this.mTmpParams = this.mPreviewItemManager.computePreviewItemDrawingParams(Math.min(this.mPreviewLayoutRule.maxNumItems(), i), i2, this.mTmpParams);
        this.mTmpParams.transX += (float) this.mBackground.basePreviewOffsetX;
        this.mTmpParams.transY += (float) this.mBackground.basePreviewOffsetY;
        float intrinsicIconSize = this.mPreviewItemManager.getIntrinsicIconSize();
        float f = this.mTmpParams.transY + ((this.mTmpParams.scale * intrinsicIconSize) / 2.0f);
        iArr[0] = Math.round(this.mTmpParams.transX + ((this.mTmpParams.scale * intrinsicIconSize) / 2.0f));
        iArr[1] = Math.round(f);
        return this.mTmpParams.scale;
    }

    public void setFolderBackground(PreviewBackground previewBackground) {
        this.mBackground = previewBackground;
        this.mBackground.setInvalidateDelegate(this);
    }

    public void setBackgroundVisible(boolean z) {
        this.mBackgroundIsVisible = z;
        invalidate();
    }

    public PreviewBackground getFolderBackground() {
        return this.mBackground;
    }

    public PreviewItemManager getPreviewItemManager() {
        return this.mPreviewItemManager;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        int i;
        super.dispatchDraw(canvas);
        if (this.mBackgroundIsVisible) {
            this.mPreviewItemManager.recomputePreviewDrawingParams();
            if (!this.mBackground.drawingDelegated()) {
                this.mBackground.drawBackground(canvas);
            }
            if (this.mFolder != null) {
                if (this.mFolder.getItemCount() != 0 || this.mAnimating) {
                    if (canvas.isHardwareAccelerated()) {
                        i = canvas.saveLayer(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), null, 20);
                    } else {
                        i = canvas.save(2);
                        if (this.mPreviewLayoutRule.clipToBackground()) {
                            canvas.clipPath(this.mBackground.getClipPath(), Op.INTERSECT);
                        }
                    }
                    this.mPreviewItemManager.draw(canvas);
                    if (this.mPreviewLayoutRule.clipToBackground() && canvas.isHardwareAccelerated()) {
                        this.mBackground.clipCanvasHardware(canvas);
                    }
                    canvas.restoreToCount(i);
                    if (this.mPreviewLayoutRule.clipToBackground() && !this.mBackground.drawingDelegated()) {
                        this.mBackground.drawBackgroundStroke(canvas);
                    }
                    drawBadge(canvas);
                }
            }
        }
    }

    public void drawBadge(Canvas canvas) {
        if ((this.mBadgeInfo != null && this.mBadgeInfo.hasBadge()) || this.mBadgeScale > 0.0f) {
            int offsetX = this.mBackground.getOffsetX();
            int offsetY = this.mBackground.getOffsetY();
            int i = (int) (((float) this.mBackground.previewSize) * this.mBackground.mScale);
            this.mTempBounds.set(offsetX, offsetY, offsetX + i, i + offsetY);
            float max = Math.max(0.0f, this.mBadgeScale - this.mBackground.getScaleProgress());
            this.mTempSpaceForBadgeOffset.set(getWidth() - this.mTempBounds.right, this.mTempBounds.top);
            Canvas canvas2 = canvas;
            this.mBadgeRenderer.draw(canvas2, IconPalette.getFolderBadgePalette(getResources()), this.mBadgeInfo, this.mTempBounds, max, this.mTempSpaceForBadgeOffset);
        }
    }

    public void setTextVisible(boolean z) {
        if (z) {
            this.mFolderName.setVisibility(0);
        } else {
            this.mFolderName.setVisibility(4);
        }
    }

    public boolean getTextVisible() {
        return this.mFolderName.getVisibility() == 0;
    }

    public List<BubbleTextView> getPreviewItems() {
        return getPreviewItemsOnPage(0);
    }

    public List<BubbleTextView> getPreviewItemsOnPage(int i) {
        this.mPreviewVerifier.setFolderInfo(this.mFolder.getInfo());
        ArrayList arrayList = new ArrayList();
        List itemsOnPage = this.mFolder.getItemsOnPage(i);
        int size = itemsOnPage.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (this.mPreviewVerifier.isItemInPreview(i, i2)) {
                arrayList.add(itemsOnPage.get(i2));
            }
            if (arrayList.size() == NUM_ITEMS_IN_PREVIEW) {
                break;
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(@NonNull Drawable drawable) {
        return this.mPreviewItemManager.verifyDrawable(drawable) || super.verifyDrawable(drawable);
    }

    public void onItemsChanged(boolean z) {
        this.mPreviewItemManager.updateItemDrawingParams(z);
        invalidate();
        requestLayout();
    }

    public void onAdd(ShortcutInfo shortcutInfo, int i) {
        boolean hasBadge = this.mBadgeInfo.hasBadge();
        this.mBadgeInfo.addBadgeInfo(this.mLauncher.getPopupDataProvider().getBadgeInfoForItem(shortcutInfo));
        updateBadgeScale(hasBadge, this.mBadgeInfo.hasBadge());
        invalidate();
        requestLayout();
    }

    public void onRemove(ShortcutInfo shortcutInfo) {
        boolean hasBadge = this.mBadgeInfo.hasBadge();
        this.mBadgeInfo.subtractBadgeInfo(this.mLauncher.getPopupDataProvider().getBadgeInfoForItem(shortcutInfo));
        updateBadgeScale(hasBadge, this.mBadgeInfo.hasBadge());
        invalidate();
        requestLayout();
    }

    public void onTitleChanged(CharSequence charSequence) {
        this.mFolderName.setText(charSequence);
        setContentDescription(getContext().getString(C0622R.string.folder_name_format, new Object[]{charSequence}));
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        if (this.mStylusEventHelper.onMotionEvent(motionEvent)) {
            this.mLongPressHelper.cancelLongPress();
            return true;
        }
        switch (motionEvent.getAction()) {
            case 0:
                this.mLongPressHelper.postCheckForLongPress();
                break;
            case 1:
            case 3:
                this.mLongPressHelper.cancelLongPress();
                break;
            case 2:
                if (!Utilities.pointInView(this, motionEvent.getX(), motionEvent.getY(), this.mSlop)) {
                    this.mLongPressHelper.cancelLongPress();
                    break;
                }
                break;
        }
        return onTouchEvent;
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public void removeListeners() {
        this.mInfo.removeListener(this);
        this.mInfo.removeListener(this.mFolder);
    }

    public void shrinkAndFadeIn(boolean z) {
        final PreviewImageView previewImageView = PreviewImageView.get(getContext());
        previewImageView.removeFromParent();
        copyToPreview(previewImageView);
        clearLeaveBehindIfExists();
        ObjectAnimator ofViewAlphaAndScale = LauncherAnimUtils.ofViewAlphaAndScale(previewImageView, 1.0f, 1.0f, 1.0f);
        ofViewAlphaAndScale.setDuration((long) getResources().getInteger(C0622R.integer.config_folderExpandDuration));
        ofViewAlphaAndScale.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                previewImageView.removeFromParent();
                FolderIcon.this.setVisibility(0);
            }
        });
        ofViewAlphaAndScale.start();
        if (!z) {
            ofViewAlphaAndScale.end();
        }
    }

    public boolean onHotseat() {
        return this.mInfo.container == -101;
    }

    public void clearLeaveBehindIfExists() {
        ((CellLayout.LayoutParams) getLayoutParams()).canReorder = true;
        if (this.mInfo.container == -101) {
            ((CellLayout) getParent().getParent()).clearFolderLeaveBehind();
        }
    }

    public void drawLeaveBehindIfExists() {
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) getLayoutParams();
        layoutParams.canReorder = false;
        if (this.mInfo.container == -101) {
            ((CellLayout) getParent().getParent()).setFolderLeaveBehindCell(layoutParams.cellX, layoutParams.cellY);
        }
    }

    public void growAndFadeOut() {
        drawLeaveBehindIfExists();
        PreviewImageView previewImageView = PreviewImageView.get(getContext());
        copyToPreview(previewImageView);
        setVisibility(4);
        ObjectAnimator ofViewAlphaAndScale = LauncherAnimUtils.ofViewAlphaAndScale(previewImageView, 0.0f, 1.5f, 1.5f);
        ofViewAlphaAndScale.setDuration((long) getResources().getInteger(C0622R.integer.config_folderExpandDuration));
        ofViewAlphaAndScale.start();
    }

    private void copyToPreview(PreviewImageView previewImageView) {
        previewImageView.copy(this);
        if (this.mFolder != null) {
            previewImageView.setPivotX(this.mFolder.getPivotXForIconAnimation());
            previewImageView.setPivotY(this.mFolder.getPivotYForIconAnimation());
            this.mFolder.bringToFront();
        }
    }

    public void onFolderClose(int i) {
        this.mPreviewItemManager.onFolderClose(i);
    }
}
