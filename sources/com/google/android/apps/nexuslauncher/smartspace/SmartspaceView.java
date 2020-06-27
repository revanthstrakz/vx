package com.google.android.apps.nexuslauncher.smartspace;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri.Builder;
import android.os.Handler;
import android.os.Process;
import android.provider.CalendarContract;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.dynamicui.WallpaperColorInfo;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.util.Themes;
import com.google.android.apps.nexuslauncher.DynamicIconProvider;
import com.google.android.apps.nexuslauncher.graphics.IcuDateTextView;
import java.util.ArrayList;
import java.util.Collections;

public class SmartspaceView extends FrameLayout implements ISmartspace, AnimatorUpdateListener, OnClickListener, OnLongClickListener, Runnable {

    /* renamed from: dB */
    private final TextPaint f132dB;

    /* renamed from: dH */
    private final ColorStateList f133dH;

    /* renamed from: dp */
    private final SmartspaceController f134dp;
    /* access modifiers changed from: private */

    /* renamed from: dq */
    public SmartspaceDataContainer f135dq;

    /* renamed from: dr */
    private BubbleTextView f136dr;

    /* renamed from: ds */
    private boolean f137ds;
    private final OnClickListener mCalendarClickListener = new OnClickListener() {
        public void onClick(View view) {
            SmartspaceView.this.mo12995cp(10000);
            Builder appendPath = CalendarContract.CONTENT_URI.buildUpon().appendPath("time");
            ContentUris.appendId(appendPath, System.currentTimeMillis());
            try {
                Launcher.getLauncher(SmartspaceView.this.getContext()).startActivitySafely(view, new Intent("android.intent.action.VIEW").setData(appendPath.build()).addFlags(270532608), null);
            } catch (ActivityNotFoundException unused) {
                LauncherAppsCompat.getInstance(SmartspaceView.this.getContext()).showAppDetailsForProfile(new ComponentName(DynamicIconProvider.GOOGLE_CALENDAR, ""), Process.myUserHandle());
            }
        }
    };
    private IcuDateTextView mClockView;
    private boolean mDoubleLine;
    private final Handler mHandler;
    private final int mSmartspaceBackgroundRes;
    private ViewGroup mSmartspaceContent;
    private ImageView mSubtitleIcon;
    private TextView mSubtitleText;
    private ViewGroup mSubtitleWeatherContent;
    private ImageView mSubtitleWeatherIcon;
    private TextView mSubtitleWeatherText;
    private View mTitleSeparator;
    private TextView mTitleText;
    private ViewGroup mTitleWeatherContent;
    private ImageView mTitleWeatherIcon;
    private TextView mTitleWeatherText;
    private final OnClickListener mWeatherClickListener = new OnClickListener() {
        public void onClick(View view) {
            if (SmartspaceView.this.f135dq != null && SmartspaceView.this.f135dq.isWeatherAvailable()) {
                SmartspaceView.this.mo12995cp(10001);
                SmartspaceView.this.f135dq.f130dO.click(view);
            }
        }
    };

    /* renamed from: com.google.android.apps.nexuslauncher.smartspace.SmartspaceView$h */
    final class C0946h implements OnClickListener {

        /* renamed from: dZ */
        final SmartspaceView f138dZ;

        C0946h(SmartspaceView smartspaceView) {
            this.f138dZ = smartspaceView;
        }

        public void onClick(View view) {
            this.f138dZ.mo12995cp(10000);
            Builder appendPath = CalendarContract.CONTENT_URI.buildUpon().appendPath("time");
            ContentUris.appendId(appendPath, System.currentTimeMillis());
            try {
                Launcher.getLauncher(this.f138dZ.getContext()).startActivitySafely(view, new Intent("android.intent.action.VIEW").setData(appendPath.build()).addFlags(270532608), null);
            } catch (ActivityNotFoundException unused) {
                LauncherAppsCompat.getInstance(this.f138dZ.getContext()).showAppDetailsForProfile(new ComponentName(DynamicIconProvider.GOOGLE_CALENDAR, ""), Process.myUserHandle());
            }
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: cp */
    public final void mo12995cp(int i) {
    }

    public SmartspaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f134dp = SmartspaceController.get(context);
        this.mHandler = new Handler();
        this.f133dH = ColorStateList.valueOf(Themes.getAttrColor(getContext(), C0622R.attr.workspaceTextColor));
        this.f137ds = this.f134dp.mo12984cY();
        this.mSmartspaceBackgroundRes = C0622R.C0624drawable.bg_smartspace;
        this.f132dB = new TextPaint();
        this.f132dB.setTextSize((float) getResources().getDimensionPixelSize(C0622R.dimen.smartspace_title_size));
    }

    private void initListeners(SmartspaceDataContainer smartspaceDataContainer) {
        boolean cS = smartspaceDataContainer.mo12989cS();
        if (this.mDoubleLine != cS) {
            this.mDoubleLine = cS;
            m68cs();
        }
        setOnClickListener(this);
        setOnLongClickListener(m67co());
        if (this.mDoubleLine) {
            loadDoubleLine(smartspaceDataContainer);
        } else {
            loadSingleLine(smartspaceDataContainer);
        }
        this.mHandler.removeCallbacks(this);
        if (smartspaceDataContainer.mo12989cS() && smartspaceDataContainer.f131dP.mo12972cv()) {
            long cw = smartspaceDataContainer.f131dP.mo12973cw();
            long currentTimeMillis = 61000 - (System.currentTimeMillis() % 60000);
            if (cw > 0) {
                currentTimeMillis = Math.min(currentTimeMillis, cw);
            }
            this.mHandler.postDelayed(this, currentTimeMillis);
        }
    }

    private void loadDoubleLine(SmartspaceDataContainer smartspaceDataContainer) {
        setBackgroundResource(this.mSmartspaceBackgroundRes);
        SmartspaceCard smartspaceCard = smartspaceDataContainer.f131dP;
        if (!TextUtils.isEmpty(smartspaceCard.getTitle())) {
            this.mTitleText.setText(smartspaceCard.mo12972cv() ? m66cn() : smartspaceCard.getTitle());
            this.mTitleText.setEllipsize(smartspaceCard.mo12974cx(true));
        }
        if (!TextUtils.isEmpty(smartspaceCard.mo12975cy()) || smartspaceCard.getIcon() != null) {
            this.mSubtitleText.setText(smartspaceCard.mo12975cy());
            this.mSubtitleText.setEllipsize(smartspaceCard.mo12974cx(false));
            if (smartspaceCard.getIcon() != null) {
                this.mSubtitleIcon.setImageTintList((!smartspaceCard.mo12976cz() || !WallpaperColorInfo.getInstance(getContext()).supportsDarkText()) ? null : this.f133dH);
                this.mSubtitleIcon.setImageBitmap(smartspaceCard.getIcon());
            }
        }
        if (smartspaceDataContainer.isWeatherAvailable()) {
            this.mSubtitleWeatherContent.setVisibility(0);
            this.mSubtitleWeatherContent.setOnClickListener(this.mWeatherClickListener);
            this.mSubtitleWeatherContent.setOnLongClickListener(m67co());
            this.mSubtitleWeatherText.setText(smartspaceDataContainer.f130dO.getTitle());
            this.mSubtitleWeatherIcon.setImageBitmap(smartspaceDataContainer.f130dO.getIcon());
            return;
        }
        this.mSubtitleWeatherContent.setVisibility(8);
    }

    private void loadSingleLine(SmartspaceDataContainer smartspaceDataContainer) {
        setBackgroundResource(0);
        if (smartspaceDataContainer.isWeatherAvailable()) {
            this.mTitleSeparator.setVisibility(0);
            this.mTitleWeatherContent.setVisibility(0);
            this.mTitleWeatherText.setText(smartspaceDataContainer.f130dO.getTitle());
            this.mTitleWeatherIcon.setImageBitmap(smartspaceDataContainer.f130dO.getIcon());
        } else {
            this.mTitleWeatherContent.setVisibility(8);
            this.mTitleSeparator.setVisibility(8);
        }
        if (!Utilities.ATLEAST_NOUGAT) {
            this.mClockView.onVisibilityAggregated(true);
        }
    }

    private void loadViews() {
        this.mTitleText = (TextView) findViewById(C0622R.C0625id.title_text);
        this.mSubtitleText = (TextView) findViewById(C0622R.C0625id.subtitle_text);
        this.mSubtitleIcon = (ImageView) findViewById(C0622R.C0625id.subtitle_icon);
        this.mTitleWeatherIcon = (ImageView) findViewById(C0622R.C0625id.title_weather_icon);
        this.mSubtitleWeatherIcon = (ImageView) findViewById(C0622R.C0625id.subtitle_weather_icon);
        this.mSmartspaceContent = (ViewGroup) findViewById(C0622R.C0625id.smartspace_content);
        this.mTitleWeatherContent = (ViewGroup) findViewById(C0622R.C0625id.title_weather_content);
        this.mSubtitleWeatherContent = (ViewGroup) findViewById(C0622R.C0625id.subtitle_weather_content);
        this.mTitleWeatherText = (TextView) findViewById(C0622R.C0625id.title_weather_text);
        this.mSubtitleWeatherText = (TextView) findViewById(C0622R.C0625id.subtitle_weather_text);
        backportClockVisibility(false);
        this.mClockView = (IcuDateTextView) findViewById(C0622R.C0625id.clock);
        backportClockVisibility(true);
        this.mTitleSeparator = findViewById(C0622R.C0625id.title_sep);
        setGoogleSans(this.mTitleText, this.mSubtitleText, this.mTitleWeatherText, this.mSubtitleWeatherText, this.mClockView);
    }

    private void setGoogleSans(TextView... textViewArr) {
        Typeface createFromAsset = Typeface.createFromAsset(getContext().getAssets(), "fonts/GoogleSans-Regular.ttf");
        for (TextView textView : textViewArr) {
            if (textView != null) {
                textView.setTypeface(createFromAsset);
            }
        }
    }

    /* renamed from: cn */
    private String m66cn() {
        SmartspaceCard smartspaceCard = this.f135dq.f131dP;
        return smartspaceCard.mo12967cC(TextUtils.ellipsize(smartspaceCard.mo12966cB(true), this.f132dB, ((float) (((getWidth() - getPaddingLeft()) - getPaddingRight()) - getResources().getDimensionPixelSize(C0622R.dimen.smartspace_horizontal_padding))) - this.f132dB.measureText(smartspaceCard.mo12965cA(true)), TruncateAt.END).toString());
    }

    /* renamed from: co */
    private OnLongClickListener m67co() {
        if (this.f137ds) {
            return this;
        }
        return null;
    }

    /* renamed from: cs */
    private void m68cs() {
        int indexOfChild = indexOfChild(this.mSmartspaceContent);
        removeView(this.mSmartspaceContent);
        addView(LayoutInflater.from(getContext()).inflate(this.mDoubleLine ? C0622R.layout.smartspace_twolines : C0622R.layout.smartspace_singleline, this, false), indexOfChild);
        loadViews();
    }

    /* renamed from: cq */
    public void mo12961cq() {
        this.f137ds = this.f134dp.mo12984cY();
        if (this.f135dq != null) {
            mo12962cr(this.f135dq);
        } else {
            Log.d("SmartspaceView", "onGsaChanged but no data present");
        }
    }

    /* renamed from: cr */
    public void mo12962cr(SmartspaceDataContainer smartspaceDataContainer) {
        this.f135dq = smartspaceDataContainer;
        boolean z = this.mSmartspaceContent.getVisibility() == 0;
        initListeners(this.f135dq);
        if (!z) {
            this.mSmartspaceContent.setVisibility(0);
            this.mSmartspaceContent.setAlpha(0.0f);
            this.mSmartspaceContent.animate().setDuration(200).alpha(1.0f);
        }
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.f134dp.mo12986da(this);
    }

    public void onClick(View view) {
        if (this.f135dq != null && this.f135dq.mo12989cS()) {
            mo12995cp(10002);
            this.f135dq.f131dP.click(view);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SmartspaceController.get(getContext()).mo12986da(null);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        loadViews();
        this.f136dr = (BubbleTextView) findViewById(C0622R.C0625id.dummyBubbleTextView);
        this.f136dr.setTag(new ItemInfo() {
            public ComponentName getTargetComponent() {
                return new ComponentName(SmartspaceView.this.getContext(), "");
            }
        });
        this.f136dr.setContentDescription("");
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.f135dq != null && this.f135dq.mo12989cS() && this.f135dq.f131dP.mo12972cv()) {
            String cn = m66cn();
            if (!cn.equals(this.mTitleText.getText())) {
                this.mTitleText.setText(cn);
            }
        }
    }

    public boolean onLongClick(View view) {
        Launcher launcher = Launcher.getLauncher(getContext());
        PopupContainerWithArrow popupContainerWithArrow = (PopupContainerWithArrow) launcher.getLayoutInflater().inflate(C0622R.layout.popup_container, launcher.getDragLayer(), false);
        popupContainerWithArrow.setVisibility(4);
        launcher.getDragLayer().addView(popupContainerWithArrow);
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(new SmartspacePreferencesShortcut());
        popupContainerWithArrow.populateAndShow(this.f136dr, Collections.EMPTY_LIST, Collections.EMPTY_LIST, arrayList);
        return true;
    }

    public void onPause() {
        this.mHandler.removeCallbacks(this);
        backportClockVisibility(false);
    }

    public void onResume() {
        if (this.f135dq != null) {
            initListeners(this.f135dq);
        }
        backportClockVisibility(true);
    }

    private void backportClockVisibility(boolean z) {
        if (!Utilities.ATLEAST_NOUGAT && this.mClockView != null) {
            this.mClockView.onVisibilityAggregated(z && !this.mDoubleLine);
        }
    }

    public void run() {
        if (this.f135dq != null) {
            initListeners(this.f135dq);
        }
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        super.setPadding(0, 0, 0, 0);
    }
}
