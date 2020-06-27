package com.android.launcher3.discovery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.launcher3.C0622R;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AppDiscoveryItemView extends RelativeLayout {
    private static boolean SHOW_REVIEW_COUNT = false;
    private ImageView mImage;
    private OnLongClickListener mOnLongClickListener;
    private TextView mPrice;
    private TextView mRatingText;
    private RatingView mRatingView;
    private TextView mReviewCount;
    private TextView mTitle;

    public AppDiscoveryItemView(Context context) {
        this(context, null);
    }

    public AppDiscoveryItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppDiscoveryItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mImage = (ImageView) findViewById(C0622R.C0625id.image);
        this.mTitle = (TextView) findViewById(C0622R.C0625id.title);
        this.mRatingText = (TextView) findViewById(C0622R.C0625id.rating);
        this.mRatingView = (RatingView) findViewById(C0622R.C0625id.rating_view);
        this.mPrice = (TextView) findViewById(C0622R.C0625id.price);
        this.mReviewCount = (TextView) findViewById(C0622R.C0625id.review_count);
    }

    public void init(OnClickListener onClickListener, AccessibilityDelegate accessibilityDelegate, OnLongClickListener onLongClickListener) {
        setOnClickListener(onClickListener);
        this.mImage.setOnClickListener(onClickListener);
        setAccessibilityDelegate(accessibilityDelegate);
        this.mOnLongClickListener = onLongClickListener;
    }

    public void apply(@NonNull AppDiscoveryAppInfo appDiscoveryAppInfo) {
        setTag(appDiscoveryAppInfo);
        this.mImage.setTag(appDiscoveryAppInfo);
        this.mImage.setImageBitmap(appDiscoveryAppInfo.iconBitmap);
        this.mImage.setOnLongClickListener(appDiscoveryAppInfo.isDragAndDropSupported() ? this.mOnLongClickListener : null);
        this.mTitle.setText(appDiscoveryAppInfo.title);
        this.mPrice.setText(appDiscoveryAppInfo.priceFormatted != null ? appDiscoveryAppInfo.priceFormatted : "");
        this.mReviewCount.setVisibility(SHOW_REVIEW_COUNT ? 0 : 8);
        if (appDiscoveryAppInfo.rating >= 0.0f) {
            this.mRatingText.setText(new DecimalFormat("#.0").format((double) appDiscoveryAppInfo.rating));
            this.mRatingView.setRating(appDiscoveryAppInfo.rating);
            this.mRatingView.setVisibility(0);
            String format = NumberFormat.getInstance().format(appDiscoveryAppInfo.reviewCount);
            TextView textView = this.mReviewCount;
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            sb.append(format);
            sb.append(")");
            textView.setText(sb.toString());
            return;
        }
        this.mRatingView.setVisibility(8);
        this.mRatingText.setText("");
        this.mReviewCount.setText("");
    }
}
