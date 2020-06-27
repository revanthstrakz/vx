package com.android.launcher3.notification;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.touch.OverScroll;
import com.android.launcher3.touch.SwipeDetector;
import com.android.launcher3.touch.SwipeDetector.Listener;
import com.android.launcher3.touch.SwipeDetector.ScrollInterpolator;
import com.android.launcher3.util.Themes;

public class NotificationMainView extends FrameLayout implements Listener {
    private int mBackgroundColor;
    private NotificationInfo mNotificationInfo;
    /* access modifiers changed from: private */
    public SwipeDetector mSwipeDetector;
    private ViewGroup mTextAndBackground;
    private TextView mTextView;
    private TextView mTitleView;

    public void onDragStart(boolean z) {
    }

    public NotificationMainView(Context context) {
        this(context, null, 0);
    }

    public NotificationMainView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NotificationMainView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTextAndBackground = (ViewGroup) findViewById(C0622R.C0625id.text_and_background);
        ColorDrawable colorDrawable = (ColorDrawable) this.mTextAndBackground.getBackground();
        this.mBackgroundColor = colorDrawable.getColor();
        this.mTextAndBackground.setBackground(new RippleDrawable(ColorStateList.valueOf(Themes.getAttrColor(getContext(), 16843820)), colorDrawable, null));
        this.mTitleView = (TextView) this.mTextAndBackground.findViewById(C0622R.C0625id.title);
        this.mTextView = (TextView) this.mTextAndBackground.findViewById(C0622R.C0625id.text);
    }

    public void applyNotificationInfo(NotificationInfo notificationInfo, View view) {
        applyNotificationInfo(notificationInfo, view, false);
    }

    public void setSwipeDetector(SwipeDetector swipeDetector) {
        this.mSwipeDetector = swipeDetector;
    }

    public void applyNotificationInfo(NotificationInfo notificationInfo, View view, boolean z) {
        this.mNotificationInfo = notificationInfo;
        CharSequence charSequence = this.mNotificationInfo.title;
        CharSequence charSequence2 = this.mNotificationInfo.text;
        if (TextUtils.isEmpty(charSequence) || TextUtils.isEmpty(charSequence2)) {
            this.mTitleView.setMaxLines(2);
            this.mTitleView.setText(TextUtils.isEmpty(charSequence) ? charSequence2.toString() : charSequence.toString());
            this.mTextView.setVisibility(8);
        } else {
            this.mTitleView.setText(charSequence.toString());
            this.mTextView.setText(charSequence2.toString());
        }
        view.setBackground(this.mNotificationInfo.getIconForBackground(getContext(), this.mBackgroundColor));
        if (this.mNotificationInfo.intent != null) {
            setOnClickListener(this.mNotificationInfo);
        }
        setTranslationX(0.0f);
        setTag(new ItemInfo());
        if (z) {
            ObjectAnimator.ofFloat(this.mTextAndBackground, ALPHA, new float[]{0.0f, 1.0f}).setDuration(150).start();
        }
    }

    public NotificationInfo getNotificationInfo() {
        return this.mNotificationInfo;
    }

    public boolean canChildBeDismissed() {
        return this.mNotificationInfo != null && this.mNotificationInfo.dismissable;
    }

    public void onChildDismissed() {
        Launcher launcher = Launcher.getLauncher(getContext());
        launcher.getPopupDataProvider().cancelNotification(this.mNotificationInfo.notificationKey);
        launcher.getUserEventDispatcher().logActionOnItem(3, 4, 8);
    }

    public boolean onDrag(float f, float f2) {
        if (!canChildBeDismissed()) {
            f = (float) OverScroll.dampedScroll(f, getWidth());
        }
        setTranslationX(f);
        animate().cancel();
        return true;
    }

    public void onDragEnd(float f, boolean z) {
        final boolean z2 = true;
        float f2 = 0.0f;
        if (canChildBeDismissed()) {
            if (z) {
                f2 = (float) (f < 0.0f ? -getWidth() : getWidth());
            } else if (Math.abs(getTranslationX()) > ((float) (getWidth() / 2))) {
                f2 = (float) (getTranslationX() < 0.0f ? -getWidth() : getWidth());
            }
            ScrollInterpolator scrollInterpolator = new ScrollInterpolator();
            scrollInterpolator.setVelocityAtZero(f);
            animate().setDuration(SwipeDetector.calculateDuration(f, (f2 - getTranslationX()) / ((float) getWidth()))).setInterpolator(scrollInterpolator).translationX(f2).withEndAction(new Runnable() {
                public void run() {
                    NotificationMainView.this.mSwipeDetector.finishedScrolling();
                    if (z2) {
                        NotificationMainView.this.onChildDismissed();
                    }
                }
            }).start();
        }
        z2 = false;
        ScrollInterpolator scrollInterpolator2 = new ScrollInterpolator();
        scrollInterpolator2.setVelocityAtZero(f);
        animate().setDuration(SwipeDetector.calculateDuration(f, (f2 - getTranslationX()) / ((float) getWidth()))).setInterpolator(scrollInterpolator2).translationX(f2).withEndAction(new Runnable() {
            public void run() {
                NotificationMainView.this.mSwipeDetector.finishedScrolling();
                if (z2) {
                    NotificationMainView.this.onChildDismissed();
                }
            }
        }).start();
    }
}
