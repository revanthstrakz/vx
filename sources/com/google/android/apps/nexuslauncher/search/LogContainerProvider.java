package com.google.android.apps.nexuslauncher.search;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;

class LogContainerProvider extends FrameLayout implements com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider {
    private final int mPredictedRank;

    public LogContainerProvider(Context context, int i) {
        super(context);
        this.mPredictedRank = i;
    }

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
        if (this.mPredictedRank >= 0) {
            target2.containerType = 7;
            target.predictedRank = this.mPredictedRank;
            return;
        }
        target2.containerType = 8;
    }
}
