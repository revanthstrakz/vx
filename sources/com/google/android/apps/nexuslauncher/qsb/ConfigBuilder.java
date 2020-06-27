package com.google.android.apps.nexuslauncher.qsb;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.p001v4.graphics.ColorUtils;
import android.support.p004v7.widget.GridLayoutManager;
import android.support.p004v7.widget.GridLayoutManager.LayoutParams;
import android.support.p004v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.p004v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.android.launcher3.AppInfo;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsRecyclerView;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.dynamicui.WallpaperColorInfo;
import com.android.launcher3.util.ComponentKeyMapper;
import com.android.launcher3.util.Themes;
import com.google.android.apps.nexuslauncher.NexusLauncherActivity;
import com.google.android.apps.nexuslauncher.search.AppSearchProvider;
import com.google.android.apps.nexuslauncher.search.nano.SearchProto.a_search;
import com.google.android.apps.nexuslauncher.search.nano.SearchProto.b_search;
import com.google.android.apps.nexuslauncher.search.nano.SearchProto.c_search;
import com.google.android.apps.nexuslauncher.search.nano.SearchProto.d_search;
import com.google.protobuf.nano.MessageNano;
import java.util.ArrayList;
import java.util.List;

public class ConfigBuilder {

    /* renamed from: co */
    private boolean f87co;
    private final NexusLauncherActivity mActivity;
    private BubbleTextView mBubbleTextView;
    private final Bundle mBundle = new Bundle();
    private final boolean mIsAllApps;
    private final c_search mNano = new c_search();
    private final AbstractQsbLayout mQsbLayout;
    private final UserManagerCompat mUserManager;

    public ConfigBuilder(AbstractQsbLayout abstractQsbLayout, boolean z) {
        this.mQsbLayout = abstractQsbLayout;
        this.mActivity = abstractQsbLayout.mActivity;
        this.mIsAllApps = z;
        this.mUserManager = UserManagerCompat.getInstance(this.mActivity);
    }

    public static Intent getSearchIntent(Rect rect, View view, View view2) {
        Intent intent = new Intent("com.google.nexuslauncher.FAST_TEXT_SEARCH");
        intent.setSourceBounds(rect);
        if (view2.getVisibility() != 0) {
            intent.putExtra("source_mic_alpha", 0.0f);
        }
        return intent.putExtra("source_round_left", true).putExtra("source_round_right", true).putExtra("source_logo_offset", getCenter(view, rect)).putExtra("source_mic_offset", getCenter(view2, rect)).putExtra("use_fade_animation", true).setPackage("com.google.android.googlequicksearchbox").addFlags(1342177280);
    }

    /* renamed from: bW */
    private void m19bW() {
        if (this.mNano.f113ez == null) {
            a_search a_search = this.mNano.f101en;
            a_search a_search2 = new a_search();
            a_search2.f93ef = a_search.f93ef;
            a_search2.f94eg = a_search.f94eg + a_search.f92ee;
            a_search2.f92ee = a_search.f92ee;
            a_search2.f95eh = a_search.f95eh;
            this.mNano.f113ez = a_search2;
        }
    }

    private AllAppsRecyclerView getAppsView() {
        return (AllAppsRecyclerView) this.mActivity.findViewById(C0622R.C0625id.apps_list_view);
    }

    private int getBackgroundColor() {
        return ColorUtils.compositeColors(Themes.getAttrColor(this.mActivity, C0622R.attr.allAppsScrimColor), ColorUtils.setAlphaComponent(WallpaperColorInfo.getInstance(this.mActivity).getMainColor(), 255));
    }

    /* renamed from: bZ */
    private b_search m20bZ(AppInfo appInfo, int i) {
        b_search b_search = new b_search();
        b_search.label = appInfo.title.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("icon_bitmap_");
        sb.append(i);
        b_search.f96ej = sb.toString();
        this.mBundle.putParcelable(b_search.f96ej, appInfo.iconBitmap);
        Uri buildUri = AppSearchProvider.buildUri(appInfo, this.mUserManager);
        b_search.f98el = buildUri.toString();
        b_search.f97ek = new Intent("com.google.android.apps.nexuslauncher.search.APP_LAUNCH", buildUri.buildUpon().appendQueryParameter("predictionRank", Integer.toString(i)).build()).toUri(0);
        return b_search;
    }

    private RemoteViews searchIconTemplate() {
        RemoteViews remoteViews = new RemoteViews(this.mActivity.getPackageName(), C0622R.layout.apps_search_icon_template);
        int iconSize = this.mBubbleTextView.getIconSize();
        int width = (this.mBubbleTextView.getWidth() - iconSize) / 2;
        int paddingTop = this.mBubbleTextView.getPaddingTop();
        int height = (this.mBubbleTextView.getHeight() - iconSize) - paddingTop;
        remoteViews.setViewPadding(16908294, width, paddingTop, width, height);
        int min = Math.min((int) (((float) iconSize) * 0.12f), Math.min(width, Math.min(paddingTop, height)));
        int i = width - min;
        int i2 = paddingTop - min;
        int i3 = height - min;
        remoteViews.setViewPadding(C0622R.C0625id.click_feedback_wrapper, i, i2, i, i3);
        remoteViews.setTextViewTextSize(16908310, 0, this.mActivity.getDeviceProfile().allAppsIconTextSizePx);
        remoteViews.setViewPadding(16908310, this.mBubbleTextView.getPaddingLeft(), this.mBubbleTextView.getCompoundDrawablePadding() + this.mBubbleTextView.getIconSize(), this.mBubbleTextView.getPaddingRight(), 0);
        return remoteViews;
    }

    private RemoteViews searchQsbTemplate() {
        int i;
        RemoteViews remoteViews = new RemoteViews(this.mActivity.getPackageName(), C0622R.layout.apps_search_qsb_template);
        int height = ((this.mQsbLayout.getHeight() - this.mQsbLayout.getPaddingTop()) - this.mQsbLayout.getPaddingBottom()) + 20;
        Bitmap bitmap = this.mQsbLayout.mShadowBitmap;
        int width = (bitmap.getWidth() - height) / 2;
        int height2 = (this.mQsbLayout.getHeight() - bitmap.getHeight()) / 2;
        remoteViews.setViewPadding(C0622R.C0625id.qsb_background_container, this.mQsbLayout.getPaddingLeft() - width, height2, this.mQsbLayout.getPaddingRight() - width, height2);
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth() / 2, bitmap.getHeight());
        Bitmap createBitmap2 = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - 20) / 2, 0, 20, bitmap.getHeight());
        remoteViews.setImageViewBitmap(C0622R.C0625id.qsb_background_1, createBitmap);
        remoteViews.setImageViewBitmap(C0622R.C0625id.qsb_background_2, createBitmap2);
        remoteViews.setImageViewBitmap(C0622R.C0625id.qsb_background_3, createBitmap);
        if (this.mQsbLayout.mMicIconView.getVisibility() != 0) {
            remoteViews.setViewVisibility(C0622R.C0625id.mic_icon, 4);
        }
        View findViewById = this.mQsbLayout.findViewById(C0622R.C0625id.g_icon);
        if (this.mQsbLayout.getLayoutDirection() == 1) {
            i = this.mQsbLayout.getWidth() - findViewById.getRight();
        } else {
            i = findViewById.getLeft();
        }
        remoteViews.setViewPadding(C0622R.C0625id.qsb_icon_container, i, 0, i, 0);
        return remoteViews;
    }

    private static Point getCenter(View view, Rect rect) {
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        Point point = new Point();
        point.x = (iArr[0] - rect.left) + (view.getWidth() / 2);
        point.y = (iArr[1] - rect.top) + (view.getHeight() / 2);
        return point;
    }

    /* renamed from: cd */
    private void m21cd() {
        this.mNano.f112ey = "search_box_template";
        this.mBundle.putParcelable(this.mNano.f112ey, searchQsbTemplate());
        this.mNano.f110ew = C0622R.C0625id.g_icon;
        this.mNano.f111ex = this.mQsbLayout.mMicIconView.getVisibility() == 0 ? C0622R.C0625id.mic_icon : 0;
        a_search viewBounds = getViewBounds(this.mActivity.getDragLayer());
        int i = this.mNano.f101en.f94eg;
        if (!this.f87co) {
            i += this.mNano.f101en.f92ee;
        }
        viewBounds.f94eg += i;
        viewBounds.f92ee -= i;
        this.mNano.f107et = viewBounds;
        Bitmap createBitmap = Bitmap.createBitmap(viewBounds.f95eh, viewBounds.f92ee, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.translate(0.0f, (float) (-i));
        AllAppsRecyclerView appsView = getAppsView();
        int[] iArr = {0, 0};
        this.mActivity.getDragLayer().mapCoordInSelfToDescendant(appsView, iArr);
        canvas.translate((float) (-iArr[0]), (float) (-iArr[1]));
        appsView.draw(canvas);
        canvas.setBitmap(null);
        this.mNano.f108eu = "preview_bitmap";
        this.mBundle.putParcelable(this.mNano.f108eu, createBitmap);
    }

    /* renamed from: ce */
    private void m22ce() {
        View view;
        int i;
        AllAppsRecyclerView appsView = getAppsView();
        SpanSizeLookup spanSizeLookup = ((GridLayoutManager) appsView.getLayoutManager()).getSpanSizeLookup();
        int min = Math.min(this.mActivity.getDeviceProfile().allAppsNumCols, appsView.getChildCount());
        int childCount = appsView.getChildCount();
        BubbleTextView[] bubbleTextViewArr = new BubbleTextView[min];
        int i2 = 0;
        int i3 = -1;
        while (true) {
            if (i2 >= childCount) {
                view = null;
                break;
            }
            ViewHolder childViewHolder = appsView.getChildViewHolder(appsView.getChildAt(i2));
            if (childViewHolder.itemView instanceof BubbleTextView) {
                int spanGroupIndex = spanSizeLookup.getSpanGroupIndex(childViewHolder.getLayoutPosition(), min);
                if (spanGroupIndex >= 0) {
                    if (i3 >= 0 && spanGroupIndex != i3) {
                        view = childViewHolder.itemView;
                        break;
                    } else {
                        bubbleTextViewArr[((LayoutParams) childViewHolder.itemView.getLayoutParams()).getSpanIndex()] = (BubbleTextView) childViewHolder.itemView;
                        i3 = spanGroupIndex;
                    }
                } else {
                    continue;
                }
            }
            i2++;
        }
        if (bubbleTextViewArr.length == 0 || bubbleTextViewArr[0] == null) {
            Log.e("ConfigBuilder", "No icons rendered in all apps");
            m23cf();
            return;
        }
        this.mBubbleTextView = bubbleTextViewArr[0];
        this.mNano.f106es = min;
        int i4 = 0;
        while (true) {
            if (i4 >= bubbleTextViewArr.length) {
                i = 0;
                break;
            } else if (bubbleTextViewArr[i4] == null) {
                int i5 = i4;
                i = min - i4;
                min = i5;
                break;
            } else {
                i4++;
            }
        }
        this.f87co = appsView.getChildViewHolder(bubbleTextViewArr[0]).getItemViewType() == 4;
        a_search viewBounds = getViewBounds(bubbleTextViewArr[min - 1]);
        a_search viewBounds2 = getViewBounds(bubbleTextViewArr[0]);
        if (!Utilities.isRtl(this.mActivity.getResources())) {
            a_search a_search = viewBounds;
            viewBounds = viewBounds2;
            viewBounds2 = a_search;
        }
        int i6 = viewBounds2.f95eh;
        int i7 = viewBounds2.f93ef - viewBounds.f93ef;
        int i8 = i7 / min;
        viewBounds.f95eh = i7 + i6;
        if (Utilities.isRtl(this.mActivity.getResources())) {
            int i9 = i * i6;
            viewBounds.f93ef -= i9;
            viewBounds.f95eh += i9;
        } else {
            viewBounds.f95eh += i * (i8 + i6);
        }
        this.mNano.f101en = viewBounds;
        if (!this.f87co) {
            viewBounds.f94eg -= viewBounds.f92ee;
        } else if (view != null) {
            a_search viewBounds3 = getViewBounds(view);
            viewBounds3.f95eh = viewBounds.f95eh;
            this.mNano.f113ez = viewBounds3;
        }
        m19bW();
        List predictedApps = appsView.getApps().getPredictedApps();
        int min2 = Math.min(predictedApps.size(), min);
        this.mNano.f102eo = new b_search[min2];
        for (int i10 = 0; i10 < min2; i10++) {
            this.mNano.f102eo[i10] = m20bZ((AppInfo) predictedApps.get(i10), i10);
        }
    }

    /* renamed from: cf */
    private void m23cf() {
        this.mNano.f106es = this.mActivity.getDeviceProfile().allAppsNumCols;
        int width = this.mActivity.getHotseat().getWidth();
        int dimensionPixelSize = this.mActivity.getResources().getDimensionPixelSize(C0622R.dimen.dynamic_grid_edge_margin);
        a_search a_search = new a_search();
        a_search.f93ef = dimensionPixelSize;
        a_search.f95eh = (width - dimensionPixelSize) - dimensionPixelSize;
        a_search.f92ee = this.mActivity.getDeviceProfile().allAppsCellHeightPx;
        this.mNano.f101en = a_search;
        m19bW();
        AlphabeticalAppsList apps = getAppsView().getApps();
        int i = 0;
        this.mBubbleTextView = (BubbleTextView) this.mActivity.getLayoutInflater().inflate(C0622R.layout.all_apps_icon, getAppsView(), false);
        ViewGroup.LayoutParams layoutParams = this.mBubbleTextView.getLayoutParams();
        layoutParams.height = a_search.f92ee;
        layoutParams.width = a_search.f95eh / this.mNano.f106es;
        if (!apps.getApps().isEmpty()) {
            this.mBubbleTextView.applyFromApplicationInfo((AppInfo) apps.getApps().get(0));
        }
        this.mBubbleTextView.measure(MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824), MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824));
        this.mBubbleTextView.layout(0, 0, layoutParams.width, layoutParams.height);
        ArrayList arrayList = new ArrayList(this.mNano.f106es);
        for (ComponentKeyMapper findApp : this.mActivity.getPredictedApps()) {
            AppInfo findApp2 = apps.findApp(findApp);
            if (findApp2 != null) {
                arrayList.add(m20bZ(findApp2, i));
                i++;
                if (i >= this.mNano.f106es) {
                    break;
                }
            }
        }
        this.mNano.f102eo = (b_search[]) arrayList.toArray(new b_search[arrayList.size()]);
    }

    private static a_search getViewBounds(View view) {
        a_search a_search = new a_search();
        if (view == null) {
            return a_search;
        }
        a_search.f95eh = view.getWidth();
        a_search.f92ee = view.getHeight();
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        a_search.f93ef = iArr[0];
        a_search.f94eg = iArr[1];
        return a_search;
    }

    public byte[] build() {
        this.mNano.f100em = getBackgroundColor();
        this.mNano.f104eq = Themes.getAttrBoolean(this.mActivity, C0622R.attr.isMainColorDark);
        if (this.mIsAllApps) {
            m22ce();
        } else {
            m23cf();
        }
        this.mNano.f103ep = "icon_view_template";
        this.mBundle.putParcelable(this.mNano.f103ep, searchIconTemplate());
        this.mNano.f105er = "icon_long_click";
        this.mBundle.putParcelable(this.mNano.f105er, PendingIntent.getBroadcast(this.mActivity, 2055, new Intent().setComponent(new ComponentName(this.mActivity, LongClickReceiver.class)), 1207959552));
        LongClickReceiver.m24bq(this.mActivity);
        this.mNano.f109ev = getViewBounds(this.mQsbLayout);
        this.mNano.f99eA = this.mIsAllApps;
        if (this.mIsAllApps) {
            m21cd();
        }
        d_search d_search = new d_search();
        d_search.f114eB = this.mNano;
        return MessageNano.toByteArray(d_search);
    }

    public Bundle getExtras() {
        return this.mBundle;
    }
}
