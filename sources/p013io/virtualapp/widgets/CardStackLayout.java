package p013io.virtualapp.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import io.va.exposed.R;
import p013io.virtualapp.C1250R;

/* renamed from: io.virtualapp.widgets.CardStackLayout */
public class CardStackLayout extends FrameLayout {
    public static final boolean PARALLAX_ENABLED_DEFAULT = false;
    public static final boolean SHOW_INIT_ANIMATION_DEFAULT = true;
    private CardStackAdapter mAdapter;
    private float mCardGap;
    private float mCardGapBottom;
    private OnCardSelected mOnCardSelectedListener;
    private boolean mParallaxEnabled;
    private int mParallaxScale;
    private boolean mShowInitAnimation;

    /* renamed from: io.virtualapp.widgets.CardStackLayout$OnCardSelected */
    public interface OnCardSelected {
        void onCardSelected(View view, int i);
    }

    public CardStackLayout(Context context) {
        super(context);
        this.mOnCardSelectedListener = null;
        this.mAdapter = null;
        resetDefaults();
    }

    public CardStackLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CardStackLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOnCardSelectedListener = null;
        this.mAdapter = null;
        handleArgs(context, attributeSet, i, 0);
    }

    @TargetApi(21)
    public CardStackLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mOnCardSelectedListener = null;
        this.mAdapter = null;
        handleArgs(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: 0000 */
    public OnCardSelected getOnCardSelectedListener() {
        return this.mOnCardSelectedListener;
    }

    public void setOnCardSelectedListener(OnCardSelected onCardSelected) {
        this.mOnCardSelectedListener = onCardSelected;
    }

    private void resetDefaults() {
        this.mOnCardSelectedListener = null;
    }

    private void handleArgs(Context context, AttributeSet attributeSet, int i, int i2) {
        resetDefaults();
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, C1250R.styleable.CardStackLayout, i, i2);
        this.mParallaxEnabled = obtainStyledAttributes.getBoolean(2, false);
        this.mShowInitAnimation = obtainStyledAttributes.getBoolean(4, true);
        this.mParallaxScale = obtainStyledAttributes.getInteger(3, getResources().getInteger(R.integer.parallax_scale_default));
        this.mCardGap = obtainStyledAttributes.getDimension(0, getResources().getDimension(R.dimen.card_gap));
        this.mCardGapBottom = obtainStyledAttributes.getDimension(1, getResources().getDimension(R.dimen.card_gap_bottom));
        obtainStyledAttributes.recycle();
    }

    public CardStackAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setAdapter(CardStackAdapter cardStackAdapter) {
        this.mAdapter = cardStackAdapter;
        this.mAdapter.setAdapterParams(this);
        for (int i = 0; i < this.mAdapter.getCount(); i++) {
            this.mAdapter.addView(i);
        }
        if (this.mShowInitAnimation) {
            postDelayed(new Runnable() {
                public final void run() {
                    CardStackLayout.this.restoreCards();
                }
            }, 500);
        }
    }

    public int getParallaxScale() {
        return this.mParallaxScale;
    }

    public void setParallaxScale(int i) {
        this.mParallaxScale = i;
    }

    public boolean isParallaxEnabled() {
        return this.mParallaxEnabled;
    }

    public void setParallaxEnabled(boolean z) {
        this.mParallaxEnabled = z;
    }

    public boolean isShowInitAnimation() {
        return this.mShowInitAnimation;
    }

    public void setShowInitAnimation(boolean z) {
        this.mShowInitAnimation = z;
    }

    public float getCardGap() {
        return this.mCardGap;
    }

    public void setCardGap(float f) {
        this.mCardGap = f;
    }

    public float getCardGapBottom() {
        return this.mCardGapBottom;
    }

    public void setCardGapBottom(float f) {
        this.mCardGapBottom = f;
    }

    public boolean isCardSelected() {
        return this.mAdapter.isCardSelected();
    }

    public void removeAdapter() {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        this.mAdapter = null;
        this.mOnCardSelectedListener = null;
    }

    public void restoreCards() {
        this.mAdapter.resetCards();
    }
}
