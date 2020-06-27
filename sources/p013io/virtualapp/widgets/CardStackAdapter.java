package p013io.virtualapp.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout.LayoutParams;
import io.va.exposed.R;
import java.util.ArrayList;
import java.util.List;

/* renamed from: io.virtualapp.widgets.CardStackAdapter */
public abstract class CardStackAdapter implements OnTouchListener, OnClickListener {
    public static final int ANIM_DURATION = 600;
    public static final int DECELERATION_FACTOR = 2;
    public static final int INVALID_CARD_POSITION = -1;
    private final int dp30;
    private float dp8;
    private int fullCardHeight;
    private float mCardGap;
    private float mCardGapBottom;
    private int mCardPaddingInternal = 0;
    private View[] mCardViews;
    private boolean mParallaxEnabled;
    private int mParallaxScale;
    private CardStackLayout mParent;
    private int mParentPaddingTop = 0;
    private final int mScreenHeight;
    private boolean mScreenTouchable = false;
    /* access modifiers changed from: private */
    public int mSelectedCardPosition = -1;
    private boolean mShowInitAnimation;
    private float mTouchDistance = 0.0f;
    private float mTouchFirstY = -1.0f;
    private float mTouchPrevY = -1.0f;
    private float scaleFactorForElasticEffect;

    public abstract View createView(int i, ViewGroup viewGroup);

    public abstract int getCount();

    public CardStackAdapter(Context context) {
        Resources resources = context.getResources();
        this.mScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        this.dp30 = (int) resources.getDimension(R.dimen.dp30);
        this.scaleFactorForElasticEffect = (float) ((int) resources.getDimension(R.dimen.dp8));
        this.dp8 = (float) ((int) resources.getDimension(R.dimen.dp8));
    }

    /* access modifiers changed from: protected */
    public float getCardGapBottom() {
        return this.mCardGapBottom;
    }

    public boolean isScreenTouchable() {
        return this.mScreenTouchable;
    }

    /* access modifiers changed from: private */
    public void setScreenTouchable(boolean z) {
        this.mScreenTouchable = z;
    }

    /* access modifiers changed from: 0000 */
    public void addView(int i) {
        View createView = createView(i, this.mParent);
        createView.setOnTouchListener(this);
        createView.setTag(R.id.cardstack_internal_position_tag, Integer.valueOf(i));
        createView.setLayerType(2, null);
        this.mCardPaddingInternal = createView.getPaddingTop();
        createView.setLayoutParams(new LayoutParams(-1, this.fullCardHeight));
        if (this.mShowInitAnimation) {
            createView.setY(getCardFinalY(i));
            setScreenTouchable(false);
        } else {
            createView.setY(getCardOriginalY(i) - ((float) this.mParentPaddingTop));
            setScreenTouchable(true);
        }
        this.mCardViews[i] = createView;
        this.mParent.addView(createView);
    }

    /* access modifiers changed from: protected */
    public float getCardFinalY(int i) {
        return (((float) (this.mScreenHeight - this.dp30)) - (((float) (getCount() - i)) * this.mCardGapBottom)) - ((float) this.mCardPaddingInternal);
    }

    /* access modifiers changed from: protected */
    public float getCardOriginalY(int i) {
        return ((float) this.mParentPaddingTop) + (this.mCardGap * ((float) i));
    }

    public void resetCards(Runnable runnable) {
        ArrayList arrayList = new ArrayList(getCount());
        for (int i = 0; i < getCount(); i++) {
            View view = this.mCardViews[i];
            arrayList.add(ObjectAnimator.ofFloat(view, View.Y, new float[]{(float) ((int) view.getY()), getCardOriginalY(i)}));
        }
        startAnimations(arrayList, runnable, true);
    }

    private void startAnimations(List<Animator> list, final Runnable runnable, final boolean z) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(list);
        animatorSet.setDuration(600);
        animatorSet.setInterpolator(new DecelerateInterpolator(2.0f));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (runnable != null) {
                    runnable.run();
                }
                CardStackAdapter.this.setScreenTouchable(true);
                if (z) {
                    CardStackAdapter.this.mSelectedCardPosition = -1;
                }
            }
        });
        animatorSet.start();
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!isScreenTouchable()) {
            return false;
        }
        float rawY = motionEvent.getRawY();
        int intValue = ((Integer) view.getTag(R.id.cardstack_internal_position_tag)).intValue();
        switch (motionEvent.getAction()) {
            case 0:
                if (this.mTouchFirstY == -1.0f) {
                    this.mTouchFirstY = rawY;
                    this.mTouchPrevY = rawY;
                    this.mTouchDistance = 0.0f;
                    break;
                } else {
                    return false;
                }
            case 1:
            case 3:
                if (this.mTouchDistance >= this.dp8 || Math.abs(rawY - this.mTouchFirstY) >= this.dp8 || this.mSelectedCardPosition != -1) {
                    resetCards();
                } else {
                    onClick(view);
                }
                this.mTouchFirstY = -1.0f;
                this.mTouchPrevY = -1.0f;
                this.mTouchDistance = 0.0f;
                return false;
            case 2:
                if (this.mSelectedCardPosition == -1) {
                    moveCards(intValue, rawY - this.mTouchFirstY);
                }
                this.mTouchDistance += Math.abs(rawY - this.mTouchPrevY);
                break;
        }
        return true;
    }

    public void onClick(View view) {
        if (isScreenTouchable()) {
            setScreenTouchable(false);
            if (this.mSelectedCardPosition == -1) {
                this.mSelectedCardPosition = ((Integer) view.getTag(R.id.cardstack_internal_position_tag)).intValue();
                ArrayList arrayList = new ArrayList(getCount());
                for (int i = 0; i < getCount(); i++) {
                    arrayList.add(getAnimatorForView(this.mCardViews[i], i, this.mSelectedCardPosition));
                }
                startAnimations(arrayList, new Runnable(view) {
                    private final /* synthetic */ View f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        CardStackAdapter.lambda$onClick$125(CardStackAdapter.this, this.f$1);
                    }
                }, false);
            }
        }
    }

    public static /* synthetic */ void lambda$onClick$125(CardStackAdapter cardStackAdapter, View view) {
        cardStackAdapter.setScreenTouchable(true);
        if (cardStackAdapter.mParent.getOnCardSelectedListener() != null) {
            cardStackAdapter.mParent.getOnCardSelectedListener().onCardSelected(view, cardStackAdapter.mSelectedCardPosition);
        }
    }

    /* access modifiers changed from: protected */
    public Animator getAnimatorForView(View view, int i, int i2) {
        if (i != i2) {
            return ObjectAnimator.ofFloat(view, View.Y, new float[]{(float) ((int) view.getY()), getCardFinalY(i)});
        }
        return ObjectAnimator.ofFloat(view, View.Y, new float[]{(float) ((int) view.getY()), getCardOriginalY(0) + (((float) i) * this.mCardGapBottom)});
    }

    private void moveCards(int i, float f) {
        int i2;
        if (f >= 0.0f && i >= 0 && i < getCount()) {
            while (i < getCount()) {
                View view = this.mCardViews[i];
                float f2 = f / this.scaleFactorForElasticEffect;
                if (!this.mParallaxEnabled) {
                    i2 = (getCount() * 2) + 1;
                } else if (this.mParallaxScale > 0) {
                    f2 *= (float) (this.mParallaxScale / 3);
                    i2 = (getCount() + 1) - i;
                } else {
                    i2 = (((this.mParallaxScale * -1) / 3) * i) + 1;
                }
                view.setY(getCardOriginalY(i) + (f2 * ((float) i2)));
                i++;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setAdapterParams(CardStackLayout cardStackLayout) {
        this.mParent = cardStackLayout;
        this.mCardViews = new View[getCount()];
        this.mCardGapBottom = cardStackLayout.getCardGapBottom();
        this.mCardGap = cardStackLayout.getCardGap();
        this.mParallaxScale = cardStackLayout.getParallaxScale();
        this.mParallaxEnabled = cardStackLayout.isParallaxEnabled();
        if (this.mParallaxEnabled && this.mParallaxScale == 0) {
            this.mParallaxEnabled = false;
        }
        this.mShowInitAnimation = cardStackLayout.isShowInitAnimation();
        this.mParentPaddingTop = cardStackLayout.getPaddingTop();
        this.fullCardHeight = (int) ((((float) (this.mScreenHeight - this.dp30)) - this.dp8) - (((float) getCount()) * this.mCardGapBottom));
    }

    public void resetCards() {
        resetCards(null);
    }

    public boolean isCardSelected() {
        return this.mSelectedCardPosition != -1;
    }

    public int getSelectedCardPosition() {
        return this.mSelectedCardPosition;
    }

    public View getCardView(int i) {
        if (this.mCardViews == null) {
            return null;
        }
        return this.mCardViews[i];
    }
}
