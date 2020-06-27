package com.android.launcher3.qsb;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.launcher3.AppWidgetResizeFrame;
import com.android.launcher3.C0622R;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;

public class QsbContainerView extends FrameLayout {

    public static class QsbFragment extends Fragment implements OnClickListener {
        private static final String QSB_WIDGET_ID = "qsb_widget_id";
        private static final int REQUEST_BIND_QSB = 1;
        private int mOrientation;
        private QsbWidgetHostView mQsb;
        private QsbWidgetHost mQsbWidgetHost;
        private AppWidgetProviderInfo mWidgetInfo;
        private FrameLayout mWrapper;

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mQsbWidgetHost = new QsbWidgetHost(getActivity());
            this.mOrientation = getContext().getResources().getConfiguration().orientation;
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            this.mWrapper = new FrameLayout(getActivity());
            if (FeatureFlags.QSB_ON_FIRST_SCREEN) {
                this.mWrapper.addView(createQsb(this.mWrapper));
            }
            return this.mWrapper;
        }

        private View createQsb(ViewGroup viewGroup) {
            Activity activity = getActivity();
            this.mWidgetInfo = QsbContainerView.getSearchWidgetProvider(activity);
            if (this.mWidgetInfo == null) {
                return QsbWidgetHostView.getDefaultView(viewGroup);
            }
            AppWidgetManager instance = AppWidgetManager.getInstance(activity);
            InvariantDeviceProfile idp = LauncherAppState.getIDP(activity);
            Bundle bundle = new Bundle();
            boolean z = true;
            Rect widgetSizeRanges = AppWidgetResizeFrame.getWidgetSizeRanges(activity, idp.numColumns, 1, null);
            bundle.putInt("appWidgetMinWidth", widgetSizeRanges.left);
            bundle.putInt("appWidgetMinHeight", widgetSizeRanges.top);
            bundle.putInt("appWidgetMaxWidth", widgetSizeRanges.right);
            bundle.putInt("appWidgetMaxHeight", widgetSizeRanges.bottom);
            int i = Utilities.getPrefs(activity).getInt(QSB_WIDGET_ID, -1);
            AppWidgetProviderInfo appWidgetInfo = instance.getAppWidgetInfo(i);
            if (appWidgetInfo == null || !appWidgetInfo.provider.equals(this.mWidgetInfo.provider)) {
                z = false;
            }
            if (!z) {
                if (i > -1) {
                    this.mQsbWidgetHost.deleteHost();
                }
                int allocateAppWidgetId = this.mQsbWidgetHost.allocateAppWidgetId();
                z = instance.bindAppWidgetIdIfAllowed(allocateAppWidgetId, this.mWidgetInfo.getProfile(), this.mWidgetInfo.provider, bundle);
                if (!z) {
                    this.mQsbWidgetHost.deleteAppWidgetId(allocateAppWidgetId);
                    allocateAppWidgetId = -1;
                }
                if (i != allocateAppWidgetId) {
                    saveWidgetId(allocateAppWidgetId);
                }
                i = allocateAppWidgetId;
            }
            if (z) {
                this.mQsb = (QsbWidgetHostView) this.mQsbWidgetHost.createView(activity, i, this.mWidgetInfo);
                this.mQsb.setId(C0622R.C0625id.qsb_widget);
                if (!Utilities.containsAll(AppWidgetManager.getInstance(activity).getAppWidgetOptions(i), bundle)) {
                    this.mQsb.updateAppWidgetOptions(bundle);
                }
                this.mQsb.setPadding(0, 0, 0, 0);
                this.mQsbWidgetHost.startListening();
                return this.mQsb;
            }
            View defaultView = QsbWidgetHostView.getDefaultView(viewGroup);
            View findViewById = defaultView.findViewById(C0622R.C0625id.btn_qsb_setup);
            findViewById.setVisibility(0);
            findViewById.setOnClickListener(this);
            return defaultView;
        }

        private void saveWidgetId(int i) {
            Utilities.getPrefs(getActivity()).edit().putInt(QSB_WIDGET_ID, i).apply();
        }

        public void onClick(View view) {
            Intent intent = new Intent("android.appwidget.action.APPWIDGET_BIND");
            intent.putExtra(Favorites.APPWIDGET_ID, this.mQsbWidgetHost.allocateAppWidgetId());
            intent.putExtra(Favorites.APPWIDGET_PROVIDER, this.mWidgetInfo.provider);
            startActivityForResult(intent, 1);
        }

        public void onActivityResult(int i, int i2, Intent intent) {
            if (i != 1) {
                return;
            }
            if (i2 == -1) {
                saveWidgetId(intent.getIntExtra(Favorites.APPWIDGET_ID, -1));
                rebindFragment();
                return;
            }
            this.mQsbWidgetHost.deleteHost();
        }

        public void onResume() {
            super.onResume();
            if (this.mQsb != null && this.mQsb.isReinflateRequired(this.mOrientation)) {
                rebindFragment();
            }
        }

        public void onDestroy() {
            this.mQsbWidgetHost.stopListening();
            super.onDestroy();
        }

        private void rebindFragment() {
            if (!(!FeatureFlags.QSB_ON_FIRST_SCREEN || this.mWrapper == null || getActivity() == null)) {
                this.mWrapper.removeAllViews();
                this.mWrapper.addView(createQsb(this.mWrapper));
            }
        }
    }

    private static class QsbWidgetHost extends AppWidgetHost {
        private static final int QSB_WIDGET_HOST_ID = 1026;

        public QsbWidgetHost(Context context) {
            super(context, QSB_WIDGET_HOST_ID);
        }

        /* access modifiers changed from: protected */
        public AppWidgetHostView onCreateView(Context context, int i, AppWidgetProviderInfo appWidgetProviderInfo) {
            return new QsbWidgetHostView(context);
        }
    }

    public QsbContainerView(Context context) {
        super(context);
    }

    public QsbContainerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public QsbContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        super.setPadding(0, 0, 0, 0);
    }

    public static AppWidgetProviderInfo getSearchWidgetProvider(Context context) {
        ComponentName globalSearchActivity = ((SearchManager) context.getSystemService("search")).getGlobalSearchActivity();
        AppWidgetProviderInfo appWidgetProviderInfo = null;
        if (globalSearchActivity == null) {
            return null;
        }
        String packageName = globalSearchActivity.getPackageName();
        for (AppWidgetProviderInfo appWidgetProviderInfo2 : AppWidgetManager.getInstance(context).getInstalledProviders()) {
            if (appWidgetProviderInfo2.provider.getPackageName().equals(packageName) && appWidgetProviderInfo2.configure == null) {
                if ((appWidgetProviderInfo2.widgetCategory & 4) != 0) {
                    return appWidgetProviderInfo2;
                }
                if (appWidgetProviderInfo == null) {
                    appWidgetProviderInfo = appWidgetProviderInfo2;
                }
            }
        }
        return appWidgetProviderInfo;
    }
}
