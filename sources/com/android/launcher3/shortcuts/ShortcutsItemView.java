package com.android.launcher3.shortcuts;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.popup.PopupItemView;
import com.android.launcher3.popup.PopupPopulator;
import com.android.launcher3.popup.PopupPopulator.Item;
import com.android.launcher3.popup.SystemShortcut.Widgets;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ShortcutsItemView extends PopupItemView implements OnLongClickListener, OnTouchListener, LogContainerProvider {
    private static final String TAG = "ShortcutsItem";
    private LinearLayout mContent;
    private final List<DeepShortcutView> mDeepShortcutViews;
    private int mHiddenShortcutsHeight;
    private final Point mIconLastTouchPos;
    private final Point mIconShift;
    private Launcher mLauncher;
    private LinearLayout mShortcutsLayout;
    private LinearLayout mSystemShortcutIcons;
    private final List<View> mSystemShortcutViews;

    public ShortcutsItemView(Context context) {
        this(context, null, 0);
    }

    public ShortcutsItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ShortcutsItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIconShift = new Point();
        this.mIconLastTouchPos = new Point();
        this.mDeepShortcutViews = new ArrayList();
        this.mSystemShortcutViews = new ArrayList();
        this.mLauncher = Launcher.getLauncher(context);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = (LinearLayout) findViewById(C0622R.C0625id.content);
        this.mShortcutsLayout = (LinearLayout) findViewById(C0622R.C0625id.shortcuts);
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0 || action == 2) {
            this.mIconLastTouchPos.set((int) motionEvent.getX(), (int) motionEvent.getY());
        }
        return false;
    }

    public boolean onLongClick(View view) {
        if (!(view.getParent() instanceof DeepShortcutView) || !this.mLauncher.isDraggingEnabled() || this.mLauncher.getDragController().isDragging()) {
            return false;
        }
        DeepShortcutView deepShortcutView = (DeepShortcutView) view.getParent();
        deepShortcutView.setWillDrawIcon(false);
        this.mIconShift.x = this.mIconLastTouchPos.x - deepShortcutView.getIconCenter().x;
        this.mIconShift.y = this.mIconLastTouchPos.y - this.mLauncher.getDeviceProfile().iconSizePx;
        this.mLauncher.getWorkspace().beginDragShared(deepShortcutView.getIconView(), (PopupContainerWithArrow) getParent(), deepShortcutView.getFinalInfo(), new ShortcutDragPreviewProvider(deepShortcutView.getIconView(), this.mIconShift), new DragOptions()).animateShift(-this.mIconShift.x, -this.mIconShift.y);
        AbstractFloatingView.closeOpenContainer(this.mLauncher, 1);
        return false;
    }

    public void addShortcutView(View view, Item item) {
        addShortcutView(view, item, -1);
    }

    private void addShortcutView(View view, Item item, int i) {
        if (item == Item.SHORTCUT) {
            this.mDeepShortcutViews.add((DeepShortcutView) view);
        } else {
            this.mSystemShortcutViews.add(view);
        }
        boolean z = true;
        int i2 = 0;
        if (item == Item.SYSTEM_SHORTCUT_ICON) {
            if (this.mSystemShortcutIcons == null) {
                this.mSystemShortcutIcons = (LinearLayout) this.mLauncher.getLayoutInflater().inflate(C0622R.layout.system_shortcut_icons, this.mContent, false);
                if (this.mShortcutsLayout.getChildCount() <= 0) {
                    z = false;
                }
                LinearLayout linearLayout = this.mContent;
                LinearLayout linearLayout2 = this.mSystemShortcutIcons;
                if (z) {
                    i2 = -1;
                }
                linearLayout.addView(linearLayout2, i2);
            }
            this.mSystemShortcutIcons.addView(view, i);
            return;
        }
        if (this.mShortcutsLayout.getChildCount() > 0) {
            View childAt = this.mShortcutsLayout.getChildAt(this.mShortcutsLayout.getChildCount() - 1);
            if (childAt instanceof DeepShortcutView) {
                childAt.findViewById(C0622R.C0625id.divider).setVisibility(0);
            }
        }
        this.mShortcutsLayout.addView(view, i);
    }

    public List<DeepShortcutView> getDeepShortcutViews(boolean z) {
        if (z) {
            Collections.reverse(this.mDeepShortcutViews);
        }
        return this.mDeepShortcutViews;
    }

    public List<View> getSystemShortcutViews(boolean z) {
        if (z || this.mSystemShortcutIcons != null) {
            Collections.reverse(this.mSystemShortcutViews);
        }
        return this.mSystemShortcutViews;
    }

    public void hideShortcuts(boolean z, int i) {
        int i2 = 0;
        this.mHiddenShortcutsHeight = (getResources().getDimensionPixelSize(C0622R.dimen.bg_popup_item_height) - this.mShortcutsLayout.getChildAt(0).getLayoutParams().height) * this.mShortcutsLayout.getChildCount();
        int childCount = this.mShortcutsLayout.getChildCount() - i;
        if (childCount > 0) {
            int childCount2 = this.mShortcutsLayout.getChildCount();
            int i3 = z ? 1 : -1;
            if (!z) {
                i2 = childCount2 - 1;
            }
            while (i2 >= 0 && i2 < childCount2) {
                View childAt = this.mShortcutsLayout.getChildAt(i2);
                if (childAt instanceof DeepShortcutView) {
                    this.mHiddenShortcutsHeight += childAt.getLayoutParams().height;
                    childAt.setVisibility(8);
                    int i4 = i2 + i3;
                    if (!z && i4 >= 0 && i4 < childCount2) {
                        this.mShortcutsLayout.getChildAt(i4).findViewById(C0622R.C0625id.divider).setVisibility(8);
                    }
                    childCount--;
                    if (childCount == 0) {
                        break;
                    }
                }
                i2 += i3;
            }
        }
    }

    public int getHiddenShortcutsHeight() {
        return this.mHiddenShortcutsHeight;
    }

    public Animator showAllShortcuts(boolean z) {
        int childCount = this.mShortcutsLayout.getChildCount();
        if (childCount == 0) {
            Log.w(TAG, "Tried to show all shortcuts but there were no shortcuts to show");
            return null;
        }
        int i = this.mShortcutsLayout.getChildAt(0).getLayoutParams().height;
        int dimensionPixelSize = getResources().getDimensionPixelSize(C0622R.dimen.bg_popup_item_height);
        for (int i2 = 0; i2 < childCount; i2++) {
            DeepShortcutView deepShortcutView = (DeepShortcutView) this.mShortcutsLayout.getChildAt(i2);
            deepShortcutView.getLayoutParams().height = dimensionPixelSize;
            deepShortcutView.requestLayout();
            deepShortcutView.setVisibility(0);
            if (i2 < childCount - 1) {
                deepShortcutView.findViewById(C0622R.C0625id.divider).setVisibility(0);
            }
        }
        AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        if (z) {
            createAnimatorSet.play(translateYFrom(this.mShortcutsLayout, -this.mHiddenShortcutsHeight));
        } else if (this.mSystemShortcutIcons != null) {
            createAnimatorSet.play(translateYFrom(this.mSystemShortcutIcons, -this.mHiddenShortcutsHeight));
            Rect rect = new Rect(this.mPillRect);
            Rect rect2 = new Rect(this.mPillRect);
            rect2.bottom += this.mHiddenShortcutsHeight;
            RoundedRectRevealOutlineProvider roundedRectRevealOutlineProvider = new RoundedRectRevealOutlineProvider(getBackgroundRadius(), getBackgroundRadius(), rect, rect2, this.mRoundedCorners);
            createAnimatorSet.play(roundedRectRevealOutlineProvider.createRevealAnimator(this, false));
        }
        for (int i3 = 0; i3 < childCount; i3++) {
            DeepShortcutView deepShortcutView2 = (DeepShortcutView) this.mShortcutsLayout.getChildAt(i3);
            int i4 = dimensionPixelSize - i;
            int i5 = 1;
            int i6 = z ? (childCount - i3) - 1 : i3;
            if (!z) {
                i5 = -1;
            }
            createAnimatorSet.play(translateYFrom(deepShortcutView2, i6 * i4 * i5));
            int i7 = (i4 / 2) * i5;
            createAnimatorSet.play(translateYFrom(deepShortcutView2.getBubbleText(), i7));
            createAnimatorSet.play(translateYFrom(deepShortcutView2.getIconView(), i7));
            createAnimatorSet.play(LauncherAnimUtils.ofPropertyValuesHolder(deepShortcutView2.getIconView(), new PropertyListBuilder().scale(1.0f).build()));
        }
        return createAnimatorSet;
    }

    private Animator translateYFrom(View view, int i) {
        float translationY = view.getTranslationY();
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, new float[]{((float) i) + translationY, translationY});
    }

    public void enableWidgetsIfExist(BubbleTextView bubbleTextView) {
        View view;
        ItemInfo itemInfo = (ItemInfo) bubbleTextView.getTag();
        Widgets widgets = new Widgets();
        OnClickListener onClickListener = widgets.getOnClickListener(this.mLauncher, itemInfo);
        Iterator it = this.mSystemShortcutViews.iterator();
        while (true) {
            if (!it.hasNext()) {
                view = null;
                break;
            }
            view = (View) it.next();
            if (view.getTag() instanceof Widgets) {
                break;
            }
        }
        Item item = this.mSystemShortcutIcons == null ? Item.SYSTEM_SHORTCUT : Item.SYSTEM_SHORTCUT_ICON;
        if (onClickListener != null && view == null) {
            View inflate = this.mLauncher.getLayoutInflater().inflate(item.layoutId, this, false);
            PopupPopulator.initializeSystemShortcut(getContext(), inflate, widgets);
            inflate.setOnClickListener(onClickListener);
            if (item == Item.SYSTEM_SHORTCUT_ICON) {
                addShortcutView(inflate, item, 0);
                return;
            }
            ((PopupContainerWithArrow) getParent()).close(false);
            PopupContainerWithArrow.showForIcon(bubbleTextView);
        } else if (onClickListener == null && view != null) {
            if (item == Item.SYSTEM_SHORTCUT_ICON) {
                this.mSystemShortcutViews.remove(view);
                this.mSystemShortcutIcons.removeView(view);
                return;
            }
            ((PopupContainerWithArrow) getParent()).close(false);
            PopupContainerWithArrow.showForIcon(bubbleTextView);
        }
    }

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
        target.itemType = 5;
        target.rank = itemInfo.rank;
        target2.containerType = 9;
    }
}
