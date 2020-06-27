package com.lody.virtual.server.notification;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.lody.virtual.helper.utils.OSUtils;
import com.microsoft.appcenter.ingestion.models.CommonProperties;

class WidthCompat {
    private static final String TAG = "WidthCompat";
    private volatile int mWidth = 0;

    WidthCompat() {
    }

    public int getNotificationWidth(Context context, int i, int i2, int i3) {
        if (this.mWidth > 0) {
            return this.mWidth;
        }
        int defaultWidth = getDefaultWidth(i, i3);
        if (OSUtils.getInstance().isEmui()) {
            defaultWidth = getEMUINotificationWidth(context, i, i2);
        } else if (OSUtils.getInstance().isMiui()) {
            if (VERSION.SDK_INT >= 21) {
                defaultWidth = getMIUINotificationWidth(context, i - (Math.round(TypedValue.applyDimension(1, 10.0f, context.getResources().getDisplayMetrics())) * 2), i2);
            } else {
                defaultWidth = getMIUINotificationWidth(context, i - (Math.round(TypedValue.applyDimension(1, 25.0f, context.getResources().getDisplayMetrics())) * 2), i2);
            }
        }
        this.mWidth = defaultWidth;
        return defaultWidth;
    }

    private int getDefaultWidth(int i, int i2) {
        return VERSION.SDK_INT >= 21 ? i - (i2 * 2) : i;
    }

    private int getMIUINotificationWidth(Context context, int i, int i2) {
        try {
            Context createPackageContext = context.createPackageContext("com.android.systemui", 3);
            int systemId = getSystemId(createPackageContext, "status_bar_notification_row", "layout");
            if (systemId != 0) {
                ViewGroup createViewGroup = createViewGroup(createPackageContext, systemId);
                int systemId2 = getSystemId(createPackageContext, "adaptive", CommonProperties.f192ID);
                if (systemId2 == 0) {
                    systemId2 = getSystemId(createPackageContext, "content", CommonProperties.f192ID);
                } else {
                    View findViewById = createViewGroup.findViewById(systemId2);
                    if (findViewById != null && (findViewById instanceof ViewGroup)) {
                        ((ViewGroup) findViewById).addView(new View(createPackageContext));
                    }
                }
                layout(createViewGroup, i, i2);
                if (systemId2 != 0) {
                    View findViewById2 = createViewGroup.findViewById(systemId2);
                    if (findViewById2 != null) {
                        return ((i - findViewById2.getLeft()) - findViewById2.getPaddingLeft()) - findViewById2.getPaddingRight();
                    }
                } else {
                    int childCount = createViewGroup.getChildCount();
                    int i3 = 0;
                    while (i3 < childCount) {
                        View childAt = createViewGroup.getChildAt(i3);
                        if (!FrameLayout.class.isInstance(childAt) && !"LatestItemView".equals(childAt.getClass().getName())) {
                            if (!"SizeAdaptiveLayout".equals(childAt.getClass().getName())) {
                                i3++;
                            }
                        }
                        return ((i - childAt.getLeft()) - childAt.getPaddingLeft()) - childAt.getPaddingRight();
                    }
                }
            }
        } catch (Exception unused) {
        }
        return i;
    }

    private int getEMUINotificationWidth(Context context, int i, int i2) {
        try {
            Context createPackageContext = context.createPackageContext("com.android.systemui", 3);
            int systemId = getSystemId(createPackageContext, "time_axis", "layout");
            if (systemId != 0) {
                ViewGroup createViewGroup = createViewGroup(createPackageContext, systemId);
                layout(createViewGroup, i, i2);
                int systemId2 = getSystemId(createPackageContext, "content_view_group", CommonProperties.f192ID);
                if (systemId2 != 0) {
                    View findViewById = createViewGroup.findViewById(systemId2);
                    return ((i - findViewById.getLeft()) - findViewById.getPaddingLeft()) - findViewById.getPaddingRight();
                }
                int childCount = createViewGroup.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = createViewGroup.getChildAt(i3);
                    if (LinearLayout.class.isInstance(childAt)) {
                        return ((i - childAt.getLeft()) - childAt.getPaddingLeft()) - childAt.getPaddingRight();
                    }
                }
            }
        } catch (Exception unused) {
        }
        return i;
    }

    private int getSystemId(Context context, String str, String str2) {
        return context.getResources().getIdentifier(str, str2, "com.android.systemui");
    }

    private ViewGroup createViewGroup(Context context, int i) {
        try {
            return (ViewGroup) LayoutInflater.from(context).inflate(i, null);
        } catch (Throwable unused) {
            return new FrameLayout(context);
        }
    }

    private void layout(View view, int i, int i2) {
        view.layout(0, 0, i, i2);
        view.measure(MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
        view.layout(0, 0, i, i2);
    }
}
