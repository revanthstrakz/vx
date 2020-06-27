package com.android.launcher3.allapps.search;

import android.content.Context;
import android.graphics.Rect;
import android.support.animation.FloatValueHolder;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.android.launcher3.C0622R;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.Launcher;
import com.android.launcher3.allapps.AllAppsGridAdapter;
import com.android.launcher3.allapps.AllAppsRecyclerView;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.allapps.SearchUiManager;
import com.android.launcher3.allapps.SearchUiManager.OnScrollRangeChangeListener;
import com.android.launcher3.allapps.search.AllAppsSearchBarController.Callbacks;
import com.android.launcher3.discovery.AppDiscoveryItem;
import com.android.launcher3.discovery.AppDiscoveryUpdateState;
import com.android.launcher3.graphics.TintedDrawableSpan;
import com.android.launcher3.util.ComponentKey;
import java.util.ArrayList;

public class AppsSearchContainerLayout extends FrameLayout implements SearchUiManager, Callbacks {
    private AllAppsGridAdapter mAdapter;
    private AlphabeticalAppsList mApps;
    private AllAppsRecyclerView mAppsRecyclerView;
    private View mDivider;
    private HeaderElevationController mElevationController;
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    /* access modifiers changed from: private */
    public final int mMinHeight;
    private final AllAppsSearchBarController mSearchBarController;
    /* access modifiers changed from: private */
    public final int mSearchBoxHeight;
    private ExtendedEditText mSearchInput;
    private final SpannableStringBuilder mSearchQueryBuilder;
    private SpringAnimation mSpring;

    public AppsSearchContainerLayout(Context context) {
        this(context, null);
    }

    public AppsSearchContainerLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppsSearchContainerLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLauncher = Launcher.getLauncher(context);
        this.mMinHeight = getResources().getDimensionPixelSize(C0622R.dimen.all_apps_search_bar_height);
        this.mSearchBoxHeight = getResources().getDimensionPixelSize(C0622R.dimen.all_apps_search_bar_field_height);
        this.mSearchBarController = new AllAppsSearchBarController();
        this.mSearchQueryBuilder = new SpannableStringBuilder();
        Selection.setSelection(this.mSearchQueryBuilder, 0);
        this.mSpring = new SpringAnimation(new FloatValueHolder()).setSpring(new SpringForce(0.0f));
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mSearchInput = (ExtendedEditText) findViewById(C0622R.C0625id.search_box_input);
        this.mDivider = findViewById(C0622R.C0625id.search_divider);
        this.mElevationController = new HeaderElevationController(this.mDivider);
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        sb.append(this.mSearchInput.getHint());
        SpannableString spannableString = new SpannableString(sb.toString());
        spannableString.setSpan(new TintedDrawableSpan(getContext(), C0622R.C0624drawable.ic_allapps_search), 0, 1, 34);
        this.mSearchInput.setHint(spannableString);
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        if (!deviceProfile.isVerticalBarLayout()) {
            LayoutParams layoutParams = (LayoutParams) this.mDivider.getLayoutParams();
            int i = deviceProfile.edgeMarginPx;
            layoutParams.rightMargin = i;
            layoutParams.leftMargin = i;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (!this.mLauncher.getDeviceProfile().isVerticalBarLayout()) {
            getLayoutParams().height = this.mLauncher.getDragLayer().getInsets().top + this.mMinHeight;
        }
        super.onMeasure(i, i2);
    }

    public void initialize(AlphabeticalAppsList alphabeticalAppsList, AllAppsRecyclerView allAppsRecyclerView) {
        this.mApps = alphabeticalAppsList;
        this.mAppsRecyclerView = allAppsRecyclerView;
        this.mAppsRecyclerView.addOnScrollListener(this.mElevationController);
        this.mAdapter = (AllAppsGridAdapter) this.mAppsRecyclerView.getAdapter();
        this.mSearchBarController.initialize(new DefaultAppSearchAlgorithm(getContext(), alphabeticalAppsList.getApps()), this.mSearchInput, this.mLauncher, this);
    }

    @NonNull
    public SpringAnimation getSpringForFling() {
        return this.mSpring;
    }

    public void refreshSearchResult() {
        this.mSearchBarController.refreshSearchResult();
    }

    public void reset() {
        this.mElevationController.reset();
        this.mSearchBarController.reset();
    }

    public void preDispatchKeyEvent(KeyEvent keyEvent) {
        if (!this.mSearchBarController.isSearchFieldFocused() && keyEvent.getAction() == 0) {
            int unicodeChar = keyEvent.getUnicodeChar();
            if ((unicodeChar > 0 && !Character.isWhitespace(unicodeChar) && !Character.isSpaceChar(unicodeChar)) && TextKeyListener.getInstance().onKeyDown(this, this.mSearchQueryBuilder, keyEvent.getKeyCode(), keyEvent) && this.mSearchQueryBuilder.length() > 0) {
                this.mSearchBarController.focusSearchField();
            }
        }
    }

    public void startAppsSearch() {
        if (this.mApps != null) {
            this.mSearchBarController.focusSearchField();
        }
    }

    public void onSearchResult(String str, ArrayList<ComponentKey> arrayList) {
        if (arrayList != null) {
            this.mApps.setOrderedFilter(arrayList);
            notifyResultChanged();
            this.mAdapter.setLastSearchQuery(str);
        }
    }

    public void clearSearchResult() {
        if (this.mApps.setOrderedFilter(null)) {
            notifyResultChanged();
        }
        this.mSearchQueryBuilder.clear();
        this.mSearchQueryBuilder.clearSpans();
        Selection.setSelection(this.mSearchQueryBuilder, 0);
    }

    public void onAppDiscoverySearchUpdate(@Nullable AppDiscoveryItem appDiscoveryItem, @NonNull AppDiscoveryUpdateState appDiscoveryUpdateState) {
        if (!this.mLauncher.isDestroyed()) {
            this.mApps.onAppDiscoverySearchUpdate(appDiscoveryItem, appDiscoveryUpdateState);
            notifyResultChanged();
        }
    }

    private void notifyResultChanged() {
        this.mElevationController.reset();
        this.mAppsRecyclerView.onSearchResultsChanged();
    }

    public void addOnScrollRangeChangeListener(final OnScrollRangeChangeListener onScrollRangeChangeListener) {
        this.mLauncher.getHotseat().addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                DeviceProfile deviceProfile = AppsSearchContainerLayout.this.mLauncher.getDeviceProfile();
                if (!deviceProfile.isVerticalBarLayout()) {
                    Rect insets = AppsSearchContainerLayout.this.mLauncher.getDragLayer().getInsets();
                    onScrollRangeChangeListener.onScrollRangeChanged(((i4 - deviceProfile.hotseatBarBottomPaddingPx) - insets.bottom) - ((insets.top + (AppsSearchContainerLayout.this.mMinHeight - AppsSearchContainerLayout.this.mSearchBoxHeight)) + ((MarginLayoutParams) AppsSearchContainerLayout.this.getLayoutParams()).bottomMargin));
                    return;
                }
                onScrollRangeChangeListener.onScrollRangeChanged(i4);
            }
        });
    }
}
