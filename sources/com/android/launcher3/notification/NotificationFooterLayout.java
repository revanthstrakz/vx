package com.android.launcher3.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.anim.PropertyResetListener;
import com.android.launcher3.popup.PopupContainerWithArrow;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationFooterLayout extends FrameLayout {
    private static final int MAX_FOOTER_NOTIFICATIONS = 5;
    private static final Rect sTempRect = new Rect();
    private int mBackgroundColor;
    LayoutParams mIconLayoutParams;
    private LinearLayout mIconRow;
    private final List<NotificationInfo> mNotifications;
    private View mOverflowEllipsis;
    private final List<NotificationInfo> mOverflowNotifications;
    private final boolean mRtl;

    public interface IconAnimationEndListener {
        void onIconAnimationEnd(NotificationInfo notificationInfo);
    }

    public NotificationFooterLayout(Context context) {
        this(context, null, 0);
    }

    public NotificationFooterLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NotificationFooterLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNotifications = new ArrayList();
        this.mOverflowNotifications = new ArrayList();
        Resources resources = getResources();
        this.mRtl = Utilities.isRtl(resources);
        int dimensionPixelSize = resources.getDimensionPixelSize(C0622R.dimen.notification_footer_icon_size);
        this.mIconLayoutParams = new LayoutParams(dimensionPixelSize, dimensionPixelSize);
        this.mIconLayoutParams.gravity = 16;
        int dimensionPixelSize2 = resources.getDimensionPixelSize(C0622R.dimen.horizontal_ellipsis_offset) + resources.getDimensionPixelSize(C0622R.dimen.horizontal_ellipsis_size);
        this.mIconLayoutParams.setMarginStart((((resources.getDimensionPixelSize(C0622R.dimen.bg_popup_item_width) - resources.getDimensionPixelSize(C0622R.dimen.notification_footer_icon_row_padding)) - dimensionPixelSize2) - (dimensionPixelSize * 5)) / 5);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mOverflowEllipsis = findViewById(C0622R.C0625id.overflow);
        this.mIconRow = (LinearLayout) findViewById(C0622R.C0625id.icon_row);
        this.mBackgroundColor = ((ColorDrawable) getBackground()).getColor();
    }

    public void addNotificationInfo(NotificationInfo notificationInfo) {
        if (this.mNotifications.size() < 5) {
            this.mNotifications.add(notificationInfo);
        } else {
            this.mOverflowNotifications.add(notificationInfo);
        }
    }

    public void commitNotificationInfos() {
        this.mIconRow.removeAllViews();
        for (int i = 0; i < this.mNotifications.size(); i++) {
            addNotificationIconForInfo((NotificationInfo) this.mNotifications.get(i));
        }
        updateOverflowEllipsisVisibility();
    }

    private void updateOverflowEllipsisVisibility() {
        this.mOverflowEllipsis.setVisibility(this.mOverflowNotifications.isEmpty() ? 8 : 0);
    }

    private View addNotificationIconForInfo(NotificationInfo notificationInfo) {
        View view = new View(getContext());
        view.setBackground(notificationInfo.getIconForBackground(getContext(), this.mBackgroundColor));
        view.setOnClickListener(notificationInfo);
        view.setTag(notificationInfo);
        view.setImportantForAccessibility(2);
        this.mIconRow.addView(view, 0, this.mIconLayoutParams);
        return view;
    }

    public void animateFirstNotificationTo(Rect rect, final IconAnimationEndListener iconAnimationEndListener) {
        AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        final View childAt = this.mIconRow.getChildAt(this.mIconRow.getChildCount() - 1);
        Rect rect2 = sTempRect;
        childAt.getGlobalVisibleRect(rect2);
        float height = ((float) rect.height()) / ((float) rect2.height());
        ObjectAnimator ofPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(childAt, new PropertyListBuilder().scale(height).translationY(((float) (rect.top - rect2.top)) + (((((float) rect2.height()) * height) - ((float) rect2.height())) / 2.0f)).build());
        ofPropertyValuesHolder.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                iconAnimationEndListener.onIconAnimationEnd((NotificationInfo) childAt.getTag());
                NotificationFooterLayout.this.removeViewFromIconRow(childAt);
            }
        });
        createAnimatorSet.play(ofPropertyValuesHolder);
        int marginStart = this.mIconLayoutParams.width + this.mIconLayoutParams.getMarginStart();
        if (this.mRtl) {
            marginStart = -marginStart;
        }
        if (!this.mOverflowNotifications.isEmpty()) {
            NotificationInfo notificationInfo = (NotificationInfo) this.mOverflowNotifications.remove(0);
            this.mNotifications.add(notificationInfo);
            createAnimatorSet.play(ObjectAnimator.ofFloat(addNotificationIconForInfo(notificationInfo), ALPHA, new float[]{0.0f, 1.0f}));
        }
        int childCount = this.mIconRow.getChildCount() - 1;
        PropertyResetListener propertyResetListener = new PropertyResetListener(TRANSLATION_X, Float.valueOf(0.0f));
        for (int i = 0; i < childCount; i++) {
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mIconRow.getChildAt(i), TRANSLATION_X, new float[]{(float) marginStart});
            ofFloat.addListener(propertyResetListener);
            createAnimatorSet.play(ofFloat);
        }
        createAnimatorSet.start();
    }

    /* access modifiers changed from: private */
    public void removeViewFromIconRow(View view) {
        this.mIconRow.removeView(view);
        this.mNotifications.remove((NotificationInfo) view.getTag());
        updateOverflowEllipsisVisibility();
        if (this.mIconRow.getChildCount() == 0) {
            PopupContainerWithArrow open = PopupContainerWithArrow.getOpen(Launcher.getLauncher(getContext()));
            if (open != null) {
                final int dimensionPixelSize = getResources().getDimensionPixelSize(C0622R.dimen.notification_empty_footer_height);
                Animator reduceNotificationViewHeight = open.reduceNotificationViewHeight(getHeight() - dimensionPixelSize, getResources().getInteger(C0622R.integer.config_removeNotificationViewDuration));
                reduceNotificationViewHeight.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ((ViewGroup) NotificationFooterLayout.this.getParent()).findViewById(C0622R.C0625id.divider).setVisibility(8);
                        NotificationFooterLayout.this.getLayoutParams().height = dimensionPixelSize;
                        NotificationFooterLayout.this.requestLayout();
                    }
                });
                reduceNotificationViewHeight.start();
            }
        }
    }

    public void trimNotifications(List<String> list) {
        if (isAttachedToWindow() && this.mIconRow.getChildCount() != 0) {
            Iterator it = this.mOverflowNotifications.iterator();
            while (it.hasNext()) {
                if (!list.contains(((NotificationInfo) it.next()).notificationKey)) {
                    it.remove();
                }
            }
            for (int childCount = this.mIconRow.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = this.mIconRow.getChildAt(childCount);
                if (!list.contains(((NotificationInfo) childAt.getTag()).notificationKey)) {
                    removeViewFromIconRow(childAt);
                }
            }
        }
    }
}
