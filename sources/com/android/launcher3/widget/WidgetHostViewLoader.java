package com.android.launcher3.widget;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.android.launcher3.AppWidgetResizeFrame;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.dragndrop.DragController.DragListener;
import com.android.launcher3.dragndrop.DragLayer.LayoutParams;
import com.android.launcher3.dragndrop.DragOptions;

public class WidgetHostViewLoader implements DragListener {
    private static final boolean LOGD = false;
    private static final String TAG = "WidgetHostViewLoader";
    private Runnable mBindWidgetRunnable = null;
    Handler mHandler;
    Runnable mInflateWidgetRunnable = null;
    final PendingAddWidgetInfo mInfo;
    Launcher mLauncher;
    final View mView;
    int mWidgetLoadingId = -1;

    public WidgetHostViewLoader(Launcher launcher, View view) {
        this.mLauncher = launcher;
        this.mHandler = new Handler();
        this.mView = view;
        this.mInfo = (PendingAddWidgetInfo) view.getTag();
    }

    public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
        preloadWidget();
    }

    public void onDragEnd() {
        this.mLauncher.getDragController().removeDragListener(this);
        this.mHandler.removeCallbacks(this.mBindWidgetRunnable);
        this.mHandler.removeCallbacks(this.mInflateWidgetRunnable);
        if (this.mWidgetLoadingId != -1) {
            this.mLauncher.getAppWidgetHost().deleteAppWidgetId(this.mWidgetLoadingId);
            this.mWidgetLoadingId = -1;
        }
        if (this.mInfo.boundWidget != null) {
            this.mLauncher.getDragLayer().removeView(this.mInfo.boundWidget);
            this.mLauncher.getAppWidgetHost().deleteAppWidgetId(this.mInfo.boundWidget.getAppWidgetId());
            this.mInfo.boundWidget = null;
        }
    }

    private boolean preloadWidget() {
        final LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = this.mInfo.info;
        if (launcherAppWidgetProviderInfo.isCustomWidget) {
            return false;
        }
        final Bundle defaultOptionsForWidget = getDefaultOptionsForWidget(this.mLauncher, this.mInfo);
        if (this.mInfo.getHandler().needsConfigure()) {
            this.mInfo.bindOptions = defaultOptionsForWidget;
            return false;
        }
        this.mBindWidgetRunnable = new Runnable() {
            public void run() {
                WidgetHostViewLoader.this.mWidgetLoadingId = WidgetHostViewLoader.this.mLauncher.getAppWidgetHost().allocateAppWidgetId();
                if (AppWidgetManagerCompat.getInstance(WidgetHostViewLoader.this.mLauncher).bindAppWidgetIdIfAllowed(WidgetHostViewLoader.this.mWidgetLoadingId, launcherAppWidgetProviderInfo, defaultOptionsForWidget)) {
                    WidgetHostViewLoader.this.mHandler.post(WidgetHostViewLoader.this.mInflateWidgetRunnable);
                }
            }
        };
        this.mInflateWidgetRunnable = new Runnable() {
            public void run() {
                if (WidgetHostViewLoader.this.mWidgetLoadingId != -1) {
                    AppWidgetHostView createView = WidgetHostViewLoader.this.mLauncher.getAppWidgetHost().createView(WidgetHostViewLoader.this.mLauncher, WidgetHostViewLoader.this.mWidgetLoadingId, launcherAppWidgetProviderInfo);
                    WidgetHostViewLoader.this.mInfo.boundWidget = createView;
                    WidgetHostViewLoader.this.mWidgetLoadingId = -1;
                    createView.setVisibility(4);
                    int[] estimateItemSize = WidgetHostViewLoader.this.mLauncher.getWorkspace().estimateItemSize(WidgetHostViewLoader.this.mInfo, false, true);
                    LayoutParams layoutParams = new LayoutParams(estimateItemSize[0], estimateItemSize[1]);
                    layoutParams.f62y = 0;
                    layoutParams.f61x = 0;
                    layoutParams.customPosition = true;
                    createView.setLayoutParams(layoutParams);
                    WidgetHostViewLoader.this.mLauncher.getDragLayer().addView(createView);
                    WidgetHostViewLoader.this.mView.setTag(WidgetHostViewLoader.this.mInfo);
                }
            }
        };
        this.mHandler.post(this.mBindWidgetRunnable);
        return true;
    }

    public static Bundle getDefaultOptionsForWidget(Context context, PendingAddWidgetInfo pendingAddWidgetInfo) {
        Rect rect = new Rect();
        AppWidgetResizeFrame.getWidgetSizeRanges(context, pendingAddWidgetInfo.spanX, pendingAddWidgetInfo.spanY, rect);
        Rect defaultPaddingForWidget = AppWidgetHostView.getDefaultPaddingForWidget(context, pendingAddWidgetInfo.componentName, null);
        float f = context.getResources().getDisplayMetrics().density;
        int i = (int) (((float) (defaultPaddingForWidget.left + defaultPaddingForWidget.right)) / f);
        int i2 = (int) (((float) (defaultPaddingForWidget.top + defaultPaddingForWidget.bottom)) / f);
        Bundle bundle = new Bundle();
        bundle.putInt("appWidgetMinWidth", rect.left - i);
        bundle.putInt("appWidgetMinHeight", rect.top - i2);
        bundle.putInt("appWidgetMaxWidth", rect.right - i);
        bundle.putInt("appWidgetMaxHeight", rect.bottom - i2);
        return bundle;
    }
}
