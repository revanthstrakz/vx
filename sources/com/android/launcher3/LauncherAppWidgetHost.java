package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.Toast;
import com.android.launcher3.LauncherSettings.Favorites;
import java.util.ArrayList;
import java.util.Iterator;

public class LauncherAppWidgetHost extends AppWidgetHost {
    public static final int APPWIDGET_HOST_ID = 1024;
    private final Context mContext;
    private final ArrayList<ProviderChangedListener> mProviderChangeListeners = new ArrayList<>();
    private final SparseArray<LauncherAppWidgetHostView> mViews = new SparseArray<>();

    public interface ProviderChangedListener {
        void notifyWidgetProvidersChanged();
    }

    public LauncherAppWidgetHost(Context context) {
        super(context, 1024);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public LauncherAppWidgetHostView onCreateView(Context context, int i, AppWidgetProviderInfo appWidgetProviderInfo) {
        LauncherAppWidgetHostView launcherAppWidgetHostView = new LauncherAppWidgetHostView(context);
        this.mViews.put(i, launcherAppWidgetHostView);
        return launcherAppWidgetHostView;
    }

    public void startListening() {
        try {
            super.startListening();
        } catch (Exception e) {
            if (!Utilities.isBinderSizeError(e)) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stopListening() {
        super.stopListening();
    }

    public int allocateAppWidgetId() {
        return super.allocateAppWidgetId();
    }

    public void addProviderChangeListener(ProviderChangedListener providerChangedListener) {
        this.mProviderChangeListeners.add(providerChangedListener);
    }

    public void removeProviderChangeListener(ProviderChangedListener providerChangedListener) {
        this.mProviderChangeListeners.remove(providerChangedListener);
    }

    /* access modifiers changed from: protected */
    public void onProvidersChanged() {
        if (!this.mProviderChangeListeners.isEmpty()) {
            Iterator it = new ArrayList(this.mProviderChangeListeners).iterator();
            while (it.hasNext()) {
                ((ProviderChangedListener) it.next()).notifyWidgetProvidersChanged();
            }
        }
    }

    public AppWidgetHostView createView(Context context, int i, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo) {
        if (launcherAppWidgetProviderInfo.isCustomWidget) {
            LauncherAppWidgetHostView launcherAppWidgetHostView = new LauncherAppWidgetHostView(context);
            ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(launcherAppWidgetProviderInfo.initialLayout, launcherAppWidgetHostView);
            launcherAppWidgetHostView.setAppWidget(0, launcherAppWidgetProviderInfo);
            launcherAppWidgetHostView.updateLastInflationOrientation();
            return launcherAppWidgetHostView;
        }
        try {
            return super.createView(context, i, launcherAppWidgetProviderInfo);
        } catch (Exception e) {
            if (Utilities.isBinderSizeError(e)) {
                LauncherAppWidgetHostView launcherAppWidgetHostView2 = (LauncherAppWidgetHostView) this.mViews.get(i);
                if (launcherAppWidgetHostView2 == null) {
                    launcherAppWidgetHostView2 = onCreateView(this.mContext, i, (AppWidgetProviderInfo) launcherAppWidgetProviderInfo);
                }
                launcherAppWidgetHostView2.setAppWidget(i, launcherAppWidgetProviderInfo);
                launcherAppWidgetHostView2.switchToErrorView();
                return launcherAppWidgetHostView2;
            }
            throw new RuntimeException(e);
        }
    }

    /* access modifiers changed from: protected */
    public void onProviderChanged(int i, AppWidgetProviderInfo appWidgetProviderInfo) {
        LauncherAppWidgetProviderInfo fromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, appWidgetProviderInfo);
        super.onProviderChanged(i, fromProviderInfo);
        fromProviderInfo.initSpans(this.mContext);
    }

    public void deleteAppWidgetId(int i) {
        super.deleteAppWidgetId(i);
        this.mViews.remove(i);
    }

    /* access modifiers changed from: protected */
    public void clearViews() {
        super.clearViews();
        this.mViews.clear();
    }

    public void startBindFlow(BaseActivity baseActivity, int i, AppWidgetProviderInfo appWidgetProviderInfo, int i2) {
        baseActivity.startActivityForResult(new Intent("android.appwidget.action.APPWIDGET_BIND").putExtra(Favorites.APPWIDGET_ID, i).putExtra(Favorites.APPWIDGET_PROVIDER, appWidgetProviderInfo.provider).putExtra("appWidgetProviderProfile", appWidgetProviderInfo.getProfile()), i2);
    }

    public void startConfigActivity(BaseActivity baseActivity, int i, int i2) {
        try {
            startAppWidgetConfigureActivityForResult(baseActivity, i, 0, i2, null);
        } catch (ActivityNotFoundException | SecurityException unused) {
            Toast.makeText(baseActivity, C0622R.string.activity_not_found, 0).show();
            sendActionCancelled(baseActivity, i2);
        }
    }

    private void sendActionCancelled(final BaseActivity baseActivity, final int i) {
        new Handler().post(new Runnable() {
            public void run() {
                baseActivity.onActivityResult(i, 0, null);
            }
        });
    }
}
