package com.google.android.apps.nexuslauncher.graphics;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import com.android.launcher3.C0622R;
import com.android.launcher3.Utilities;
import java.util.Locale;

public class IcuDateTextView extends DoubleShadowTextView {
    private DateFormat mDateFormat;
    private boolean mIsVisible;
    private final BroadcastReceiver mTimeChangeReceiver;

    public IcuDateTextView(Context context) {
        this(context, null);
    }

    public IcuDateTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        this.mIsVisible = false;
        this.mTimeChangeReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                IcuDateTextView.this.reloadDateFormat(!"android.intent.action.TIME_TICK".equals(intent.getAction()));
            }
        };
    }

    @TargetApi(24)
    public void reloadDateFormat(boolean z) {
        String str;
        if (Utilities.ATLEAST_NOUGAT) {
            if (this.mDateFormat == null || z) {
                DateFormat instanceForSkeleton = DateFormat.getInstanceForSkeleton(getContext().getString(C0622R.string.icu_abbrev_wday_month_day_no_year), Locale.getDefault());
                this.mDateFormat = instanceForSkeleton;
                instanceForSkeleton.setContext(DisplayContext.CAPITALIZATION_FOR_STANDALONE);
            }
            str = this.mDateFormat.format(Long.valueOf(System.currentTimeMillis()));
        } else {
            str = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(), 65554);
        }
        setText(str);
        setContentDescription(str);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        getContext().registerReceiver(this.mTimeChangeReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        getContext().unregisterReceiver(this.mTimeChangeReceiver);
    }

    public void onVisibilityAggregated(boolean z) {
        if (Utilities.ATLEAST_NOUGAT) {
            super.onVisibilityAggregated(z);
        }
        if (!this.mIsVisible && z) {
            this.mIsVisible = true;
            registerReceiver();
            reloadDateFormat(true);
        } else if (this.mIsVisible && !z) {
            unregisterReceiver();
            this.mIsVisible = false;
        }
    }
}
