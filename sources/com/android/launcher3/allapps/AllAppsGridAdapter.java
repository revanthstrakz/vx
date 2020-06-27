package com.android.launcher3.allapps;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.p001v4.view.accessibility.AccessibilityEventCompat;
import android.support.p001v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.p001v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.p001v4.view.accessibility.AccessibilityRecordCompat;
import android.support.p004v7.widget.GridLayoutManager;
import android.support.p004v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.p004v7.widget.RecyclerView.Adapter;
import android.support.p004v7.widget.RecyclerView.Recycler;
import android.support.p004v7.widget.RecyclerView.State;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem;
import com.android.launcher3.anim.SpringAnimationHandler;
import com.android.launcher3.anim.SpringAnimationHandler.AnimationFactory;
import com.android.launcher3.discovery.AppDiscoveryAppInfo;
import com.android.launcher3.discovery.AppDiscoveryItemView;
import com.android.launcher3.util.PackageManagerHelper;
import java.util.List;

public class AllAppsGridAdapter extends Adapter<ViewHolder> {
    public static final String TAG = "AppsGridAdapter";
    public static final int VIEW_TYPE_APPS_LOADING_DIVIDER = 128;
    public static final int VIEW_TYPE_DISCOVERY_ITEM = 256;
    public static final int VIEW_TYPE_EMPTY_SEARCH = 8;
    public static final int VIEW_TYPE_ICON = 2;
    public static final int VIEW_TYPE_MASK_CONTENT = 262;
    public static final int VIEW_TYPE_MASK_DIVIDER = 96;
    public static final int VIEW_TYPE_MASK_HAS_SPRINGS = 70;
    public static final int VIEW_TYPE_MASK_ICON = 6;
    public static final int VIEW_TYPE_PREDICTION_DIVIDER = 64;
    public static final int VIEW_TYPE_PREDICTION_ICON = 4;
    public static final int VIEW_TYPE_SEARCH_MARKET = 16;
    public static final int VIEW_TYPE_SEARCH_MARKET_DIVIDER = 32;
    /* access modifiers changed from: private */
    public final AlphabeticalAppsList mApps;
    /* access modifiers changed from: private */
    public int mAppsPerRow;
    private BindViewCallback mBindViewCallback;
    private String mEmptySearchMessage;
    private final GridLayoutManager mGridLayoutMgr;
    private final GridSpanSizer mGridSizer = new GridSpanSizer();
    private final OnClickListener mIconClickListener;
    private OnFocusChangeListener mIconFocusListener;
    private final OnLongClickListener mIconLongClickListener;
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    private final LayoutInflater mLayoutInflater;
    /* access modifiers changed from: private */
    public Intent mMarketSearchIntent;
    private SpringAnimationHandler<ViewHolder> mSpringAnimationHandler;

    private class AllAppsSpringAnimationFactory implements AnimationFactory<ViewHolder> {
        private static final float DEFAULT_MAX_VALUE_PX = 100.0f;
        private static final float DEFAULT_MIN_VALUE_PX = -100.0f;
        private static final float MAX_SPRING_STIFFNESS = 900.0f;
        private static final float MIN_SPRING_STIFFNESS = 580.0f;
        private static final float ROW_STIFFNESS_COEFFICIENT = 50.0f;
        private static final float SPRING_DAMPING_RATIO = 0.55f;

        private int getAppPosition(int i, int i2, int i3) {
            if (i < i2) {
                return i;
            }
            return (i + (i3 - i2)) - (i2 == 0 ? 0 : 1);
        }

        private AllAppsSpringAnimationFactory() {
        }

        public SpringAnimation initialize(ViewHolder viewHolder) {
            return SpringAnimationHandler.forView(viewHolder.itemView, DynamicAnimation.TRANSLATION_Y, 0.0f);
        }

        public void update(SpringAnimation springAnimation, ViewHolder viewHolder) {
            int appPosition = getAppPosition(viewHolder.getAdapterPosition(), Math.min(AllAppsGridAdapter.this.mAppsPerRow, AllAppsGridAdapter.this.mApps.getPredictedApps().size()), AllAppsGridAdapter.this.mAppsPerRow);
            int access$100 = appPosition % AllAppsGridAdapter.this.mAppsPerRow;
            int access$1002 = appPosition / AllAppsGridAdapter.this.mAppsPerRow;
            int numAppRows = AllAppsGridAdapter.this.mApps.getNumAppRows() - 1;
            if (access$1002 > numAppRows / 2) {
                access$1002 = Math.abs(numAppRows - access$1002);
            }
            calculateSpringValues(springAnimation, access$1002, access$100);
        }

        public void setDefaultValues(SpringAnimation springAnimation) {
            calculateSpringValues(springAnimation, 0, AllAppsGridAdapter.this.mAppsPerRow / 2);
        }

        private void calculateSpringValues(SpringAnimation springAnimation, int i, int i2) {
            float columnFactor = (((float) (i + 1)) * 0.5f) + getColumnFactor(i2, AllAppsGridAdapter.this.mAppsPerRow);
            float f = DEFAULT_MIN_VALUE_PX * columnFactor;
            float f2 = columnFactor * DEFAULT_MAX_VALUE_PX;
            ((SpringAnimation) ((SpringAnimation) springAnimation.setMinValue(f)).setMaxValue(f2)).getSpring().setStiffness(Utilities.boundToRange(MAX_SPRING_STIFFNESS - (((float) i) * 50.0f), (float) MIN_SPRING_STIFFNESS, (float) MAX_SPRING_STIFFNESS)).setDampingRatio(SPRING_DAMPING_RATIO);
        }

        private float getColumnFactor(int i, int i2) {
            float f = (float) (i2 / 2);
            float f2 = (float) i;
            int abs = (int) Math.abs(f2 - f);
            if ((i2 % 2 == 0) && f2 < f) {
                abs--;
            }
            float f3 = 0.0f;
            while (abs > 0) {
                f3 += abs == 1 ? 0.2f : 0.1f;
                abs--;
            }
            return f3;
        }
    }

    public class AppsGridLayoutManager extends GridLayoutManager {
        public AppsGridLayoutManager(Context context) {
            super(context, 1, 1, false);
        }

        public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            AccessibilityRecordCompat asRecord = AccessibilityEventCompat.asRecord(accessibilityEvent);
            asRecord.setItemCount(AllAppsGridAdapter.this.mApps.getNumFilteredApps());
            asRecord.setFromIndex(Math.max(0, asRecord.getFromIndex() - getRowsNotForAccessibility(asRecord.getFromIndex())));
            asRecord.setToIndex(Math.max(0, asRecord.getToIndex() - getRowsNotForAccessibility(asRecord.getToIndex())));
        }

        public int getRowCountForAccessibility(Recycler recycler, State state) {
            return super.getRowCountForAccessibility(recycler, state) - getRowsNotForAccessibility(AllAppsGridAdapter.this.mApps.getAdapterItems().size() - 1);
        }

        public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfoForItem(recycler, state, view, accessibilityNodeInfoCompat);
            LayoutParams layoutParams = view.getLayoutParams();
            CollectionItemInfoCompat collectionItemInfo = accessibilityNodeInfoCompat.getCollectionItemInfo();
            if ((layoutParams instanceof GridLayoutManager.LayoutParams) && collectionItemInfo != null) {
                accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(collectionItemInfo.getRowIndex() - getRowsNotForAccessibility(((GridLayoutManager.LayoutParams) layoutParams).getViewAdapterPosition()), collectionItemInfo.getRowSpan(), collectionItemInfo.getColumnIndex(), collectionItemInfo.getColumnSpan(), collectionItemInfo.isHeading(), collectionItemInfo.isSelected()));
            }
        }

        private int getRowsNotForAccessibility(int i) {
            List adapterItems = AllAppsGridAdapter.this.mApps.getAdapterItems();
            int max = Math.max(i, AllAppsGridAdapter.this.mApps.getAdapterItems().size() - 1);
            int i2 = 0;
            for (int i3 = 0; i3 <= max; i3++) {
                if (!AllAppsGridAdapter.isViewType(((AdapterItem) adapterItems.get(i3)).viewType, AllAppsGridAdapter.VIEW_TYPE_MASK_CONTENT)) {
                    i2++;
                }
            }
            return i2;
        }
    }

    public interface BindViewCallback {
        void onBindView(ViewHolder viewHolder);
    }

    public class GridSpanSizer extends SpanSizeLookup {
        public GridSpanSizer() {
            setSpanIndexCacheEnabled(true);
        }

        public int getSpanSize(int i) {
            if (AllAppsGridAdapter.isIconViewType(((AdapterItem) AllAppsGridAdapter.this.mApps.getAdapterItems().get(i)).viewType)) {
                return 1;
            }
            return AllAppsGridAdapter.this.mAppsPerRow;
        }
    }

    public static class ViewHolder extends android.support.p004v7.widget.RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    public static boolean isViewType(int i, int i2) {
        return (i & i2) != 0;
    }

    public boolean onFailedToRecycleView(ViewHolder viewHolder) {
        return true;
    }

    public AllAppsGridAdapter(Launcher launcher, AlphabeticalAppsList alphabeticalAppsList, OnClickListener onClickListener, OnLongClickListener onLongClickListener) {
        Resources resources = launcher.getResources();
        this.mLauncher = launcher;
        this.mApps = alphabeticalAppsList;
        this.mEmptySearchMessage = resources.getString(C0622R.string.all_apps_loading_message);
        this.mGridLayoutMgr = new AppsGridLayoutManager(launcher);
        this.mGridLayoutMgr.setSpanSizeLookup(this.mGridSizer);
        this.mLayoutInflater = LayoutInflater.from(launcher);
        this.mIconClickListener = onClickListener;
        this.mIconLongClickListener = onLongClickListener;
        this.mSpringAnimationHandler = new SpringAnimationHandler<>(0, new AllAppsSpringAnimationFactory());
    }

    public SpringAnimationHandler getSpringAnimationHandler() {
        return this.mSpringAnimationHandler;
    }

    public static boolean isDividerViewType(int i) {
        return isViewType(i, 96);
    }

    public static boolean isIconViewType(int i) {
        return isViewType(i, 6);
    }

    public void setNumAppsPerRow(int i) {
        this.mAppsPerRow = i;
        this.mGridLayoutMgr.setSpanCount(i);
    }

    public int getNumAppsPerRow() {
        return this.mAppsPerRow;
    }

    public void setIconFocusListener(OnFocusChangeListener onFocusChangeListener) {
        this.mIconFocusListener = onFocusChangeListener;
    }

    public void setLastSearchQuery(String str) {
        this.mEmptySearchMessage = this.mLauncher.getResources().getString(C0622R.string.all_apps_no_search_results, new Object[]{str});
        this.mMarketSearchIntent = PackageManagerHelper.getMarketSearchIntent(this.mLauncher, str);
    }

    public void setBindViewCallback(BindViewCallback bindViewCallback) {
        this.mBindViewCallback = bindViewCallback;
    }

    public GridLayoutManager getLayoutManager() {
        return this.mGridLayoutMgr;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 2 || i == 4) {
            BubbleTextView bubbleTextView = (BubbleTextView) this.mLayoutInflater.inflate(C0622R.layout.all_apps_icon, viewGroup, false);
            bubbleTextView.setOnClickListener(this.mIconClickListener);
            bubbleTextView.setOnLongClickListener(this.mIconLongClickListener);
            bubbleTextView.setLongPressTimeout(ViewConfiguration.getLongPressTimeout());
            bubbleTextView.setOnFocusChangeListener(this.mIconFocusListener);
            bubbleTextView.getLayoutParams().height = this.mLauncher.getDeviceProfile().allAppsCellHeightPx;
            return new ViewHolder(bubbleTextView);
        } else if (i == 8) {
            return new ViewHolder(this.mLayoutInflater.inflate(C0622R.layout.all_apps_empty_search, viewGroup, false));
        } else {
            if (i == 16) {
                View inflate = this.mLayoutInflater.inflate(C0622R.layout.all_apps_search_market, viewGroup, false);
                inflate.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        AllAppsGridAdapter.this.mLauncher.startActivitySafely(view, AllAppsGridAdapter.this.mMarketSearchIntent, null);
                    }
                });
                return new ViewHolder(inflate);
            } else if (i == 32 || i == 64) {
                return new ViewHolder(this.mLayoutInflater.inflate(C0622R.layout.all_apps_divider, viewGroup, false));
            } else {
                if (i == 128) {
                    return new ViewHolder(this.mLayoutInflater.inflate(C0622R.layout.all_apps_discovery_loading_divider, viewGroup, false));
                }
                if (i == 256) {
                    AppDiscoveryItemView appDiscoveryItemView = (AppDiscoveryItemView) this.mLayoutInflater.inflate(C0622R.layout.all_apps_discovery_item, viewGroup, false);
                    appDiscoveryItemView.init(this.mIconClickListener, this.mLauncher.getAccessibilityDelegate(), this.mIconLongClickListener);
                    return new ViewHolder(appDiscoveryItemView);
                }
                throw new RuntimeException("Unexpected view type");
            }
        }
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 2 || itemViewType == 4) {
            ((BubbleTextView) viewHolder.itemView).applyFromApplicationInfo(((AdapterItem) this.mApps.getAdapterItems().get(i)).appInfo);
        } else {
            int i2 = 8;
            if (itemViewType == 8) {
                TextView textView = (TextView) viewHolder.itemView;
                textView.setText(this.mEmptySearchMessage);
                textView.setGravity(this.mApps.hasNoFilteredResults() ? 17 : 8388627);
            } else if (itemViewType == 16) {
                TextView textView2 = (TextView) viewHolder.itemView;
                if (this.mMarketSearchIntent != null) {
                    textView2.setVisibility(0);
                } else {
                    textView2.setVisibility(8);
                }
            } else if (itemViewType == 128) {
                int i3 = this.mApps.isAppDiscoveryRunning() ? 0 : 8;
                if (!this.mApps.isAppDiscoveryRunning()) {
                    i2 = 0;
                }
                viewHolder.itemView.findViewById(C0622R.C0625id.loadingProgressBar).setVisibility(i3);
                viewHolder.itemView.findViewById(C0622R.C0625id.loadedDivider).setVisibility(i2);
            } else if (itemViewType == 256) {
                ((AppDiscoveryItemView) viewHolder.itemView).apply((AppDiscoveryAppInfo) ((AdapterItem) this.mApps.getAdapterItems().get(i)).appInfo);
            }
        }
        if (this.mBindViewCallback != null) {
            this.mBindViewCallback.onBindView(viewHolder);
        }
    }

    public void onViewAttachedToWindow(ViewHolder viewHolder) {
        if (isViewType(viewHolder.getItemViewType(), 70)) {
            this.mSpringAnimationHandler.add(viewHolder.itemView, viewHolder);
        }
    }

    public void onViewDetachedFromWindow(ViewHolder viewHolder) {
        if (isViewType(viewHolder.getItemViewType(), 70)) {
            this.mSpringAnimationHandler.remove(viewHolder.itemView);
        }
    }

    public int getItemCount() {
        return this.mApps.getAdapterItems().size();
    }

    public int getItemViewType(int i) {
        return ((AdapterItem) this.mApps.getAdapterItems().get(i)).viewType;
    }
}
