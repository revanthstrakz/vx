package com.android.launcher3.notification;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.anim.PropertyResetListener;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider;
import com.android.launcher3.notification.NotificationFooterLayout.IconAnimationEndListener;
import com.android.launcher3.popup.PopupItemView;
import com.android.launcher3.touch.SwipeDetector;
import com.android.launcher3.touch.SwipeDetector.Listener;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import com.android.launcher3.util.Themes;
import java.util.List;

public class NotificationItemView extends PopupItemView implements LogContainerProvider {
    private static final Rect sTempRect = new Rect();
    /* access modifiers changed from: private */
    public boolean mAnimatingNextIcon;
    private NotificationFooterLayout mFooter;
    private TextView mHeaderCount;
    private TextView mHeaderText;
    /* access modifiers changed from: private */
    public NotificationMainView mMainView;
    private int mNotificationHeaderTextColor;
    private SwipeDetector mSwipeDetector;

    public NotificationItemView(Context context) {
        this(context, null, 0);
    }

    public NotificationItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NotificationItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNotificationHeaderTextColor = 0;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mHeaderText = (TextView) findViewById(C0622R.C0625id.notification_text);
        this.mHeaderCount = (TextView) findViewById(C0622R.C0625id.notification_count);
        this.mMainView = (NotificationMainView) findViewById(C0622R.C0625id.main_view);
        this.mFooter = (NotificationFooterLayout) findViewById(C0622R.C0625id.footer);
        this.mSwipeDetector = new SwipeDetector(getContext(), (Listener) this.mMainView, SwipeDetector.HORIZONTAL);
        this.mSwipeDetector.setDetectableScrollConditions(3, false);
        this.mMainView.setSwipeDetector(this.mSwipeDetector);
    }

    public NotificationMainView getMainView() {
        return this.mMainView;
    }

    public int getHeightMinusFooter() {
        if (this.mFooter.getParent() == null) {
            return getHeight();
        }
        return getHeight() - (this.mFooter.getHeight() - getResources().getDimensionPixelSize(C0622R.dimen.notification_empty_footer_height));
    }

    public Animator animateHeightRemoval(int i, boolean z) {
        AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        Rect rect = new Rect(this.mPillRect);
        Rect rect2 = new Rect(this.mPillRect);
        if (z) {
            rect2.top += i;
        } else {
            rect2.bottom -= i;
        }
        RoundedRectRevealOutlineProvider roundedRectRevealOutlineProvider = new RoundedRectRevealOutlineProvider(getBackgroundRadius(), getBackgroundRadius(), rect, rect2, this.mRoundedCorners);
        createAnimatorSet.play(roundedRectRevealOutlineProvider.createRevealAnimator(this, false));
        View findViewById = findViewById(C0622R.C0625id.gutter_bottom);
        if (findViewById != null && findViewById.getVisibility() == 0) {
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(findViewById, TRANSLATION_Y, new float[]{(float) (-i)});
            ofFloat.addListener(new PropertyResetListener(TRANSLATION_Y, Float.valueOf(0.0f)));
            createAnimatorSet.play(ofFloat);
        }
        return createAnimatorSet;
    }

    public void updateHeader(int i, @Nullable IconPalette iconPalette) {
        this.mHeaderCount.setText(i <= 1 ? "" : String.valueOf(i));
        if (iconPalette != null) {
            if (this.mNotificationHeaderTextColor == 0) {
                this.mNotificationHeaderTextColor = IconPalette.resolveContrastColor(getContext(), iconPalette.dominantColor, Themes.getAttrColor(getContext(), C0622R.attr.popupColorPrimary));
            }
            this.mHeaderText.setTextColor(this.mNotificationHeaderTextColor);
            this.mHeaderCount.setTextColor(this.mNotificationHeaderTextColor);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mMainView.getNotificationInfo() == null) {
            return false;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        this.mSwipeDetector.onTouchEvent(motionEvent);
        return this.mSwipeDetector.isDraggingOrSettling();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (this.mMainView.getNotificationInfo() == null) {
            return false;
        }
        if (this.mSwipeDetector.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent)) {
            z = true;
        }
        return z;
    }

    public void applyNotificationInfos(List<NotificationInfo> list) {
        if (!list.isEmpty()) {
            this.mMainView.applyNotificationInfo((NotificationInfo) list.get(0), this.mIconView);
            for (int i = 1; i < list.size(); i++) {
                this.mFooter.addNotificationInfo((NotificationInfo) list.get(i));
            }
            this.mFooter.commitNotificationInfos();
        }
    }

    public void trimNotifications(List<String> list) {
        if (!(!list.contains(this.mMainView.getNotificationInfo().notificationKey)) || this.mAnimatingNextIcon) {
            this.mFooter.trimNotifications(list);
            return;
        }
        this.mAnimatingNextIcon = true;
        this.mMainView.setVisibility(4);
        this.mMainView.setTranslationX(0.0f);
        this.mIconView.getGlobalVisibleRect(sTempRect);
        this.mFooter.animateFirstNotificationTo(sTempRect, new IconAnimationEndListener() {
            public void onIconAnimationEnd(NotificationInfo notificationInfo) {
                if (notificationInfo != null) {
                    NotificationItemView.this.mMainView.applyNotificationInfo(notificationInfo, NotificationItemView.this.mIconView, true);
                    NotificationItemView.this.mMainView.setVisibility(0);
                }
                NotificationItemView.this.mAnimatingNextIcon = false;
            }
        });
    }

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
        target.itemType = 8;
        target2.containerType = 9;
    }
}
