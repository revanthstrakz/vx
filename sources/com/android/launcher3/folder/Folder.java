package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.Selection;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Alarm;
import com.android.launcher3.AppInfo;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.ExtendedEditText.OnBackKeyListener;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.FolderInfo.FolderListener;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LogDecelerateInterpolator;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.UninstallDropTarget.DropTargetSource;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace.ItemOperator;
import com.android.launcher3.accessibility.AccessibleDragListenerAdapter;
import com.android.launcher3.anim.AnimationLayerSet;
import com.android.launcher3.anim.CircleRevealOutlineProvider;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragController.DragListener;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragLayer.LayoutParams;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.pageindicators.PageIndicatorDots;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Folder extends AbstractFloatingView implements DragSource, OnClickListener, OnLongClickListener, DropTarget, FolderListener, OnEditorActionListener, OnFocusChangeListener, DragListener, DropTargetSource, OnBackKeyListener {
    private static final int FOLDER_NAME_ANIMATION_DURATION = 633;
    private static final float ICON_OVERSCROLL_WIDTH_FACTOR = 0.45f;
    public static final Comparator<ItemInfo> ITEM_POS_COMPARATOR = new Comparator<ItemInfo>() {
        public int compare(ItemInfo itemInfo, ItemInfo itemInfo2) {
            if (itemInfo.rank != itemInfo2.rank) {
                return itemInfo.rank - itemInfo2.rank;
            }
            if (itemInfo.cellY != itemInfo2.cellY) {
                return itemInfo.cellY - itemInfo2.cellY;
            }
            return itemInfo.cellX - itemInfo2.cellX;
        }
    };
    private static final int MIN_CONTENT_DIMEN = 5;
    private static final int ON_EXIT_CLOSE_DELAY = 400;
    private static final int REORDER_DELAY = 250;
    public static final int RESCROLL_DELAY = 900;
    public static final int SCROLL_HINT_DURATION = 500;
    public static final int SCROLL_LEFT = 0;
    public static final int SCROLL_NONE = -1;
    public static final int SCROLL_RIGHT = 1;
    static final int STATE_ANIMATING = 1;
    static final int STATE_NONE = -1;
    static final int STATE_OPEN = 2;
    static final int STATE_SMALL = 0;
    private static final String TAG = "Launcher.Folder";
    private static String sDefaultFolderName;
    private static String sHintText;
    private static final Rect sTempRect = new Rect();
    FolderPagedView mContent;
    /* access modifiers changed from: private */
    public AnimatorSet mCurrentAnimator;
    private View mCurrentDragView;
    int mCurrentScrollDir = -1;
    private boolean mDeferDropAfterUninstall;
    Runnable mDeferredAction;
    private boolean mDeleteFolderOnDropCompleted = false;
    @ExportedProperty(category = "launcher")
    private boolean mDestroyed;
    protected DragController mDragController;
    private boolean mDragInProgress = false;
    int mEmptyCellRank;
    private final int mExpandDuration;
    FolderIcon mFolderIcon;
    float mFolderIconPivotX;
    float mFolderIconPivotY;
    public ExtendedEditText mFolderName;
    /* access modifiers changed from: private */
    public View mFooter;
    private int mFooterHeight;
    public FolderInfo mInfo;
    /* access modifiers changed from: private */
    public boolean mIsEditingName = false;
    private boolean mIsExternalDrag;
    private boolean mItemAddedBackToSelfViaIcon = false;
    final ArrayList<View> mItemsInReadingOrder = new ArrayList<>();
    boolean mItemsInvalidated = false;
    protected final Launcher mLauncher;
    public final int mMaterialExpandDuration;
    private final int mMaterialExpandStagger;
    private final Alarm mOnExitAlarm = new Alarm();
    OnAlarmListener mOnExitAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            Folder.this.completeDragExit();
        }
    };
    private final Alarm mOnScrollHintAlarm = new Alarm();
    /* access modifiers changed from: private */
    public PageIndicatorDots mPageIndicator;
    int mPrevTargetRank;
    @ExportedProperty(category = "launcher")
    private boolean mRearrangeOnClose = false;
    private final Alarm mReorderAlarm = new Alarm();
    OnAlarmListener mReorderAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            Folder.this.mContent.realTimeReorder(Folder.this.mEmptyCellRank, Folder.this.mTargetRank);
            Folder.this.mEmptyCellRank = Folder.this.mTargetRank;
        }
    };
    private int mScrollAreaOffset;
    int mScrollHintDir = -1;
    final Alarm mScrollPauseAlarm = new Alarm();
    @ExportedProperty(category = "launcher", mapping = {@IntToString(from = -1, to = "STATE_NONE"), @IntToString(from = 0, to = "STATE_SMALL"), @IntToString(from = 1, to = "STATE_ANIMATING"), @IntToString(from = 2, to = "STATE_OPEN")})
    int mState = -1;
    private boolean mSuppressFolderDeletion = false;
    int mTargetRank;
    private boolean mUninstallSuccessful;

    private class OnScrollFinishedListener implements OnAlarmListener {
        private final DragObject mDragObject;

        OnScrollFinishedListener(DragObject dragObject) {
            this.mDragObject = dragObject;
        }

        public void onAlarm(Alarm alarm) {
            Folder.this.onDragOver(this.mDragObject, 1);
        }
    }

    private class OnScrollHintListener implements OnAlarmListener {
        private final DragObject mDragObject;

        OnScrollHintListener(DragObject dragObject) {
            this.mDragObject = dragObject;
        }

        public void onAlarm(Alarm alarm) {
            if (Folder.this.mCurrentScrollDir == 0) {
                Folder.this.mContent.scrollLeft();
                Folder.this.mScrollHintDir = -1;
            } else if (Folder.this.mCurrentScrollDir == 1) {
                Folder.this.mContent.scrollRight();
                Folder.this.mScrollHintDir = -1;
            } else {
                return;
            }
            Folder.this.mCurrentScrollDir = -1;
            Folder.this.mScrollPauseAlarm.setOnAlarmListener(new OnScrollFinishedListener(this.mDragObject));
            Folder.this.mScrollPauseAlarm.setAlarm(900);
        }
    }

    private class SuppressInfoChanges implements AutoCloseable {
        SuppressInfoChanges() {
            Folder.this.mInfo.removeListener(Folder.this);
        }

        public void close() {
            Folder.this.mInfo.addListener(Folder.this);
            Folder.this.updateTextViewFocus();
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return true;
    }

    public float getIntrinsicIconScaleFactor() {
        return 1.0f;
    }

    public int getLogContainerType() {
        return 3;
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 1) != 0;
    }

    public void onTitleChanged(CharSequence charSequence) {
    }

    public boolean supportsAppInfoDropTarget() {
        return true;
    }

    public boolean supportsDeleteDropTarget() {
        return true;
    }

    public Folder(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setAlwaysDrawnWithCacheEnabled(false);
        Resources resources = getResources();
        this.mExpandDuration = resources.getInteger(C0622R.integer.config_folderExpandDuration);
        this.mMaterialExpandDuration = resources.getInteger(C0622R.integer.config_materialFolderExpandDuration);
        this.mMaterialExpandStagger = resources.getInteger(C0622R.integer.config_materialFolderExpandStagger);
        if (sDefaultFolderName == null) {
            sDefaultFolderName = resources.getString(C0622R.string.folder_name);
        }
        if (sHintText == null) {
            sHintText = resources.getString(C0622R.string.folder_hint_text);
        }
        this.mLauncher = Launcher.getLauncher(context);
        setFocusableInTouchMode(true);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = (FolderPagedView) findViewById(C0622R.C0625id.folder_content);
        this.mContent.setFolder(this);
        this.mPageIndicator = (PageIndicatorDots) findViewById(C0622R.C0625id.folder_page_indicator);
        this.mFolderName = (ExtendedEditText) findViewById(C0622R.C0625id.folder_name);
        this.mFolderName.setOnBackKeyListener(this);
        this.mFolderName.setOnFocusChangeListener(this);
        if (!Utilities.ATLEAST_MARSHMALLOW) {
            this.mFolderName.setCustomSelectionActionModeCallback(new Callback() {
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    return false;
                }

                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode actionMode) {
                }

                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }
            });
        }
        this.mFolderName.setOnEditorActionListener(this);
        this.mFolderName.setSelectAllOnFocus(true);
        this.mFolderName.setInputType((this.mFolderName.getInputType() & -32769 & -524289) | 8192);
        this.mFolderName.forceDisableSuggestions(true);
        this.mFooter = findViewById(C0622R.C0625id.folder_footer);
        this.mFooter.measure(0, 0);
        this.mFooterHeight = this.mFooter.getMeasuredHeight();
    }

    public void onClick(View view) {
        if (view.getTag() instanceof ShortcutInfo) {
            this.mLauncher.onClick(view);
        }
    }

    public boolean onLongClick(View view) {
        if (!this.mLauncher.isDraggingEnabled()) {
            return true;
        }
        return startDrag(view, new DragOptions());
    }

    public boolean startDrag(View view, DragOptions dragOptions) {
        Object tag = view.getTag();
        if (tag instanceof ShortcutInfo) {
            this.mEmptyCellRank = ((ShortcutInfo) tag).rank;
            this.mCurrentDragView = view;
            this.mDragController.addDragListener(this);
            if (dragOptions.isAccessibleDrag) {
                this.mDragController.addDragListener(new AccessibleDragListenerAdapter(this.mContent, 1) {
                    /* access modifiers changed from: protected */
                    public void enableAccessibleDrag(boolean z) {
                        super.enableAccessibleDrag(z);
                        Folder.this.mFooter.setImportantForAccessibility(z ? 4 : 0);
                    }
                });
            }
            this.mLauncher.getWorkspace().beginDragShared(view, this, dragOptions);
        }
        return true;
    }

    public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
        Throwable th;
        if (dragObject.dragSource == this) {
            this.mContent.removeItem(this.mCurrentDragView);
            if (dragObject.dragInfo instanceof ShortcutInfo) {
                this.mItemsInvalidated = true;
                SuppressInfoChanges suppressInfoChanges = new SuppressInfoChanges();
                try {
                    this.mInfo.remove((ShortcutInfo) dragObject.dragInfo, true);
                    suppressInfoChanges.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            this.mDragInProgress = true;
            this.mItemAddedBackToSelfViaIcon = false;
            return;
        }
        return;
        throw th;
    }

    public void onDragEnd() {
        if (this.mIsExternalDrag && this.mDragInProgress) {
            completeDragExit();
        }
        this.mDragInProgress = false;
        this.mDragController.removeDragListener(this);
    }

    public boolean isEditingName() {
        return this.mIsEditingName;
    }

    public void startEditingFolderName() {
        post(new Runnable() {
            public void run() {
                Folder.this.mFolderName.setHint("");
                Folder.this.mIsEditingName = true;
            }
        });
    }

    public boolean onBackKey() {
        String obj = this.mFolderName.getText().toString();
        this.mInfo.setTitle(obj);
        this.mLauncher.getModelWriter().updateItemInDatabase(this.mInfo);
        this.mFolderName.setHint(sDefaultFolderName.contentEquals(obj) ? sHintText : null);
        Utilities.sendCustomAccessibilityEvent(this, 32, getContext().getString(C0622R.string.folder_renamed, new Object[]{obj}));
        this.mFolderName.clearFocus();
        Selection.setSelection(this.mFolderName.getText(), 0, 0);
        this.mIsEditingName = false;
        return true;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        this.mFolderName.dispatchBackKey();
        return true;
    }

    public ExtendedEditText getActiveTextView() {
        if (isEditingName()) {
            return this.mFolderName;
        }
        return null;
    }

    public FolderIcon getFolderIcon() {
        return this.mFolderIcon;
    }

    public void setDragController(DragController dragController) {
        this.mDragController = dragController;
    }

    public void setFolderIcon(FolderIcon folderIcon) {
        this.mFolderIcon = folderIcon;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        requestFocus();
        super.onAttachedToWindow();
    }

    public View focusSearch(int i) {
        return FocusFinder.getInstance().findNextFocus(this, null, i);
    }

    public FolderInfo getInfo() {
        return this.mInfo;
    }

    /* access modifiers changed from: 0000 */
    public void bind(FolderInfo folderInfo) {
        this.mInfo = folderInfo;
        ArrayList<ShortcutInfo> arrayList = folderInfo.contents;
        Collections.sort(arrayList, ITEM_POS_COMPARATOR);
        Iterator it = this.mContent.bindItems(arrayList).iterator();
        while (it.hasNext()) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) it.next();
            this.mInfo.remove(shortcutInfo, false);
            this.mLauncher.getModelWriter().deleteItemFromDatabase(shortcutInfo);
        }
        if (((LayoutParams) getLayoutParams()) == null) {
            LayoutParams layoutParams = new LayoutParams(0, 0);
            layoutParams.customPosition = true;
            setLayoutParams(layoutParams);
        }
        centerAboutIcon();
        this.mItemsInvalidated = true;
        updateTextViewFocus();
        this.mInfo.addListener(this);
        if (!sDefaultFolderName.contentEquals(this.mInfo.title)) {
            this.mFolderName.setText(this.mInfo.title);
            this.mFolderName.setHint(null);
        } else {
            this.mFolderName.setText("");
            this.mFolderName.setHint(sHintText);
        }
        this.mFolderIcon.post(new Runnable() {
            public void run() {
                if (Folder.this.getItemCount() <= 1) {
                    Folder.this.replaceFolderWithFinalItem();
                }
            }
        });
    }

    @SuppressLint({"InflateParams"})
    static Folder fromXml(Launcher launcher) {
        return (Folder) launcher.getLayoutInflater().inflate(FeatureFlags.LAUNCHER3_DISABLE_ICON_NORMALIZATION ? C0622R.layout.user_folder : C0622R.layout.user_folder_icon_normalized, null);
    }

    private void positionAndSizeAsIcon() {
        if (getParent() instanceof DragLayer) {
            setScaleX(0.8f);
            setScaleY(0.8f);
            setAlpha(0.0f);
            this.mState = 0;
        }
    }

    private void prepareReveal() {
        setScaleX(1.0f);
        setScaleY(1.0f);
        setAlpha(1.0f);
        this.mState = 0;
    }

    private void startAnimation(final AnimatorSet animatorSet) {
        if (this.mCurrentAnimator != null && this.mCurrentAnimator.isRunning()) {
            this.mCurrentAnimator.cancel();
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                Folder.this.mState = 1;
                Folder.this.mCurrentAnimator = animatorSet;
            }

            public void onAnimationEnd(Animator animator) {
                Folder.this.mCurrentAnimator = null;
            }
        });
        animatorSet.start();
    }

    private AnimatorSet getOpeningAnimator() {
        prepareReveal();
        this.mFolderIcon.growAndFadeOut();
        AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        int folderWidth = getFolderWidth();
        int folderHeight = getFolderHeight();
        float pivotX = (((float) (folderWidth / 2)) - getPivotX()) * -0.075f;
        float pivotY = (((float) (folderHeight / 2)) - getPivotY()) * -0.075f;
        setTranslationX(pivotX);
        setTranslationY(pivotY);
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_X, new float[]{pivotX, 0.0f}), PropertyValuesHolder.ofFloat(TRANSLATION_Y, new float[]{pivotY, 0.0f})});
        ofPropertyValuesHolder.setDuration((long) this.mMaterialExpandDuration);
        ofPropertyValuesHolder.setStartDelay((long) this.mMaterialExpandStagger);
        ofPropertyValuesHolder.setInterpolator(new LogDecelerateInterpolator(100, 0));
        ValueAnimator createRevealAnimator = new CircleRevealOutlineProvider((int) getPivotX(), (int) getPivotY(), 0.0f, (float) Math.hypot((double) ((int) Math.max(Math.max(((float) folderWidth) - getPivotX(), 0.0f), getPivotX())), (double) ((int) Math.max(Math.max(((float) folderHeight) - getPivotY(), 0.0f), getPivotY())))).createRevealAnimator(this);
        createRevealAnimator.setDuration((long) this.mMaterialExpandDuration);
        createRevealAnimator.setInterpolator(new LogDecelerateInterpolator(100, 0));
        this.mContent.setAlpha(0.0f);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mContent, "alpha", new float[]{0.0f, 1.0f});
        ofFloat.setDuration((long) this.mMaterialExpandDuration);
        ofFloat.setStartDelay((long) this.mMaterialExpandStagger);
        ofFloat.setInterpolator(new AccelerateInterpolator(1.5f));
        this.mFooter.setAlpha(0.0f);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.mFooter, "alpha", new float[]{0.0f, 1.0f});
        ofFloat2.setDuration((long) this.mMaterialExpandDuration);
        ofFloat2.setStartDelay((long) this.mMaterialExpandStagger);
        ofFloat2.setInterpolator(new AccelerateInterpolator(1.5f));
        createAnimatorSet.play(ofPropertyValuesHolder);
        createAnimatorSet.play(ofFloat);
        createAnimatorSet.play(ofFloat2);
        createAnimatorSet.play(createRevealAnimator);
        AnimationLayerSet animationLayerSet = new AnimationLayerSet();
        animationLayerSet.addView(this.mContent);
        animationLayerSet.addView(this.mFooter);
        createAnimatorSet.addListener(animationLayerSet);
        return createAnimatorSet;
    }

    public void animateOpen() {
        AnimatorSet animatorSet;
        Folder open = getOpen(this.mLauncher);
        if (!(open == null || open == this)) {
            open.close(true);
        }
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        if (getParent() == null) {
            dragLayer.addView(this);
            this.mDragController.addDropTarget(this);
        }
        this.mIsOpen = true;
        this.mContent.completePendingPageChanges();
        if (!this.mDragInProgress) {
            this.mContent.snapToPageImmediately(0);
        }
        this.mDeleteFolderOnDropCompleted = false;
        centerAboutIcon();
        if (FeatureFlags.LAUNCHER3_NEW_FOLDER_ANIMATION) {
            animatorSet = new FolderAnimationManager(this, true).getAnimator();
        } else {
            animatorSet = getOpeningAnimator();
        }
        final C07246 r3 = new Runnable() {
            public void run() {
                Folder.this.mLauncher.getUserEventDispatcher().resetElapsedContainerMillis();
            }
        };
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                if (FeatureFlags.LAUNCHER3_NEW_FOLDER_ANIMATION) {
                    Folder.this.mFolderIcon.setBackgroundVisible(false);
                    Folder.this.mFolderIcon.drawLeaveBehindIfExists();
                } else {
                    Folder.this.mFolderIcon.setVisibility(4);
                }
                Utilities.sendCustomAccessibilityEvent(Folder.this, 32, Folder.this.mContent.getAccessibilityDescription());
            }

            public void onAnimationEnd(Animator animator) {
                Folder.this.mState = 2;
                r3.run();
                Folder.this.mContent.setFocusOnFirstChild();
            }
        });
        if (this.mContent.getPageCount() <= 1 || this.mInfo.hasOption(4)) {
            this.mFolderName.setTranslationX(0.0f);
        } else {
            float desiredWidth = (((float) ((this.mContent.getDesiredWidth() - this.mFooter.getPaddingLeft()) - this.mFooter.getPaddingRight())) - this.mFolderName.getPaint().measureText(this.mFolderName.getText().toString())) / 2.0f;
            ExtendedEditText extendedEditText = this.mFolderName;
            if (this.mContent.mIsRtl) {
                desiredWidth = -desiredWidth;
            }
            extendedEditText.setTranslationX(desiredWidth);
            this.mPageIndicator.prepareEntryAnimation();
            final boolean z = true ^ this.mDragInProgress;
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @SuppressLint({"InlinedApi"})
                public void onAnimationEnd(Animator animator) {
                    Folder.this.mFolderName.animate().setDuration(633).translationX(0.0f).setInterpolator(AnimationUtils.loadInterpolator(Folder.this.mLauncher, AndroidResources.FAST_OUT_SLOW_IN));
                    Folder.this.mPageIndicator.playEntryAnimation();
                    if (z) {
                        Folder.this.mInfo.setOption(4, true, Folder.this.mLauncher.getModelWriter());
                    }
                }
            });
        }
        this.mPageIndicator.stopAllAnimations();
        startAnimation(animatorSet);
        if (this.mDragController.isDragging()) {
            this.mDragController.forceTouchMove();
        }
        this.mContent.verifyVisibleHighResIcons(this.mContent.getNextPage());
        sendAccessibilityEvent(32);
        dragLayer.sendAccessibilityEvent(2048);
    }

    public void beginExternalDrag() {
        this.mEmptyCellRank = this.mContent.allocateRankForNewItem();
        this.mIsExternalDrag = true;
        this.mDragInProgress = true;
        this.mDragController.addDragListener(this);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        this.mIsOpen = false;
        if (isEditingName()) {
            this.mFolderName.dispatchBackKey();
        }
        if (this.mFolderIcon != null) {
            if (FeatureFlags.LAUNCHER3_NEW_FOLDER_ANIMATION) {
                this.mFolderIcon.clearLeaveBehindIfExists();
            } else {
                this.mFolderIcon.shrinkAndFadeIn(z);
            }
        }
        if (getParent() instanceof DragLayer) {
            DragLayer dragLayer = (DragLayer) getParent();
            if (z) {
                animateClosed();
            } else {
                closeComplete(false);
            }
            dragLayer.sendAccessibilityEvent(32);
        }
    }

    private AnimatorSet getClosingAnimator() {
        AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        createAnimatorSet.play(LauncherAnimUtils.ofViewAlphaAndScale(this, 0.0f, 0.9f, 0.9f));
        AnimationLayerSet animationLayerSet = new AnimationLayerSet();
        animationLayerSet.addView(this);
        createAnimatorSet.addListener(animationLayerSet);
        createAnimatorSet.setDuration((long) this.mExpandDuration);
        return createAnimatorSet;
    }

    private void animateClosed() {
        AnimatorSet animatorSet;
        if (FeatureFlags.LAUNCHER3_NEW_FOLDER_ANIMATION) {
            animatorSet = new FolderAnimationManager(this, false).getAnimator();
        } else {
            animatorSet = getClosingAnimator();
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Folder.this.closeComplete(true);
            }

            public void onAnimationStart(Animator animator) {
                Utilities.sendCustomAccessibilityEvent(Folder.this, 32, Folder.this.getContext().getString(C0622R.string.folder_closed));
            }
        });
        startAnimation(animatorSet);
    }

    /* access modifiers changed from: private */
    public void closeComplete(boolean z) {
        DragLayer dragLayer = (DragLayer) getParent();
        if (dragLayer != null) {
            dragLayer.removeView(this);
        }
        this.mDragController.removeDropTarget(this);
        clearFocus();
        if (this.mFolderIcon != null) {
            this.mFolderIcon.setVisibility(0);
            if (FeatureFlags.LAUNCHER3_NEW_FOLDER_ANIMATION) {
                this.mFolderIcon.setBackgroundVisible(true);
                this.mFolderIcon.mFolderName.setTextVisibility(true);
            }
            if (z) {
                if (FeatureFlags.LAUNCHER3_NEW_FOLDER_ANIMATION) {
                    this.mFolderIcon.mBackground.fadeInBackgroundShadow();
                    this.mFolderIcon.mBackground.animateBackgroundStroke();
                    this.mFolderIcon.onFolderClose(this.mContent.getCurrentPage());
                }
                if (this.mFolderIcon.hasBadge()) {
                    this.mFolderIcon.createBadgeScaleAnimator(0.0f, 1.0f).start();
                }
                this.mFolderIcon.requestFocus();
            }
        }
        if (this.mRearrangeOnClose || this.mFolderIcon.onHotseat()) {
            rearrangeChildren();
            this.mRearrangeOnClose = false;
        }
        if (getItemCount() <= 1) {
            if (!this.mDragInProgress && !this.mSuppressFolderDeletion) {
                replaceFolderWithFinalItem();
            } else if (this.mDragInProgress) {
                this.mDeleteFolderOnDropCompleted = true;
            }
        }
        this.mSuppressFolderDeletion = false;
        clearDragInfo();
        this.mState = 0;
        this.mContent.setCurrentPage(0);
    }

    public boolean acceptDrop(DragObject dragObject) {
        int i = dragObject.dragInfo.itemType;
        if ((i == 0 || i == 1 || i == 6) && !isFull()) {
            return true;
        }
        return false;
    }

    public void onDragEnter(DragObject dragObject) {
        this.mPrevTargetRank = -1;
        this.mOnExitAlarm.cancelAlarm();
        this.mScrollAreaOffset = dragObject.dragView.getDragRegionWidth() - dragObject.xOffset;
    }

    public boolean isLayoutRtl() {
        return getLayoutDirection() == 1;
    }

    public void onDragOver(DragObject dragObject) {
        onDragOver(dragObject, 250);
    }

    private int getTargetRank(DragObject dragObject, float[] fArr) {
        float[] visualCenter = dragObject.getVisualCenter(fArr);
        return this.mContent.findNearestArea(((int) visualCenter[0]) - getPaddingLeft(), ((int) visualCenter[1]) - getPaddingTop());
    }

    /* access modifiers changed from: 0000 */
    public void onDragOver(DragObject dragObject, int i) {
        if (!this.mScrollPauseAlarm.alarmPending()) {
            float[] fArr = new float[2];
            this.mTargetRank = getTargetRank(dragObject, fArr);
            if (this.mTargetRank != this.mPrevTargetRank) {
                this.mReorderAlarm.cancelAlarm();
                this.mReorderAlarm.setOnAlarmListener(this.mReorderAlarmListener);
                this.mReorderAlarm.setAlarm(250);
                this.mPrevTargetRank = this.mTargetRank;
                if (dragObject.stateAnnouncer != null) {
                    dragObject.stateAnnouncer.announce(getContext().getString(C0622R.string.move_to_position, new Object[]{Integer.valueOf(this.mTargetRank + 1)}));
                }
            }
            float f = fArr[0];
            int nextPage = this.mContent.getNextPage();
            float cellWidth = ((float) this.mContent.getCurrentCellLayout().getCellWidth()) * ICON_OVERSCROLL_WIDTH_FACTOR;
            boolean z = f < cellWidth;
            boolean z2 = f > ((float) getWidth()) - cellWidth;
            if (nextPage > 0 && (!this.mContent.mIsRtl ? z : z2)) {
                showScrollHint(0, dragObject);
            } else if (nextPage >= this.mContent.getPageCount() - 1 || (!this.mContent.mIsRtl ? !z2 : !z)) {
                this.mOnScrollHintAlarm.cancelAlarm();
                if (this.mScrollHintDir != -1) {
                    this.mContent.clearScrollHint();
                    this.mScrollHintDir = -1;
                }
            } else {
                showScrollHint(1, dragObject);
            }
        }
    }

    private void showScrollHint(int i, DragObject dragObject) {
        if (this.mScrollHintDir != i) {
            this.mContent.showScrollHint(i);
            this.mScrollHintDir = i;
        }
        if (!this.mOnScrollHintAlarm.alarmPending() || this.mCurrentScrollDir != i) {
            this.mCurrentScrollDir = i;
            this.mOnScrollHintAlarm.cancelAlarm();
            this.mOnScrollHintAlarm.setOnAlarmListener(new OnScrollHintListener(dragObject));
            this.mOnScrollHintAlarm.setAlarm(500);
            this.mReorderAlarm.cancelAlarm();
            this.mTargetRank = this.mEmptyCellRank;
        }
    }

    public void completeDragExit() {
        if (this.mIsOpen) {
            close(true);
            this.mRearrangeOnClose = true;
        } else if (this.mState == 1) {
            this.mRearrangeOnClose = true;
        } else {
            rearrangeChildren();
            clearDragInfo();
        }
    }

    private void clearDragInfo() {
        this.mCurrentDragView = null;
        this.mIsExternalDrag = false;
    }

    public void onDragExit(DragObject dragObject) {
        if (!dragObject.dragComplete) {
            this.mOnExitAlarm.setOnAlarmListener(this.mOnExitAlarmListener);
            this.mOnExitAlarm.setAlarm(400);
        }
        this.mReorderAlarm.cancelAlarm();
        this.mOnScrollHintAlarm.cancelAlarm();
        this.mScrollPauseAlarm.cancelAlarm();
        if (this.mScrollHintDir != -1) {
            this.mContent.clearScrollHint();
            this.mScrollHintDir = -1;
        }
    }

    public void prepareAccessibilityDrop() {
        if (this.mReorderAlarm.alarmPending()) {
            this.mReorderAlarm.cancelAlarm();
            this.mReorderAlarmListener.onAlarm(this.mReorderAlarm);
        }
    }

    public void onDropCompleted(View view, DragObject dragObject, boolean z, boolean z2) {
        View view2;
        Throwable th;
        if (this.mDeferDropAfterUninstall) {
            Log.d(TAG, "Deferred handling drop because waiting for uninstall.");
            final View view3 = view;
            final DragObject dragObject2 = dragObject;
            final boolean z3 = z;
            final boolean z4 = z2;
            C071312 r2 = new Runnable() {
                public void run() {
                    Folder.this.onDropCompleted(view3, dragObject2, z3, z4);
                    Folder.this.mDeferredAction = null;
                }
            };
            this.mDeferredAction = r2;
            return;
        }
        boolean z5 = z2 && (!(this.mDeferredAction != null) || this.mUninstallSuccessful);
        if (!z5) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) dragObject.dragInfo;
            if (this.mCurrentDragView == null || this.mCurrentDragView.getTag() != shortcutInfo) {
                view2 = this.mContent.createNewView(shortcutInfo);
            } else {
                view2 = this.mCurrentDragView;
            }
            ArrayList itemsInReadingOrder = getItemsInReadingOrder();
            itemsInReadingOrder.add(shortcutInfo.rank, view2);
            this.mContent.arrangeChildren(itemsInReadingOrder, itemsInReadingOrder.size());
            this.mItemsInvalidated = true;
            SuppressInfoChanges suppressInfoChanges = new SuppressInfoChanges();
            try {
                this.mFolderIcon.onDrop(dragObject);
                suppressInfoChanges.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
        } else if (this.mDeleteFolderOnDropCompleted && !this.mItemAddedBackToSelfViaIcon && view != this) {
            replaceFolderWithFinalItem();
        }
        if (view != this) {
            if (this.mOnExitAlarm.alarmPending()) {
                this.mOnExitAlarm.cancelAlarm();
                if (!z5) {
                    this.mSuppressFolderDeletion = true;
                }
                this.mScrollPauseAlarm.cancelAlarm();
            }
            completeDragExit();
        }
        this.mDeleteFolderOnDropCompleted = false;
        this.mDragInProgress = false;
        this.mItemAddedBackToSelfViaIcon = false;
        this.mCurrentDragView = null;
        updateItemLocationsInDatabaseBatch();
        if (getItemCount() <= this.mContent.itemsPerPage()) {
            this.mInfo.setOption(4, false, this.mLauncher.getModelWriter());
        }
        if (!z) {
            this.mLauncher.exitSpringLoadedDragModeDelayed(z5, 500, null);
        }
        return;
        throw th;
    }

    public void deferCompleteDropAfterUninstallActivity() {
        this.mDeferDropAfterUninstall = true;
    }

    public void onDragObjectRemoved(boolean z) {
        this.mDeferDropAfterUninstall = false;
        this.mUninstallSuccessful = z;
        if (this.mDeferredAction != null) {
            this.mDeferredAction.run();
        }
    }

    private void updateItemLocationsInDatabaseBatch() {
        ArrayList itemsInReadingOrder = getItemsInReadingOrder();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < itemsInReadingOrder.size(); i++) {
            ItemInfo itemInfo = (ItemInfo) ((View) itemsInReadingOrder.get(i)).getTag();
            itemInfo.rank = i;
            arrayList.add(itemInfo);
        }
        this.mLauncher.getModelWriter().moveItemsInDatabase(arrayList, this.mInfo.f52id, 0);
    }

    public void notifyDrop() {
        if (this.mDragInProgress) {
            this.mItemAddedBackToSelfViaIcon = true;
        }
    }

    public boolean isDropEnabled() {
        boolean z = true;
        if (!FeatureFlags.LAUNCHER3_NEW_FOLDER_ANIMATION) {
            return true;
        }
        if (this.mState == 1) {
            z = false;
        }
        return z;
    }

    public boolean isFull() {
        return this.mContent.isFull();
    }

    private void centerAboutIcon() {
        int i;
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        DragLayer dragLayer = (DragLayer) this.mLauncher.findViewById(C0622R.C0625id.drag_layer);
        int folderWidth = getFolderWidth();
        int folderHeight = getFolderHeight();
        dragLayer.getDescendantRectRelativeToSelf(this.mFolderIcon, sTempRect);
        int i2 = folderWidth / 2;
        int centerX = sTempRect.centerX() - i2;
        int i3 = folderHeight / 2;
        int centerY = sTempRect.centerY() - i3;
        this.mLauncher.getWorkspace().getPageAreaRelativeToDragLayer(sTempRect);
        int min = Math.min(Math.max(sTempRect.left, centerX), sTempRect.right - folderWidth);
        int min2 = Math.min(Math.max(sTempRect.top, centerY), sTempRect.bottom - folderHeight);
        int paddingLeft = this.mLauncher.getWorkspace().getPaddingLeft() + getPaddingLeft();
        if (deviceProfile.isPhone && deviceProfile.availableWidthPx - folderWidth < paddingLeft * 4) {
            min = (deviceProfile.availableWidthPx - folderWidth) / 2;
        } else if (folderWidth >= sTempRect.width()) {
            min = sTempRect.left + ((sTempRect.width() - folderWidth) / 2);
        }
        if (folderHeight >= sTempRect.height()) {
            i = sTempRect.top + ((sTempRect.height() - folderHeight) / 2);
        } else {
            Rect absoluteOpenFolderBounds = deviceProfile.getAbsoluteOpenFolderBounds();
            min = Math.max(absoluteOpenFolderBounds.left, Math.min(min, absoluteOpenFolderBounds.right - folderWidth));
            i = Math.max(absoluteOpenFolderBounds.top, Math.min(min2, absoluteOpenFolderBounds.bottom - folderHeight));
        }
        int i4 = i3 + (centerY - i);
        float f = (float) (i2 + (centerX - min));
        setPivotX(f);
        float f2 = (float) i4;
        setPivotY(f2);
        this.mFolderIconPivotX = (float) ((int) (((float) this.mFolderIcon.getMeasuredWidth()) * ((f * 1.0f) / ((float) folderWidth))));
        this.mFolderIconPivotY = (float) ((int) (((float) this.mFolderIcon.getMeasuredHeight()) * ((f2 * 1.0f) / ((float) folderHeight))));
        layoutParams.width = folderWidth;
        layoutParams.height = folderHeight;
        layoutParams.f61x = min;
        layoutParams.f62y = i;
    }

    public float getPivotXForIconAnimation() {
        return this.mFolderIconPivotX;
    }

    public float getPivotYForIconAnimation() {
        return this.mFolderIconPivotY;
    }

    private int getContentAreaHeight() {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        return Math.max(Math.min((deviceProfile.availableHeightPx - deviceProfile.getTotalWorkspacePadding().y) - this.mFooterHeight, this.mContent.getDesiredHeight()), 5);
    }

    private int getContentAreaWidth() {
        return Math.max(this.mContent.getDesiredWidth(), 5);
    }

    private int getFolderWidth() {
        return getPaddingLeft() + getPaddingRight() + this.mContent.getDesiredWidth();
    }

    private int getFolderHeight() {
        return getFolderHeight(getContentAreaHeight());
    }

    private int getFolderHeight(int i) {
        return getPaddingTop() + getPaddingBottom() + i + this.mFooterHeight;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int contentAreaWidth = getContentAreaWidth();
        int contentAreaHeight = getContentAreaHeight();
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(contentAreaWidth, 1073741824);
        int makeMeasureSpec2 = MeasureSpec.makeMeasureSpec(contentAreaHeight, 1073741824);
        this.mContent.setFixedSize(contentAreaWidth, contentAreaHeight);
        this.mContent.measure(makeMeasureSpec, makeMeasureSpec2);
        if (this.mContent.getChildCount() > 0) {
            int cellWidth = (this.mContent.getPageAt(0).getCellWidth() - this.mLauncher.getDeviceProfile().iconSizePx) / 2;
            this.mFooter.setPadding(this.mContent.getPaddingLeft() + cellWidth, this.mFooter.getPaddingTop(), this.mContent.getPaddingRight() + cellWidth, this.mFooter.getPaddingBottom());
        }
        this.mFooter.measure(makeMeasureSpec, MeasureSpec.makeMeasureSpec(this.mFooterHeight, 1073741824));
        setMeasuredDimension(getPaddingLeft() + getPaddingRight() + contentAreaWidth, getFolderHeight(contentAreaHeight));
    }

    public void rearrangeChildren() {
        rearrangeChildren(-1);
    }

    public void rearrangeChildren(int i) {
        ArrayList itemsInReadingOrder = getItemsInReadingOrder();
        this.mContent.arrangeChildren(itemsInReadingOrder, Math.max(i, itemsInReadingOrder.size()));
        this.mItemsInvalidated = true;
    }

    public int getItemCount() {
        return this.mContent.getItemCount();
    }

    /* access modifiers changed from: 0000 */
    public void replaceFolderWithFinalItem() {
        C071413 r0 = new Runnable() {
            public void run() {
                int size = Folder.this.mInfo.contents.size();
                if (size <= 1) {
                    View view = null;
                    if (size == 1) {
                        ShortcutInfo shortcutInfo = (ShortcutInfo) Folder.this.mInfo.contents.remove(0);
                        view = Folder.this.mLauncher.createShortcut(Folder.this.mLauncher.getCellLayout(Folder.this.mInfo.container, Folder.this.mInfo.screenId), shortcutInfo);
                        Folder.this.mLauncher.getModelWriter().addOrMoveItemInDatabase(shortcutInfo, Folder.this.mInfo.container, Folder.this.mInfo.screenId, Folder.this.mInfo.cellX, Folder.this.mInfo.cellY);
                    }
                    Folder.this.mLauncher.removeItem(Folder.this.mFolderIcon, Folder.this.mInfo, true);
                    if (Folder.this.mFolderIcon instanceof DropTarget) {
                        Folder.this.mDragController.removeDropTarget((DropTarget) Folder.this.mFolderIcon);
                    }
                    if (view != null) {
                        Folder.this.mLauncher.getWorkspace().addInScreenFromBind(view, Folder.this.mInfo);
                        view.requestFocus();
                    }
                }
            }
        };
        if (this.mContent.getLastItem() != null) {
            this.mFolderIcon.performDestroyAnimation(r0);
        } else {
            r0.run();
        }
        this.mDestroyed = true;
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public void updateTextViewFocus() {
        View firstItem = this.mContent.getFirstItem();
        final View lastItem = this.mContent.getLastItem();
        if (firstItem != null && lastItem != null) {
            this.mFolderName.setNextFocusDownId(lastItem.getId());
            this.mFolderName.setNextFocusRightId(lastItem.getId());
            this.mFolderName.setNextFocusLeftId(lastItem.getId());
            this.mFolderName.setNextFocusUpId(lastItem.getId());
            this.mFolderName.setNextFocusForwardId(firstItem.getId());
            setNextFocusDownId(firstItem.getId());
            setNextFocusRightId(firstItem.getId());
            setNextFocusLeftId(firstItem.getId());
            setNextFocusUpId(firstItem.getId());
            setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    boolean z = true;
                    if (i != 61 || !keyEvent.hasModifiers(1)) {
                        z = false;
                    }
                    if (!z || !Folder.this.isFocused()) {
                        return false;
                    }
                    return lastItem.requestFocus();
                }
            });
        }
    }

    public void onDrop(DragObject dragObject) {
        View view;
        Throwable th;
        Runnable r0 = (dragObject.dragSource == this.mLauncher.getWorkspace() || (dragObject.dragSource instanceof Folder)) ? null : new Runnable() {
            public void run() {
                Folder.this.mLauncher.exitSpringLoadedDragModeDelayed(true, 500, null);
            }
        };
        if (!this.mContent.rankOnCurrentPage(this.mEmptyCellRank)) {
            this.mTargetRank = getTargetRank(dragObject, null);
            this.mReorderAlarmListener.onAlarm(this.mReorderAlarm);
            this.mOnScrollHintAlarm.cancelAlarm();
            this.mScrollPauseAlarm.cancelAlarm();
        }
        this.mContent.completePendingPageChanges();
        PendingAddShortcutInfo pendingAddShortcutInfo = dragObject.dragInfo instanceof PendingAddShortcutInfo ? (PendingAddShortcutInfo) dragObject.dragInfo : null;
        ShortcutInfo createShortcutInfo = pendingAddShortcutInfo != null ? pendingAddShortcutInfo.activityInfo.createShortcutInfo() : null;
        if (pendingAddShortcutInfo == null || createShortcutInfo != null) {
            if (createShortcutInfo == null) {
                if (dragObject.dragInfo instanceof AppInfo) {
                    createShortcutInfo = ((AppInfo) dragObject.dragInfo).makeShortcut();
                } else {
                    createShortcutInfo = (ShortcutInfo) dragObject.dragInfo;
                }
            }
            if (this.mIsExternalDrag) {
                view = this.mContent.createAndAddViewForRank(createShortcutInfo, this.mEmptyCellRank);
                this.mLauncher.getModelWriter().addOrMoveItemInDatabase(createShortcutInfo, this.mInfo.f52id, 0, createShortcutInfo.cellX, createShortcutInfo.cellY);
                if (dragObject.dragSource != this) {
                    updateItemLocationsInDatabaseBatch();
                }
                this.mIsExternalDrag = false;
            } else {
                view = this.mCurrentDragView;
                this.mContent.addViewForRank(view, createShortcutInfo, this.mEmptyCellRank);
            }
            if (dragObject.dragView.hasDrawn()) {
                float scaleX = getScaleX();
                float scaleY = getScaleY();
                setScaleX(1.0f);
                setScaleY(1.0f);
                this.mLauncher.getDragLayer().animateViewIntoPosition(dragObject.dragView, view, r0, null);
                setScaleX(scaleX);
                setScaleY(scaleY);
            } else {
                dragObject.deferDragViewCleanupPostAnimation = false;
                view.setVisibility(0);
            }
            this.mItemsInvalidated = true;
            rearrangeChildren();
            SuppressInfoChanges suppressInfoChanges = new SuppressInfoChanges();
            try {
                this.mInfo.add(createShortcutInfo, false);
                suppressInfoChanges.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
        } else {
            pendingAddShortcutInfo.container = this.mInfo.f52id;
            pendingAddShortcutInfo.rank = this.mEmptyCellRank;
            this.mLauncher.addPendingItem(pendingAddShortcutInfo, pendingAddShortcutInfo.container, pendingAddShortcutInfo.screenId, null, pendingAddShortcutInfo.spanX, pendingAddShortcutInfo.spanY);
            dragObject.deferDragViewCleanupPostAnimation = false;
            this.mRearrangeOnClose = true;
        }
        this.mDragInProgress = false;
        if (this.mContent.getPageCount() > 1) {
            this.mInfo.setOption(4, true, this.mLauncher.getModelWriter());
        }
        if (dragObject.stateAnnouncer != null) {
            dragObject.stateAnnouncer.completeAction(C0622R.string.item_moved);
            return;
        }
        return;
        throw th;
    }

    public void hideItem(ShortcutInfo shortcutInfo) {
        getViewForInfo(shortcutInfo).setVisibility(4);
    }

    public void showItem(ShortcutInfo shortcutInfo) {
        getViewForInfo(shortcutInfo).setVisibility(0);
    }

    public void onAdd(ShortcutInfo shortcutInfo, int i) {
        View createAndAddViewForRank = this.mContent.createAndAddViewForRank(shortcutInfo, i);
        this.mLauncher.getModelWriter().addOrMoveItemInDatabase(shortcutInfo, this.mInfo.f52id, 0, shortcutInfo.cellX, shortcutInfo.cellY);
        ArrayList arrayList = new ArrayList(getItemsInReadingOrder());
        arrayList.add(i, createAndAddViewForRank);
        this.mContent.arrangeChildren(arrayList, arrayList.size());
        this.mItemsInvalidated = true;
    }

    public void onRemove(ShortcutInfo shortcutInfo) {
        this.mItemsInvalidated = true;
        this.mContent.removeItem(getViewForInfo(shortcutInfo));
        if (this.mState == 1) {
            this.mRearrangeOnClose = true;
        } else {
            rearrangeChildren();
        }
        if (getItemCount() > 1) {
            return;
        }
        if (this.mIsOpen) {
            close(true);
        } else {
            replaceFolderWithFinalItem();
        }
    }

    private View getViewForInfo(final ShortcutInfo shortcutInfo) {
        return this.mContent.iterateOverItems(new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                return itemInfo == shortcutInfo;
            }
        });
    }

    public void onItemsChanged(boolean z) {
        updateTextViewFocus();
    }

    public void prepareAutoUpdate() {
        close(false);
    }

    public ArrayList<View> getItemsInReadingOrder() {
        if (this.mItemsInvalidated) {
            this.mItemsInReadingOrder.clear();
            this.mContent.iterateOverItems(new ItemOperator() {
                public boolean evaluate(ItemInfo itemInfo, View view) {
                    Folder.this.mItemsInReadingOrder.add(view);
                    return false;
                }
            });
            this.mItemsInvalidated = false;
        }
        return this.mItemsInReadingOrder;
    }

    public List<BubbleTextView> getItemsOnPage(int i) {
        ArrayList itemsInReadingOrder = getItemsInReadingOrder();
        int pageCount = this.mContent.getPageCount() - 1;
        int size = itemsInReadingOrder.size();
        int itemsPerPage = this.mContent.itemsPerPage();
        int i2 = i == pageCount ? size - (itemsPerPage * i) : itemsPerPage;
        int i3 = i * itemsPerPage;
        int min = Math.min(i3 + i2, itemsInReadingOrder.size());
        ArrayList arrayList = new ArrayList(i2);
        while (i3 < min) {
            arrayList.add((BubbleTextView) itemsInReadingOrder.get(i3));
            i3++;
        }
        return arrayList;
    }

    public void onFocusChange(View view, boolean z) {
        if (view != this.mFolderName) {
            return;
        }
        if (z) {
            startEditingFolderName();
        } else {
            this.mFolderName.dispatchBackKey();
        }
    }

    public void getHitRectRelativeToDragLayer(Rect rect) {
        getHitRect(rect);
        rect.left -= this.mScrollAreaOffset;
        rect.right += this.mScrollAreaOffset;
    }

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
        target.gridX = itemInfo.cellX;
        target.gridY = itemInfo.cellY;
        target.pageIndex = this.mContent.getCurrentPage();
        target2.containerType = 3;
    }

    public static Folder getOpen(Launcher launcher) {
        return (Folder) getOpenView(launcher, 1);
    }
}
