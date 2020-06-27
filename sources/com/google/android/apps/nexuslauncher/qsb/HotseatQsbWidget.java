package com.google.android.apps.nexuslauncher.qsb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.C0622R;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.dragndrop.DragLayer;
import p013io.virtualapp.utils.HanziToPinyin.Token;

public class HotseatQsbWidget extends AbstractQsbLayout {
    /* access modifiers changed from: private */
    public AnimatorSet mAnimatorSet;
    private final BroadcastReceiver mBroadcastReceiver;
    private boolean mGoogleHasFocus;
    private boolean mIsDefaultLiveWallpaper;
    private boolean mSearchRequested;

    public HotseatQsbWidget(Context context) {
        this(context, null);
    }

    public HotseatQsbWidget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HotseatQsbWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                HotseatQsbWidget.this.setGoogleColored();
            }
        };
        this.mIsDefaultLiveWallpaper = isDefaultLiveWallpaper();
        setColors();
        setOnClickListener(this);
    }

    static int getBottomMargin(Launcher launcher) {
        Rect insets = launcher.getDragLayer().getInsets();
        Resources resources = launcher.getResources();
        return resources.getDimensionPixelSize(C0622R.dimen.qsb_hotseat_bottom_margin) + (insets.bottom == 0 ? resources.getDimensionPixelSize(C0622R.dimen.qsb_hotseat_bottom_margin_hw) : insets.bottom);
    }

    private void setColors() {
        View.inflate(new ContextThemeWrapper(getContext(), this.mIsDefaultLiveWallpaper ? C0622R.style.HotseatQsbTheme_Colored : C0622R.style.HotseatQsbTheme), C0622R.layout.qsb_hotseat_content, this);
        mo12905bz(getResources().getColor(this.mIsDefaultLiveWallpaper ? C0622R.color.qsb_background_hotseat_white : C0622R.color.qsb_background_hotseat_default));
    }

    private void openQSB() {
        this.mSearchRequested = false;
        this.mGoogleHasFocus = true;
        playAnimation(true, true);
    }

    private void closeQSB(boolean z) {
        this.mSearchRequested = false;
        if (this.mGoogleHasFocus) {
            this.mGoogleHasFocus = false;
            playAnimation(false, z);
        }
    }

    private Intent getSearchIntent() {
        int[] iArr = new int[2];
        getLocationInWindow(iArr);
        Rect rect = new Rect(0, 0, getWidth(), getHeight());
        rect.offset(iArr[0], iArr[1]);
        rect.inset(getPaddingLeft(), getPaddingTop());
        return ConfigBuilder.getSearchIntent(rect, findViewById(C0622R.C0625id.g_icon), this.mMicIconView);
    }

    /* access modifiers changed from: private */
    public void setGoogleColored() {
        if (this.mIsDefaultLiveWallpaper != isDefaultLiveWallpaper()) {
            this.mIsDefaultLiveWallpaper = !this.mIsDefaultLiveWallpaper;
            removeAllViews();
            setColors();
            loadAndGetPreferences();
        }
    }

    /* access modifiers changed from: private */
    public void playQsbAnimation() {
        if (hasWindowFocus()) {
            this.mSearchRequested = true;
        } else {
            openQSB();
        }
    }

    private boolean isDefaultLiveWallpaper() {
        WallpaperInfo wallpaperInfo = WallpaperManager.getInstance(getContext()).getWallpaperInfo();
        return wallpaperInfo != null && wallpaperInfo.getComponent().flattenToString().equals(getContext().getString(C0622R.string.default_live_wallpaper));
    }

    private void doOnClick() {
        ConfigBuilder configBuilder = new ConfigBuilder(this, false);
        if (this.mActivity.getGoogleNow().startSearch(configBuilder.build(), configBuilder.getExtras())) {
            SharedPreferences devicePrefs = Utilities.getDevicePrefs(getContext());
            devicePrefs.edit().putInt("key_hotseat_qsb_tap_count", devicePrefs.getInt("key_hotseat_qsb_tap_count", 0) + 1).apply();
            playQsbAnimation();
            return;
        }
        getContext().sendOrderedBroadcast(getSearchIntent(), null, new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                StringBuilder sb = new StringBuilder();
                sb.append(getResultCode());
                sb.append(Token.SEPARATOR);
                sb.append(getResultData());
                Log.e("HotseatQsbSearch", sb.toString());
                if (getResultCode() == 0) {
                    HotseatQsbWidget.this.fallbackSearch("com.google.android.googlequicksearchbox.TEXT_ASSIST");
                } else {
                    HotseatQsbWidget.this.playQsbAnimation();
                }
            }
        }, null, 0, null, null);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x004e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void noGoogleAppSearch() {
        /*
            r5 = this;
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "com.google.android.apps.searchlite.WIDGET_ACTION"
            r0.<init>(r1)
            java.lang.String r1 = "com.google.android.apps.searchlite/.ui.SearchActivity"
            android.content.ComponentName r1 = android.content.ComponentName.unflattenFromString(r1)
            android.content.Intent r0 = r0.setComponent(r1)
            r1 = 268468224(0x10008000, float:2.5342157E-29)
            android.content.Intent r0 = r0.setFlags(r1)
            java.lang.String r1 = "showKeyboard"
            r2 = 1
            android.content.Intent r0 = r0.putExtra(r1, r2)
            java.lang.String r1 = "contentType"
            r2 = 12
            android.content.Intent r0 = r0.putExtra(r1, r2)
            android.content.Context r1 = r5.getContext()
            android.content.pm.PackageManager r2 = r1.getPackageManager()
            r3 = 0
            java.util.List r2 = r2.queryIntentActivities(r0, r3)
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x0074
            android.content.Intent r0 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x004e }
            java.lang.String r2 = "android.intent.action.VIEW"
            java.lang.String r4 = "https://google.com"
            android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ ActivityNotFoundException -> 0x004e }
            r0.<init>(r2, r4)     // Catch:{ ActivityNotFoundException -> 0x004e }
            r1.startActivity(r0)     // Catch:{ ActivityNotFoundException -> 0x004e }
            r5.openQSB()     // Catch:{ ActivityNotFoundException -> 0x004e }
            goto L_0x0081
        L_0x004e:
            android.content.Context r0 = r5.getContext()     // Catch:{ NameNotFoundException -> 0x0081 }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ NameNotFoundException -> 0x0081 }
            java.lang.String r1 = "com.google.android.googlequicksearchbox"
            r0.getPackageInfo(r1, r3)     // Catch:{ NameNotFoundException -> 0x0081 }
            android.content.Context r0 = r5.getContext()     // Catch:{ NameNotFoundException -> 0x0081 }
            com.android.launcher3.compat.LauncherAppsCompat r0 = com.android.launcher3.compat.LauncherAppsCompat.getInstance(r0)     // Catch:{ NameNotFoundException -> 0x0081 }
            android.content.ComponentName r1 = new android.content.ComponentName     // Catch:{ NameNotFoundException -> 0x0081 }
            java.lang.String r2 = "com.google.android.googlequicksearchbox"
            java.lang.String r3 = ".SearchActivity"
            r1.<init>(r2, r3)     // Catch:{ NameNotFoundException -> 0x0081 }
            android.os.UserHandle r2 = android.os.Process.myUserHandle()     // Catch:{ NameNotFoundException -> 0x0081 }
            r0.showAppDetailsForProfile(r1, r2)     // Catch:{ NameNotFoundException -> 0x0081 }
            goto L_0x0081
        L_0x0074:
            r5.openQSB()
            android.animation.AnimatorSet r2 = r5.mAnimatorSet
            com.google.android.apps.nexuslauncher.qsb.HotseatQsbWidget$3 r3 = new com.google.android.apps.nexuslauncher.qsb.HotseatQsbWidget$3
            r3.<init>(r1, r0)
            r2.addListener(r3)
        L_0x0081:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.apps.nexuslauncher.qsb.HotseatQsbWidget.noGoogleAppSearch():void");
    }

    private void playAnimation(boolean z, boolean z2) {
        if (this.mAnimatorSet != null) {
            this.mAnimatorSet.cancel();
        }
        this.mAnimatorSet = new AnimatorSet();
        this.mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator, boolean z) {
                if (animator == HotseatQsbWidget.this.mAnimatorSet) {
                    HotseatQsbWidget.this.mAnimatorSet = null;
                }
            }
        });
        DragLayer dragLayer = this.mActivity.getDragLayer();
        float[] fArr = new float[1];
        float[] fArr2 = new float[1];
        if (z) {
            fArr[0] = 0.0f;
            this.mAnimatorSet.play(ObjectAnimator.ofFloat(dragLayer, View.ALPHA, fArr));
            fArr2[0] = (float) ((-this.mActivity.getHotseat().getHeight()) / 2);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(dragLayer, View.TRANSLATION_Y, fArr2);
            ofFloat.setInterpolator(new AccelerateInterpolator());
            this.mAnimatorSet.play(ofFloat);
        } else {
            fArr[0] = 1.0f;
            this.mAnimatorSet.play(ObjectAnimator.ofFloat(dragLayer, View.ALPHA, fArr));
            fArr2[0] = 0.0f;
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(dragLayer, View.TRANSLATION_Y, fArr2);
            ofFloat2.setInterpolator(new DecelerateInterpolator());
            this.mAnimatorSet.play(ofFloat2);
        }
        this.mAnimatorSet.setDuration(200);
        this.mAnimatorSet.start();
        if (!z2) {
            this.mAnimatorSet.end();
        }
    }

    /* access modifiers changed from: protected */
    public int getWidth(int i) {
        CellLayout layout = this.mActivity.getHotseat().getLayout();
        return (i - layout.getPaddingLeft()) - layout.getPaddingRight();
    }

    /* access modifiers changed from: protected */
    public void loadBottomMargin() {
        ((MarginLayoutParams) getLayoutParams()).bottomMargin = getBottomMargin(this.mActivity);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().registerReceiver(this.mBroadcastReceiver, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"));
    }

    public void onClick(View view) {
        super.onClick(view);
        if (view == this) {
            doOnClick();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(this.mBroadcastReceiver);
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!z && this.mSearchRequested) {
            openQSB();
        } else if (z) {
            closeQSB(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        closeQSB(false);
    }
}
