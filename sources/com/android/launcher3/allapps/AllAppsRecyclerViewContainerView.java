package com.android.launcher3.allapps;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.BubbleTextView.BubbleTextShadowHandler;
import com.android.launcher3.C0622R;
import com.android.launcher3.ClickShadowView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;

public class AllAppsRecyclerViewContainerView extends RelativeLayout implements BubbleTextShadowHandler {
    private final ClickShadowView mTouchFeedbackView;

    public AllAppsRecyclerViewContainerView(Context context) {
        this(context, null);
    }

    public AllAppsRecyclerViewContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AllAppsRecyclerViewContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        DeviceProfile deviceProfile = Launcher.getLauncher(context).getDeviceProfile();
        this.mTouchFeedbackView = new ClickShadowView(context);
        int extraSize = deviceProfile.allAppsIconSizePx + this.mTouchFeedbackView.getExtraSize();
        addView(this.mTouchFeedbackView, extraSize, extraSize);
    }

    public void setPressedIcon(BubbleTextView bubbleTextView, Bitmap bitmap) {
        if (bubbleTextView == null || bitmap == null) {
            this.mTouchFeedbackView.setBitmap(null);
            this.mTouchFeedbackView.animate().cancel();
        } else if (this.mTouchFeedbackView.setBitmap(bitmap)) {
            this.mTouchFeedbackView.alignWithIconView(bubbleTextView, (ViewGroup) bubbleTextView.getParent(), findViewById(C0622R.C0625id.apps_list_view));
            this.mTouchFeedbackView.animateShadow();
        }
    }
}
