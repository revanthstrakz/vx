package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Point;
import android.support.p004v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.launcher3.BaseContainerView;
import com.android.launcher3.C0622R;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageUserKey;
import java.util.List;

public class WidgetsContainerView extends BaseContainerView implements OnLongClickListener, OnClickListener, DragSource {
    private static final boolean LOGD = false;
    private static final String TAG = "WidgetsContainerView";
    private WidgetsListAdapter mAdapter;
    Launcher mLauncher;
    private WidgetsRecyclerView mRecyclerView;
    private Toast mWidgetInstructionToast;

    public float getIntrinsicIconScaleFactor() {
        return 0.0f;
    }

    public boolean supportsAppInfoDropTarget() {
        return true;
    }

    public boolean supportsDeleteDropTarget() {
        return false;
    }

    public WidgetsContainerView(Context context) {
        this(context, null);
    }

    public WidgetsContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetsContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLauncher = Launcher.getLauncher(context);
        LauncherAppState instance = LauncherAppState.getInstance(context);
        WidgetsListAdapter widgetsListAdapter = new WidgetsListAdapter(context, LayoutInflater.from(context), instance.getWidgetCache(), new AlphabeticIndexCompat(context), this, this, new WidgetsDiffReporter(instance.getIconCache()));
        this.mAdapter = widgetsListAdapter;
        this.mAdapter.setNotifyListener();
    }

    public View getTouchDelegateTargetView() {
        return this.mRecyclerView;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mRecyclerView = (WidgetsRecyclerView) getContentView().findViewById(C0622R.C0625id.widgets_list_view);
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void scrollToTop() {
        this.mRecyclerView.scrollToPosition(0);
    }

    public void onClick(View view) {
        if (this.mLauncher.isWidgetsViewVisible() && !this.mLauncher.getWorkspace().isSwitchingState() && (view instanceof WidgetCell)) {
            handleClick();
        }
    }

    public void handleClick() {
        if (this.mWidgetInstructionToast != null) {
            this.mWidgetInstructionToast.cancel();
        }
        this.mWidgetInstructionToast = Toast.makeText(getContext(), Utilities.wrapForTts(getContext().getText(C0622R.string.long_press_widget_to_add), getContext().getString(C0622R.string.long_accessible_way_to_add)), 0);
        this.mWidgetInstructionToast.show();
    }

    public boolean onLongClick(View view) {
        if (!this.mLauncher.isWidgetsViewVisible()) {
            return false;
        }
        return handleLongClick(view);
    }

    public boolean handleLongClick(View view) {
        if (!this.mLauncher.getWorkspace().isSwitchingState() && this.mLauncher.isDraggingEnabled()) {
            return beginDragging(view);
        }
        return false;
    }

    private boolean beginDragging(View view) {
        if (!(view instanceof WidgetCell)) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Unexpected dragging view: ");
            sb.append(view);
            Log.e(str, sb.toString());
        } else if (!beginDraggingWidget((WidgetCell) view)) {
            return false;
        }
        if (this.mLauncher.getDragController().isDragging()) {
            this.mLauncher.enterSpringLoadedDragMode();
        }
        return true;
    }

    private boolean beginDraggingWidget(WidgetCell widgetCell) {
        WidgetImageView widgetImageView = (WidgetImageView) widgetCell.findViewById(C0622R.C0625id.widget_preview);
        if (widgetImageView.getBitmap() == null) {
            return false;
        }
        int[] iArr = new int[2];
        this.mLauncher.getDragLayer().getLocationInDragLayer(widgetImageView, iArr);
        new PendingItemDragHelper(widgetCell).startDrag(widgetImageView.getBitmapBounds(), widgetImageView.getBitmap().getWidth(), widgetImageView.getWidth(), new Point(iArr[0], iArr[1]), this, new DragOptions());
        return true;
    }

    public void onDropCompleted(View view, DragObject dragObject, boolean z, boolean z2) {
        if (z || !z2 || (view != this.mLauncher.getWorkspace() && !(view instanceof DeleteDropTarget) && !(view instanceof Folder))) {
            this.mLauncher.exitSpringLoadedDragModeDelayed(true, 500, null);
        }
        this.mLauncher.unlockScreenOrientation(false);
        if (!z2) {
            dragObject.deferDragViewCleanupPostAnimation = false;
        }
    }

    public void setWidgets(MultiHashMap<PackageItemInfo, WidgetItem> multiHashMap) {
        this.mAdapter.setWidgets(multiHashMap);
        View findViewById = getContentView().findViewById(C0622R.C0625id.loader);
        if (findViewById != null) {
            ((ViewGroup) getContentView()).removeView(findViewById);
        }
    }

    public boolean isEmpty() {
        return this.mAdapter.getItemCount() == 0;
    }

    public List<WidgetItem> getWidgetsForPackageUser(PackageUserKey packageUserKey) {
        return this.mAdapter.copyWidgetsForPackageUser(packageUserKey);
    }

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
        target2.containerType = 5;
    }
}
