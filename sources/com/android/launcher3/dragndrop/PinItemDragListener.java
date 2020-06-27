package com.android.launcher3.dragndrop;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.LauncherApps.PinItemRequest;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.view.DragEvent;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.PendingItemDragHelper;
import com.android.launcher3.widget.WidgetAddFlowHandler;

@TargetApi(26)
public class PinItemDragListener extends BaseItemDragListener implements Parcelable {
    public static final Creator<PinItemDragListener> CREATOR = new Creator<PinItemDragListener>() {
        public PinItemDragListener createFromParcel(Parcel parcel) {
            return new PinItemDragListener(parcel);
        }

        public PinItemDragListener[] newArray(int i) {
            return new PinItemDragListener[i];
        }
    };
    public static final String EXTRA_PIN_ITEM_DRAG_LISTENER = "pin_item_drag_listener";
    private final PinItemRequest mRequest;

    public int describeContents() {
        return 0;
    }

    public PinItemDragListener(PinItemRequest pinItemRequest, Rect rect, int i, int i2) {
        super(rect, i, i2);
        this.mRequest = pinItemRequest;
    }

    private PinItemDragListener(Parcel parcel) {
        super(parcel);
        this.mRequest = (PinItemRequest) PinItemRequest.CREATOR.createFromParcel(parcel);
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        this.mRequest.writeToParcel(parcel, i);
    }

    /* access modifiers changed from: protected */
    public boolean onDragStart(DragEvent dragEvent) {
        if (!this.mRequest.isValid()) {
            return false;
        }
        return super.onDragStart(dragEvent);
    }

    /* access modifiers changed from: protected */
    public PendingItemDragHelper createDragHelper() {
        Object obj;
        if (this.mRequest.getRequestType() == 1) {
            obj = new PendingAddShortcutInfo(new PinShortcutRequestActivityInfo(this.mRequest, this.mLauncher));
        } else {
            LauncherAppWidgetProviderInfo fromProviderInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this.mLauncher, this.mRequest.getAppWidgetProviderInfo(this.mLauncher));
            final PinWidgetFlowHandler pinWidgetFlowHandler = new PinWidgetFlowHandler(fromProviderInfo, this.mRequest);
            obj = new PendingAddWidgetInfo(fromProviderInfo) {
                public WidgetAddFlowHandler getHandler() {
                    return pinWidgetFlowHandler;
                }
            };
        }
        View view = new View(this.mLauncher);
        view.setTag(obj);
        PendingItemDragHelper pendingItemDragHelper = new PendingItemDragHelper(view);
        if (this.mRequest.getRequestType() == 2) {
            pendingItemDragHelper.setPreview(getPreview(this.mRequest));
        }
        return pendingItemDragHelper;
    }

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
        target2.containerType = 10;
    }

    public static RemoteViews getPreview(PinItemRequest pinItemRequest) {
        Bundle extras = pinItemRequest.getExtras();
        if (extras == null || !(extras.get("appWidgetPreview") instanceof RemoteViews)) {
            return null;
        }
        return (RemoteViews) extras.get("appWidgetPreview");
    }

    public static boolean handleDragRequest(Launcher launcher, Intent intent) {
        if (!Utilities.ATLEAST_OREO || intent == null || !"android.intent.action.MAIN".equals(intent.getAction())) {
            return false;
        }
        Parcelable parcelableExtra = intent.getParcelableExtra("pin_item_drag_listener");
        if (!(parcelableExtra instanceof PinItemDragListener)) {
            return false;
        }
        PinItemDragListener pinItemDragListener = (PinItemDragListener) parcelableExtra;
        pinItemDragListener.setLauncher(launcher);
        launcher.getDragLayer().setOnDragListener(pinItemDragListener);
        return true;
    }
}
