package com.google.android.apps.nexuslauncher.qsb;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.annotation.NonNull;
import android.support.p001v4.graphics.ColorUtils;
import android.support.p004v7.widget.RecyclerView;
import android.support.p004v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup.MarginLayoutParams;
import com.android.launcher3.BaseRecyclerView;
import com.android.launcher3.C0622R;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsRecyclerView;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.allapps.SearchUiManager;
import com.android.launcher3.allapps.SearchUiManager.OnScrollRangeChangeListener;
import com.android.launcher3.dynamicui.WallpaperColorInfo;
import com.android.launcher3.dynamicui.WallpaperColorInfo.OnChangeListener;
import com.android.launcher3.util.Themes;

public class AllAppsQsbLayout extends AbstractQsbLayout implements SearchUiManager, OnChangeListener {
    private int mAlpha;
    private AlphabeticalAppsList mApps;
    private Bitmap mBitmap;
    /* access modifiers changed from: private */
    public FallbackAppsSearchView mFallback;
    private AllAppsRecyclerView mRecyclerView;
    private SpringAnimation mSpring;
    /* access modifiers changed from: private */
    public float mStartY;

    /* access modifiers changed from: protected */
    public void loadBottomMargin() {
    }

    public void preDispatchKeyEvent(KeyEvent keyEvent) {
    }

    public AllAppsQsbLayout(Context context) {
        this(context, null);
    }

    public AllAppsQsbLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AllAppsQsbLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAlpha = 0;
        setOnClickListener(this);
        this.mStartY = getTranslationY();
        setTranslationY((float) Math.round(this.mStartY));
        this.mSpring = new SpringAnimation(this, new FloatPropertyCompat<AllAppsQsbLayout>("allAppsQsbLayoutSpringAnimation") {
            public float getValue(AllAppsQsbLayout allAppsQsbLayout) {
                return allAppsQsbLayout.getTranslationY() + AllAppsQsbLayout.this.mStartY;
            }

            public void setValue(AllAppsQsbLayout allAppsQsbLayout, float f) {
                allAppsQsbLayout.setTranslationY((float) Math.round(AllAppsQsbLayout.this.mStartY + f));
            }
        }, 0.0f);
    }

    public void addOnScrollRangeChangeListener(final OnScrollRangeChangeListener onScrollRangeChangeListener) {
        this.mActivity.getHotseat().addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                if (AllAppsQsbLayout.this.mActivity.getDeviceProfile().isVerticalBarLayout()) {
                    onScrollRangeChangeListener.onScrollRangeChanged(i4);
                } else {
                    onScrollRangeChangeListener.onScrollRangeChanged((i4 - HotseatQsbWidget.getBottomMargin(AllAppsQsbLayout.this.mActivity)) - ((((MarginLayoutParams) AllAppsQsbLayout.this.getLayoutParams()).topMargin + ((int) AllAppsQsbLayout.this.getTranslationY())) + AllAppsQsbLayout.this.getResources().getDimensionPixelSize(C0622R.dimen.qsb_widget_height)));
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void useAlpha(int i) {
        int boundToRange = Utilities.boundToRange(i, 0, 255);
        if (this.mAlpha != boundToRange) {
            this.mAlpha = boundToRange;
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public int getWidth(int i) {
        if (this.mActivity.getDeviceProfile().isVerticalBarLayout()) {
            return (i - this.mRecyclerView.getPaddingLeft()) - this.mRecyclerView.getPaddingRight();
        }
        CellLayout layout = this.mActivity.getHotseat().getLayout();
        return (i - layout.getPaddingLeft()) - layout.getPaddingRight();
    }

    public void draw(Canvas canvas) {
        if (this.mAlpha > 0) {
            if (this.mBitmap == null) {
                this.mBitmap = createBitmap(getResources().getDimension(C0622R.dimen.hotseat_qsb_scroll_shadow_blur_radius), getResources().getDimension(C0622R.dimen.hotseat_qsb_scroll_key_shadow_offset), 0);
            }
            this.mShadowPaint.setAlpha(this.mAlpha);
            loadDimensions(this.mBitmap, canvas);
            this.mShadowPaint.setAlpha(255);
        }
        super.draw(canvas);
    }

    public void initialize(AlphabeticalAppsList alphabeticalAppsList, AllAppsRecyclerView allAppsRecyclerView) {
        this.mApps = alphabeticalAppsList;
        allAppsRecyclerView.setPadding(allAppsRecyclerView.getPaddingLeft(), (getLayoutParams().height / 2) + getResources().getDimensionPixelSize(C0622R.dimen.all_apps_extra_search_padding), allAppsRecyclerView.getPaddingRight(), allAppsRecyclerView.getPaddingBottom());
        allAppsRecyclerView.addOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                AllAppsQsbLayout.this.useAlpha(((BaseRecyclerView) recyclerView).getCurrentScrollY());
            }
        });
        allAppsRecyclerView.setVerticalFadingEdgeEnabled(true);
        this.mRecyclerView = allAppsRecyclerView;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) getLayoutParams();
        int i = marginLayoutParams.topMargin;
        Resources resources = getResources();
        if (identifier <= 0) {
            identifier = C0622R.dimen.status_bar_height;
        }
        marginLayoutParams.topMargin = i + resources.getDimensionPixelSize(identifier);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        WallpaperColorInfo instance = WallpaperColorInfo.getInstance(getContext());
        instance.addOnChangeListener(this);
        onExtractedColorsChanged(instance);
    }

    public void onClick(View view) {
        super.onClick(view);
        if (view == this && (this.mFallback == null || this.mFallback.getVisibility() == 8)) {
            if (Utilities.ATLEAST_OREO) {
                ConfigBuilder configBuilder = new ConfigBuilder(this, true);
                if (this.mActivity.getGoogleNow().startSearch(configBuilder.build(), configBuilder.getExtras())) {
                    return;
                }
            }
            startAppsSearch();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        WallpaperColorInfo.getInstance(getContext()).removeOnChangeListener(this);
        super.onDetachedFromWindow();
    }

    public void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo) {
        mo12905bz(ColorUtils.compositeColors(ColorUtils.compositeColors(getResources().getColor(Themes.getAttrBoolean(this.mActivity, C0622R.attr.isMainColorDark) ? C0622R.color.qsb_background_drawer_dark : C0622R.color.qsb_background_drawer_default), Themes.getAttrColor(this.mActivity, C0622R.attr.allAppsScrimColor)), wallpaperColorInfo.getMainColor()));
    }

    public void startAppsSearch() {
        if (this.mFallback == null) {
            this.mFallback = (FallbackAppsSearchView) this.mActivity.getLayoutInflater().inflate(C0622R.layout.all_apps_google_search_fallback, this, false);
            this.mFallback.initialize(this, this.mApps, this.mRecyclerView);
            this.mFallback.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View view, boolean z) {
                    if (!z && TextUtils.isEmpty(AllAppsQsbLayout.this.mFallback.getText())) {
                        AllAppsQsbLayout.this.mFallback.setVisibility(8);
                    }
                }
            });
            addView(this.mFallback);
        }
        this.mFallback.setVisibility(0);
        this.mFallback.showKeyboard();
    }

    public void refreshSearchResult() {
        if (this.mFallback != null) {
            this.mFallback.refreshSearchResult();
        }
    }

    public void reset() {
        useAlpha(0);
        if (this.mFallback != null) {
            this.mFallback.clearSearchResult();
            setOnClickListener(this);
            removeView(this.mFallback);
            this.mFallback = null;
        }
    }

    @NonNull
    public SpringAnimation getSpringForFling() {
        return this.mSpring;
    }
}
