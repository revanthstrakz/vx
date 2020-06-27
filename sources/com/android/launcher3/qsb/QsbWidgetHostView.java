package com.android.launcher3.qsb;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;

public class QsbWidgetHostView extends AppWidgetHostView {
    @ExportedProperty(category = "launcher")
    private int mPreviousOrientation;

    public QsbWidgetHostView(Context context) {
        super(context);
    }

    public void updateAppWidget(RemoteViews remoteViews) {
        this.mPreviousOrientation = getResources().getConfiguration().orientation;
        super.updateAppWidget(remoteViews);
    }

    public boolean isReinflateRequired(int i) {
        return this.mPreviousOrientation != i;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        try {
            super.onLayout(z, i, i2, i3, i4);
        } catch (RuntimeException unused) {
            post(new Runnable() {
                public void run() {
                    QsbWidgetHostView.this.updateAppWidget(new RemoteViews(QsbWidgetHostView.this.getAppWidgetInfo().provider.getPackageName(), 0));
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public View getErrorView() {
        return getDefaultView(this);
    }

    /* access modifiers changed from: protected */
    public View getDefaultView() {
        View defaultView = super.getDefaultView();
        defaultView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Launcher.getLauncher(QsbWidgetHostView.this.getContext()).startSearch("", false, null, true);
            }
        });
        return defaultView;
    }

    public static View getDefaultView(ViewGroup viewGroup) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(C0622R.layout.qsb_default_view, viewGroup, false);
        inflate.findViewById(C0622R.C0625id.btn_qsb_search).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Launcher.getLauncher(view.getContext()).startSearch("", false, null, true);
            }
        });
        return inflate;
    }
}
